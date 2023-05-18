package com.ascien.app.Activities;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;
import com.ascien.app.Utils.VideoConfig;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
public class FullScreenLessonPlayerActivity  extends YouTubeBaseActivity {
    private String  mVideoType;
    private String  mVideoUrl;
    YouTubePlayerView myouTubePlayerView;
    VideoView html5VideoPlayer;
    ImageView emptyVideoScreen;
    private ProgressBar progressBarForVideoPlayer;
    YouTubePlayer.OnInitializedListener  mOnInitializedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_activity);
        init();
        getTheLessonDetails();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width) , (int) (height));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);
        playLessonOnFullScreen(mVideoType, mVideoUrl);
    }

    private void init() {
        html5VideoPlayer = (VideoView) findViewById(R.id.html5Player);
        emptyVideoScreen = findViewById(R.id.emptyVideoScreen);
        initProgressBar();
    }

    // Initialize the progress bar
    private void initProgressBar() {
        progressBarForVideoPlayer = findViewById(R.id.loadingVideoPlayer);
        Sprite waveLoading = new Wave();
        progressBarForVideoPlayer.setIndeterminateDrawable(waveLoading);
    }

    private void getTheLessonDetails() {
        mVideoType = "html5";
        mVideoUrl  = (String) getIntent().getSerializableExtra("videoURl");
    }

    private void playLessonOnFullScreen(String videoType, String videoUrl) {
        if (videoType.equals("html5") && !videoUrl.equals("")){
            playHTML5Video(videoUrl);
        }else{
            Toast.makeText(this, "Proper Video URL is Missing", Toast.LENGTH_SHORT).show();
            html5VideoPlayer.setVisibility(View.GONE);
            emptyVideoScreen.setVisibility(View.VISIBLE);
        }
    }

    private void playHTML5Video(String videourl) {
        final VideoView videoView;
        videoView = (VideoView) findViewById(R.id.html5Player);
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(videourl);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();
    }
    private void playYouTubeVideo(String videoUrl) {
        final String youtubeVideoId = VideoConfig.extractYoutubeId(videoUrl);
        myouTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubePlayer);
        myouTubePlayerView.setVisibility(View.VISIBLE);
        mOnInitializedListener = new YouTubePlayer.OnInitializedListener(){
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(youtubeVideoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        SharedPreferences preferences = this.getSharedPreferences(Helpers.SHARED_PREF, 0);
        String YouTubeAPIKey = preferences.getString("youtube_api_key", "");
        myouTubePlayerView.initialize(YouTubeAPIKey, mOnInitializedListener);
    }

    private void playVimeoOnWebView(String videoUrl) {
        String vimeoVideoId = VideoConfig.extractVimeoId(videoUrl);
        WebView webView = (WebView) findViewById(R.id.videoPlayerWebView);
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript
        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setAppCacheEnabled(true);
//        webView.getSettings().setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDatabasePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/databases");
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl("https://player.vimeo.com/video/"+vimeoVideoId+"?player_id=player&title=0&byline=0&portrait=0&autoplay=1&api=1");
    }
}
