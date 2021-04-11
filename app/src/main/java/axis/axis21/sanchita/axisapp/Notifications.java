package axis.axis21.sanchita.axisapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import axis.axis21.sanchita.axisapp.Objects.Notification;

public class Notifications extends AppCompatActivity {

    private DatabaseReference mRef;
    private RecyclerView nView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        nView = (RecyclerView)findViewById(R.id.notifications_view);
        mRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        LinearLayoutManager manager = new LinearLayoutManager(Notifications.this, LinearLayoutManager.VERTICAL, true);
        manager.setReverseLayout(true);
        nView.setLayoutManager(manager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Notification> options=new FirebaseRecyclerOptions.Builder<Notification>()
                .setQuery(mRef,Notification.class)
                .build();
        FirebaseRecyclerAdapter<Notification,NotificationViewHolder> adapter=new FirebaseRecyclerAdapter<Notification, NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull Notification model) {
                holder.setmView(model.getTitle(),model.getContent());
            }

            @NonNull
            @Override
            public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout,parent,false);
                return new NotificationViewHolder(view);
            }
        };
        nView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView title;
        private TextView content;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            title = mView.findViewById(R.id.notification_title);
            content = mView.findViewById(R.id.notification_content);
        }

        public void setmView(String title, String content){
            this.content.setText(content);
            this.title.setText(title);
        }
    }

    public void goBack(View view){
        finish();
    }

}