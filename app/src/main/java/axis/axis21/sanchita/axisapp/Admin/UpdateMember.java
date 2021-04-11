package axis.axis21.sanchita.axisapp.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import axis.axis21.sanchita.axisapp.R;

public class UpdateMember extends AppCompatActivity {

    private RoundedImageView image;
    private EditText position,insta,mail,linkedin;
    private Button updateButton;
    private String name,id,Position,downloadImageUrl,Insta,Mail,Linkedin,retrieveImage;
    private DatabaseReference personRef;
    private static final int Gallery_pick=1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private StorageReference TeamImagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_member);

        Intent intent=getIntent();
        name=intent.getStringExtra("name");
        id=intent.getStringExtra("id");

        image=(RoundedImageView)findViewById(R.id.image_member);
        position=(EditText)findViewById(R.id.update_position);
        insta=(EditText)findViewById(R.id.update_insta);
        mail=(EditText)findViewById(R.id.update_mail);
        linkedin=(EditText)findViewById(R.id.update_linkedin);
        updateButton=(Button)findViewById(R.id.update_member_button);

        TeamImagesRef= FirebaseStorage.getInstance().getReference().child("Team Images");
        personRef= FirebaseDatabase.getInstance().getReference().child("Team").child(id).child(name);
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
    }

    private void ValidateData() {
        Position=position.getText().toString();
        Insta=insta.getText().toString();
        Mail=mail.getText().toString();
        Linkedin=linkedin.getText().toString();

        if(TextUtils.isEmpty(Position)){
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
            StorageReference filePath = TeamImagesRef.child(ImageUri.getLastPathSegment() + ".jpg");

            final UploadTask uploadTask = filePath.putFile(ImageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(UpdateMember.this, "Error : " + message, Toast.LENGTH_SHORT).show();
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
        productMap.put("position",Position);
        productMap.put("name",name);
        productMap.put("insta",Insta);
        productMap.put("mail",Mail);
        productMap.put("linkedin",Linkedin);

        personRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(UpdateMember.this, "Updated successfully", Toast.LENGTH_SHORT).show();

                    finish();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(UpdateMember.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
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

    private void RetrieveInfo() {
        personRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String retrievePosition=snapshot.child("position").getValue().toString();
                    retrieveImage=snapshot.child("image").getValue().toString();
                    String retrieveMail=snapshot.child("mail").getValue().toString();
                    String retrieveInsta=snapshot.child("insta").getValue().toString();
                    String retrieveLinkedin=snapshot.child("linkedin").getValue().toString();

                    position.setText(retrievePosition);
                    mail.setText(retrieveMail);
                    insta.setText(retrieveInsta);
                    linkedin.setText(retrieveLinkedin);
                    Picasso.get().load(retrieveImage).into(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}