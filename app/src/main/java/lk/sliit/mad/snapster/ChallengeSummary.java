package lk.sliit.mad.snapster;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ChallengeSummary extends AppCompatActivity {
    Button btnChallenge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_summary);

        Intent Challenge = getIntent();
        String PostId = Challenge.getStringExtra("PostId");
        String Challenger = Challenge.getStringExtra("Challenger");
        String Challengee = Challenge.getStringExtra("Challengee");
        String imgUrl = Challenge.getStringExtra("url");

        btnChallenge = (Button) findViewById(R.id.btnChallenge);
        btnChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("username", "tiffany");

                //params.setForceMultipartEntityContentType(true);

                client.get("https://hidden-shore-36246.herokuapp.com/api/feed/funny", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                        // Pull out the first event on the public timeline

                        Log.d("Request", "Done .. setting up now ....");
                    }
                });
            }
        });
    }
}
