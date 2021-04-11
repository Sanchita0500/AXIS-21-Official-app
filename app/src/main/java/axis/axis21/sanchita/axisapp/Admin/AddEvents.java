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

public class AddEvents extends AppCompatActivity {

    private EditText title,link,about,play,rules;
    private ImageView eventImg;
    private Button addEventButton,addContactsButton;
    private StorageReference EventImagesRef;
    private DatabaseReference EventRef;
    private static final int Gallery_pick=1;
    private String Title,Link,downloadImageUrl,About,Play,Rules,event_category;
    private String saveCurrentDate,saveCurrentTime,RandomKey;
    private Uri ImageUri;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_events);

        title=(EditText)findViewById(R.id.title_event);
        link=(EditText)findViewById(R.id.link_event);
        about=(EditText)findViewById(R.id.about_event);
        play=(EditText)findViewById(R.id.play_event);
        rules=(EditText)findViewById(R.id.rules_event);
        eventImg=(ImageView)findViewById(R.id.select_event_image);
        addEventButton=(Button)findViewById(R.id.update_event_info_button);
        addContactsButton=(Button)findViewById(R.id.add_contacts_events_button);

        Intent intent = getIntent();
        event_category = intent.getStringExtra("id");

        EventImagesRef= FirebaseStorage.getInstance().getReference().child("Event Images");
        EventRef= FirebaseDatabase.getInstance().getReference().child("Events").child(event_category);
        loadingBar=new ProgressDialog(this);

        eventImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });

        addContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Title=title.getText().toString();
                Intent intent=new Intent(AddEvents.this, AddContacts.class);
                intent.putExtra("top",event_category);
                intent.putExtra("cat","Events");
                intent.putExtra("title",Title);
                startActivity(intent);
                //finish();
            }
        });

    }

    private void ValidateData() {
        Title=title.getText().toString();
        Link=link.getText().toString();
        About=about.getText().toString();
        Play=play.getText().toString();
        Rules=rules.getText().toString();

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
        loadingBar.setTitle("Adding Event");
        loadingBar.setMessage("Please Wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,YYYY");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        RandomKey=saveCurrentDate + " " + saveCurrentTime;

        StorageReference filePath=EventImagesRef.child(ImageUri.getLastPathSegment() + ".jpg");

        final UploadTask uploadTask=filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AddEvents.this, "Error : "+message, Toast.LENGTH_SHORT).show();
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
        productMap.put("register",Link);
        productMap.put("about",About);
        productMap.put("rules",Rules);
        productMap.put("statement",Play);

        EventRef.child(Title).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(AddEvents.this, "Event Added successfully", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(AddEvents.this,AddEventCategories.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AddEvents.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
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
            eventImg.setImageURI(ImageUri);
        }
    }
}