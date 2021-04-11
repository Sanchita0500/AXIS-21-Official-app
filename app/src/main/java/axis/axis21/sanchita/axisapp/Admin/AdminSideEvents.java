package axis.axis21.sanchita.axisapp.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import axis.axis21.sanchita.axisapp.Objects.EventCategory;
import axis.axis21.sanchita.axisapp.R;

public class AdminSideEvents extends AppCompatActivity {

    private RecyclerView eventsCatView;
    private LinearLayoutManager manager;
    private EditText name;
    private ImageView image;
    private Button updateButton,addButton;
    private String Name,downloadImageUrl;
    private static String id;
    private static final int Gallery_pick=1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private DatabaseReference EventCatRef,EventsRef;
    private StorageReference EventCatImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_side_events);

        name=(EditText)findViewById(R.id.eventcat_name);
        image=(ImageView)findViewById(R.id.eventcat_image);
        updateButton=(Button)findViewById(R.id.eventcat_button);
        addButton=(Button)findViewById(R.id.event_button);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");

        EventCatImageRef= FirebaseStorage.getInstance().getReference().child("Events Category Images");
        EventCatRef= FirebaseDatabase.getInstance().getReference().child("Events").child("EventsList").child(id);
        EventsRef= FirebaseDatabase.getInstance().getReference().child("Events").child(id);
        loadingBar=new ProgressDialog(this);

        RetrieveInfo();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSideEvents.this, AddEvents.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();;
            }
        });

        eventsCatView = (RecyclerView)findViewById(R.id.eventcat_recycler);
        manager = new LinearLayoutManager(AdminSideEvents.this, LinearLayoutManager.VERTICAL, true);
        eventsCatView.setLayoutManager(manager);
        eventsCatView.hasFixedSize();
    }

    private void ValidateData() {
        Name=name.getText().toString();

        if (ImageUri==null){
            Toast.makeText(this, "Image is mandatory", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(Name)){
            Toast.makeText(this, "Title is mandatory", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreInfo();
        }
    }

    private void StoreInfo() {
        loadingBar.setTitle("Updating");
        loadingBar.setMessage("Please Wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        StorageReference filePath=EventCatImageRef.child(ImageUri.getLastPathSegment() + ".jpg");

        final UploadTask uploadTask=filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AdminSideEvents.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AddFeed.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUrl=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUrl=task.getResult().toString();
                            //Toast.makeText(AddFeed.this, "Got Product Image url successfully", Toast.LENGTH_SHORT).show();
                            SaveInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveInfoToDatabase() {
        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("image",downloadImageUrl);
        productMap.put("title",Name);

        EventCatRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(AdminSideEvents.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AdminSideEvents.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void RetrieveInfo() {
        EventCatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String retrieveTitle=snapshot.child("title").getValue().toString();
                    String retrieveImage=snapshot.child("image").getValue().toString();

                    name.setText(retrieveTitle);
                    Picasso.get().load(retrieveImage).into(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<EventCategory> options=new FirebaseRecyclerOptions.Builder<EventCategory>()
                .setQuery(EventsRef,EventCategory.class)
                .build();

        FirebaseRecyclerAdapter<EventCategory, EventCatViewHolder> adapter=new FirebaseRecyclerAdapter<EventCategory, EventCatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EventCatViewHolder holder, int position, @NonNull EventCategory model) {
                holder.setmView(model.getTitle());
                holder.implementListener(AdminSideEvents.this, getRef(position).getKey());
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

        public void implementListener(final Context context, final String name){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UpdateEvents.class);
                    intent.putExtra("id", id);
                    intent.putExtra("title",name);
                    context.startActivity(intent);
                }
            });
        }
    }

    private void OpenGallery() {
        Intent GalleryIntent=new Intent();
        GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        GalleryIntent.setType("image/*");
        startActivityForResult(GalleryIntent,Gallery_pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_pick && resultCode==RESULT_OK && data!=null){
            ImageUri=data.getData();
            image.setImageURI(ImageUri);
        }
    }

}