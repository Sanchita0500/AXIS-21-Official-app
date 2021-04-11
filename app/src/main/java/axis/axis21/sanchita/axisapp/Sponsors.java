package axis.axis21.sanchita.axisapp;

import android.app.ProgressDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import axis.axis21.sanchita.axisapp.Objects.Sponsor;

public class Sponsors extends AppCompatActivity {

    private DatabaseReference mRef;
    private RecyclerView sponsors_view;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsors);

        mRef = FirebaseDatabase.getInstance().getReference().child("sponsors");
        sponsors_view = (RecyclerView)findViewById(R.id.sponsors_view);

        LinearLayoutManager manager = new GridLayoutManager(Sponsors.this, 2);
        manager.setReverseLayout(true);
        sponsors_view.setLayoutManager(manager);

        mDialog = new ProgressDialog(Sponsors.this);
        mDialog.setMessage("Please wait..");
        mDialog.setTitle("Loading");
        mDialog.setCanceledOnTouchOutside(false);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        sponsors_view.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0 || parent.getChildLayoutPosition(view) == 1 ) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDialog.show();

        FirebaseRecyclerOptions<Sponsor> options=new FirebaseRecyclerOptions.Builder<Sponsor>()
                .setQuery(mRef,Sponsor.class)
                .build();

        FirebaseRecyclerAdapter<Sponsor,SponsorViewHolder> adapter=new FirebaseRecyclerAdapter<Sponsor, SponsorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SponsorViewHolder holder, int position, @NonNull Sponsor model) {
                holder.setmView(model.getLogo(),model.getTitle(),model.getDescription());
                mDialog.dismiss();
            }

            @NonNull
            @Override
            public SponsorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sponsor_layout,parent,false);
                return new SponsorViewHolder(view);
            }
        };
        sponsors_view.setAdapter(adapter);
        adapter.startListening();
    }

    public static class SponsorViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private ImageView logo;
        private TextView desc;
        private TextView name;

        public SponsorViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            logo = mView.findViewById(R.id.sponsor_logo);
            desc = mView.findViewById(R.id.sponsor_desc);
            name = mView.findViewById(R.id.sponsor_name);
        }

        public void setmView(String logo, String name, String desc){
            this.name.setText(name);
            this.desc.setText(desc);
            try{
                Picasso.get().load(logo).into(this.logo);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void goBack(View view){
        finish();
    }
}