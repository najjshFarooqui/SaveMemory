package com.smnetinfo.savememory.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.smnetinfo.savememory.R;
import com.smnetinfo.savememory.extras.WebConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by siva on 16/10/17.
 */

public class ImagesGridRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements WebConstants {

    private int size;
    private boolean isEdit;
    private JSONArray item;
    private Context context;
    private boolean[] isSelected;
    private OnClickListener onClickListener;

    public ImagesGridRecyclerAdapter(Context context, JSONArray item, int size, boolean isEdit) {
        this.item = item;
        this.size = size;
        this.isEdit = isEdit;
        this.context = context;
        this.isSelected = new boolean[item.length()];
        Arrays.fill(isSelected, false);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_images, parent, false);
        return new RecyclerViewHolders(layoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        final RecyclerViewHolders holder = (RecyclerViewHolders) viewHolder;

        try {
            final JSONObject jsonObject = item.getJSONObject(position);

            if (isEdit) {
                holder.itemGridImagesEditCV.setVisibility(View.VISIBLE);
            } else {
                holder.itemGridImagesEditCV.setVisibility(View.GONE);
            }

            Picasso.with(context)
                    .load(jsonObject.getString(CONTENT_URL))
                    .into(holder.itemGridImagesIV);

            holder.itemGridImagesMainRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isEdit) {
                        if (isSelected[viewHolder.getAdapterPosition()]) {
                            isSelected[viewHolder.getAdapterPosition()] = false;
                            holder.itemGridImagesEditCV.setCardBackgroundColor(context.getResources().getColor(R.color.colorBlackAlpha));
                            onClickListener.onDeselect(jsonObject, viewHolder.getAdapterPosition());
                        } else {
                            isSelected[viewHolder.getAdapterPosition()] = true;
                            holder.itemGridImagesEditCV.setCardBackgroundColor(context.getResources().getColor(R.color.kakaoGreen));
                            onClickListener.onSelect(jsonObject, viewHolder.getAdapterPosition());
                        }
                    } else {
                        onClickListener.onClick(jsonObject, viewHolder.getAdapterPosition());
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

    public void enableEdit(boolean isEdit) {
        this.isEdit = isEdit;
        if (!isEdit) {
            Arrays.fill(isSelected, false);
        }
        notifyDataSetChanged();
    }

    public void notifyList(JSONArray item) {
        this.item = item;
        this.isSelected = new boolean[item.length()];
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(JSONObject jsonObject, int position);

        void onSelect(JSONObject jsonObject, int position);

        void onDeselect(JSONObject jsonObject, int position);
    }

    private class RecyclerViewHolders extends RecyclerView.ViewHolder {

        CardView itemGridImagesEditCV;
        RelativeLayout itemGridImagesMainRL;
        AppCompatImageView itemGridImagesIV;

        RecyclerViewHolders(View itemView) {
            super(itemView);

            itemGridImagesIV = itemView.findViewById(R.id.itemGridImagesIV);
            itemGridImagesEditCV = itemView.findViewById(R.id.itemGridImagesEditCV);
            itemGridImagesMainRL = itemView.findViewById(R.id.itemGridImagesMainRL);

            itemGridImagesMainRL.setLayoutParams(new LinearLayout.LayoutParams(size, size));

        }
    }

}