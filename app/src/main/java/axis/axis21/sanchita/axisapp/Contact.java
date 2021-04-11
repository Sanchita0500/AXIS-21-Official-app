package axis.axis21.sanchita.axisapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Contact extends AppCompatActivity {

    private DatabaseReference mRef;
    private EditText name;
    private EditText subject;
    private EditText message;
    private ProgressDialog dialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mRef = FirebaseDatabase.getInstance().getReference().child("Messages");

        name = (EditText)findViewById(R.id.con_name);
        subject = (EditText)findViewById(R.id.con_sub);
        message = (EditText)findViewById(R.id.con_message);

        dialog = new ProgressDialog(Contact.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Sending Message");
        dialog.setMessage("Please wait ..");
    }

    private void sendAlert(final String name, final String subject, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Contact.this);
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sendMessage(name, subject, message);
            }
        });
        builder.setTitle("Send Message?");
        builder.setMessage("Do you want to send this message? We'll respond to you soon!");
        AlertDialog confirmation_dialog = builder.create();
        confirmation_dialog.show();
    }

    private void sendMessage(String name, String subject, String message) {
        dialog.show();
        DatabaseReference aref = mRef.push();
        aref.child("name").setValue(name);
        aref.child("college").setValue(subject);
        aref.child("message").setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(Contact.this, "Message sent. We'll get back to you soon!", Toast.LENGTH_SHORT).show();
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "info@axisvnit.org" });
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "AXIS 21 - Query from user");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                    // set type of intent
                    emailIntent.setType("message/rfc822");
                    startActivity(Intent.createChooser(emailIntent, null));
                }
                else {
                    Toast.makeText(Contact.this, "Message sending failed! Please try again later.", Toast.LENGTH_SHORT).show();
                }
                //finish();
            }
        });
    }

    public void submit(View view){
        String name = this.name.getText().toString();
        String subject = this.subject.getText().toString();
        String message = this.message.getText().toString();

        if (name.isEmpty()){
            Toast.makeText(this, "Please Enter your name", Toast.LENGTH_SHORT).show();
        }
        if (subject.isEmpty()){
            Toast.makeText(this, "Please Enter a subject", Toast.LENGTH_SHORT).show();
        }
        if (message.isEmpty()){
            Toast.makeText(this, "Please type some message to be sent", Toast.LENGTH_SHORT).show();
        }
        else {
            sendAlert(name,subject,message);
        }
    }

    public void goBack(View view){
        finish();
    }
}