package axis.axis21.sanchita.axisapp.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import axis.axis21.sanchita.axisapp.Home;
import axis.axis21.sanchita.axisapp.R;

public class AddInfo extends AppCompatActivity {

    private Button logoutButton;
    private TextView promo_image,feed,galleryImages,notifications,eventCategories,sponsors,teams,workshops,blog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

        //Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
        logoutButton=(Button)findViewById(R.id.logout_button);
        promo_image=(TextView)findViewById(R.id.promo_image);
        galleryImages=(TextView)findViewById(R.id.gallery_images);
        notifications=(TextView)findViewById(R.id.display_notifications);
        feed=(TextView)findViewById(R.id.feed);
        eventCategories=(TextView)findViewById(R.id.event_categories);
        sponsors=(TextView)findViewById(R.id.sponsors_tile);
        teams=(TextView)findViewById(R.id.teams);
        workshops=(TextView)findViewById(R.id.workshop);
        blog=(TextView)findViewById(R.id.blog);

        promo_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddInfo.this, AddPromoImage.class);
                startActivity(intent);
            }
        });

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddInfo.this, AddNotifications.class);
                startActivity(intent);
            }
        });

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddInfo.this, Feed.class);
                startActivity(intent);
            }
        });

        galleryImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddInfo.this, AddGalleryImages.class);
                startActivity(intent);
            }
        });

        eventCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddInfo.this, AddEventCategories.class);
                startActivity(intent);
            }
        });

        sponsors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddInfo.this,AddSponsors.class);
                startActivity(intent);
            }
        });

        teams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddInfo.this,AddTeams.class);
                startActivity(intent);
            }
        });

        workshops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddInfo.this,WorkshopsList.class);
                startActivity(intent);
            }
        });

        blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddInfo.this,BlogsList.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddInfo.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
    }
}