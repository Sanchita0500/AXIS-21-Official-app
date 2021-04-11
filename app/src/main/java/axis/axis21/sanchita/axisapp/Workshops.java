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

import axis.axis21.sanchita.axisapp.Objects.Workshop;

public class Workshops extends AppCompatActivity {

    private RecyclerView workshop_view;
    private DatabaseReference mRef;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshops);

        mRef = FirebaseDatabase.getInstance().getReference().child("workshops");
        workshop_view = (RecyclerView)findViewById(R.id.workshops_view);

        LinearLayoutManager manager = new LinearLayoutManager(Workshops.this, LinearLayoutManager.VERTICAL, false);
        workshop_view.setLayoutManager(manager);

        mDialog = new ProgressDialog(Workshops.this);
        mDialog.setMessage("Please wait..");
        mDialog.setTitle("Loading");
        mDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onStart() {
        mDialog.show();
        super.onStart();

        FirebaseRecyclerOptions<Workshop> options=new FirebaseRecyclerOptions.Builder<Workshop>()
                .setQuery(mRef,Workshop.class)
                .build();
        FirebaseRecyclerAdapter<Workshop,WorkshopViewHolder> adapter=new FirebaseRecyclerAdapter<Workshop, WorkshopViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull WorkshopViewHolder holder, int position, @NonNull Workshop model) {
                holder.setmView(Workshops.this,model.getTitle(),model.getDate(),model.getVenue(),
                        model.getImage(),getRef(position).getKey().toString());
                mDialog.dismiss();

            }

            @NonNull
            @Override
            public WorkshopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.workshop_box,parent,false);
                return new WorkshopViewHolder(view);
            }
        };
        workshop_view.setAdapter(adapter);
        adapter.startListening();
    }

    public static class WorkshopViewHolder extends RecyclerView.ViewHolder{

        private ImageView w_image;
        private TextView w_title;
        private TextView w_date;
        private TextView w_venue;
        private View mView;

        public WorkshopViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            w_image = mView.findViewById(R.id.workshop_image);
            w_title = mView.findViewById(R.id.workshop_name);
            w_date = mView.findViewById(R.id.workshop_date);
            w_venue = mView.findViewById(R.id.workshop_venue);
        }

        public void setmView(final Context context, String title, String date, String venue, String image, final String id){
            w_title.setText(title);
            w_date.setText(date);
            w_venue.setText(venue);
            try {
                Picasso.get().load(image).into(w_image);
            }catch (Exception e){
                e.printStackTrace();
            }

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WorkshopDetails.class);
                    intent.putExtra("workshop_id", id);
                    context.startActivity(intent);
                }
            });
        }

    }

    public void goBack(View view){
        finish();
    }
}