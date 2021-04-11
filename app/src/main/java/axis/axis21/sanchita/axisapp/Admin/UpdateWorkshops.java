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

public class UpdateWorkshops extends AppCompatActivity {

    private String id,Title,Link,Description,Date,Time,Venue,Fees,retrieveImage,downloadImageUrl;
    private EditText title,link,description,date,time,venue,fees;
    private ImageView image;
    private Button updateWorkshop,updateContacts;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private StorageReference WorkshopImagesRef;
    private DatabaseReference WorkshopRef;
    private static final int Gallery_pick=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_workshops);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");

        title=(EditText)findViewById(R.id.title_workshop_update);
        image=(ImageView)findViewById(R.id.select_workshop_image_update);
        link=(EditText)findViewById(R.id.link_workshop_update);
        description=(EditText)findViewById(R.id.description_workshop_update);
        date=(EditText)findViewById(R.id.date_workshop_update);
        time=(EditText)findViewById(R.id.time_workshop_update);
        venue=(EditText)findViewById(R.id.venue_workshop_update);
        fees=(EditText)findViewById(R.id.fees_workshop_update);
        updateWorkshop=(Button)findViewById(R.id.update_workshop);
        updateContacts=(Button)findViewById(R.id.update_contacts_button_workshop);

        WorkshopImagesRef= FirebaseStorage.getInstance().getReference().child("Workshop Images");
        WorkshopRef= FirebaseDatabase.getInstance().getReference().child("workshops").child(id);
        loadingBar=new ProgressDialog(this);

        RetrieveInfo();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        updateWorkshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });

        updateContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateWorkshops.this, AddContacts.class);
                intent.putExtra("title", id);
                intent.putExtra("cat", "workshops");
                startActivity(intent);
            }
        });

    }

    private void ValidateData() {
        Title=title.getText().toString();
        Link=link.getText().toString();
        Description=description.getText().toString();
        Date=date.getText().toString();
        Time=time.getText().toString();
        Venue=venue.getText().toString();
        Fees=fees.getText().toString();

        if(TextUtils.isEmpty(Title)){
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
            StorageReference filePath = WorkshopImagesRef.child(ImageUri.getLastPathSegment() + ".jpg");

            final UploadTask uploadTask = filePath.putFile(ImageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(UpdateWorkshops.this, "Error : " + message, Toast.LENGTH_SHORT).show();
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
        else{
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
        productMap.put("title",Title);
        productMap.put("date",Date);
        productMap.put("link",Link);
        productMap.put("description",Description);
        productMap.put("fees",Fees);
        productMap.put("time",Time);
        productMap.put("venue",Venue);

        WorkshopRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(UpdateWorkshops.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(UpdateWorkshops.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void RetrieveInfo() {
        WorkshopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String retrieveTitle=snapshot.child("title").getValue().toString();
                    retrieveImage=snapshot.child("image").getValue().toString();
                    String retrieveDate=snapshot.child("date").getValue().toString();
                    String retrieveDescription=snapshot.child("description").getValue().toString();
                    String retrieveFees=snapshot.child("fees").getValue().toString();
                    String retrieveLink=snapshot.child("link").getValue().toString();
                    String retrieveTime=snapshot.child("time").getValue().toString();
                    String retrieveVenue=snapshot.child("venue").getValue().toString();

                    title.setText(retrieveTitle);
                    link.setText(retrieveLink);
                    date.setText(retrieveDate);
                    description.setText(retrieveDescription);
                    fees.setText(retrieveFees);
                    time.setText(retrieveTime);
                    venue.setText(retrieveVenue);
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