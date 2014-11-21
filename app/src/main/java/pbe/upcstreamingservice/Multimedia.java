package pbe.upcstreamingservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import pbe.upcstreamingservice.DownloaderTask.DownloadVideosTasks;


public class Multimedia extends ActionBarActivity implements MediaController.MediaPlayerControl, View.OnClickListener{

    private VideoView mVideoView;
    private MediaController mMediaController;
    private TextView mMediaDescription;
    private String urlVideo;
    private int videoDuration;

    private HashMap<String, Float> urls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras.isEmpty())
            return;

        urlVideo = extras.getString(MainActivity.VIDEO).split("$")[2]; //URL de l'arxiu m3u8

        downloadVideo(urlVideo);

        mVideoView = (VideoView)findViewById(R.id.video);
        mMediaController = new MediaController(this);
        mMediaDescription = (TextView)findViewById(R.id.mediaInfo);

        mVideoView.setMediaController(mMediaController);

    }

    /**
     * Ens descarragarem el video i la descripció que hi hagi i ho colocarem on calgui..
     *
     * @param urlVideo
     */
    private void downloadVideo(String urlVideo) {
        DownloadVideosTasks dvt = new DownloadVideosTasks();
        dvt.execute(urlVideo);
    }

    //De moment implementació bàsica, només una resolució, no idiomes, no coses rares..

    private void parsingURLs(FileInputStream m3u8) {
        String urlsfile = m3u8.toString();
        String[] files = urlsfile.split("\n");
        String tag="";
        for (String s : files){
            if (s.substring(0,0).equals("#")){
                tag = s.substring(1);
                if (tag.contains("EXT-X-TARGETDURATION:")){
                    videoDuration = Integer.parseInt(tag.substring(21));
                }
            }else if (!tag.equals("")){
                urls.put(s , Float.parseFloat(tag.substring(7, tag.length()-2)));
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multimedia, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.preguntes:
                Intent i = new Intent(Multimedia.this, ChatActivity.class);
                i.putExtra(MainActivity.VIDEO, urlVideo);
                startActivity(i);
        }
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

    }
}
