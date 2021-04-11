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

import axis.axis21.sanchita.axisapp.Objects.TeamMember;
import axis.axis21.sanchita.axisapp.R;

public class AddTeamMember extends AppCompatActivity {

    private Button AddTeamMemberButton;
    private DatabaseReference TeamsRef;
    private ProgressDialog loadingBar;
    private RecyclerView teamView;
    private LinearLayoutManager manager;
    private static String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team_member);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        AddTeamMemberButton=(Button)findViewById(R.id.add_team_member_button);
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setTitle("Loading");
        loadingBar.setCanceledOnTouchOutside(false);

        TeamsRef= FirebaseDatabase.getInstance().getReference().child("Team").child(id);

        AddTeamMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddTeamMember.this,AddMember.class);
                intent.putExtra("id",id);
                startActivity(intent);
                finish();
            }
        });

        teamView = (RecyclerView)findViewById(R.id.admin_team_member);
        manager = new LinearLayoutManager(AddTeamMember.this, LinearLayoutManager.VERTICAL, true);
        teamView.setLayoutManager(manager);
        teamView.hasFixedSize();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<TeamMember> options=new FirebaseRecyclerOptions.Builder<TeamMember>()
                .setQuery(TeamsRef,TeamMember.class)
                .build();

        FirebaseRecyclerAdapter<TeamMember,TeamViewHolder> adapter=new FirebaseRecyclerAdapter<TeamMember, TeamViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TeamViewHolder holder, int position, @NonNull TeamMember model) {
                holder.setmView(model.getName());
                holder.implementListener(AddTeamMember.this, getRef(position).getKey());
            }

            @NonNull
            @Override
            public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.event_clickable_layout,parent,false);
                return new TeamViewHolder(view);
            }
        };

        teamView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder{

        private TextView EventCatTitle;
        private View view;

        public TeamViewHolder(@NonNull View itemView){
            super(itemView);
            view=itemView;
            EventCatTitle=itemView.findViewById(R.id.event_cat_title);
        }

        public void setmView(String title){
            EventCatTitle.setText(title);
        }

        public void implementListener(final Context context, final String name){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UpdateMember.class);
                    intent.putExtra("name", name);
                    intent.putExtra("id",id);
                    context.startActivity(intent);
                }
            });
        }
    }
}