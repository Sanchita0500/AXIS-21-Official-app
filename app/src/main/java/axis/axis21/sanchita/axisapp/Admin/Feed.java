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

import axis.axis21.sanchita.axisapp.R;

public class Feed extends AppCompatActivity {

    private Button addFeedButton;
    private ProgressDialog loadingBar;
    private DatabaseReference FeedRef;
    private RecyclerView feedView;
    private LinearLayoutManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        addFeedButton=(Button)findViewById(R.id.add_feed_button);
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setTitle("Loading");
        loadingBar.setCanceledOnTouchOutside(false);

        FeedRef= FirebaseDatabase.getInstance().getReference().child("Timeline");

        addFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Feed.this,AddFeed.class);
                startActivity(intent);
                finish();
            }
        });

        feedView = (RecyclerView)findViewById(R.id.admin_feed);
        manager = new LinearLayoutManager(Feed.this, LinearLayoutManager.VERTICAL, true);
        feedView.setLayoutManager(manager);
        feedView.hasFixedSize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingBar.show();

        FirebaseRecyclerOptions<axis.axis21.sanchita.axisapp.Objects.Feed> options=new FirebaseRecyclerOptions.Builder<axis.axis21.sanchita.axisapp.Objects.Feed>()
                .setQuery(FeedRef, axis.axis21.sanchita.axisapp.Objects.Feed.class)
                .build();

        FirebaseRecyclerAdapter<axis.axis21.sanchita.axisapp.Objects.Feed,FeedViewHolder> adapter=new FirebaseRecyclerAdapter<axis.axis21.sanchita.axisapp.Objects.Feed, FeedViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FeedViewHolder holder, int position, @NonNull axis.axis21.sanchita.axisapp.Objects.Feed model) {
                holder.setmView(model.getTitle());
                holder.implementListener(Feed.this, getRef(position).getKey());
                loadingBar.dismiss();
            }

            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.event_clickable_layout,parent,false);
                return new FeedViewHolder(view);
            }
        };
        feedView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder{

        private TextView FeedTitle;
        private View view;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            FeedTitle=itemView.findViewById(R.id.event_cat_title);
        }

        public void setmView(String title){
            FeedTitle.setText(title);
        }

        public void implementListener(final Context context, final String id){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UpdateFeed.class);
                    intent.putExtra("id", id);
                    context.startActivity(intent);
                }
            });
        }
    }
}