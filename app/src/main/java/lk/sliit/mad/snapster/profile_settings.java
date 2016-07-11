package lk.sliit.mad.snapster;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class profile_settings extends AppCompatActivity {
    String[] data = new String[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b=this.getIntent().getExtras();
        data = b.getStringArray("Data");

        TextView Name = (TextView) findViewById(R.id.editText_name);
        TextView Email = (TextView) findViewById(R.id.editEmail);
        TextView Username= (TextView)findViewById(R.id.editText_username);

        Name.setText(data[0]);
        Email.setText(data[2]);
        Username.setText(data[3]);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.profile_settings) {
            //logout
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
