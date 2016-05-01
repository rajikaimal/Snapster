package lk.sliit.mad.snapster;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FunnyFeed extends AppCompatActivity {
    String [] titles;
    String [] descriptions;
    int[] images = {R.drawable.kendall, R.drawable.yuri, R.drawable.tiffany, R.drawable.jessica};

    ArrayList<Post> posts;
    FunnyAdapter adapter;

    ToggleButton likeToggle;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://hidden-shore-36246.herokuapp.com");
        } catch (URISyntaxException e) {}
    }


    private Emitter.Listener onNewPost = new Emitter.Listener() {


        @Override
        public void call(Object... args) {
            Log.d("socket", "socket call");
            JSONObject data = (JSONObject) args[0];
            try {
                Post newPost = new Post(data);
                setPostData(newPost, adapter);
                //message = data.getString("message");
            } catch (Exception e) {
                return;
            }

        }
    };

    public void setPostData(Post newPost, FunnyAdapter adapter) {
        adapter.add(newPost);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funny_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Resources res = getResources();

        ListView listView = (ListView) findViewById(R.id.listViewFunny);
        likeToggle = (ToggleButton) findViewById(R.id.liketoggle);

        posts = new ArrayList<Post>();
        adapter = new FunnyAdapter(this,posts);
        listView.setAdapter(adapter);


//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String id = (String) adapterView.getItemAtPosition(i);
//                Log.d("Item ", id);
//                return false;
//            }
//        });

        mSocket.emit("funnyfeedpost", "My message");
        Log.d("socket", "sent post !!");
        mSocket.on("newfunnypost", onNewPost);
        mSocket.connect();


        AsyncHttpClient client = new AsyncHttpClient();

//        client.get("https://hidden-shore-36246.herokuapp.com/api/feed/funny", new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//                // called before request is started
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//                Log.d("HTtp response", response.toString());
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
//                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//            }
//
//            @Override
//            public void onRetry(int retryNo) {
//                // called when request is retried
//            }
//        });

        client.get("https://hidden-shore-36246.herokuapp.com/api/feed/funny", null, new JsonHttpResponseHandler() {
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

                        Post newPost = new Post(obj);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent funnyHOF = new Intent(getBaseContext(), FunnyHOF.class);
                startActivity(funnyHOF);
            }
        });
    }

}

class Post {
    String _id;
    String username;
    String image;
    String description;
    int likes;

    public Post(JSONObject object) {
        try {
            this._id = object.getString("_id");
            this.username = object.getString("username");
            this.image = object.getString("image");
            this.description = object.getString("description");
            this.likes = object.getInt("likes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

class FunnyAdapter extends ArrayAdapter<Post>
{
    Context context;
    int images[];
    String[] titles;
    String[] descriptions;

    Button funnyFeedComment;

    ToggleButton likeToggle;

    public FunnyAdapter(Context c, ArrayList<Post> posts)
    {
        super(c, 0, posts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post post = getItem(position);
        final Post postI  = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_row_funny, parent, false);
        }
        ImageView imageview = (ImageView) convertView.findViewById(R.id.imageViewFeed);
        TextView username = (TextView) convertView.findViewById(R.id.txtName);
        TextView description = (TextView) convertView.findViewById(R.id.txtDescription);
        final TextView likes = (TextView)convertView.findViewById(R.id.txtLikesFeed);

        funnyFeedComment = (Button) convertView.findViewById(R.id.funnyFeedComment);

        likeToggle = (ToggleButton) convertView.findViewById(R.id.liketoggle);

        likeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean likeStatus) {
                AsyncHttpClient client = new AsyncHttpClient();
                Log.d("Toogle state", String.valueOf(likeStatus));
                if(likeStatus) {
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
                .placeholder(R.drawable.broken_heart)
                .error(R.drawable.yuri)
                .animateIn(fadeInAnimation)
                .load("http://res.cloudinary.com/rajikaimal/image/upload/" + post.image);
        likes.setText(post.likes + " likes");
        return convertView;
    }
}
