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

public class AddFeed extends AppCompatActivity {

    private EditText title,content,link;
    private ImageView feedImage;
    private Button updateFeedButton;
    private ProgressDialog loadingBar;
    private StorageReference FeedImagesRef;
    private DatabaseReference FeedRef;
    private static final int Gallery_pick=1;
    private String Title,Content,Link,saveCurrentDate,saveCurrentTime,productRandomKey,downloadImageUrl;
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        title=(EditText)findViewById(R.id.title_feed);
        content=(EditText)findViewById(R.id.content_feed);
        link=(EditText)findViewById(R.id.link_feed);
        feedImage=(ImageView)findViewById(R.id.select_feed_image);
        updateFeedButton=(Button)findViewById(R.id.update_feed_button);

        FeedImagesRef= FirebaseStorage.getInstance().getReference().child("Feed Images");
        FeedRef= FirebaseDatabase.getInstance().getReference().child("Timeline");
        loadingBar=new ProgressDialog(this);

        feedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        updateFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });
    }

    private void ValidateData() {
        Title=title.getText().toString();
        Content=content.getText().toString();
        Link=link.getText().toString();

        if (ImageUri==null){
            Toast.makeText(this, "Image is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Title)){
            Toast.makeText(this, "Title is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Content)){
            Toast.makeText(this, "Please write about the event", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreInfo();
        }
    }

    private void StoreInfo() {
        loadingBar.setTitle("Adding Feed");
        loadingBar.setMessage("Please Wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,YYYY");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        productRandomKey=saveCurrentDate + saveCurrentTime;

        StorageReference filePath=FeedImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask=filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AddFeed.this, "Error : "+message, Toast.LENGTH_SHORT).show();
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
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("title",Title);
        productMap.put("image",downloadImageUrl);
        productMap.put("content",Content);
        productMap.put("link",Link);

        FeedRef.child(Title).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(AddFeed.this, "Feed Updated successfully", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(AddFeed.this,AddInfo.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AddFeed.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
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
            feedImage.setImageURI(ImageUri);
        }
    }
}