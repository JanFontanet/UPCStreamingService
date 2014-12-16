package pbe.upcstreamingservice.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pbe.upcstreamingservice.R;

public class MediaAdapter extends ArrayAdapter<String> {

    private String[] content;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param objects  The objects to represent in the ListView.
     */
    public MediaAdapter(Context context, String[] objects) {
        super(context, R.layout.list_row, objects);
        content = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String aux = content[position];
        ViewHolder vh;
        if (convertView==null){
            vh = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row, parent ,false);
            vh.titol = (TextView)convertView.findViewById(R.id.title);
            vh.subTitol = (TextView)convertView.findViewById(R.id.subtitle);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder)convertView.getTag();
        }

        vh.titol.setText(aux.split("\"")[0]);
        vh.subTitol.setText(aux.split("\"")[1]);

        return convertView;
    }

    @Override
    public String getItem(int position) {
        return content[position];
    }

    public static class ViewHolder{
        // each data item is just a string in this case
        public TextView titol;
        public TextView subTitol;
    }

}
