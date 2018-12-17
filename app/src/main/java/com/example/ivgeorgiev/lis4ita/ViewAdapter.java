package com.example.ivgeorgiev.lis4ita;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ViewAdapter extends PagerAdapter {

    List<String> listWords;
    Context context;
    LayoutInflater layoutInflater;

    public ViewAdapter(List<String> listWords, Context context) {
        this.listWords = listWords;
        this.context = context;

        layoutInflater= LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(listWords.size()==0)
            return 0;

        else return listWords.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view= layoutInflater.inflate(R.layout.activity_card_item,container,false);
        TextView wordView=view.findViewById(R.id.cardTextView);
        wordView.setText(listWords.get(position));
        container.addView(view);
        return view;
    }
}
