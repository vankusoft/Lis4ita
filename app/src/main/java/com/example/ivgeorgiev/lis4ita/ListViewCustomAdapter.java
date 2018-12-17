package com.example.ivgeorgiev.lis4ita;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ListViewCustomAdapter extends BaseAdapter {

    List<String> players;
    Context context;
    int layout;

    public ListViewCustomAdapter(List<String> players, Context context, int layout) {
        this.players=players;
        this.context=context;
        this.layout=layout;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){

            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView= layoutInflater.inflate(layout, parent, false);

            TextView playersTextView=convertView.findViewById(R.id.cardViewPlayerText);

            playersTextView.setText(players.get(position));
        }


        return convertView;
    }
}
