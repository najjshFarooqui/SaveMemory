package com.smnetinfo.savememory.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

public class ChooseBeneficiaryListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements WebConstants {

    private JSONArray item;
    private Context context;
    private OnClickListener onClickListener;

    public ChooseBeneficiaryListRecyclerAdapter(Context context, JSONArray item) {
        this.item = item;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_beneficiary_choose, parent, false);
        return new RecyclerViewHolders(layoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        final RecyclerViewHolders holder = (RecyclerViewHolders) viewHolder;

        try {
            final JSONObject jsonObject = item.getJSONObject(position);

            holder.itemListMyBeneficiaryChooseCB.setChecked(jsonObject.getBoolean(STATUS));

            holder.itemListMyBeneficiaryChooseNameTV.setText(jsonObject.getString(FIRST_NAME) + " " + jsonObject.getString(LAST_NAME));

            holder.itemListMyBeneficiaryChooseMainRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (holder.itemListMyBeneficiaryChooseCB.isChecked()) {
                            onClickListener.onDeselected(viewHolder.getAdapterPosition(), jsonObject.getString(ID));
                        } else {
                            onClickListener.onSelected(viewHolder.getAdapterPosition(), jsonObject.getString(ID));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onSelected(int position, String id);

        void onDeselected(int position, String id);
    }

    private class RecyclerViewHolders extends RecyclerView.ViewHolder {

        CheckBox itemListMyBeneficiaryChooseCB;
        TextView itemListMyBeneficiaryChooseNameTV;
        RelativeLayout itemListMyBeneficiaryChooseMainRL;

        RecyclerViewHolders(View itemView) {
            super(itemView);

            itemListMyBeneficiaryChooseCB = itemView.findViewById(R.id.itemListMyBeneficiaryChooseCB);
            itemListMyBeneficiaryChooseNameTV = itemView.findViewById(R.id.itemListMyBeneficiaryChooseNameTV);
            itemListMyBeneficiaryChooseMainRL = itemView.findViewById(R.id.itemListMyBeneficiaryChooseMainRL);

        }
    }

}