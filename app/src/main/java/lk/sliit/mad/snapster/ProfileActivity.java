package lk.sliit.mad.snapster;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;

public class ProfileActivity extends AppCompatActivity {
    String[] data = new String[10];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b=this.getIntent().getExtras();
         data = b.getStringArray("Data");
        //String data = getIntent().getExtras().getString("Data");
       // Toast.makeText(this.getApplicationContext(), data, Toast.LENGTH_SHORT);

        TextView Username = (TextView) findViewById(R.id.Username);
        Username.setText(data[0]);

        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) findViewById(R.id.image);
        profilePictureView.setProfileId(data[1]);

        Button edit = (Button)findViewById(R.id.edit_button);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b=new Bundle();
                b.putStringArray("Data", new String[]{data[0], data[1],data[2],data[3]});
                Intent i = new Intent(getApplicationContext(), profile_settings.class);
                i.putExtras(b);
                startActivity(i);
            }
        });

    }





}
