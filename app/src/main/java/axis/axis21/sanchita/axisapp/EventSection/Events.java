package axis.axis21.sanchita.axisapp.EventSection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import axis.axis21.sanchita.axisapp.Objects.EventCategory;
import axis.axis21.sanchita.axisapp.R;

public class Events extends AppCompatActivity {

    private RecyclerView eventsView;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        eventsView = (RecyclerView)findViewById(R.id.events_cat);
        mRef = FirebaseDatabase.getInstance().getReference().child("Events").child("EventsList");

        RecyclerView.LayoutManager manager = new GridLayoutManager(Events.this, 2);
        eventsView.setLayoutManager(manager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<EventCategory> options=new FirebaseRecyclerOptions.Builder<EventCategory>()
                .setQuery(mRef,EventCategory.class)
                .build();

        FirebaseRecyclerAdapter<EventCategory,EventViewHolder> adapter=new FirebaseRecyclerAdapter<EventCategory, EventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull EventCategory model) {
                holder.setmView(getRef(position).getKey().toString(),model.getImage());
                holder.implementListener(Events.this,getRef(position).getKey().toString());
            }

            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.eventframe,parent,false);
                return new EventViewHolder(view);
            }
        };
        eventsView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private RoundedImageView event_image;
        private TextView event_name;
        private String title;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            event_image = (RoundedImageView)mView.findViewById(R.id.event_box_image);
            event_name = (TextView)mView.findViewById(R.id.event_box_title);
        }

        public void setmView(String id, String image){
            event_name.setText(id);
            try{
                Picasso.get().load(image).into(event_image);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void implementListener(final Context context, final String id){
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventsSection.class);
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