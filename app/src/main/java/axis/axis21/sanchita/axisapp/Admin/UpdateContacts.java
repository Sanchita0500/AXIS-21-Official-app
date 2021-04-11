package axis.axis21.sanchita.axisapp.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import axis.axis21.sanchita.axisapp.Objects.Coordinator;
import axis.axis21.sanchita.axisapp.R;

public class UpdateContacts extends AppCompatActivity {

    private DatabaseReference ContactsRef;
    private String id,Title,category;
    private Button updateButton;
    private ProgressDialog loadingBar;
    private RecyclerView contactView;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contacts);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        Title=intent.getStringExtra("title");
        category=intent.getStringExtra("cat");

        updateButton=(Button)findViewById(R.id.update_contacts_button);

        if(category.equals("Events")) {
            ContactsRef = FirebaseDatabase.getInstance().getReference().child(category).child(id).child(Title).child("contact");
        }
        else if(category.equals("workshops")){
            ContactsRef = FirebaseDatabase.getInstance().getReference().child(category).child(id).child("contact");
        }
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setTitle("Loading");
        loadingBar.setCanceledOnTouchOutside(false);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Updating");
                loadingBar.setMessage("Please Wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                FirebaseRecyclerOptions<Coordinator> options=new FirebaseRecyclerOptions.Builder<Coordinator>()
                        .setQuery(ContactsRef,Coordinator.class)
                        .build();

                FirebaseRecyclerAdapter<Coordinator, UpdateContactViewHolder> adapter=new FirebaseRecyclerAdapter<Coordinator, UpdateContactViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UpdateContactViewHolder holder, int position, @NonNull Coordinator model) {
                        holder.setmView();
                    }

                    @NonNull
                    @Override
                    public UpdateContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout,parent,false);
                        return new UpdateContactViewHolder(view);
                    }
                };
                contactView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                loadingBar.dismiss();
                adapter.startListening();
            }
        });

        /*updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });*/

        contactView = (RecyclerView)findViewById(R.id.admin_contacts);
        manager = new LinearLayoutManager(UpdateContacts.this, LinearLayoutManager.VERTICAL, true);
        contactView.setLayoutManager(manager);
        contactView.hasFixedSize();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Coordinator> options=new FirebaseRecyclerOptions.Builder<Coordinator>()
                .setQuery(ContactsRef,Coordinator.class)
                .build();

        FirebaseRecyclerAdapter<Coordinator, ContactViewHolder> adapter=new FirebaseRecyclerAdapter<Coordinator, ContactViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactViewHolder holder, int position, @NonNull Coordinator model) {
                holder.setmView(model.getName(),model.getNumber(),model.getEmail());
            }

            @NonNull
            @Override
            public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout,parent,false);
                return new ContactViewHolder(view);
            }
        };
        contactView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private EditText name,number,email;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            name=itemView.findViewById(R.id.name_update);
            number=itemView.findViewById(R.id.phone_update);
            email=itemView.findViewById(R.id.mail_update);

            name.setText(name.getText().toString());
        }

        public void setmView(String name_string,String number_string,String email_string){
            name.setText(name_string);
            number.setText(number_string);
            email.setText(email_string);
        }
    }

    public static class UpdateContactViewHolder extends RecyclerView.ViewHolder{

        private EditText name,number,email;
        private View view;

        public UpdateContactViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            name=itemView.findViewById(R.id.name_update);
            number=itemView.findViewById(R.id.phone_update);
            email=itemView.findViewById(R.id.mail_update);
        }

        public void setmView(){
            name.setText(name.getText().toString());
            number.setText(number.getText().toString());
            email.setText(email.getText().toString());
        }
    }
}