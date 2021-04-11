package axis.axis21.sanchita.axisapp.EventSection;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import axis.axis21.sanchita.axisapp.Objects.Coordinator;
import axis.axis21.sanchita.axisapp.R;

public class ContactFragment extends Fragment {

    public ContactFragment() {
        // Required empty public constructor
    }

    private String category;
    private String id;
    private DatabaseReference mRef;
    private RecyclerView coordinators_view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString("event_category");
            id = getArguments().getString("event_id");
            mRef = FirebaseDatabase.getInstance().getReference().child("Events").child(category).child(id).child("contact");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        coordinators_view = view.findViewById(R.id.coordinators);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        coordinators_view.setLayoutManager(manager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Coordinator> options=new FirebaseRecyclerOptions.Builder<Coordinator>()
                .setQuery(mRef,Coordinator.class)
                .build();

        FirebaseRecyclerAdapter<Coordinator,CoordinatorViewHolder> adapter=new FirebaseRecyclerAdapter<Coordinator, CoordinatorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CoordinatorViewHolder holder, int position, @NonNull Coordinator model) {
                holder.setmView(getActivity(), model.getName(), model.getNumber(), model.getEmail());
            }

            @NonNull
            @Override
            public CoordinatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.coordinators_box,parent,false);
                return new CoordinatorViewHolder(view);
            }
        };

        coordinators_view.setAdapter(adapter);
        adapter.startListening();
    }

    public static class CoordinatorViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView name;
        private ImageButton call, mail;

        public CoordinatorViewHolder(@NonNull View itemView) {
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
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "AXIS 21 Event Details Enquiry");
                    context.startActivity(Intent.createChooser(emailIntent, null));
                }
            });

        }

    }
}