package com.denisgolubets.dreambook.util;

import java.lang.Exception;import java.lang.Override;import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.denisgolubets.dreambook.R;

public class HomeListAdapter extends ArrayAdapter<HomeItem> implements OnClickListener {
    public Context context;
    public ArrayList<HomeItem> HomeItemList;

    public HomeListAdapter(Context context, int resource, ArrayList<HomeItem> HomeItemLst) {
        super(context, resource, HomeItemLst);
        this.context = context;
        this.HomeItemList = HomeItemLst;
    }

    @Override
    public int getCount() {
        if (HomeItemList != null)
            return HomeItemList.size();

        return 0;
    }

    @Override
    public HomeItem getItem(int position) {
        return HomeItemList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View myConvertView = null;
        try {
            final HomeItem HomeItem = HomeItemList.get(position);
            myConvertView = convertView;
            if (myConvertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                myConvertView = inflater.inflate(R.layout.home_list_item_view, null);
            }
            TextView CatHeader = (TextView) myConvertView.findViewById(R.id.name);
            TextView CatTitle = (TextView) myConvertView.findViewById(R.id.description);

            CatHeader.setText(HomeItem.getHomeItemName());
            CatTitle.setText(HomeItem.getHomeItemDescription());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return myConvertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

}