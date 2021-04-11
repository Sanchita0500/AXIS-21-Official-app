package axis.axis21.sanchita.axisapp.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import axis.axis21.sanchita.axisapp.R;

public class AddNotifications extends AppCompatActivity {

    private EditText title,content;
    private Button pushButton;
    private DatabaseReference NotificationRef;
    private ProgressDialog loadingBar;
    private String Title,Content,RandomKey,saveCurrentDate,saveCurrentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notifications);

        title=(EditText)findViewById(R.id.title_notification);
        content=(EditText)findViewById(R.id.content_notification);
        pushButton=(Button)findViewById(R.id.update_notification_button);

        NotificationRef= FirebaseDatabase.getInstance().getReference().child("Notifications");
        loadingBar=new ProgressDialog(this);

        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });
    }

    private void ValidateData() {
        Title=title.getText().toString();
        Content=content.getText().toString();

        if(TextUtils.isEmpty(Title)){
            Toast.makeText(this, "Title is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Content)){
            Toast.makeText(this, "Please write content of the notification", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreInfotoDatabase();
        }
    }

    private void StoreInfotoDatabase() {

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,YYYY");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        RandomKey=saveCurrentDate + " " +saveCurrentTime;

        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("title",Title);
        productMap.put("content",Content);

        NotificationRef.child(RandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(AddNotifications.this, "Notification Sent", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(AddNotifications.this,AddInfo.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AddNotifications.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}