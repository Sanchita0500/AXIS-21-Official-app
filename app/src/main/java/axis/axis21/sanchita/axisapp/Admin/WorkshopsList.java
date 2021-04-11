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

import axis.axis21.sanchita.axisapp.Objects.Workshop;
import axis.axis21.sanchita.axisapp.R;

public class WorkshopsList extends AppCompatActivity {

    private Button addButton;
    private ProgressDialog loadingBar;
    private DatabaseReference WorkshopRef;
    private RecyclerView workshopView;
    private LinearLayoutManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshops_list);

        addButton=(Button)findViewById(R.id.add_workshops_button);
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setTitle("Loading");
        loadingBar.setCanceledOnTouchOutside(false);

        WorkshopRef= FirebaseDatabase.getInstance().getReference().child("workshops");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WorkshopsList.this,AddWorkshop.class);
                startActivity(intent);
                finish();
            }
        });

        workshopView = (RecyclerView)findViewById(R.id.admin_workshops);
        manager = new LinearLayoutManager(WorkshopsList.this, LinearLayoutManager.VERTICAL, true);
        workshopView.setLayoutManager(manager);
        workshopView.hasFixedSize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingBar.show();

        FirebaseRecyclerOptions<Workshop> options=new FirebaseRecyclerOptions.Builder<Workshop>()
                .setQuery(WorkshopRef,Workshop.class)
                .build();

        FirebaseRecyclerAdapter<Workshop, WorkshopViewHolder> adapter=new FirebaseRecyclerAdapter<Workshop, WorkshopViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull WorkshopViewHolder holder, int position, @NonNull Workshop model) {
                holder.setmView(model.getTitle());
                holder.implementListener(WorkshopsList.this, getRef(position).getKey());
                loadingBar.dismiss();
            }

            @NonNull
            @Override
            public WorkshopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.event_clickable_layout,parent,false);
                return new WorkshopViewHolder(view);
            }
        };
        workshopView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class WorkshopViewHolder extends RecyclerView.ViewHolder{

        private TextView EventCatTitle;
        private View view;

        public WorkshopViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            EventCatTitle=itemView.findViewById(R.id.event_cat_title);
        }

        public void setmView(String title){
            EventCatTitle.setText(title);
        }

        public void implementListener(final Context context, final String id){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UpdateWorkshops.class);
                    intent.putExtra("id", id);
                    context.startActivity(intent);
                }
            });
        }
    }


}