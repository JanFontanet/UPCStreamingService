package pbe.upcstreamingservice.DownloaderTask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by janfontanetcastillo on 2/11/14.
 */
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
                aux+=scanner.nextLine();
            }while (scanner.hasNextLine());
            return aux;
        }finally {
            if (is!=null)
                is.close();
        }
    }
}
