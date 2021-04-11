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

import axis.axis21.sanchita.axisapp.Objects.EventCategory;
import axis.axis21.sanchita.axisapp.R;

public class AddEventCategories extends AppCompatActivity {

    private Button AddEventCategoriesButton;
    private DatabaseReference EventsRef;
    private ProgressDialog loadingBar;
    private RecyclerView eventsCatView;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_categories);

        AddEventCategoriesButton=(Button)findViewById(R.id.add_event_categories_button);
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setTitle("Loading");
        loadingBar.setCanceledOnTouchOutside(false);

        EventsRef= FirebaseDatabase.getInstance().getReference().child("Events").child("EventsList");

        AddEventCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddEventCategories.this,AddEventTile.class);
                startActivity(intent);
                finish();
            }
        });

        eventsCatView = (RecyclerView)findViewById(R.id.admin_events_cat);
        manager = new LinearLayoutManager(AddEventCategories.this, LinearLayoutManager.VERTICAL, true);
        eventsCatView.setLayoutManager(manager);
        eventsCatView.hasFixedSize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingBar.show();

        FirebaseRecyclerOptions<EventCategory> options=new FirebaseRecyclerOptions.Builder<EventCategory>()
                .setQuery(EventsRef,EventCategory.class)
                .build();

        FirebaseRecyclerAdapter<EventCategory,EventCatViewHolder> adapter=new FirebaseRecyclerAdapter<EventCategory, EventCatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EventCatViewHolder holder, int position, @NonNull EventCategory model) {
                holder.setmView(model.getTitle());
                holder.implementListener(AddEventCategories.this, getRef(position).getKey());
                loadingBar.dismiss();
            }

            @NonNull
            @Override
            public EventCatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.event_clickable_layout,parent,false);
                return new EventCatViewHolder(view);
            }
        };
        eventsCatView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class EventCatViewHolder extends RecyclerView.ViewHolder{

        private TextView EventCatTitle;
        private View view;

        public EventCatViewHolder(@NonNull View itemView) {
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
                    Intent intent = new Intent(context, AdminSideEvents.class);
                    intent.putExtra("id", id);
                    context.startActivity(intent);
                }
            });
        }
    }

}