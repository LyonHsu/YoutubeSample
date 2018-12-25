package lyon.youtube.pi.test.youtubepi3sample;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import lyon.youtube.pi.test.youtubepi3sample.Youtube.Play.YoutubeFragment;

public class YoutubePlayer extends FragmentActivity {
    public  String VIDEO_ID = "OsUr8N7t4zc";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        RelativeLayout youtubePlayerFragment = (RelativeLayout) findViewById(R.id.youtubePlayerFragment);
        Bundle bundle = getIntent().getExtras();
        final YoutubeFragment fragment = new YoutubeFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.youtubePlayerFragment, fragment)
                .commit();

        final Button loopBtn= (Button) findViewById(R.id.loopBtn);
        loopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.setLoop(!fragment.getIsLoop());
                if(fragment.getIsLoop()){
                    loopBtn.setText("Loop");
                }else{
                    loopBtn.setText("NoLoop");
                }
            }
        });
        if(fragment.getIsLoop()){
            loopBtn.setText("Loop");
        }else{
            loopBtn.setText("NoLoop");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
