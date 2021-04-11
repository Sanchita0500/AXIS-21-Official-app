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

public class AddMember extends AppCompatActivity {

    private EditText name,position,insta,mail,linkedin;
    private ImageView memberImage;
    private Button addButton;
    private StorageReference TeamImagesRef;
    private DatabaseReference TeamRef;
    private static final int Gallery_pick=1;
    private ProgressDialog loadingBar;
    private String id,Name,Position,downloadImageUrl,Insta,Mail,Linkedin;
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        name=(EditText)findViewById(R.id.name_member);
        position=(EditText)findViewById(R.id.position_member);
        insta=(EditText)findViewById(R.id.insta_member);
        mail=(EditText)findViewById(R.id.mail_member);
        linkedin=(EditText)findViewById(R.id.linkedin_member);
        memberImage=(ImageView)findViewById(R.id.select_member_image);
        addButton=(Button)findViewById(R.id.add_member_button);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        TeamImagesRef= FirebaseStorage.getInstance().getReference().child("Team Images");
        TeamRef= FirebaseDatabase.getInstance().getReference().child("Team").child(id);
        loadingBar=new ProgressDialog(this);

        memberImage.setOnClickListener(new View.OnClickListener() {
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
        Name=name.getText().toString();
        Position=position.getText().toString();
        Insta=insta.getText().toString();
        Mail=mail.getText().toString();
        Linkedin=linkedin.getText().toString();

        if (ImageUri==null){
            Toast.makeText(this, "Image is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Name)){
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Position)){
            Toast.makeText(this, "Please mention your position", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreInfo();
        }
    }

    private void StoreInfo() {
        loadingBar.setTitle("Adding Member");
        loadingBar.setMessage("Please Wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        StorageReference filePath=TeamImagesRef.child(ImageUri.getLastPathSegment() + ".jpg");

        final UploadTask uploadTask=filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AddMember.this, "Error : "+message, Toast.LENGTH_SHORT).show();
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

//        if(Position.equals("Treasure")){
//            Name = "Z_"+Name;
//        }
        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("name",Name);
        productMap.put("image",downloadImageUrl);
        productMap.put("position",Position);
        productMap.put("insta",Insta);
        productMap.put("mail",Mail);
        productMap.put("linkedin",Linkedin);

        TeamRef.child(Name).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(AddMember.this, "Added successfully", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(AddMember.this,AddTeams.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AddMember.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
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
            memberImage.setImageURI(ImageUri);
        }
    }
}