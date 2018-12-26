package lyon.youtube.pi.test.youtubepi3sample;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Intent;
import android.drm.DrmStore;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lyon.youtube.pi.test.youtubepi3sample.Youtube.Search.SearchYoutube;
import lyon.youtube.pi.test.youtubepi3sample.Youtube.YoutubePoster;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

public class MainActivity extends AppCompatActivity {
    String TAG =MainActivity.class.getName();

    TextView title;
    EditText searchEditText;
    Button searchBtn;
    RecyclerView mRecyclerView;
    List<YoutubePoster> youtubePosters;
    LinearLayoutManager mLayoutManager;
    GridLayoutManager gridLayoutManager;
    YoutubeAdapter mAdapter;
    public final int NOTIFYCHANGE=1;
    public final int NOTIFYCHANGE2=2;
    String nexttoken;
    int nextpostion=0;
    int TAG_CHECK_SCROLL_UP=-1;
    int TAG_CHECK_SCROLL_DOWN=1;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = (TextView) findViewById(R.id.title);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
             @Override
             public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                 //按下完成鍵要執行的動作
                 if (actionId==IME_ACTION_SEARCH){
                     searchBtn.callOnClick();
                 }
                 return false;
             }
         });
        searchEditText.setFocusable(true);
            searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.requestFocus();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclesView);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyWord=searchEditText.getText().toString();
                Log.d(TAG,"searchBtn onClick:"+keyWord);
                nextpostion=0;
                new SearchYoutube(keyWord){
                    public void YoutubePosters(List<YoutubePoster> posters){
                        Log.d(TAG,"YoutubeAdapter searchBtn onClick: YoutubePosters size:"+posters.size());
                        youtubePosters = posters;

                        Message msg = serviceHandler.obtainMessage();
                        msg.what=NOTIFYCHANGE;
                        msg.obj=youtubePosters;
                        serviceHandler.sendMessage(msg);
                    }

                    @Override
                    public void getNextPageToken(String NextPageToken) {
                        nexttoken=NextPageToken;
                    }

                    @Override
                    public void getPrevPageToken(String PextPageToken) {

                    }
                }.execute();
            }
        });



// use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        gridLayoutManager = new GridLayoutManager(this,6);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        youtubePosters = new ArrayList<>();
        // specify an adapter (see also next example)
        mAdapter = new YoutubeAdapter(youtubePosters);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new YoutubeAdapter.VideoClickListener() {
            @Override
            public void onItemClick(View v, int postion) {
                YoutubePoster youtubePoster = youtubePosters.get(postion);
                Intent intent = new Intent(MainActivity.this, YoutubePlayer.class);
                Bundle bundle = new Bundle();
                String videoId=youtubePoster.getYoutubeId();
                for(int i=0;i<youtubePosters.size();i++){
                    videoId=videoId+","+youtubePosters.get(i).getYoutubeId();
                }
                bundle.putString("videoId",videoId);
                Log.d(TAG,"videoId:"+videoId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        searchEditText.setText("ayasa");
        searchBtn.callOnClick();


        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                Log.i(TAG, "-----------mList2 onScrollStateChanged-----------");
//                Log.i(TAG, "mList2 newState: " + newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                Log.i(TAG, "-----------mList onScrolled-----------");
//                Log.i(TAG, "mList dx: " + dx);
//                Log.i(TAG, "mList dy: " + dy);
//                //RecyclerView.canScrollVertically(-1)的值表示是否能向上滚动，false表示已经滚动到顶部
//                Log.i(TAG, "mList CHECK_SCROLL_UP 顶部: " + recyclerView.canScrollVertically(TAG_CHECK_SCROLL_UP));
//                //RecyclerView.canScrollVertically(1)的值表示是否能向下滚动，false表示已经滚动到底部
//                Log.i(TAG, "mList CHECK_SCROLL_DOWN 底部: " + recyclerView.canScrollVertically(TAG_CHECK_SCROLL_DOWN));

                if(!recyclerView.canScrollVertically(TAG_CHECK_SCROLL_DOWN)){
                    Log.i(TAG, "mList CHECK_SCROLL_DOWN 底部: " + recyclerView.canScrollVertically(TAG_CHECK_SCROLL_DOWN));
                    String keyWord=searchEditText.getText().toString();
                    if(nexttoken!=null) {
                        new SearchYoutube(keyWord, nexttoken) {
                            public void YoutubePosters(List<YoutubePoster> posters) {
                                Log.d(TAG, "YoutubeAdapter searchBtn onClick: posters size:" + posters.size());
                                nextpostion=youtubePosters.size()-1;
                                for(int i=0;i<posters.size();i++){
                                    youtubePosters.add(posters.get(i));
                                }
                                Log.d(TAG, "YoutubeAdapter searchBtn onClick: YoutubePosters size:" + youtubePosters.size());

                                Message msg = serviceHandler.obtainMessage();
                                msg.what = NOTIFYCHANGE;
                                msg.obj = youtubePosters;
                                serviceHandler.sendMessage(msg);
                            }

                            @Override
                            public void getNextPageToken(String NextPageToken) {
                                nexttoken = NextPageToken;
                            }

                            @Override
                            public void getPrevPageToken(String PextPageToken) {

                            }
                        }.execute();
                    }
                }

            }
        });

    }

    private final Handler serviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOTIFYCHANGE:
                    mAdapter.setNotifyDataSetChanged(youtubePosters);
                    break;
            }
        }
    };
}
