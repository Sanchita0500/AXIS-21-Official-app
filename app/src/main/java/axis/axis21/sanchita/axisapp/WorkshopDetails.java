package axis.axis21.sanchita.axisapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import axis.axis21.sanchita.axisapp.Objects.Coordinator;

public class WorkshopDetails extends AppCompatActivity {

    private TextView workshop_fees;
    private TextView workshop_time;
    private TextView workshop_date;
    private TextView workshop_venue;
    private TextView workshop_title;
    private TextView workshop_details;
    private DatabaseReference mRef,contactRef;
    private RecyclerView workshop_contacts;
    private ProgressDialog dialog;
    private String registration_link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshop_details);

        workshop_fees = (TextView)findViewById(R.id.w_fees);
        workshop_contacts = (RecyclerView) findViewById(R.id.w_contacts);
        workshop_date= (TextView)findViewById(R.id.w_date);
        workshop_details= (TextView) findViewById(R.id.w_details);
        workshop_time= (TextView)findViewById(R.id.w_time);
        workshop_venue= (TextView)findViewById(R.id.w_venue);
        workshop_title = (TextView)findViewById(R.id.w_name);

        Intent intent = getIntent();
        String id = intent.getStringExtra("workshop_id");

        mRef = FirebaseDatabase.getInstance().getReference().child("workshops").child(id);
        contactRef=FirebaseDatabase.getInstance().getReference().child("workshops").child(id).child("contact");

        dialog = new ProgressDialog(WorkshopDetails.this);
        dialog.setTitle("Loading Details");
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);


        LinearLayoutManager manager = new LinearLayoutManager(WorkshopDetails.this, LinearLayoutManager.VERTICAL, false);
        workshop_contacts.setLayoutManager(manager);

        setView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.show();

        FirebaseRecyclerOptions<Coordinator> options=new FirebaseRecyclerOptions.Builder<Coordinator>()
                .setQuery(contactRef,Coordinator.class)
                .build();
        FirebaseRecyclerAdapter<Coordinator,ContactViewHolder> adapter=new FirebaseRecyclerAdapter<Coordinator, ContactViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactViewHolder holder, int position, @NonNull Coordinator model) {
                holder.setmView(WorkshopDetails.this,model.getName(),model.getNumber(),model.getEmail());
                dialog.dismiss();
            }

            @NonNull
            @Override
            public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.coordinators_box,parent,false);
                return new ContactViewHolder(view);
            }
        };
        workshop_contacts.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView name;
        private ImageButton call, mail;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            name = mView.findViewById(R.id.c_name);
            call = mView.findViewById(R.id.c_call);
            mail = mView.findViewById(R.id.c_mail);
        }

        public void setmView(final Context context, String name, final String mobile, final String mail) {
            this.name.setText(name);
//
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ mobile));
                    context.startActivity(intent);
                }
            });

            this.mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", mail, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "AXIS 21 Workshop Details Enquiry");
                    context.startActivity(Intent.createChooser(emailIntent, null));
                }
            });

        }

    }

    private void setView() {

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String retrieveTitle=snapshot.child("title").getValue().toString();
                    String retrieveTime=snapshot.child("time").getValue().toString();
                    String retrieveVenue=snapshot.child("venue").getValue().toString();
                    String retrieveLink=snapshot.child("link").getValue().toString();
                    String retrieveFees=snapshot.child("fees").getValue().toString();
                    String retrieveDetails=snapshot.child("description").getValue().toString();
                    String retrieveDate=snapshot.child("date").getValue().toString();

                    workshop_title.setText(retrieveTitle);
                    workshop_venue.setText(retrieveVenue);
                    registration_link=retrieveLink;
                    workshop_fees.setText(retrieveFees);
                    workshop_details.setText(retrieveDetails);
                    workshop_time.setText(retrieveTime);
                    workshop_date.setText(retrieveDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void goBack(View view){
        finish();
    }

    public void Register(View view){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(registration_link));
        startActivity(i);
    }
}