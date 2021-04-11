package axis.axis21.sanchita.axisapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import axis.axis21.sanchita.axisapp.Objects.Activity;

public class GalleryActivity extends AppCompatActivity {

    private DatabaseReference mref;
    private RecyclerView grid;
    private ProgressDialog dialog;
    private RecyclerView.LayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        dialog = new ProgressDialog(GalleryActivity.this);
        dialog.setMessage("Fetching data. Please wait ..");
        dialog.setTitle("Loading Images");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                finish();
            }
        });

        mref = FirebaseDatabase.getInstance().getReference().child("Gallery");
        grid = (RecyclerView)findViewById(R.id.activities_grid);
        manager = new GridLayoutManager(GalleryActivity.this, 2);

        grid.setLayoutManager(manager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.show();

        FirebaseRecyclerOptions<Activity> options=new FirebaseRecyclerOptions.Builder<Activity>()
                .setQuery(mref, Activity.class)
                .build();

        FirebaseRecyclerAdapter<Activity,ActivityViewHolder> adapter=new FirebaseRecyclerAdapter<Activity, ActivityViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ActivityViewHolder holder, int position, @NonNull Activity model) {
                holder.setImage(model.getImg());
                holder.implementListener(GalleryActivity.this,model.getImg());
            }

            @NonNull
            @Override
            public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item,parent,false);
                return new ActivityViewHolder(view);
            }
        };
        grid.setAdapter(adapter);
        loadData();
        adapter.startListening();
    }

   private void loadData(){
        if (manager.getChildCount() > 0){
            dialog.dismiss();
        }
        else
        {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            },400);
        }
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        View view;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            image = view.findViewById(R.id.grid_image);
        }

        public void setImage(String url){
            try {
                Picasso.get().load(url).into(image);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        public void implementListener(final Context context, final String image){

            this.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog builder = new Dialog(context);
                    builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    builder.getWindow().setBackgroundDrawable(
                            new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //nothing;
                        }
                    });

                    ImageView imageView = new ImageView(context);
                    imageView.setAdjustViewBounds(true);
                    try {
                        Picasso.get().load(Uri.parse(image)).into(imageView);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                            800,
                            1000));
                    builder.show();
                }
            });
        }
    }

    public void goBack(View view){
        finish();
    }
}