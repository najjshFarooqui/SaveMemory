package com.smnetinfo.savememory.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smnetinfo.savememory.R;
import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siva on 16/10/17.
 */

public class BeneficiaryListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements WebConstants {

    private JSONArray item;
    private Context context;
    private OnClickListener onClickListener;

    public BeneficiaryListRecyclerAdapter(Context context, JSONArray item) {
        this.item = item;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_beneficiary, parent, false);
        return new RecyclerViewHolders(layoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        final RecyclerViewHolders holder = (RecyclerViewHolders) viewHolder;

        try {
            final JSONObject jsonObject = item.getJSONObject(position);

            holder.itemListMyBeneficiaryLastNameTV.setText(jsonObject.getString(LAST_NAME));
            holder.itemListMyBeneficiaryFirstNameTV.setText(jsonObject.getString(FIRST_NAME));

            holder.itemListBeneficiaryDetailsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(jsonObject);
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

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(JSONObject jsonObject);
    }

    private class RecyclerViewHolders extends RecyclerView.ViewHolder {

        LinearLayout itemListBeneficiaryDetailsLL;
        TextView itemListMyBeneficiaryFirstNameTV, itemListMyBeneficiaryLastNameTV;

        RecyclerViewHolders(View itemView) {
            super(itemView);

            itemListBeneficiaryDetailsLL = itemView.findViewById(R.id.itemListBeneficiaryDetailsLL);
            itemListMyBeneficiaryLastNameTV = itemView.findViewById(R.id.itemListMyBeneficiaryLastNameTV);
            itemListMyBeneficiaryFirstNameTV = itemView.findViewById(R.id.itemListMyBeneficiaryFirstNameTV);
        }
    }

}