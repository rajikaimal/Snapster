package lk.sliit.mad.snapster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

public class Challenge extends AppCompatActivity {
    TextView txtChallenger;
    TextView txtChallengee;
    ImageView postImage, challengeImage;
    Button btnTakeSelfie;

    String PostId;
    String Challenger;
    String Challengee;
    String challengerimgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        txtChallengee = (TextView) findViewById(R.id.txtChallengee);
        txtChallenger = (TextView) findViewById(R.id.txtChallenger);
        postImage = (ImageView) findViewById(R.id.imgViewChallenge);
        challengeImage = (ImageView) findViewById(R.id.imgViewChallengePhoto);
        btnTakeSelfie = (Button) findViewById(R.id.btnSelfie);
        challengeImage.setImageResource(R.drawable.tiffany);

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
                Intent challengeSummary = new Intent(getBaseContext(), ChallengeSummary.class);
                challengeSummary.putExtra("PostId", PostId);
                challengeSummary.putExtra("Challenger", Challenger);
                challengeSummary.putExtra("Challengee", Challengee);
                challengeSummary.putExtra("url", challengerimgUrl);

                startActivity(challengeSummary);

            }
        });
    }
}
