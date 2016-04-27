package lk.sliit.mad.snapster;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FunnyFeed extends AppCompatActivity {
    String [] titles;
    String [] descriptions;
    int[] images = {R.drawable.kendall, R.drawable.yuri, R.drawable.tiffany, R.drawable.jessica};

    ArrayList<Post> posts;
    FunnyAdapter adapter;

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

    //funnyfeed like button
    Button funnyFeedLike;
    Button funnyFeedComment;

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
        ImageView imageview = (ImageView) convertView.findViewById(R.id.imageView2);
        TextView username = (TextView) convertView.findViewById(R.id.textView4);
        TextView likes = (TextView)convertView.findViewById(R.id.textView6);

        funnyFeedLike = (Button) convertView.findViewById(R.id.funnyFeedLike);
        funnyFeedComment = (Button) convertView.findViewById(R.id.funnyFeedComment);
        try {
            funnyFeedLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RelativeLayout rl = (RelativeLayout) view.getParent();
                    Log.d("Button click", String.valueOf(postI._id));
                    funnyFeedLike.setBackgroundResource(R.drawable.filledheart);
                }
            });

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
