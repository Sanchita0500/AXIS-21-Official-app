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

public class BlogDetails extends AppCompatActivity {

    private TextView title,date,name,content;
    private DatabaseReference mRef;
    private ProgressDialog dialog;
    private String facebook,instagram,linkedIn,twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        title=(TextView)findViewById(R.id.b_name);
        date=(TextView)findViewById(R.id.date);
        name=(TextView)findViewById(R.id.by);
        content=(TextView)findViewById(R.id.content_blog);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        mRef = FirebaseDatabase.getInstance().getReference().child("blogs").child(id);

        dialog = new ProgressDialog(BlogDetails.this);
        dialog.setTitle("Loading Details");
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        setView();
    }

    private void setView() {

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String retrieveTitle=snapshot.child("title").getValue().toString();
                    String retrieveAuthor=snapshot.child("author").getValue().toString();
                    String retrieveContent=snapshot.child("content").getValue().toString();
                    String retrieveDate=snapshot.child("date").getValue().toString();
                    facebook=snapshot.child("facebook").getValue().toString();
                    instagram=snapshot.child("instagram").getValue().toString();
                    linkedIn=snapshot.child("linkedIn").getValue().toString();
                    twitter=snapshot.child("twitter").getValue().toString();

                    title.setText(retrieveTitle);
                    content.setText(retrieveContent);
                    name.setText(retrieveAuthor);
                    date.setText(retrieveDate);
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
            case R.id.facebook:
                if(facebook==null) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com/axisvnit")));
                }
                else{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebook)));
                }
                break;

            case R.id.instagram:
                Uri uri;
                if(instagram==null) {
                    uri = Uri.parse("http://instagram.com/_u/axis_vnit");
                }
                else{
                    uri = Uri.parse(instagram);
                }
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/axis_vnit")));
                }
                break;

            case R.id.linkedin:
                if(linkedIn==null) {
                    url = "https://www.linkedin.com/company/axis-vnit-nagpur/";
                }
                else{
                    url = linkedIn;
                }
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;

            case R.id.twitter:
                if(twitter==null) {
                    url = "https://twitter.com/axisvnit";
                }
                else{
                    url = twitter;
                }
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