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

import java.util.HashMap;

import axis.axis21.sanchita.axisapp.R;

public class AddContacts extends AppCompatActivity {

    private EditText name,phone,email;
    private Button addContactsButton;
    private String Name,Phone,Mail,category,Title,topNode;
    private ProgressDialog loadingBar;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        name=(EditText)findViewById(R.id.name);
        phone=(EditText)findViewById(R.id.phone);
        email=(EditText)findViewById(R.id.mail);
        addContactsButton=(Button)findViewById(R.id.add_contact_info_button);

        Intent intent=getIntent();
        category=intent.getStringExtra("cat");
        Title=intent.getStringExtra("title");

        loadingBar=new ProgressDialog(this);
        if(category.equals("workshops")) {
            mRef = FirebaseDatabase.getInstance().getReference().child(category).child(Title).child("contact");
        }
        else if(category.equals("Events")){
            topNode=intent.getStringExtra("top");
            mRef = FirebaseDatabase.getInstance().getReference().child(category).child(topNode).child(Title).child("contact");
        }

        addContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });
    }

    private void ValidateData() {
        Name=name.getText().toString();
        Phone=phone.getText().toString();
        Mail=email.getText().toString();

        if(TextUtils.isEmpty(Name)){
            Toast.makeText(this, "Name of coordinator is mandatory", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(Phone)){
            Toast.makeText(this, "Phone Number of coordinator is mandatory", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(Mail)){
            Toast.makeText(this, "e-mail id of coordinator is mandatory", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreInfo();
        }
    }

    private void StoreInfo() {
        loadingBar.setTitle("Adding Contact");
        loadingBar.setMessage("Please Wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        HashMap<String,Object> contactMap=new HashMap<>();
        contactMap.put("name",Name);
        contactMap.put("number",Phone);
        contactMap.put("email",Mail);

        mRef.child(Name).updateChildren(contactMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Toast.makeText(AddContacts.this, "Contact Info Added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    loadingBar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AddContacts.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}