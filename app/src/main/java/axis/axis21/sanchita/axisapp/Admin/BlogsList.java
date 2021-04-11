package axis.axis21.sanchita.axisapp.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import axis.axis21.sanchita.axisapp.Objects.Blog;
import axis.axis21.sanchita.axisapp.R;

public class BlogsList extends AppCompatActivity {

    private Button addButton;
    private ProgressDialog loadingBar;
    private DatabaseReference BlogRef;
    private RecyclerView blogView;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogs_list);

        addButton=(Button)findViewById(R.id.add_blogs_button);

        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setTitle("Loading");
        loadingBar.setCanceledOnTouchOutside(false);

        BlogRef= FirebaseDatabase.getInstance().getReference().child("blogs");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BlogsList.this,AddBlog.class);
                startActivity(intent);
                finish();
            }
        });

        blogView = (RecyclerView)findViewById(R.id.admin_blogs);
        manager = new LinearLayoutManager(BlogsList.this, LinearLayoutManager.VERTICAL, true);
        blogView.setLayoutManager(manager);
        blogView.hasFixedSize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingBar.show();

        FirebaseRecyclerOptions<Blog> options=new FirebaseRecyclerOptions.Builder<Blog>()
                .setQuery(BlogRef,Blog.class)
                .build();

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> adapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull Blog model) {
                holder.setmView(model.getTitle());
                holder.implementListener(BlogsList.this, getRef(position).getKey());
                loadingBar.dismiss();
            }

            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.event_clickable_layout,parent,false);
                return new BlogViewHolder(view);
            }
        };
        blogView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        private TextView EventCatTitle;
        private View view;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            EventCatTitle=itemView.findViewById(R.id.event_cat_title);
        }

        public void setmView(String title){
            EventCatTitle.setText(title);
        }

        public void implementListener(final Context context, final String id){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UpdateBlogs.class);
                    intent.putExtra("id", id);
                    context.startActivity(intent);
                }
            });
        }
    }
}