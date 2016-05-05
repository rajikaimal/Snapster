package lk.sliit.mad.snapster;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChallengeSummary extends AppCompatActivity {
    ArrayList<ChallengePost> posts;
    ChallengeAdapter adapter;

    ToggleButton likeToggle;

    public void setPostData(Post newPost, FunnyAdapter adapter) {
        adapter.add(newPost);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Resources res = getResources();

        ListView listView = (ListView) findViewById(R.id.challengersList);
        likeToggle = (ToggleButton) findViewById(R.id.liketoggle);

        posts = new ArrayList<ChallengePost>();
        adapter = new ChallengeAdapter(getBaseContext(), posts);
        listView.setAdapter(adapter);

        AsyncHttpClient client = new AsyncHttpClient();
        Intent i = getIntent();
        String postId = i.getStringExtra("postid");
        String imgUrl = i.getStringExtra("imgUrl");
        ImageView challengeeImage = (ImageView) findViewById(R.id.imgViewChallenge);
        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setInterpolator(new DecelerateInterpolator()); //add this
        fadeInAnimation.setDuration(1000);
        Ion.with(challengeeImage)
                .error(R.drawable.camera)
                .animateIn(fadeInAnimation)
                .load("http://res.cloudinary.com/rajikaimal/image/upload/" + imgUrl);

        RequestParams params = new RequestParams();
        //params.put("postid", postId);
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
                for (int i = 0; i < timeline.length(); i++) {
                    try {
                        JSONObject obj = timeline.getJSONObject(i);

                        ChallengePost newPost = new ChallengePost(obj);
                        adapter.add(newPost);
                        adapter.notifyDataSetInvalidated();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("Request", "Done .. setting up now ....");
            }
        });
    }
}



class ChallengePost {
    String _id;
    String username;
    String image;
    String description;
    boolean likestate;
    int likes;

    public ChallengePost(JSONObject object) {
        try {
            this._id = object.getString("_id");
            this.username = object.getString("username");
            this.image = object.getString("image");
            this.description = object.getString("description");
            this.likestate = object.getBoolean("likestate");
            this.likes = object.getInt("likes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

class ChallengeAdapter extends ArrayAdapter<ChallengePost>
{
    Context context;
    int images[];
    String[] titles;
    String[] descriptions;

    Button funnyFeedComment;

    ToggleButton likeToggle;

    public ChallengeAdapter(Context c, ArrayList<ChallengePost> posts)
    {
        super(c, 0, posts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChallengePost post = getItem(position);
        final ChallengePost postI  = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.challenge_row, parent, false);
        }
        ImageView imageview = (ImageView) convertView.findViewById(R.id.imageViewFeedChallenge);
        TextView username = (TextView) convertView.findViewById(R.id.txtNameChallenge);
        TextView description = (TextView) convertView.findViewById(R.id.txtDescriptionChallenge);
        final TextView likes = (TextView)convertView.findViewById(R.id.txtLikesFeedChallenge);

        funnyFeedComment = (Button) convertView.findViewById(R.id.funnyFeedCommentChallenge);

        likeToggle = (ToggleButton) convertView.findViewById(R.id.liketoggleChallenge);

        if(post.likestate) {
            likeToggle.setChecked(true);
        }
        else {
            likeToggle.setChecked(false);
        }

        likeToggle.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncHttpClient client = new AsyncHttpClient();

                if(likeToggle.isChecked()) {
                    RequestParams params = new RequestParams();
                    params.put("postid", String.valueOf(postI._id));
                    params.put("username", "tiffany");

                    params.setForceMultipartEntityContentType(true);

                    client.post("https://hidden-shore-36246.herokuapp.com/api/post/like", params ,new ResponseHandlerInterface() {
                        @Override
                        public void sendResponseMessage(HttpResponse response) throws IOException {

                        }

                        @Override
                        public void sendStartMessage() {

                        }

                        @Override
                        public void sendFinishMessage() {

                        }

                        @Override
                        public void sendProgressMessage(long bytesWritten, long bytesTotal) {

                        }

                        @Override
                        public void sendCancelMessage() {

                        }

                        @Override
                        public void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBody) {
                            Log.d("LIKE response", "WOhoooo");
                        }

                        @Override
                        public void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }

                        @Override
                        public void sendRetryMessage(int retryNo) {

                        }

                        @Override
                        public URI getRequestURI() {
                            return null;
                        }

                        @Override
                        public void setRequestURI(URI requestURI) {

                        }

                        @Override
                        public Header[] getRequestHeaders() {
                            return new Header[0];
                        }

                        @Override
                        public void setRequestHeaders(Header[] requestHeaders) {

                        }

                        @Override
                        public boolean getUseSynchronousMode() {
                            return false;
                        }

                        @Override
                        public void setUseSynchronousMode(boolean useSynchronousMode) {

                        }

                        @Override
                        public boolean getUsePoolThread() {
                            return false;
                        }

                        @Override
                        public void setUsePoolThread(boolean usePoolThread) {

                        }

                        @Override
                        public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {

                        }

                        @Override
                        public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {

                        }

                        @Override
                        public Object getTag() {
                            return null;
                        }

                        @Override
                        public void setTag(Object TAG) {

                        }
                    });
                } else {
                    RequestParams params = new RequestParams();
                    params.put("postid", String.valueOf(postI._id));
                    params.put("username", "tiffany");

                    params.setForceMultipartEntityContentType(true);

                    client.post("https://hidden-shore-36246.herokuapp.com/api/post/unlike", params ,new ResponseHandlerInterface() {
                        @Override
                        public void sendResponseMessage(HttpResponse response) throws IOException {

                        }

                        @Override
                        public void sendStartMessage() {

                        }

                        @Override
                        public void sendFinishMessage() {

                        }

                        @Override
                        public void sendProgressMessage(long bytesWritten, long bytesTotal) {

                        }

                        @Override
                        public void sendCancelMessage() {

                        }

                        @Override
                        public void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBody) {
                            Log.d("LIKE response", "WOhoooo");
                        }

                        @Override
                        public void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }

                        @Override
                        public void sendRetryMessage(int retryNo) {

                        }

                        @Override
                        public URI getRequestURI() {
                            return null;
                        }

                        @Override
                        public void setRequestURI(URI requestURI) {

                        }

                        @Override
                        public Header[] getRequestHeaders() {
                            return new Header[0];
                        }

                        @Override
                        public void setRequestHeaders(Header[] requestHeaders) {

                        }

                        @Override
                        public boolean getUseSynchronousMode() {
                            return false;
                        }

                        @Override
                        public void setUseSynchronousMode(boolean useSynchronousMode) {

                        }

                        @Override
                        public boolean getUsePoolThread() {
                            return false;
                        }

                        @Override
                        public void setUsePoolThread(boolean usePoolThread) {

                        }

                        @Override
                        public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {

                        }

                        @Override
                        public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {

                        }

                        @Override
                        public Object getTag() {
                            return null;
                        }

                        @Override
                        public void setTag(Object TAG) {

                        }
                    });

                }
            }
        });

        try {

            funnyFeedComment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    RelativeLayout rl = (RelativeLayout) view.getParent();
                    Log.d("Button click", String.valueOf(postI._id));
                    Intent i = new Intent(getContext(), Comment.class);
                    getContext().startActivity(i);
                }
            });
        }
        catch (NullPointerException e) {
            Log.d("Exception: ", e.getStackTrace().toString());
        }
        username.setText(post.username);
        description.setText(post.description);
        likes.setText(post.likes);



        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setInterpolator(new DecelerateInterpolator()); //add this
        fadeInAnimation.setDuration(1000);
        Ion.with(imageview)
                .placeholder(R.drawable.yuri)
                .error(R.drawable.camera)
                .animateIn(fadeInAnimation)
                .load("http://res.cloudinary.com/rajikaimal/image/upload/" + post.image);
        likes.setText(post.likes + " likes");

        final String PostId = post._id;
        final String Challenger = "Tiffany";
        final String Challengee = post.username;
        final String imgUrl = post.image;

        imageview.buildDrawingCache();
        final Bitmap imgBitMap = imageview.getDrawingCache();
        imageview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent challenge = new Intent(getContext(), Challenge.class);
                challenge.putExtra("PostId", PostId);
                challenge.putExtra("Challenger", Challenger);
                challenge.putExtra("Challengee", Challengee);
                challenge.putExtra("url", imgUrl);
                //challenge.putExtra("Image", imgBitMap);
                getContext().startActivity(challenge);
                return false;
            }
        });
        return convertView;
    }
}