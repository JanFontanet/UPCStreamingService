package pbe.upcstreamingservice.DownloaderTask;

import android.os.AsyncTask;

import java.io.IOException;

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

    private String download(String url) throws IOException{
        return null;
    }
}
