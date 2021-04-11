package axis.axis21.sanchita.axisapp.EventSection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import axis.axis21.sanchita.axisapp.R;

public class EventDetails extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private String event_category;
    private String event_id;
    private String title;
    private TextView titleBar;
    private ImageView details_image;
    private String link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        event_category = intent.getStringExtra("cat");
        event_id = intent.getStringExtra("id");

        details_image = (ImageView)findViewById(R.id.details_image);
        FirebaseDatabase.getInstance().getReference().child("Events").child(event_category).child(event_id).child("image")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            try {
                                Picasso.get().load(dataSnapshot.getValue().toString()).into(details_image);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
            //required empty constructor
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event_details, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public void Register(View view){
        FirebaseDatabase.getInstance().getReference().child("Events").child(event_category).child(event_id).child("register")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    link = dataSnapshot.getValue().toString();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(link));
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:{
                    Fragment fragment = new AboutFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("event_category", event_category);
                    bundle.putString("event_id", event_id);
                    bundle.putString("section", "about");
                    fragment.setArguments(bundle);
                    return fragment;
                }

                case 1:{
                    Fragment fragment = new StatementFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("event_category", event_category);
                    bundle.putString("event_id", event_id);
                    bundle.putString("section", "statement");
                    fragment.setArguments(bundle);
                    return fragment;
                }

                case 2:{
                    Fragment fragment = new RulesFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("event_category", event_category);
                    bundle.putString("event_id", event_id);
                    bundle.putString("section", "rules");
                    fragment.setArguments(bundle);
                    return fragment;
                }

                default: {
                    Fragment fragment = new ContactFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("event_category", event_category);
                    bundle.putString("event_id", event_id);
                    bundle.putString("section", "contact");
                    fragment.setArguments(bundle);
                    return fragment;
                }
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}