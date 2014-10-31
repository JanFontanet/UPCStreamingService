package pbe.upcstreamingservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;


public class Multimedia extends ActionBarActivity implements MediaController.MediaPlayerControl, View.OnClickListener{

    private VideoView mVideoView;
    private MediaController mMediaController;
    private TextView mMediaDescription;

    private Button mBtnEnrrera;
    private Button mBtnPlayPause;
    private Button mBtnEndeban;
    private Button mBtnFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras.isEmpty())
            return;
        String urlVideo = extras.getString(MainActivity.VIDEO);

        downloadVideo(urlVideo);

        mVideoView = (VideoView)findViewById(R.id.video);
        mMediaController = (MediaController)findViewById(R.id.mediaController);
        mMediaDescription = (TextView)findViewById(R.id.mediaInfo);

        mBtnEndeban = (Button)mMediaController.findViewById(R.id.endeban);
        mBtnEnrrera = (Button)mMediaController.findViewById(R.id.enrrera);
        mBtnPlayPause = (Button)mMediaController.findViewById(R.id.play_pause);
        mBtnFullScreen = (Button)mMediaController.findViewById(R.id.fullScreen);

    }

    /**
     * Ens descarragarem el video i la descripció que hi hagi i ho colocarem on calgui..
     *
     * @param urlVideo
     */
    private void downloadVideo(String urlVideo) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multimedia, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void start() {
        mVideoView.start();
    }

    @Override
    public void pause() {
        mVideoView.pause();
    }

    @Override
    public int getDuration() {
        return mVideoView.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mVideoView.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mVideoView.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mVideoView.getBufferPercentage();
    }

    @Override
    public boolean canPause() {
        return mVideoView.canPause();
    }

    @Override
    public boolean canSeekBackward() {
        return mVideoView.canSeekBackward();
    }

    @Override
    public boolean canSeekForward() {
        return mVideoView.canSeekForward();
    }

    /**
     * Get the audio session id for the player used by this VideoView. This can be used to
     * apply audio effects to the audio track of a video.
     *
     * @return The audio session, or 0 if there was an error.
     */
    @Override
    public int getAudioSessionId() {
        return 0; // no l'usarem
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.enrrera:
                int newPos = mVideoView.getCurrentPosition()-5*1000;
                if (mVideoView.canSeekBackward() && newPos>0)
                    mVideoView.seekTo(newPos);

            case R.id.play_pause:
                if (isPlaying()){
                    pause();
                    mBtnPlayPause.setBackgroundResource(R.drawable.ic_action_play);
                }else{
                    start();
                    mBtnPlayPause.setBackgroundResource(R.drawable.ic_action_pause);
                }
            case R.id.endeban:
                int newPos2 = mVideoView.getCurrentPosition()-5*1000;
                if (mVideoView.canSeekBackward() && newPos2<getDuration())
                    mVideoView.seekTo(newPos2);
            case R.id.fullScreen:

        }
    }
}
