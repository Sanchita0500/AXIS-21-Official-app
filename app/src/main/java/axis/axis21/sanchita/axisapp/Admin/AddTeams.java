package axis.axis21.sanchita.axisapp.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import axis.axis21.sanchita.axisapp.R;

public class AddTeams extends AppCompatActivity {

    private TextView core,app,publicity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teams);

        core=(TextView)findViewById(R.id.core_update);
        app=(TextView)findViewById(R.id.app_update);
        publicity=(TextView)findViewById(R.id.publicity_update);

        core.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddTeams.this, AddTeamMember.class);
                intent.putExtra("id", "core");
                startActivity(intent);
            }
        });

        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddTeams.this, AddTeamMember.class);
                intent.putExtra("id", "app");
                startActivity(intent);
            }
        });

        publicity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddTeams.this, AddTeamMember.class);
                intent.putExtra("id", "publicity");
                startActivity(intent);
            }
        });

    }
}