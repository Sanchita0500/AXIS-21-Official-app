package axis.axis21.sanchita.axisapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import axis.axis21.sanchita.axisapp.Objects.Blog;

public class Blogs extends AppCompatActivity {

    private RecyclerView blog_view;
    private DatabaseReference mRef;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogs);

        mRef = FirebaseDatabase.getInstance().getReference().child("blogs");
        blog_view = (RecyclerView)findViewById(R.id.blog_view);

        LinearLayoutManager manager = new LinearLayoutManager(Blogs.this, LinearLayoutManager.VERTICAL, false);
        blog_view.setLayoutManager(manager);

        mDialog = new ProgressDialog(Blogs.this);
        mDialog.setMessage("Please wait..");
        mDialog.setTitle("Loading");
        mDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onStart() {
        mDialog.show();
        super.onStart();

        FirebaseRecyclerOptions<Blog> options=new FirebaseRecyclerOptions.Builder<Blog>()
                .setQuery(mRef,Blog.class)
                .build();
        FirebaseRecyclerAdapter<Blog,BlogViewHolder> adapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull Blog model) {
                holder.setmView(Blogs.this,model.getTitle(),model.getDate(),model.getAuthor(), model.getImage(),getRef(position).getKey().toString());
                mDialog.dismiss();
            }

            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_box,parent,false);
                return new BlogViewHolder(view);
            }
        };
        blog_view.setAdapter(adapter);
        adapter.startListening();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        private ImageView b_image;
        private TextView b_title;
        private TextView b_date;
        private TextView author;
        private View mView;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            b_image = mView.findViewById(R.id.blog_image);
            b_title = mView.findViewById(R.id.blog_title);
            b_date = mView.findViewById(R.id.blog_date);
            author = mView.findViewById(R.id.author_name);
        }

        public void setmView(final Context context, String title, String date, String name, String image, final String id){
            b_title.setText(title);
            b_date.setText(date);
            author.setText(name);
            try {
                Picasso.get().load(image).into(b_image);
            }catch (Exception e){
                e.printStackTrace();
            }

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BlogDetails.class);
                    intent.putExtra("id", id);
                    context.startActivity(intent);
                }
            });
        }

    }

    public void goBack(View view){
        finish();
    }
}