package axis.axis21.sanchita.axisapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailFeed extends AppCompatActivity {

    private DatabaseReference mRef;
    private TextView postTitle, postDescription;
    private ImageView postImage;
    private TextView Description;
    private Button FeedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_feed);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        mRef = FirebaseDatabase.getInstance().getReference().child("Timeline").child(id);

        postTitle = (TextView)findViewById(R.id.title_detail_feed);
        Description = (TextView)findViewById(R.id.content_detail_feed);
        postImage = (ImageView)findViewById(R.id.image_detail_feed);
        FeedButton = (Button)findViewById(R.id.feed_button);

        updateView();
    }

    private void updateView() {
        mRef.child("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postTitle.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef.child("content").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Description.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef.child("image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    Picasso.get().load(dataSnapshot.getValue().toString()).into(postImage);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef.child("link").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    FeedButton.setVisibility(View.VISIBLE);
                    FeedButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String link = dataSnapshot.getValue().toString();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(link));
                            startActivity(i);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}