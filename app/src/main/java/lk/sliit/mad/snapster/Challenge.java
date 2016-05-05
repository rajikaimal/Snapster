package lk.sliit.mad.snapster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;

public class Challenge extends AppCompatActivity {
    TextView txtChallenger;
    TextView txtChallengee;
    ImageView postImage, challengeImage;
    Button btnTakeSelfie, upload;

    String PostId;
    String Challenger;
    String Challengee;
    String challengerimgUrl;
    String imagePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    final int CAMERA_REQUEST = 1888;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_challenge);

        txtChallengee = (TextView) findViewById(R.id.txtChallengee);
        txtChallenger = (TextView) findViewById(R.id.txtChallenger);
        postImage = (ImageView) findViewById(R.id.imgViewChallenge);
        challengeImage = (ImageView) findViewById(R.id.imgViewChallengePhoto);
        btnTakeSelfie = (Button) findViewById(R.id.btnSelfie);
        upload = (Button) findViewById(R.id.btnUpload);

        upload.setVisibility(View.INVISIBLE);

        Intent Challenge = getIntent();
        PostId = Challenge.getStringExtra("PostId");
        Challenger = Challenge.getStringExtra("Challenger");
        Challengee = Challenge.getStringExtra("Challengee");
        challengerimgUrl = Challenge.getStringExtra("url");

        txtChallengee.setText(Challengee);
        txtChallenger.setText(Challenger);

        Animation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setInterpolator(new DecelerateInterpolator()); //add this
        fadeInAnimation.setDuration(1000);
        Ion.with(postImage)
                .error(R.drawable.camera)
                .animateIn(fadeInAnimation)
                .load("http://res.cloudinary.com/rajikaimal/image/upload/" + challengerimgUrl);

        btnTakeSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        imagePath = String.valueOf(photoFile);
                        Log.d("Image bugger !", String.valueOf(photoFile));
                        //takePictureIntent.putExtra("image", photoFile);

                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }


//                Intent challengeSummary = new Intent(getBaseContext(), ChallengeSummary.class);
//                challengeSummary.putExtra("PostId", PostId);
//                challengeSummary.putExtra("Challenger", Challenger);
//                challengeSummary.putExtra("Challengee", Challengee);
//                challengeSummary.putExtra("url", challengerimgUrl);
//
//                startActivity(challengeSummary);

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ion.with(getBaseContext())
                        .load("https://hidden-shore-36246.herokuapp.com/api/challenge/create")
                        .setMultipartParameter("postid", PostId)
                        .setMultipartParameter("username", "Rajika")
                        .setMultipartFile("image", new File(imagePath))
                        .asJsonObject();
            }
        });


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                try {
                    Bitmap mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                    challengeImage.setImageBitmap(mImageBitmap);
                    upload.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").toString();
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
}
