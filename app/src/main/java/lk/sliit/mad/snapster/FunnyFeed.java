package lk.sliit.mad.snapster;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FunnyFeed extends AppCompatActivity {
    String [] titles;
    String [] descriptions;
    int[] images = {R.drawable.kendall, R.drawable.yuri, R.drawable.tiffany, R.drawable.jessica};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funny_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Resources res = getResources();
        //get titles and descriptions from strings.xml
        titles = res.getStringArray(R.array.titles);
        descriptions = res.getStringArray(R.array.descriptions);
        //link listview
        ListView listView = (ListView) findViewById(R.id.listViewFunny);

        ArrayList<Post> posts = new ArrayList<Post>();
        final FunnyAdapter adapter = new FunnyAdapter(this,posts);
        listView.setAdapter(adapter);

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
    String username;
    String image;
    //String description;
    int likes;

    public Post(JSONObject object) {
        try {
            this.username = object.getString("username");
            this.image = object.getString("image");
            //this.description = object.getString("description");
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
    public FunnyAdapter(Context c, ArrayList<Post> posts)
    {
        super(c, 0, posts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post post = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_row_funny, parent, false);
        }
        ImageView imageview = (ImageView) convertView.findViewById(R.id.imageView2);
        TextView username = (TextView) convertView.findViewById(R.id.textView4);
        TextView likes = (TextView)convertView.findViewById(R.id.textView6);

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
