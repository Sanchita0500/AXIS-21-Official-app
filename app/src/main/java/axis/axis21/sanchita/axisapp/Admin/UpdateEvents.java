package axis.axis21.sanchita.axisapp.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import axis.axis21.sanchita.axisapp.R;

public class UpdateEvents extends AppCompatActivity {

    private String id,Title,Name,downloadImageUrl,Link,About,Play,Rules,retrieveImage;
    private EditText title,link,about,play,rules;
    private ImageView image;
    private Button updateButton,updateContacts,addContacts;
    private DatabaseReference EventsRef;
    private ProgressDialog loadingBar;
    private StorageReference EventImageRef;
    private static final int Gallery_pick=1;
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_events);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        Title=intent.getStringExtra("title");

        title=(EditText)findViewById(R.id.title_event_update);
        link=(EditText)findViewById(R.id.link_event_update);
        about=(EditText)findViewById(R.id.about_event_update);
        play=(EditText)findViewById(R.id.play_event_update);
        rules=(EditText)findViewById(R.id.rules_event_update);
        image=(ImageView)findViewById(R.id.update_event_image);
        updateButton=(Button)findViewById(R.id.event_info_update);
        //updateContacts=(Button)findViewById(R.id.contact_info_update);
        addContacts=(Button)findViewById(R.id.add_contacts_events_update);

        EventImageRef= FirebaseStorage.getInstance().getReference().child("Event Images");
        EventsRef= FirebaseDatabase.getInstance().getReference().child("Events").child(id).child(Title);
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

        /*updateContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateEvents.this, UpdateContacts.class);
                intent.putExtra("id", id);
                intent.putExtra("title",Title);
                intent.putExtra("cat","Events");
                startActivity(intent);
            }
        });*/

        addContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Title=title.getText().toString();
                Intent intent=new Intent(UpdateEvents.this, AddContacts.class);
                intent.putExtra("top",id);
                intent.putExtra("cat","Events");
                intent.putExtra("title",Title);
                startActivity(intent);
                //finish();
            }
        });

    }

    private void ValidateData() {
        Name=title.getText().toString();
        Link=link.getText().toString();
        About=about.getText().toString();
        Rules=rules.getText().toString();
        Play=play.getText().toString();

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


        if(ImageUri!=null) {
            StorageReference filePath = EventImageRef.child(ImageUri.getLastPathSegment() + ".jpg");

            final UploadTask uploadTask = filePath.putFile(ImageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(UpdateEvents.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(AddFeed.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            downloadImageUrl = filePath.getDownloadUrl().toString();
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadImageUrl = task.getResult().toString();
                                //Toast.makeText(AddFeed.this, "Got Product Image url successfully", Toast.LENGTH_SHORT).show();
                                SaveInfoToDatabase();
                            }
                        }
                    });
                }
            });
        }
        else {
            SaveInfoToDatabase();
        }

    }

    private void SaveInfoToDatabase() {
        HashMap<String,Object> productMap=new HashMap<>();
        if(ImageUri==null){
            productMap.put("image",retrieveImage);
        }
        else{
            productMap.put("image",downloadImageUrl);
        }
        productMap.put("title",Name);
        productMap.put("about",About);
        productMap.put("register",Link);
        productMap.put("rules",Rules);
        productMap.put("statement",Play);

        EventsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(UpdateEvents.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(UpdateEvents.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void RetrieveInfo() {
        EventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String retrieveTitle=snapshot.child("title").getValue().toString();
                    retrieveImage=snapshot.child("image").getValue().toString();
                    String retrieveAbout=snapshot.child("about").getValue().toString();
                    String retrieveLink=snapshot.child("register").getValue().toString();
                    String retrieveRules=snapshot.child("rules").getValue().toString();
                    String retrievePlay=snapshot.child("statement").getValue().toString();


                    title.setText(retrieveTitle);
                    about.setText(retrieveAbout);
                    link.setText(retrieveLink);
                    rules.setText(retrieveRules);
                    play.setText(retrievePlay);
                    Picasso.get().load(retrieveImage).into(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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