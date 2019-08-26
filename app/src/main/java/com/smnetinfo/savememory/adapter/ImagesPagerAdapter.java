package com.smnetinfo.savememory.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.smnetinfo.savememory.R;
import com.smnetinfo.savememory.extras.WebConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Siva on 10-Jul-17.
 */

public class ImagesPagerAdapter extends PagerAdapter implements WebConstants {

    private Context context;
    private JSONArray items;
    private LayoutInflater layoutInflater;

    public ImagesPagerAdapter(Context context, JSONArray items) {
        this.items = items;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.length();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.item_list_images, container, false);
        container.addView(itemView);

        GestureImageView itemListImagesIV = itemView.findViewById(R.id.itemListImagesIV);

        try {
            JSONObject jsonObject = items.getJSONObject(position);
            Picasso.with(context).load(jsonObject.getString(CONTENT_URL)).into(itemListImagesIV);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

}
