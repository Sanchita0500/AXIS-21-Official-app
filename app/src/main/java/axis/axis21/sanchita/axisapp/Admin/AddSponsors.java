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

public class AddSponsors extends AppCompatActivity {

    private EditText name,description;
    private ImageView sponsorImage;
    private Button addSponsorButton;
    private ProgressDialog loadingBar;
    private StorageReference SponsorImagesRef;
    private DatabaseReference SponsorRef;
    private static final int Gallery_pick=1;
    private String Name,Description,downloadImageUrl,saveCurrentDate,saveCurrentTime,RandomKey;
    private Uri ImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sponsors);

        name=(EditText)findViewById(R.id.title_sponsor);
        description=(EditText)findViewById(R.id.description_sponsor);
        sponsorImage=(ImageView)findViewById(R.id.select_sponsor_image);
        addSponsorButton=(Button)findViewById(R.id.update_sponsor_button);

        SponsorImagesRef= FirebaseStorage.getInstance().getReference().child("Sponsor Images");
        SponsorRef= FirebaseDatabase.getInstance().getReference().child("sponsors");
        loadingBar=new ProgressDialog(this);

        sponsorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        addSponsorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });
    }

    private void ValidateData() {
        Name=name.getText().toString();
        Description=description.getText().toString();

        if (ImageUri==null){
            Toast.makeText(this, "Image is mandatory", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(Name)){
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreInfo();
        }
    }

    private void StoreInfo() {
        loadingBar.setTitle("Adding Sponsor");
        loadingBar.setMessage("Please Wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,YYYY");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        RandomKey=saveCurrentDate + saveCurrentTime;

        StorageReference filePath=SponsorImagesRef.child(ImageUri.getLastPathSegment() + ".jpg");

        final UploadTask uploadTask=filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AddSponsors.this, "Error : "+message, Toast.LENGTH_SHORT).show();
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
        productMap.put("title",Name);
        productMap.put("logo",downloadImageUrl);
        productMap.put("description",Description);

        SponsorRef.child(RandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(AddSponsors.this, "Sponsor Added successfully", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(AddSponsors.this,AddInfo.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AddSponsors.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
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
            sponsorImage.setImageURI(ImageUri);
        }
    }
}