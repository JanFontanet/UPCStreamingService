package pbe.upcstreamingservice;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import io.vov.vitamio.MediaPlayer;


public class Multimedia extends ActionBarActivity implements MediaController.MediaPlayerControl, View.OnClickListener, MediaPlayer.OnInfoListener, SurfaceHolder.Callback, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener {

    public static final int MODO_UNA_QUALIDADES = 10;
    public static final int MODO_DOS_QUALIDADES = 11;
    public static final int MODO_TRES_QUALIDADES = 12;

    private VideoView mVideoView;
    private MediaController mMediaController;
    private TextView mMediaDescription;
    private String urlVideo;
    private int videoDuration;
    private ProgressBar spinner;
    private String urlHost;

    private ArrayList<String> urls;
    private float duracio;

    //new..

    private boolean needResume;

    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;

    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private String path;
    private Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        extras = intent.getExtras();

        if (extras.isEmpty())
            return;
/*
        mPreview = (SurfaceView) findViewById(R.id.surface);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBA_8888);
        //---
*/      String ext =extras.getString(MainActivity.VIDEO);
        urlHost = extras.getString(MainActivity.SPHOSTURL);
        //---
        path = "http://"+urlHost+"/"+ext.split("\"")[2];

        urlVideo = ext.split("\"")[2]; //URL de l'arxiu m3u8
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        urls = new ArrayList<String>();

        mVideoView = (VideoView)findViewById(R.id.video);
        mMediaController = new MediaController(this);
        mMediaDescription = (TextView)findViewById(R.id.mediaInfo);

        mMediaController.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mMediaController);


        mMediaDescription.setText(ext.split("\"")[1]);
        downloadVideo(urlVideo);
    }

    public void playVideo(){
        doCleanUp();
        try{
            // Create a new media player and set the listeners
            mMediaPlayer = new MediaPlayer(this);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            setVolumeControlStream(AudioManager.STREAM_MUSIC);

        } catch (Exception e) {
            Log.e("HOLAAA ", "error: " + e.getMessage(), e);
        }

    }

    /**
     * Ens descarragarem el video i la descripció que hi hagi i ho colocarem on calgui..
     *
     * @param urlVideo
     */
    private void downloadVideo(String urlVideo) {
        DownloadVideosTasks dvt = new DownloadVideosTasks();
        spinner.setVisibility(View.VISIBLE);
        mVideoView.setVideoURI(Uri.parse("http://"+urlHost+"/"+urlVideo));
        mVideoView.requestFocus();
        mVideoView.start();
        dvt.execute("http://"+urlHost+"/"+urlVideo);
    }

    //De moment implementació bàsica, només una resolució, no idiomes, no coses rares..

    private void parsingURLs(String m3u8) {
        String urlsfile = m3u8;
        String[] files = urlsfile.split("\n");
        String tag="";
        for (String s : files){

            if (s.length()>0 && s.substring(0,1).equals("#")){
                tag = s.substring(1);
                if (tag.contains("EXT-X-TARGETDURATION:")){
                    videoDuration = Integer.parseInt(tag.substring(21));
                }
            }else if (s.length()>0 && !tag.equals("")){
                urls.add(s);
                duracio = Float.parseFloat(tag.substring(7, tag.length()-1));
            }
        }

        spinner.setVisibility(View.GONE);

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

    /**
     * Called to indicate an info or a warning.
     *
     * @param mp    the MediaPlayer the info pertains to.
     * @param what  the type of info or warning.
     *
     * @param extra an extra code, specific to the info. Typically
     *              implementation dependent.
     * @return True if the method handled the info, false if it didn't.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the info to be discarded.
     */
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what){
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (isPlaying()){
                    pause();
                    needResume=true;
                }
                spinner.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (needResume){
                    start();
                }
                spinner.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                //extra es la qualidad..
                break;
        }
        return true;
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.d("LELE", "surfaceChanged called");

    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.d("LELE", "surfaceDestroyed called");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("LELE", "surfaceCreated called");
        playVideo();

    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
        // Log.d(TAG, "onBufferingUpdate percent:" + percent);

    }

    public void onCompletion(MediaPlayer arg0) {
        Log.d("LELE", "onCompletion called");
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.v("LELE", "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e("LELE", "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d("LELE", "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        Log.v("LELE ", "startVideoPlayback");
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        mMediaPlayer.start();
    }

    public class DownloadVideosTasks extends AsyncTask<String, Void, String> {
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param urls The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String doInBackground(String[] urls) {
            try{

                return download(urls[0]);
            }catch(IOException e){
                return "Unable to Conect";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            parsingURLs(s);
        }

        private String download(String s) throws IOException{
            InputStream is = null;

            try {
                Log.d("DOWNLOADER", s.toString());

                URL url = new URL(s);

                HttpURLConnection huc = (HttpURLConnection) url.openConnection();

                huc.setReadTimeout(5000);
                huc.setConnectTimeout(60000);
                huc.setRequestMethod("GET");
                huc.setDoInput(true);

                Log.d("DOWNLOADER", "Apunto de hacer huc.connect()..");

                huc.connect();
                int response = huc.getResponseCode();

                Log.d("DOWNLOADER", "The response is: " + response);
                is = huc.getInputStream();
                Scanner scanner = new Scanner(is);
                String aux="";
                do {
                    aux+=scanner.nextLine()+"\n";
                }while (scanner.hasNextLine());
                Log.d("DOWNLOADER ", aux);
                return aux;
            }finally {
                if (is!=null)
                    is.close();
            }
        }
    }
}
