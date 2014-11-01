package pbe.upcstreamingservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import pbe.upcstreamingservice.Adapters.MediaAdapter;


public class MainActivity extends ActionBarActivity {

    public static String VIDEO = "video";

    private RecyclerView mListView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mManager;

    private Button mSyncButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (RecyclerView)findViewById(R.id.my_recycler_view);
        mSyncButton = (Button)findViewById(R.id.syncbtn);

        mListView.setHasFixedSize(false);

        // use a linear layout manager
        mManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mManager);

        mAdapter = new MediaAdapter(new String[]{"Lore Ipsum$Lorem ipsum$http://192.168.1.100/index.html"});  //String[] amb -> {"Titol $ Subtitol $ url m3u", ... }
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
                    startActivity(i);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });

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

}
