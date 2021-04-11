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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import axis.axis21.sanchita.axisapp.R;

public class AddWorkshop extends AppCompatActivity {

    private EditText title,link,description,date,time,venue,fees;
    private ImageView workshopImg;
    private Button addWorkshopButton,addContactsButton;
    private StorageReference WorkshopImagesRef;
    private DatabaseReference WorkshopRef;
    private static final int Gallery_pick=1;
    private String Title,Link,downloadImageUrl,Description,Date,Time,Venue,Fees;
    private String saveCurrentDate,saveCurrentTime,RandomKey;
    private Uri ImageUri;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workshop);

        title=(EditText)findViewById(R.id.title_workshop);
        link=(EditText)findViewById(R.id.link_workshop);
        date=(EditText)findViewById(R.id.date_workshop);
        description=(EditText)findViewById(R.id.description_workshop);
        time=(EditText)findViewById(R.id.time_workshop);
        venue=(EditText)findViewById(R.id.venue_workshop);
        fees=(EditText)findViewById(R.id.fees_workshop);
        workshopImg=(ImageView)findViewById(R.id.select_workshop_image);
        addWorkshopButton=(Button)findViewById(R.id.update_workshop_info_button);
        addContactsButton=(Button)findViewById(R.id.add_contacts_button);

        WorkshopImagesRef= FirebaseStorage.getInstance().getReference().child("Workshop Images");
        WorkshopRef= FirebaseDatabase.getInstance().getReference().child("workshops");
        loadingBar=new ProgressDialog(this);

        workshopImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        addWorkshopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });

        addContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Title=title.getText().toString();
                Intent intent=new Intent(AddWorkshop.this, AddContacts.class);
                intent.putExtra("cat","workshops");
                intent.putExtra("title",Title);
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

        if (ImageUri==null){
            Toast.makeText(this, "Image is mandatory", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(Title)){
            Toast.makeText(this, "Title is mandatory", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreInfo();
        }
    }

    private void StoreInfo() {
        loadingBar.setTitle("Adding Workshop");
        loadingBar.setMessage("Please Wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,YYYY");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        RandomKey=saveCurrentDate + " " + saveCurrentTime;

        StorageReference filePath=WorkshopImagesRef.child(ImageUri.getLastPathSegment() + ".jpg");

        final UploadTask uploadTask=filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AddWorkshop.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AddEvents.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
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
                            //Toast.makeText(AddEvents.this, "Got Image url successfully", Toast.LENGTH_SHORT).show();
                            SaveInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveInfoToDatabase() {
        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("title",Title);
        productMap.put("image",downloadImageUrl);
        productMap.put("link",Link);
        productMap.put("description",Description);
        productMap.put("fees",Fees);
        productMap.put("date",Date);
        productMap.put("time",Time);
        productMap.put("venue",Venue);

        WorkshopRef.child(Title).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(AddWorkshop.this, "Workshop Added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AddWorkshop.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                }
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
            workshopImg.setImageURI(ImageUri);
        }
    }
}