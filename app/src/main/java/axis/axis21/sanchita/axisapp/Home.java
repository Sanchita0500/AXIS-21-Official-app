
package axis.axis21.sanchita.axisapp;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import axis.axis21.sanchita.axisapp.EventSection.Events;
import axis.axis21.sanchita.axisapp.Objects.Feed;

public class Home extends AppCompatActivity {

    private TextView admin;
    private RecyclerView feedView;
    private LinearLayoutManager manager;
    private DatabaseReference ImageDatabaseRef,mRef;
    private ActionBarDrawerToggle toggle;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        admin=(TextView)findViewById(R.id.admin);
        ImageDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Promo");
        mRef = FirebaseDatabase.getInstance().getReference().child("Timeline");

        mDialog = new ProgressDialog(Home.this);
        mDialog.setMessage("Please wait..");
        mDialog.setTitle("Loading");
        mDialog.setCanceledOnTouchOutside(false);

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home.this, axis.axis21.sanchita.axisapp.LoginActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Home.this, axis.axis21.sanchita.axisapp.Contact.class);
                startActivity(intent);
                //finish();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;

                switch (menuItem.getItemId()) {
                    case R.id.gallery:
                        intent=new Intent(Home.this, axis.axis21.sanchita.axisapp.GalleryActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.events:
                        intent=new Intent(Home.this, Events.class);
                        startActivity(intent);
                        break;

                    case R.id.sponsors:
                        intent=new Intent(Home.this, axis.axis21.sanchita.axisapp.Sponsors.class);
                        startActivity(intent);
                        break;

                    case R.id.team:
                        intent=new Intent(Home.this, axis.axis21.sanchita.axisapp.TeamDetails.class);
                        startActivity(intent);
                        break;

                    case R.id.workshops:
                        intent=new Intent(Home.this, axis.axis21.sanchita.axisapp.Workshops.class);
                        startActivity(intent);
                        break;

                    case R.id.blogs:
                        intent=new Intent(Home.this, axis.axis21.sanchita.axisapp.Blogs.class);
                        startActivity(intent);
                        break;
                    case R.id.share:
                        final String appPackageName = getPackageName();
                        String url = "https://play.google.com/store/apps/details?id=" + appPackageName;
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_SEND);
                        i.putExtra(Intent.EXTRA_TEXT,"Hey, Download the AXIS app to stay updated about all events of AXIS'21.Click the link below to download \n "+url);
                        i.setType("text/plain");
                        startActivity(i);
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        feedView = (RecyclerView)findViewById(R.id.main_feed);
        manager = new LinearLayoutManager(Home.this, LinearLayoutManager.VERTICAL, true);
        //manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        feedView.setLayoutManager(manager);
        feedView.hasFixedSize();

        final ImageView promo_image = (RoundedImageView)findViewById(R.id.promo_image);

        ImageDatabaseRef.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    String url=snapshot.getValue().toString();
                    try {
                        Picasso.get().load(url).into(promo_image);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Home.this);
            LayoutInflater inflater = getLayoutInflater();
            View DialogLayout = inflater.inflate(R.layout.exit_dialog, null);
            builder.setView(DialogLayout);

            Button ok = (Button) DialogLayout.findViewById(R.id.btn_ok);
            Button cancel = (Button) DialogLayout.findViewById(R.id.btn_cancel);

            final android.app.AlertDialog exit_dialog = builder.create();

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAffinity();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exit_dialog.dismiss();
                }
            });

            exit_dialog.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDialog.show();

        FirebaseRecyclerOptions<Feed> options=new FirebaseRecyclerOptions.Builder<Feed>()
                .setQuery(mRef,Feed.class)
                .build();
        FirebaseRecyclerAdapter<Feed,FeedViewHolder> adapter=new FirebaseRecyclerAdapter<Feed, FeedViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FeedViewHolder holder, int position, @NonNull Feed model) {
                holder.setmView(model.getTitle(), model.getImage(), model.getContent());
                holder.implementListener(Home.this, getRef(position).getKey());
                mDialog.dismiss();
            }

            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_layout,parent,false);
                return new FeedViewHolder(view);
            }
        };
        feedView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView titleView;
        private ImageView imageView;
        private TextView contentView;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            titleView = mView.findViewById(R.id.feed_title);
            imageView = mView.findViewById(R.id.feed_image);
            contentView = mView.findViewById(R.id.feed_content);
        }

        public void setmView(String title, String image_url, String content){
            titleView.setText(title);
            contentView.setText(content);
            try{
                Picasso.get().load(image_url).into(imageView);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void implementListener(final Context context, final String id){
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, axis.axis21.sanchita.axisapp.DetailFeed.class);
                    intent.putExtra("id", id);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (toggle.onOptionsItemSelected(item)){
            return true;
        }

        if (id == R.id.notifications){
            startActivity(new Intent(Home.this, axis.axis21.sanchita.axisapp.Notifications.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void openSocial(View view){
        Intent intent;
        String url;
        Intent i;
        switch (view.getId()){
            case R.id.btn_fb:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com/axisvnit")));
                break;

            case R.id.btn_insta:
                Uri uri = Uri.parse("http://instagram.com/_u/axis_vnit");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/axis_vnit")));
                }
                break;

            case R.id.btn_linkedin:
                url = "https://www.linkedin.com/company/axis-vnit-nagpur/";
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;

            case R.id.btn_web:
                url = "https://twitter.com/axisvnit";
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
    }

}