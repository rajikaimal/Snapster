package lk.sliit.mad.snapster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.*;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

import lk.sliit.mad.snapster.Fragment.EffectsFilterFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    private CallbackManager callbackManager;
    private TextView info;
    private LoginButton loginButton;
    private TextView fbLoginResultTextView;
    static String[] data = new String[10];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        fbLoginResultTextView = (TextView) findViewById(R.id.fb_login_textview);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                displayFBUserMessage();
                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                } else {
                                    data[0] = me.optString("name");
                                    data[1] = me.optString("id");
                                    data[2] = me.optString("email");
                                    data[3] = me.optString("username");
                                    Log.d(me.toString(), me.toString());
                                    Log.d("hy", "hy");
                                    // send email and id to your web server
                                }
                            }
                        }).executeAsync();
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt cancelled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
        /**
         * Set the user display message to say hello to the current user
         */

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(cameraIntent);
            }
        });


    }

    private void displayFBUserMessage() {
        // profile is NULL if user is not logged in
        final Profile profile = Profile.getCurrentProfile();
        String message;
        if (profile != null) {
            Log.d(TAG, "current profile is active");
            message = "Hello there " +  profile.getFirstName() + "!";

        } else {
            Log.e(TAG, "current profile is null");
            message = "I'm sorry, you aren't logged in.";
        }
        if (fbLoginResultTextView != null) {
            fbLoginResultTextView.setText(message);
        } else {
            Log.e(TAG, "Couldn't set text view, view is NULL");
        }
    }

    public String[] FBdata() {
        // profile is NULL if user is not logged in
        final Profile profile = Profile.getCurrentProfile();
        String[] message =new String[10];
        if (profile != null) {
            Log.d(TAG, "current profile is active");
            message[0] = profile.getName();
            message[1]=profile.getId();
            

        } else {
            Log.e(TAG, "current profile is null");
            message[0] = "I'm sorry, you aren't logged in.";
        }
       return message;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayFBUserMessage();
        //AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
 //       /AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //String name= FBdata();

            //data=FBdata();

            Bundle b=new Bundle();
            b.putStringArray("Data", new String[]{data[0], data[1],data[2],data[3]});
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            i.putExtras(b);
            this.startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
//        Intent i = new Intent(this, Categories.class);
//        this.startActivity(i);
    }

    public void launchCategories(View v)
    {
        Intent i = new Intent(MainActivity.this, Categories.class);
        this.startActivity(i);
    }

}
