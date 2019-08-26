package com.smnetinfo.savememory.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smnetinfo.savememory.EditWillActivity;
import com.smnetinfo.savememory.R;
import com.smnetinfo.savememory.extras.WebConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by siva on 16/10/17.
 */

public class WillListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements WebConstants {

    private JSONArray item;
    private Context context;

    public WillListRecyclerAdapter(Context context, JSONArray item) {
        this.item = item;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_will, parent, false);
        return new RecyclerViewHolders(layoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        final RecyclerViewHolders holder = (RecyclerViewHolders) viewHolder;

        try {
            final JSONObject jsonObject = item.getJSONObject(position);

            holder.itemListMyWillTitleTV.setText(jsonObject.getString(TITLE));
            holder.itemListMyWillDescriptionTV.setText(jsonObject.getString(DESCRIPTION));
            holder.itemListMyWillCreatedTV.setText(convertTime(jsonObject.getString(CREATED_AT)));

            holder.itemListMyWillMainLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, EditWillActivity.class).putExtra(DATA, jsonObject.toString()));
                }
            });

            switch (jsonObject.getInt(TYPE)) {
                case POST_TYPE_TEXT:
                    holder.itemListMyWillImageIV.setVisibility(View.GONE);
                    break;
                case POST_TYPE_IMAGE:
                    holder.itemListMyWillImageIV.setVisibility(View.VISIBLE);
                    Picasso.with(context)
                            .load(jsonObject.getString(CONTENT_URL))
                            .into(holder.itemListMyWillImageIV);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return item.length();
    }

    public void notifyList(JSONArray item) {
        this.item = item;
        notifyDataSetChanged();
    }

    private String convertTime(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        Date date;
        String returnTime = "";
        try {
            date = simpleDateFormat.parse(time);
            returnTime = timeFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!returnTime.equals("")) {
            return returnTime;
        } else {
            return time;
        }
    }

    private Bitmap convertToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str.substring(base64Str.indexOf(",") + 1), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private class RecyclerViewHolders extends RecyclerView.ViewHolder {

        LinearLayout itemListMyWillMainLL;
        AppCompatImageView itemListMyWillImageIV;
        TextView itemListMyWillTitleTV, itemListMyWillDescriptionTV, itemListMyWillCreatedTV;

        RecyclerViewHolders(View itemView) {
            super(itemView);

            itemListMyWillMainLL = itemView.findViewById(R.id.itemListMyWillMainLL);
            itemListMyWillTitleTV = itemView.findViewById(R.id.itemListMyWillTitleTV);
            itemListMyWillImageIV = itemView.findViewById(R.id.itemListMyWillImageIV);
            itemListMyWillCreatedTV = itemView.findViewById(R.id.itemListMyWillCreatedTV);
            itemListMyWillDescriptionTV = itemView.findViewById(R.id.itemListMyWillDescriptionTV);

        }
    }

}