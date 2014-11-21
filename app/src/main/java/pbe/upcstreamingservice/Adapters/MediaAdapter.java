package pbe.upcstreamingservice.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.StringTokenizer;

import pbe.upcstreamingservice.R;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private String[] content;

    public MediaAdapter (String[] mContent){
        content = mContent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View title = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row, viewGroup, false);


        return new ViewHolder(title);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (content!=null) {
            String[] aux = this.content[i].split("$");
            Log.d("MEDIA_ADAPTER ", "aux[0] ==" + aux[0]);
            viewHolder.titol.setText(aux[0]);
            String st = aux[1];
            /*
            StringTokenizer subcontent = new StringTokenizer(this.content[i], "$");
            viewHolder.titol.setText(subcontent.nextToken());
            String st = subcontent.nextToken();
            */
            if (st.length() > 100)
                viewHolder.subTitol.setText(st.substring(0, 100) + "...");
            else
                viewHolder.subTitol.setText(st);

            viewHolder.itemView.setTag(this.content[i]);

        }
    }

    @Override
    public int getItemCount() {
        if (content!=null)
            return content.length;
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public TextView titol;
        public TextView subTitol;
        public ViewHolder(View v) {
            super(v);
            titol = (TextView)v.findViewById(R.id.title);
            subTitol = (TextView)v.findViewById(R.id.subtitle);
        }

    }

}
