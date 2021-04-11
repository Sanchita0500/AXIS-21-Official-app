package axis.axis21.sanchita.axisapp.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class AddGalleryImages extends AppCompatActivity {

    private ImageView image;
    private static final int Gallery_pick=1;
    private Button AddImageButton;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private StorageReference ImageRef;
    private DatabaseReference ImageDatabaseRef;
    private String downloadImageUrl,saveCurrentDate,saveCurrentTime,productRandomKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gallery_images);

        image=(ImageView)findViewById(R.id.select_image_for_gallery);
        AddImageButton=(Button)findViewById(R.id.add_galleryImage_button);
        loadingBar=new ProgressDialog(this);

        ImageRef= FirebaseStorage.getInstance().getReference().child("Gallery Images");
        ImageDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Gallery");

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        AddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });
    }

    private void ValidateData() {
        if (ImageUri==null){
            Toast.makeText(this, "Image is mandatory", Toast.LENGTH_SHORT).show();
        }else{
            StoreImageInfo();
        }
    }

    private void StoreImageInfo() {
        loadingBar.setTitle("Adding Image");
        loadingBar.setMessage("Please Wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,YYYY");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        productRandomKey=saveCurrentDate + saveCurrentTime;


        StorageReference filePath=ImageRef.child(ImageUri.getLastPathSegment() + ".jpg");

        final UploadTask uploadTask=filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                //Toast.makeText(AddPromoImage.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AddPromoImage.this, "Promo Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
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
                            //Toast.makeText(AddPromoImage.this, "Got Product Image url successfully", Toast.LENGTH_SHORT).show();
                            SaveInfoToDatabase();
                        }
                    }
                });
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

    private void SaveInfoToDatabase() {
        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("img",downloadImageUrl);

        ImageDatabaseRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(AddGalleryImages.this, "Image added in Gallery", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(AddGalleryImages.this, AddInfo.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AddGalleryImages.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}