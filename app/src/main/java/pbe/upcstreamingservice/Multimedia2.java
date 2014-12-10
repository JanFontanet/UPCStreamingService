package pbe.upcstreamingservice;

import android.content.Intent;
//import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class Multimedia2 extends ActionBarActivity{

    private static final int QUALITY_LOW = 501;
    private static final int QUALITY_MID = 502;
    private static final int QUALITY_HI = 503;
    private static final int AUDIO_ONLY = 550;

    private Bundle extras;
    private String urlHost;
    private String path;
    private String urlVideo;
    private ProgressBar spinner;

    private ArrayList<String> urls;
    private ArrayList<String> lUrls;
    private ArrayList<String> mUrls;
    private ArrayList<String> hUrls;
    private ArrayList<String> audio;

    private VideoView mVideoView;
    private MediaController mMediaController;
    private TextView mMediaDescription;
    private int videoDuration;
    private long duracio;

    File defaultPath = Environment.getExternalStorageDirectory();

    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        extras = intent.getExtras();

        if (extras.isEmpty())
            return;

        lUrls= new ArrayList<String>();
        mUrls = new ArrayList<String>();
        hUrls = new ArrayList<String>();
        audio = new ArrayList<String>();

        String ext =extras.getString(MainActivity.VIDEO);
        urlHost = extras.getString(MainActivity.SPHOSTURL);
        //---
        path = "http://"+urlHost+"/"+ext.split("\"")[2];

        urlVideo = ext.split("\"")[2]; //URL de l'arxiu m3u8
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        urls = new ArrayList<String>();

        mVideoView = (VideoView)findViewById(R.id.video);
        mMediaController = new MediaController(this);
        mMediaDescription = (TextView)findViewById(R.id.descripcio);

        mMediaController.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mMediaController);


        mMediaDescription.setText(ext.split("\"")[1]);
        downloadVideo(urlVideo);
    }

    private void downloadVideo(String urlVideo) {
        DownloadVideosTasks dvt = new DownloadVideosTasks();
        spinner.setVisibility(View.VISIBLE);
        /*
        mVideoView.setVideoURI(Uri.parse("http://" + urlHost + "/" + urlVideo));
        mVideoView.requestFocus();
        mVideoView.start();
        */
        dvt.execute("http://"+urlHost+"/"+urlVideo);
    }

    private void parsingURLs(String m3u8, int quality) {
        String[] files = m3u8.split("\n");
        String tag="";
        int q=-1;
        boolean tornarhi=false;
        for (String s : files){

            if (s.length()>0 && s.substring(0,1).equals("#")){
                tag = s.substring(1);
                if (tag.contains("EXT-X-TARGETDURATION:")){
                    videoDuration = Integer.parseInt(tag.substring(21));
                }else if(tag.contains("EXT-X-STREAM-INF:")){  // identifies a media URI as a Playlist file containing a multimedia presentation and provides information about that presentation.
                    if (tag.contains("BANDWIDTH=") && !tag.contains("CODECS=")){
                        tornarhi=true;
                    }else if (tag.contains("CODECS=")){
                        tornarhi=true;
                        q=AUDIO_ONLY -1;
                    }
                }else if (tag.contains("EXT-X-ENDLIST"))
                    break;
            }else if (s.length()>0 && tag.contains("EXTINF:")){
                if (quality==QUALITY_LOW){
                    lUrls.add(s);
                }else if (quality==QUALITY_MID){
                    mUrls.add(s);
                }else if (quality==QUALITY_HI){
                    hUrls.add(s);
                }else if(quality==AUDIO_ONLY){
                    audio.add(s);
                }else {
                    lUrls.add(s);
                }
                duracio = Long.parseLong(tag.substring(7, tag.length() - 1));
            }else if(tag.contains("EXT-X-STREAM-INF:")){
                if(tornarhi){
                    q++;
                    parsingURLs(s, 500+q);
                }

            }
        }
        //spinner.setVisibility(View.GONE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multimedia, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                NavUtils.navigateUpTo(this, upIntent);
        }
        return super.onOptionsItemSelected(item);
    }

private void initVideo() {
    spinner.setVisibility(View.GONE);
    if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(Multimedia2.this))
        return;

    mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);
    mVideoView.setVideoPath(defaultPath+"/temp"+currentIndex+".ts");
    mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //delete file
            currentIndex++;
            if (currentIndex<lUrls.size()){
                playNext(mediaPlayer);
            }
        }
    });

}

    private void playNext(MediaPlayer mediaPlayer) {
        mediaPlayer.reset();
        try{
            mediaPlayer.setDataSource(defaultPath+"/temp"+currentIndex+".ts");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
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
            parsingURLs(s, 0);
            DownloadTS dTS = new DownloadTS();

            dTS.execute(lUrls);
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

    public class DownloadTS extends AsyncTask<ArrayList<String>, Integer, Void>{


        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0]==0){
                initVideo();
            }
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(ArrayList<String>... params) {
            for (int i=0; i<params[0].size(); i++){

                download(params[0].get(i), defaultPath, "temp" + i + ".ts");
                publishProgress(i);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        private void download(String tsUrl, File path, String fileName) {

            FileOutputStream fos;
            InputStream is = null;
            try{
                File file = new File(path, fileName);
                fos = new FileOutputStream(file);
                is = new URL(tsUrl).openConnection().getInputStream();

                byte[] buffer = new byte[999999];
                int leido = is.read(buffer);
                while (leido>0){
                    fos.write(buffer, 0, leido);
                    leido = is.read(buffer);
                }
                is.close();
                fos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
