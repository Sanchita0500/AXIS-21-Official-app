package axis.axis21.sanchita.axisapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import axis.axis21.sanchita.axisapp.Objects.TeamMember;

public class PublicityFragment extends Fragment {

    private DatabaseReference mRef;
    private RecyclerView teamView;

    public PublicityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRef = FirebaseDatabase.getInstance().getReference().child("Team").child("publicity");
            Log.d("mref",mRef.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publicity, container, false);
        teamView = view.findViewById(R.id.publicity_team_view);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        teamView.setLayoutManager(manager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<TeamMember> options=new FirebaseRecyclerOptions.Builder<TeamMember>()
                .setQuery(mRef,TeamMember.class)
                .build();
        FirebaseRecyclerAdapter<TeamMember,TeamViewHolder> adapter=new FirebaseRecyclerAdapter<TeamMember, TeamViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TeamViewHolder holder, int position, @NonNull TeamMember model) {
                holder.setmView(model.getName(),model.getImage(),model.getPosition());
                holder.implementListener(getActivity().getApplicationContext(), getRef(position).getKey());
            }

            @NonNull
            @Override
            public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.team_member_box_layout,parent,false);
                return new TeamViewHolder(view);
            }
        };

        teamView.setAdapter(adapter);
        adapter.startListening();
    }

    public class TeamViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView nameView;
        private ImageView photoView;
        private TextView positionView;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            nameView = mView.findViewById(R.id.member_name);
            photoView = mView.findViewById(R.id.member_image);
            positionView = mView.findViewById(R.id.member_position);
        }

        public void setmView(String name, String image, String position){
            nameView.setText(name);
            positionView.setText(position);
            try {
                Picasso.get().load(image).into(photoView);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void implementListener(final Context context, String name) {
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MemberPage.class);
                    intent.putExtra("name", name);
                    intent.putExtra("id","publicity");
                    startActivity(intent);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            });
        }
    }
}