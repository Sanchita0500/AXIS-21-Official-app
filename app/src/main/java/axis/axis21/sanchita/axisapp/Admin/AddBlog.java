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

import java.util.HashMap;

import axis.axis21.sanchita.axisapp.R;

public class AddBlog extends AppCompatActivity {

    private EditText title,author,date,facebook,instagram,linkedIn,twitter,content;
    private ImageView image;
    private Button addButton;
    private StorageReference BlogImagesRef;
    private DatabaseReference BlogRef;
    private static final int Gallery_pick=1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private String Title,Author,Date,Facebook,Instagram,LinkedIn,Twitter,Content,downloadImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);

        title=(EditText)findViewById(R.id.title_blog);
        image=(ImageView)findViewById(R.id.select_blog_image);
        author=(EditText)findViewById(R.id.author_blog);
        date=(EditText)findViewById(R.id.date_blog);
        facebook=(EditText)findViewById(R.id.author_facebook);
        instagram=(EditText)findViewById(R.id.author_instagram);
        linkedIn=(EditText)findViewById(R.id.author_linkedIn);
        twitter=(EditText)findViewById(R.id.author_twitter);
        content=(EditText)findViewById(R.id.blog_content);
        addButton=(Button)findViewById(R.id.add_blog_button);

        BlogImagesRef= FirebaseStorage.getInstance().getReference().child("Blog Images");
        BlogRef= FirebaseDatabase.getInstance().getReference().child("blogs");
        loadingBar=new ProgressDialog(this);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });
    }

    private void ValidateData() {
        Title=title.getText().toString();
        Author=author.getText().toString();
        Content=content.getText().toString();
        Date=date.getText().toString();
        Facebook=facebook.getText().toString();
        Instagram=instagram.getText().toString();
        LinkedIn=linkedIn.getText().toString();
        Twitter=twitter.getText().toString();

        if (ImageUri==null){
            Toast.makeText(this, "Image is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Title)){
            Toast.makeText(this, "Title is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Author)){
            Toast.makeText(this, "Please write author's name as anonymous", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreInfo();
        }
    }

    private void StoreInfo() {
        loadingBar.setTitle("Adding Blog");
        loadingBar.setMessage("Please Wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        StorageReference filePath=BlogImagesRef.child(ImageUri.getLastPathSegment() + ".jpg");

        final UploadTask uploadTask=filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AddBlog.this, "Error : "+message, Toast.LENGTH_SHORT).show();
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
        productMap.put("author",Author);
        productMap.put("content", Content);
        productMap.put("date",Date);
        productMap.put("facebook",Facebook);
        productMap.put("instagram",Instagram);
        productMap.put("linkedIn",LinkedIn);
        productMap.put("twitter",Twitter);

        BlogRef.child(Title).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(AddBlog.this, "Blog Added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AddBlog.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
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
            image.setImageURI(ImageUri);
        }
    }
}