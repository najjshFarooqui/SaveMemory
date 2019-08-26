package com.smnetinfo.savememory.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smnetinfo.savememory.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Siva on 10/8/16.
 */
@SuppressLint({"SetTextI18n", "InflateParams"})
public class CountrySpinnerAdapter extends BaseAdapter {

    Context context;
    private JSONArray item;

    public CountrySpinnerAdapter(Context context, JSONArray item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.length();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.item_country_spinner, null);
        }

        TextView itemCountryCodeTextView = convertView.findViewById(R.id.itemCountryCodeTextView);
        TextView itemCountryNameTextView = convertView.findViewById(R.id.itemCountryNameTextView);

        ImageView itemCountryFlagImageView = convertView.findViewById(R.id.itemCountryFlagImageView);

        try {
            JSONObject jsonObject = item.getJSONObject(i);

            //String flag = "https://www.countryflags.io/"+countryModel.getCode().toLowerCase()+"/flat/64.png";
            String flag = "https://www.countryflags.io/" + jsonObject.getString("code").toLowerCase() + "/shiny/64.png";

            Picasso.with(context)
                    .load(flag)
                    .into(itemCountryFlagImageView);

            //itemCountryNameTextView.setText(""+countryModel.getCode());
            itemCountryCodeTextView.setText("+" + jsonObject.getString("dial_code"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;

    }

}