package pbe.upcstreamingservice;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import pbe.upcstreamingservice.Adapters.MediaAdapter;


public class MainActivity extends ActionBarActivity {

    public static final String SPTITLE = "titulo";
    public static final String SPHOSTURL= "hosturl";

    public static String VIDEO = "video";

    private ListView mListView;
    private MediaAdapter mAdapter;

    private Button mSyncButton;

    private String hostURI;

    private ProgressBar spinner;

    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView)findViewById(R.id.my_recycler_view);
        mSyncButton = (Button)findViewById(R.id.syncbtn);
        mSyncButton.setVisibility(View.GONE);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        /*
        mAdapter = new MediaAdapter(this, null);  //String[] amb -> {"Titol $ Subtitol $ url m3u", ... }
        mListView.setAdapter(mAdapter);
        */
        sp= getSharedPreferences(SPTITLE, MODE_PRIVATE);
        hostURI=sp.getString(SPHOSTURL, "");

        selectNetwork();

    }

    private void loadList(String[] s){
        if (s[0].equals("1")){
            mAdapter = new MediaAdapter(this, new String[]{});
            mListView.setAdapter(mAdapter);
            mostrarDialog(getString(R.string.impCon));
        }else {
            String al = "";
            for (String a : s) {
                al += a + "\"-\"" + a + "¬¬";
            }
            String[] aux = al.split("¬¬");

            mAdapter = new MediaAdapter(this, aux);  //String[] amb -> {"Titol $ Subtitol $ url m3u", ... }
            mListView.setAdapter(mAdapter);
            spinner.setVisibility(View.GONE);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, Multimedia2.class);
                    intent.putExtra(SPHOSTURL, hostURI);
                    intent.putExtra(VIDEO, mAdapter.getItem(position));
                    startActivity(intent);
                }
            });
        }

    }
    
    private void selectNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isConnected() && hostURI.equals("")) {
            mostrarDialog(getString(R.string.dirHost));
        } else {
            Toast.makeText(this, getString(R.string.conectWifiHost), Toast.LENGTH_SHORT).show();
            DownloadWebInfo dww = new DownloadWebInfo();
            URL url = null;
            try {
                url = new URL("http://"+hostURI+"/");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            spinner.setVisibility(View.VISIBLE);
            dww.execute(url);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_host:
                mostrarDialog(getResources().getString(R.string.dirHost));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialog(String s) {
        final Dialog dialog  = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ssid);
        dialog.setCancelable(false);

        final Context c = this;

        TextView title = (TextView)dialog.findViewById(R.id.title);
        final EditText host = (EditText)dialog.findViewById(R.id.host);
        Button ok = (Button)dialog.findViewById(R.id.ok);
        Button re = (Button)dialog.findViewById(R.id.reintentar);

        title.setText(s);
        host.setHint(getString(R.string.host));
        host.setText(hostURI);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!host.getText().toString().equals("")) {
                    dialog.dismiss();
                    hostURI=(host.getText().toString());
                    sp.edit().putString(SPHOSTURL, hostURI).commit();

                    DownloadWebInfo dww = new DownloadWebInfo();
                    URL url = null;
                    try {
                        url = new URL("http://"+hostURI+"/");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    spinner.setVisibility(View.VISIBLE);
                    dww.execute(url);
                }else{
                    Toast.makeText(c, getString(R.string.insertHost), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (s.equals(getResources().getString(R.string.dirHost))){
            re.setVisibility(View.GONE);
        }else{
            re.setVisibility(View.VISIBLE);
        }

        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!host.getText().toString().equals("")) {
                    dialog.dismiss();
                    hostURI=(host.getText().toString());
                    sp.edit().putString(SPHOSTURL, hostURI).commit();

                    DownloadWebInfo dww = new DownloadWebInfo();
                    URL url = null;
                    try {
                        url = new URL("http://"+hostURI+"/");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    spinner.setVisibility(View.VISIBLE);
                    dww.execute(url);
                }else{
                    Toast.makeText(c, getString(R.string.insertHost), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }


    private class DownloadWebInfo extends AsyncTask<URL, Void, String[]> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param url The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String[] doInBackground(URL[] url) {
            try {
                Log.d("DoInBackground ", "entrado a download"+url[0]);

                return download(url[0]);
            } catch (IOException e) {
                return new String[]{"1"};
            }
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
             loadList(s);//aqui s'omplira la llista..
        }

        private String[] download(URL s) throws IOException{
            InputStream is = null;

            try{
                Log.d("DOWNLOADER", s.toString());

                HttpURLConnection huc = (HttpURLConnection)s.openConnection();

                huc.setReadTimeout(5000);
                huc.setConnectTimeout(60000);
                huc.setRequestMethod("GET");
                huc.setDoInput(true);

                Log.d("DOWNLOADER", "Apunto de hacer huc.connect()..");

                huc.connect();
                int response = huc.getResponseCode();

                Log.d("DOWNLOADER", "The response is: " + response);
                is = huc.getInputStream();

                // Convert the InputStream into a string
                String[] contentAsString = readIt(is);
                return contentAsString;
            }finally {
                Log.d("DOWNLOADER", "FINALLY");
                if (is!=null)
                    is.close();
            }
        }
        private String[] readIt(InputStream is) throws IOException, UnsupportedEncodingException {
            Scanner scan = new Scanner(is);
            String toReturn="";
            while (scan.hasNextLine()){
                String linea = scan.nextLine();
                String aux="";
                if (linea.contains("<a href=")){
                    Log.d("DOWNLOADER ", "Hi ha <a href=");
                    int contador=0;
                    for (int i=0; i<linea.length();i++){
                        Boolean primer = false;
                        if (linea.substring(i,i+1).equals("\"") && contador==0){
                            contador++;
                            primer = true;
                        }else if(linea.substring(i,i+1).equals("\"") && contador>0){
                            break;
                        }
                        if (contador==1 && !primer){
                            aux+=linea.substring(i,i+1);
                        }
                    }
                    if (aux.substring(aux.length()-4, aux.length()).equals("m3u8"))
                        toReturn+=aux+"¬¬";
                }

            }
            return toReturn.split("¬¬");
        }
    }
}
