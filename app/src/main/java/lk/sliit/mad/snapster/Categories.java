package lk.sliit.mad.snapster;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Categories extends AppCompatActivity {
    String [] titles;
    String [] descriptions;
    int[] images = {R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Resources res = getResources();
        //get titles and descriptions from strings.xml
        titles = res.getStringArray(R.array.titles);
        descriptions = res.getStringArray(R.array.descriptions);
        //link listview
        listView = (ListView) findViewById(R.id.listView);

        SnapAdapter adapter = new SnapAdapter(this, titles, images, descriptions);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                String selected = ((TextView) view.findViewById(R.id.textView2)).getText().toString();
                Intent i = null;
                if(selected.equals("Funny"))
                {
                    i = new Intent(getBaseContext(), FunnyFeed.class);
                    startActivity(i);

                }
                else if(selected.equals("Moody"))
                {
                    i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                }

                Toast toast = Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT);
                toast.show();

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}

class SnapAdapter extends ArrayAdapter<String>
{
    Context context;
    int images[];
    String[] titles;
    String[] descriptions;
    SnapAdapter(Context c, String[] titles, int imgs[], String[] descriptions)
    {
        super(c, R.layout.single_row, R.id.textView, titles);
        this.context = c;
        this.images = imgs;
        this.titles = titles;
        this.descriptions = descriptions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflator.inflate(R.layout.single_row, parent, false);
        ImageView imageview = (ImageView) row.findViewById(R.id.imageView);
        TextView txt1 = (TextView) row.findViewById(R.id.textView2);
        TextView txt2 = (TextView) row.findViewById(R.id.textView3);

        imageview.setImageResource(images[position]);
        txt1.setText(titles[position]);
        txt2.setText(descriptions[position]);
        return row;
    }
}