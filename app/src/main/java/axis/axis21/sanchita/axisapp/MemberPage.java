package axis.axis21.sanchita.axisapp;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class MemberPage extends AppCompatActivity {

    private TextView name,position;
    private RoundedImageView image;
    private String nameNode,id,retrievePosition,retrieveImage,retrieveName,retrieveInsta,retrieveMail,retrieveLinkedin;
    private ProgressDialog loadingBar;
    private StorageReference TeamImagesRef;
    private DatabaseReference personRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_page);

        Intent intent=getIntent();
        nameNode=intent.getStringExtra("name");
        id=intent.getStringExtra("id");

        image=(RoundedImageView)findViewById(R.id.person_image);
        position=(TextView)findViewById(R.id.person_position);
        name=(TextView)findViewById(R.id.person_name);

        TeamImagesRef= FirebaseStorage.getInstance().getReference().child("Team Images");
        personRef= FirebaseDatabase.getInstance().getReference().child("Team").child(id).child(nameNode);
        loadingBar=new ProgressDialog(this);
        RetrieveInfo();
    }


    private void RetrieveInfo() {

        personRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    retrieveName = snapshot.child("name").getValue().toString();
                    retrievePosition = snapshot.child("position").getValue().toString();
                    retrieveImage = snapshot.child("image").getValue().toString();
                    retrieveMail = snapshot.child("mail").getValue().toString();
                    retrieveInsta = snapshot.child("insta").getValue().toString();
                    retrieveLinkedin = snapshot.child("linkedin").getValue().toString();
                    //String changeName = retrieveName;

                    if(retrieveName.equals("Z_Pratyush Mate")){
                        name.setText("Pratyush Mate");
                    }
                    position.setText(retrievePosition);
                    Picasso.get().load(retrieveImage).into(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void openSocial(View view){
        Intent intent;
        String url;
        Intent i;
        switch (view.getId()){
            case R.id.mail_button:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {retrieveMail});
                // set type of intent
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, null));
                break;

            case R.id.insta_button:
                Uri uri = Uri.parse(retrieveInsta);
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(retrieveInsta)));
                }
                break;

            case R.id.linkedin_button:
                url = retrieveLinkedin;
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;

        }
    }
    public void goBack(View view){
        finish();
    }
}