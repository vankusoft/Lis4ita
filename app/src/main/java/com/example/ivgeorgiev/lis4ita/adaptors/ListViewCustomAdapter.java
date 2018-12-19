package com.example.ivgeorgiev.lis4ita.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ivgeorgiev.lis4ita.R;

import java.util.ArrayList;
import java.util.List;

public class ListViewCustomAdapter extends BaseAdapter {

    List<ArrayList<String>> players;
    Context context;
    int layout;

    public ListViewCustomAdapter(List<ArrayList<String>> players, Context context, int layout) {
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

            ArrayList<String> teamPlayers=players.get(position);

            String playersText="";

            for(int i=0;i<teamPlayers.size();i++){
                playersText= playersText + teamPlayers.get(i)+ "\n";
            }

            playersTextView.setText(playersText);
        }

        return convertView;
    }
}
