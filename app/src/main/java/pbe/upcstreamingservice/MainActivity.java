package pbe.upcstreamingservice;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import pbe.upcstreamingservice.Adapters.MediaAdapter;


public class MainActivity extends ActionBarActivity {

    public static String VIDEO = "video";

    private RecyclerView mListView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mManager;

    private Button mSyncButton;

    private String hostURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (RecyclerView)findViewById(R.id.my_recycler_view);
        mSyncButton = (Button)findViewById(R.id.syncbtn);
        mSyncButton.setVisibility(View.GONE);

        mListView.setHasFixedSize(false);

        // use a linear layout manager
        mManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mManager);

        selectNetwork();

    }

    private void loadList(){
        mAdapter = new MediaAdapter(new String[]{"Lorem Ipsum$Lorem ipsum ad his scripta blandit partiendo, eum fastidii accumsan euripidis in, eum liber hendrerit an. Qui ut wisi vocibus suscipiantur, quo dicit ridens inciderint id. Quo mundi lobortis reformidans eu, legimus senserit definiebas an eos. Eu sit tincidunt incorrupte definitionem, vis mutat affert percipit cu, eirmod consectetuer signiferumque eu per. In usu latine equidem dolores. Quo no falli viris intellegam, ut fugit veritus placerat per.\n" +
                "Ius id vidit volumus mandamus, vide veritus democritum te nec, ei eos debet libris consulatu. No mei ferri graeco dicunt, ad cum veri accommodare. Sed at malis omnesque delicata, usu et iusto zzril meliore. Dicunt maiorum eloquentiam cum cu, sit summo dolor essent te. Ne quodsi nusquam legendos has, ea dicit voluptua eloquentiam pro, ad sit quas qualisque. Eos vocibus deserunt quaestio ei.$http://192.168.1.100/index.html"});  //String[] amb -> {"Titol $ Subtitol $ url m3u", ... }
        mListView.setAdapter(mAdapter);

        mListView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                Log.d("MAIN", "onClick()...");
                if (child!=null){
                    Log.d("MAIN", "onClick()... child!=null..");
                    Intent i = new Intent(MainActivity.this, Multimedia.class);
                    i.putExtra(VIDEO, (String)child.getTag());
                    //unregisterReceiver(receiver);
                    startActivity(i);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });
    }

    private void selectNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isConnected() && hostURI.equals("")) {
            final Dialog dialog  = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_ssid);
            dialog.setCancelable(false);

            final Context c = this;

            TextView title = (TextView)dialog.findViewById(R.id.title);
            final EditText host = (EditText)dialog.findViewById(R.id.host);
            Button ok = (Button)dialog.findViewById(R.id.ok);

            title.setText(R.string.seleccioHost);
            host.setHint(getString(R.string.host));
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!host.getText().toString().equals("")) {
                        dialog.dismiss();
                        hostURI=(host.getText().toString());
                        DownloadWebInfo dww = new DownloadWebInfo();
                        dww.doInBackground(new String[]{"index.html"});

                    }else{
                        Toast.makeText(c, getString(R.string.insertHost), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.show();
        } else {
            Toast.makeText(this, getString(R.string.conectWifiHost), Toast.LENGTH_SHORT).show();
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

        return super.onOptionsItemSelected(item);
    }


    private class DownloadWebInfo extends AsyncTask<String, Void, String> {

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
        protected String doInBackground(String[] url) {
            try {
                return download(url[0]);
            } catch (IOException e) {
                return "Unable to Conect";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
             loadList();//aqui s'omplira la llista..
        }

        private String download(String s) throws IOException{
            InputStream is = null;
            int len = 9999;
            try{
                URL url = new URL(hostURI+"/"+s);
                HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                huc.setReadTimeout(5000);
                huc.setConnectTimeout(10000);
                huc.setRequestMethod("GET");
                huc.setDoInput(false);

                huc.connect();
                int response = huc.getResponseCode();

                Log.d("DOWNLOADER", "The response is: " + response);
                is = huc.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;
            }finally {
                if (is!=null)
                    is.close();
            }
        }

        private String readIt(InputStream is, int len) throws IOException, UnsupportedEncodingException {
            Scanner scan = new Scanner(is);
            String toReturn="";
            while (scan.hasNextLine()){
                String linea = scan.nextLine();
                String aux="";
                if (linea.contains("<a href=")){
                    int contador=0;
                    for (int i=0; i<linea.length();i++){
                        if (linea.substring(i,i).equals("\"") && contador==0){
                            contador=1;
                        }else if(linea.substring(i,i).equals("\"") && contador==0){
                            break;
                        }
                        if (contador==1){
                            aux+=linea.substring(i,i);
                        }
                    }
                    toReturn+=aux+"¬¬";
                }


            }
            return toReturn;
        }
    }
}
