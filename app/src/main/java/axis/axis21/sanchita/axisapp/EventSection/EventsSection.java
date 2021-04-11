package axis.axis21.sanchita.axisapp.EventSection;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import axis.axis21.sanchita.axisapp.Objects.Event;
import axis.axis21.sanchita.axisapp.R;

public class EventsSection extends AppCompatActivity {

    private ImageView headerImage;
    private TextView title;
    private RecyclerView eventsList;
    private DatabaseReference mRef;
    private static String e_id;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_section);

        headerImage = (ImageView)findViewById(R.id.event_sec_image);
        title = (TextView) findViewById(R.id.event_sec_title);
        eventsList = (RecyclerView)findViewById(R.id.event_sec_list);

        Intent intent = getIntent();

        e_id = intent.getStringExtra("id");

        mRef = FirebaseDatabase.getInstance().getReference().child("Events").child(e_id);

        GridLayoutManager manager = new GridLayoutManager(EventsSection.this, 2);
        manager.setSmoothScrollbarEnabled(true);
        eventsList.setLayoutManager(manager);

        mDialog = new ProgressDialog(EventsSection.this);
        mDialog.setMessage("Please wait..");
        mDialog.setTitle("Loading");
        mDialog.setCanceledOnTouchOutside(false);

        setLayout();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDialog.show();

        FirebaseRecyclerOptions<Event> options=new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(mRef,Event.class)
                .build();

        FirebaseRecyclerAdapter<Event,EventViewHolder> adapter=new FirebaseRecyclerAdapter<Event, EventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull Event model) {
                holder.setmView(model.getImage(),model.getTitle());
                holder.implementListener(EventsSection.this,getRef(position).getKey().toString());
                mDialog.dismiss();
            }

            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.eventframe,parent,false);
                return new EventViewHolder(view);
            }
        };
        eventsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private RoundedImageView Image;
        private TextView Title;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            Image = (RoundedImageView)mView.findViewById(R.id.event_box_image);
            Title = (TextView)mView.findViewById(R.id.event_box_title);
        }

        public void setmView(String image, String title){
            Title.setText(title);
            try{
                Picasso.get().load(image).into(Image);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void implementListener(final Context context, final String id){
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventDetails.class);
                    intent.putExtra("id", id);
                    intent.putExtra("cat",e_id);
                    context.startActivity(intent);
                }
            });
        }
    }

    private void setLayout() {

        //set header image
        title.setText(e_id);
    }
}