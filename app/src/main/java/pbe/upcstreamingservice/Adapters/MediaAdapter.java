package pbe.upcstreamingservice.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pbe.upcstreamingservice.MainActivity;
import pbe.upcstreamingservice.Multimedia;
import pbe.upcstreamingservice.R;

/**
 * Created by janfontanetcastillo on 1/11/14.
 */
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private String[] content;

    public MediaAdapter (String[] mContent){
        content = mContent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View title = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row, viewGroup, false);


        ViewHolder vh = new ViewHolder(title);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String content = this.content[i];
        viewHolder.titol.setText(content.split("$")[0]);
        viewHolder.subTitol.setText(content.split("$")[1]);
        viewHolder.itemView.setTag(content);
    }

    @Override
    public int getItemCount() {
        return content.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView titol;
        public TextView subTitol;
        public ViewHolder(View v) {
            super(v);
            titol = (TextView)v.findViewById(R.id.title);
            subTitol = (TextView)v.findViewById(R.id.subtitle);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), Multimedia.class);
            i.putExtra(MainActivity.VIDEO, (String)v.getTag());
            v.getContext().startActivity(i);
        }
    }

}
