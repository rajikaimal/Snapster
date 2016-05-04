package lk.sliit.mad.snapster;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import lk.sliit.mad.snapster.Fragment.EffectsFilterFragment;

public class CameraActivity extends AppCompatActivity {
    ImageView img;
    String picturePath;
    Dialog choosePhoto;
    Button btnGallery;
    Button btnCamera;
    Button btnCancel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Button btnSelect = (Button) findViewById(R.id.btnSelect);
        Button btnUpload = (Button) findViewById(R.id.btnUpload);
        img =(ImageView) findViewById(R.id.imgProfile);

        choosePhoto = new Dialog(CameraActivity.this);
        choosePhoto.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        choosePhoto.setContentView(R.layout.choose_photo_dialog);
        btnGallery = (Button) choosePhoto.findViewById(R.id.btnSelectFromGallery);
        btnCamera = (Button) choosePhoto.findViewById(R.id.btnCamera);
        btnCancel = (Button) choosePhoto.findViewById(R.id.btnCancel);

        changeFragment(new EffectsFilterFragment());

        btnSelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                selectImage();
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                UploadPhoto uploadPhoto = new UploadPhoto();
                uploadPhoto.execute(picturePath);
            }
        });
    }
    private void selectImage(){
        choosePhoto.show();
        btnGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                choosePhoto.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"),200);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                choosePhoto.dismiss();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, 100);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                choosePhoto.dismiss();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 100){
                File file = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : file.listFiles()){
                    if (temp.getName().equals("temp.jpg")){
                        file = temp;
                        break;
                    }
                }
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),bitmapOptions);
                img.setImageBitmap(bitmap);

                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sample/";
                file.delete();
                OutputStream fOut;
                File f = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");

                picturePath = f.getAbsolutePath();

                try {
                    fOut = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (requestCode == 200){
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

                img.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            }
        }

    }

    private class UploadPhoto extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try
            {
                HttpClient client = new DefaultHttpClient();

                HttpPost post = new HttpPost("https://hidden-shore-36246.herokuapp.com/api/post/funnyfeed");



                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                //you can add some additional data to file
                entityBuilder.addTextBody("User","Rajika Imal");
                entityBuilder.addTextBody("Description","Funny Feed");

                String path = params[0];
                File file= new File(path);
               // entityBuilder.addPart("image", new FileBody(file));

                if(file.exists()){
                    entityBuilder.addBinaryBody("image", file);
                }else {
                    Log.i("log", "file does not exists !");
                }

                HttpEntity entity = entityBuilder.build();
                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                HttpEntity httpEntity = response.getEntity();
                result = EntityUtils.toString(httpEntity);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getInt("success")==1){
                    Toast.makeText(getApplicationContext(), "Photo Uploaded Successfully ;-)", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Upload Failed, Try Again :-(",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeFragment(Fragment targetFragment) {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, targetFragment);
        transaction.commit();
    }

}
