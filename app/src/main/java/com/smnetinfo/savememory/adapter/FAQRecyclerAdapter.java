package com.smnetinfo.savememory.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smnetinfo.savememory.R;
import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siva on 16/10/17.
 */

public class FAQRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements WebConstants {

    private JSONArray item;
    private Context context;

    public FAQRecyclerAdapter(Context context, JSONArray item) {
        this.item = item;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_faq, parent, false);
        return new RecyclerViewHolders(layoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        final RecyclerViewHolders holder = (RecyclerViewHolders) viewHolder;

        try {
            final JSONObject jsonObject = item.getJSONObject(position);

            holder.itemListFAQTitleTV.setText(jsonObject.getString(TITLE));
            holder.itemListFAQDescriptionTV.setText(jsonObject.getString(DESCRIPTION));

            holder.itemListFAQTitleRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.itemListFAQDescriptionTV.getVisibility() == View.VISIBLE) {
                        holder.itemListFAQDescriptionTV.setVisibility(View.GONE);
                    } else {
                        holder.itemListFAQDescriptionTV.setVisibility(View.VISIBLE);
                    }
                }
            });

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

    private class RecyclerViewHolders extends RecyclerView.ViewHolder {

        RelativeLayout itemListFAQTitleRL;
        TextView itemListFAQTitleTV, itemListFAQDescriptionTV;

        RecyclerViewHolders(View itemView) {
            super(itemView);

            itemListFAQTitleRL = itemView.findViewById(R.id.itemListFAQTitleRL);
            itemListFAQTitleTV = itemView.findViewById(R.id.itemListFAQTitleTV);
            itemListFAQDescriptionTV = itemView.findViewById(R.id.itemListFAQDescriptionTV);

        }
    }

}