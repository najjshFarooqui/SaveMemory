package com.smnetinfo.savememory.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.smnetinfo.savememory.FileWebView;
import com.smnetinfo.savememory.ImagesActivity;
import com.smnetinfo.savememory.R;
import com.smnetinfo.savememory.TextViewMoreActivity;
import com.smnetinfo.savememory.VideoStreamActivity;
import com.smnetinfo.savememory.database.SettingsDataSource;
import com.smnetinfo.savememory.extras.WebConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by siva on 16/10/17.
 */

public class PostListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements WebConstants {

    private Context context;
    private List<JSONObject> item;
    private MediaPlayer mediaPlayer;
    private CountDownTimer countDownTimer;
    private int seconds = 0;
    private int[] audioLength;
    private OnClickListener onClickListener;
    private SettingsDataSource settingsDataSource;
    private int currentAudio = -1;

    public PostListRecyclerAdapter(Context context, List<JSONObject> item) {
        this.item = item;
        this.context = context;
        this.audioLength = new int[item.size()];
        settingsDataSource = SettingsDataSource.sharedInstance(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_posts, parent, false);
        return new RecyclerViewHolders(layoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        final RecyclerViewHolders holder = (RecyclerViewHolders) viewHolder;

        audioLength[position] = 0;
        final JSONObject jsonObject = item.get(position);

        try {
            switch (jsonObject.getInt(TYPE)) {
                case POST_TYPE_VIDEO:
                    holder.itemListPostMainLL.setVisibility(View.VISIBLE);
                    holder.itemListPostContentVideoCV.setVisibility(View.VISIBLE);
                    holder.itemListPostSplitDateRL.setVisibility(View.GONE);
                    holder.itemListPostContentDocCV.setVisibility(View.GONE);
                    holder.documentTv.setVisibility(View.GONE);
                    holder.itemListPostContentTextTV.setVisibility(View.GONE);
                    holder.itemListPostContentAudioCV.setVisibility(View.GONE);
                    holder.itemListPostContentImage4LL.setVisibility(View.GONE);
                    holder.itemListPostContentViewMoreCV.setVisibility(View.GONE);
                    holder.itemListPostVideoThumbIV.setImageBitmap(convertToBitmap(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(THUMB_URL)));
                    holder.itemListPostCreatedAtTV.setText(convertTime(jsonObject.getString(CREATED_AT)));

                    holder.itemListPostVideoThumbIV.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                            return false;
                        }
                    });
                    break;
                case POST_TYPE_TEXT:
                    holder.itemListPostMainLL.setVisibility(View.VISIBLE);
                    holder.itemListPostContentTextTV.setText(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL));
                    holder.itemListPostContentTextTV.setTextSize(settingsDataSource.getFontSize());
                    holder.itemListPostContentTextTV.setVisibility(View.VISIBLE);
                    if (holder.itemListPostContentTextTV.getText().length() < 500) {
                        holder.itemListPostContentTextTV.setSingleLine(false);
                        holder.itemListPostContentViewMoreCV.setVisibility(View.GONE);
                    } else {
                        holder.itemListPostContentTextTV.setLines(10);
                        holder.itemListPostContentViewMoreCV.setVisibility(View.VISIBLE);
                    }

                    holder.itemListPostSplitDateRL.setVisibility(View.GONE);
                    holder.itemListPostContentDocCV.setVisibility(View.GONE);
                    holder.documentTv.setVisibility(View.GONE);
                    holder.itemListPostContentVideoCV.setVisibility(View.GONE);
                    holder.itemListPostContentAudioCV.setVisibility(View.GONE);
                    holder.itemListPostContentImage4LL.setVisibility(View.GONE);
                    holder.itemListPostCreatedAtTV.setText(convertTime(jsonObject.getString(CREATED_AT)));
                    holder.itemListPostContentViewMoreTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, TextViewMoreActivity.class).putExtra(DATA, jsonObject.toString()));
                        }
                    });
                    break;
                case POST_TYPE_IMAGE:
                    holder.itemListPostMainLL.setVisibility(View.VISIBLE);
                    holder.itemListPostSplitDateRL.setVisibility(View.GONE);
                    holder.itemListPostContentDocCV.setVisibility(View.GONE);
                    holder.documentTv.setVisibility(View.GONE);
                    holder.itemListPostContentTextTV.setVisibility(View.GONE);
                    holder.itemListPostContentViewMoreCV.setVisibility(View.GONE);
                    holder.itemListPostContentVideoCV.setVisibility(View.GONE);
                    holder.itemListPostContentAudioCV.setVisibility(View.GONE);
                    if (jsonObject.getJSONArray(CONTENT_ARRAY).length() == 1) {
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage41IV);

                        holder.itemListPostImage41IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage42CV.setVisibility(View.GONE);
                        holder.itemListPostImage43CV.setVisibility(View.GONE);
                        holder.itemListPostImage44CV.setVisibility(View.GONE);
                        holder.itemListPostImage45CV.setVisibility(View.GONE);
                        holder.itemListPostImage46CV.setVisibility(View.GONE);
                        holder.itemListPostImage47CV.setVisibility(View.GONE);
                        holder.itemListPostImage48CV.setVisibility(View.GONE);
                        holder.itemListPostMultipleImage4TV.setVisibility(View.GONE);
                        holder.itemListPostContentImage4LL.setVisibility(View.VISIBLE);
                    } else if (jsonObject.getJSONArray(CONTENT_ARRAY).length() == 2) {
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage41IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(1).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage42IV);

                        holder.itemListPostImage41IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage42IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage43CV.setVisibility(View.GONE);
                        holder.itemListPostImage44CV.setVisibility(View.GONE);
                        holder.itemListPostImage45CV.setVisibility(View.GONE);
                        holder.itemListPostImage46CV.setVisibility(View.GONE);
                        holder.itemListPostImage47CV.setVisibility(View.GONE);
                        holder.itemListPostImage48CV.setVisibility(View.GONE);
                        holder.itemListPostImage42CV.setVisibility(View.VISIBLE);
                        holder.itemListPostMultipleImage4TV.setVisibility(View.GONE);
                        holder.itemListPostContentImage4LL.setVisibility(View.VISIBLE);
                    } else if (jsonObject.getJSONArray(CONTENT_ARRAY).length() == 3) {
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage41IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(1).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage42IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(2).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage43IV);

                        holder.itemListPostImage41IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage42IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage43IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage44CV.setVisibility(View.GONE);
                        holder.itemListPostImage45CV.setVisibility(View.GONE);
                        holder.itemListPostImage46CV.setVisibility(View.GONE);
                        holder.itemListPostImage47CV.setVisibility(View.GONE);
                        holder.itemListPostImage48CV.setVisibility(View.GONE);
                        holder.itemListPostImage42CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage43CV.setVisibility(View.VISIBLE);
                        holder.itemListPostMultipleImage4TV.setVisibility(View.GONE);
                        holder.itemListPostContentImage4LL.setVisibility(View.VISIBLE);
                    } else if (jsonObject.getJSONArray(CONTENT_ARRAY).length() == 4) {
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage41IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(1).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage42IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(2).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage43IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(3).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage44IV);

                        holder.itemListPostImage41IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage42IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage43IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage44IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage45CV.setVisibility(View.GONE);
                        holder.itemListPostImage46CV.setVisibility(View.GONE);
                        holder.itemListPostImage47CV.setVisibility(View.GONE);
                        holder.itemListPostImage48CV.setVisibility(View.GONE);
                        holder.itemListPostImage44CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage42CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage43CV.setVisibility(View.VISIBLE);
                        holder.itemListPostMultipleImage4TV.setVisibility(View.GONE);
                        holder.itemListPostContentImage4LL.setVisibility(View.VISIBLE);
                    } else if (jsonObject.getJSONArray(CONTENT_ARRAY).length() == 5) {
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage41IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(1).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage42IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(2).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage43IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(3).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage44IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(4).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage45IV);

                        holder.itemListPostImage41IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage42IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage43IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage44IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage45IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage46CV.setVisibility(View.GONE);
                        holder.itemListPostImage47CV.setVisibility(View.GONE);
                        holder.itemListPostImage48CV.setVisibility(View.GONE);
                        holder.itemListPostImage45CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage44CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage42CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage43CV.setVisibility(View.VISIBLE);
                        holder.itemListPostMultipleImage4TV.setVisibility(View.GONE);
                        holder.itemListPostContentImage4LL.setVisibility(View.VISIBLE);
                    } else if (jsonObject.getJSONArray(CONTENT_ARRAY).length() == 6) {
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage41IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(1).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage42IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(2).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage43IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(3).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage44IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(4).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage45IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(5).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage46IV);

                        holder.itemListPostImage41IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage42IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage43IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage44IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage45IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage46IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage47CV.setVisibility(View.GONE);
                        holder.itemListPostImage48CV.setVisibility(View.GONE);
                        holder.itemListPostImage46CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage45CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage44CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage42CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage43CV.setVisibility(View.VISIBLE);
                        holder.itemListPostMultipleImage4TV.setVisibility(View.GONE);
                        holder.itemListPostContentImage4LL.setVisibility(View.VISIBLE);
                    } else if (jsonObject.getJSONArray(CONTENT_ARRAY).length() == 7) {
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage41IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(1).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage42IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(2).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage43IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(3).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage44IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(4).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage45IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(5).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage46IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(6).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage47IV);

                        holder.itemListPostImage41IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage42IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage43IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage44IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage45IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage46IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage47IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage48CV.setVisibility(View.GONE);
                        holder.itemListPostImage42CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage43CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage44CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage45CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage46CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage47CV.setVisibility(View.VISIBLE);
                        holder.itemListPostMultipleImage4TV.setVisibility(View.GONE);
                        holder.itemListPostContentImage4LL.setVisibility(View.VISIBLE);
                    } else if (jsonObject.getJSONArray(CONTENT_ARRAY).length() == 8) {
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage41IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(1).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage42IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(2).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage43IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(3).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage44IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(4).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage45IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(5).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage46IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(6).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage47IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(7).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage48IV);

                        holder.itemListPostImage41IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage42IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage43IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage44IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage45IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage46IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage47IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage48IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage42CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage43CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage44CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage45CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage46CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage47CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage48CV.setVisibility(View.VISIBLE);
                        holder.itemListPostMultipleImage4TV.setVisibility(View.GONE);
                        holder.itemListPostContentImage4LL.setVisibility(View.VISIBLE);
                    } else if (jsonObject.getJSONArray(CONTENT_ARRAY).length() > 8) {
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage41IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(1).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage42IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(2).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage43IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(3).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage44IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(4).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage45IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(5).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage46IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(6).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage47IV);
                        Picasso.with(context)
                                .load(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(7).getString(CONTENT_URL))
                                .resize(128, 128)
                                .centerInside()
                                .into(holder.itemListPostImage48IV);

                        holder.itemListPostImage41IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage42IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage43IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage44IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage45IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage46IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage47IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostImage48IV.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                                return false;
                            }
                        });

                        holder.itemListPostMultipleImage4TV.setText("+" + (jsonObject.getJSONArray(CONTENT_ARRAY).length() - 8));

                        holder.itemListPostImage42CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage43CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage44CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage45CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage46CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage47CV.setVisibility(View.VISIBLE);
                        holder.itemListPostImage48CV.setVisibility(View.VISIBLE);
                        holder.itemListPostContentImage4LL.setVisibility(View.VISIBLE);
                        holder.itemListPostMultipleImage4TV.setVisibility(View.VISIBLE);
                    }
                    holder.itemListPostCreatedAtTV.setText(convertTime(jsonObject.getString(CREATED_AT)));
                    break;
                case POST_TYPE_AUDIO:
                    holder.itemListPostMainLL.setVisibility(View.VISIBLE);
                    holder.itemListPostContentAudioCV.setVisibility(View.VISIBLE);
                    holder.itemListPostSplitDateRL.setVisibility(View.GONE);
                    holder.itemListPostContentDocCV.setVisibility(View.GONE);
                    holder.documentTv.setVisibility(View.GONE);
                    holder.itemListPostContentTextTV.setVisibility(View.GONE);
                    holder.itemListPostContentViewMoreCV.setVisibility(View.GONE);
                    holder.itemListPostContentVideoCV.setVisibility(View.GONE);
                    holder.itemListPostContentImage4LL.setVisibility(View.GONE);
                    holder.itemListPostAudioTimeTV.setText(jsonObject.getString(DURATION));
                    holder.itemListPostCreatedAtTV.setText(convertTime(jsonObject.getString(CREATED_AT)));
                    break;
                case POST_TYPE_DOC:
                    holder.itemListPostMainLL.setVisibility(View.VISIBLE);
                    holder.itemListPostContentDocCV.setVisibility(View.VISIBLE);
                    holder.documentTv.setVisibility(View.VISIBLE);
                    holder.itemListPostSplitDateRL.setVisibility(View.GONE);
                    holder.itemListPostContentTextTV.setVisibility(View.GONE);
                    holder.itemListPostContentVideoCV.setVisibility(View.GONE);
                    holder.itemListPostContentAudioCV.setVisibility(View.GONE);
                    holder.itemListPostContentImage4LL.setVisibility(View.GONE);
                    holder.itemListPostContentViewMoreCV.setVisibility(View.GONE);
                    holder.itemListPostCreatedAtTV.setText(convertTime(jsonObject.getString(CREATED_AT)));
                    break;
                case POST_TYPE_DATE:
                    holder.itemListPostSplitDateRL.setVisibility(View.VISIBLE);
                    holder.itemListPostMainLL.setVisibility(View.GONE);
                    holder.itemListPostSplitDateTV.setText(getDayFromTimeStamp(jsonObject.getString(CREATED_AT)) + ", " + convertDatePost(jsonObject.getString(CREATED_AT)));
                    break;
            }

            holder.itemListPostImage41IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, ImagesActivity.class);
                        intent.putExtra(ID, jsonObject.getString(ID));
                        intent.putExtra(DATA, jsonObject.getJSONArray(CONTENT_ARRAY).toString());
                        intent.putExtra(POSITION, 0);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemListPostImage42IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, ImagesActivity.class);
                        intent.putExtra(ID, jsonObject.getString(ID));
                        intent.putExtra(DATA, jsonObject.getJSONArray(CONTENT_ARRAY).toString());
                        intent.putExtra(POSITION, 1);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemListPostImage43IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, ImagesActivity.class);
                        intent.putExtra(ID, jsonObject.getString(ID));
                        intent.putExtra(DATA, jsonObject.getJSONArray(CONTENT_ARRAY).toString());
                        intent.putExtra(POSITION, 2);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemListPostImage44IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, ImagesActivity.class);
                        intent.putExtra(ID, jsonObject.getString(ID));
                        intent.putExtra(DATA, jsonObject.getJSONArray(CONTENT_ARRAY).toString());
                        intent.putExtra(POSITION, 3);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemListPostImage45IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, ImagesActivity.class);
                        intent.putExtra(ID, jsonObject.getString(ID));
                        intent.putExtra(DATA, jsonObject.getJSONArray(CONTENT_ARRAY).toString());
                        intent.putExtra(POSITION, 4);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemListPostImage46IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, ImagesActivity.class);
                        intent.putExtra(ID, jsonObject.getString(ID));
                        intent.putExtra(DATA, jsonObject.getJSONArray(CONTENT_ARRAY).toString());
                        intent.putExtra(POSITION, 5);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemListPostImage47IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, ImagesActivity.class);
                        intent.putExtra(ID, jsonObject.getString(ID));
                        intent.putExtra(DATA, jsonObject.getJSONArray(CONTENT_ARRAY).toString());
                        intent.putExtra(POSITION, 6);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemListPostImage48IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, ImagesActivity.class);
                        intent.putExtra(ID, jsonObject.getString(ID));
                        intent.putExtra(DATA, jsonObject.getJSONArray(CONTENT_ARRAY).toString());
                        intent.putExtra(POSITION, 7);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemListPostContentVideoCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        context.startActivity(new Intent(context, VideoStreamActivity.class).putExtra(URL, jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemListPostVideoThumbIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.itemListPostContentVideoCV.performClick();
                }
            });

            holder.itemListPostContentVideoIconCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.itemListPostContentVideoCV.performClick();
                }
            });

            holder.itemListPostAudioPlayIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (mediaPlayer != null && currentAudio == viewHolder.getAdapterPosition()) {
                            if (mediaPlayer.isPlaying()) {
                                holder.itemListPostAudioPlayIV.setImageResource(R.drawable.image_play);
                                audioLength[viewHolder.getAdapterPosition()] = pauseAudio();
                                holder.playCv.setVisibility(View.INVISIBLE);
                                holder.playAudioCv.setVisibility(View.INVISIBLE);
//                                holder.playCv.setBackgroundResource(R.color.newBlue);
//                                holder.playAudioCv.setBackgroundResource(R.color.newBlue);
                            } else {
                                holder.itemListPostAudioPlayIV.setImageResource(R.drawable.image_pause);
                                resumeAudio(audioLength[viewHolder.getAdapterPosition()], holder.itemListPostAudioSB, holder.itemListPostAudioTimeTV);
                                holder.playCv.setVisibility(View.VISIBLE);
                                holder.playAudioCv.setVisibility(View.VISIBLE);
                            }
                        } else {
                            currentAudio = viewHolder.getAdapterPosition();
                            holder.itemListPostAudioPlayIV.setImageResource(R.drawable.image_pause);
                            playAudio(jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL), holder.itemListPostAudioSB, holder.itemListPostAudioTimeTV);
                            holder.playCv.setVisibility(View.VISIBLE);
                            holder.playAudioCv.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemListPostMainLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(jsonObject, viewHolder.getAdapterPosition());
                }
            });

            holder.itemListPostMainLL.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onClickListener.onLongClick(jsonObject, viewHolder.getAdapterPosition());
                    return true;
                }
            });

            holder.itemListPostContentDocCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        context.startActivity(new Intent(context, FileWebView.class).putExtra(URL, jsonObject.getJSONArray(CONTENT_ARRAY).getJSONObject(0).getString(CONTENT_URL)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void notifyMessage(List<JSONObject> item) {
        this.item = item;
        this.audioLength = new int[item.size()];
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

    private String convertDatePost(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date date;
        Calendar dateCal = Calendar.getInstance();
        Calendar todayDateCal = Calendar.getInstance();
        String returnTime = "";
        try {
            date = simpleDateFormat.parse(time);
            dateCal.setTime(date);
            int years = todayDateCal.get(Calendar.YEAR) - dateCal.get(Calendar.YEAR);
            int months = todayDateCal.get(Calendar.MONTH) - dateCal.get(Calendar.MONTH);
            int days = todayDateCal.get(Calendar.DATE) - dateCal.get(Calendar.DATE);
            if (years == 0) {
                if (months == 0) {
                    if (days == 0) {
                        returnTime = "Today";
                    } else if (days == 1) {
                        returnTime = "Yesterday";
                    } else {
                        returnTime = dateTimeFormat.format(date);
                    }
                } else if (months == 1) {
                    if (todayDateCal.get(Calendar.DATE) == 1) {
                        returnTime = "Yesterday";
                    } else {
                        returnTime = dateTimeFormat.format(date);
                    }
                } else {
                    returnTime = dateTimeFormat.format(date);
                }
            } else if (years == 1) {
                if (todayDateCal.get(Calendar.MONTH) == 1) {
                    if (todayDateCal.get(Calendar.DATE) == 1) {
                        returnTime = "Yesterday";
                    } else {
                        returnTime = dateTimeFormat.format(date);
                    }
                } else {
                    returnTime = dateTimeFormat.format(date);
                }
            } else {
                returnTime = dateTimeFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!returnTime.equals("")) {
            return returnTime;
        } else {
            return time;
        }
    }

    private String getDayFromTimeStamp(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        Date date;
        Calendar dateCal = Calendar.getInstance();
        try {
            date = simpleDateFormat.parse(time);
            dateCal.setTime(date);
            SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayOfWeekFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("SetTextI18n")
    private void playAudio(String url, final RoundCornerProgressBar seekBar, final TextView textView) {
        final int[] counterListener = {0};
        textView.setText("00:00");
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
            countDownTimer = new CountDownTimer(mediaPlayer.getDuration(), 250) {
                public void onTick(long millisUntilFinished) {
                    float progress = seekBar.getProgress() + 250;
                    seekBar.setProgress(progress);
                    if (counterListener[0] == 4) {
                        seconds++;
                        counterListener[0] = 0;
                        if (seconds < 60) {
                            if (seconds < 10) {
                                textView.setText("00:0" + seconds);
                            } else {
                                textView.setText("00:" + seconds);
                            }
                        } else {
                            int min = seconds / 60;
                            int remainingSeconds = seconds - (min * 60);
                            if (remainingSeconds < 10) {
                                textView.setText(min + ":0" + remainingSeconds);
                            } else {
                                textView.setText(min + ":" + remainingSeconds);
                            }
                        }
                    }
                    counterListener[0]++;
                }

                public void onFinish() {
                }
            }.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    seekBar.setProgress(seekBar.getMax());
                    notifyDataSetChanged();
                    stopAudio();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void resumeAudio(int resumePosition, final RoundCornerProgressBar seekBar, final TextView textView) {
        final int[] counterListener = {0};
        mediaPlayer.start();
        mediaPlayer.seekTo(resumePosition);
        seekBar.setProgress(resumePosition);
        seekBar.setMax(mediaPlayer.getDuration());
        countDownTimer = new CountDownTimer(mediaPlayer.getDuration(), 250) {
            public void onTick(long millisUntilFinished) {
                float progress = seekBar.getProgress() + 250;
                seekBar.setProgress(progress);
                if (counterListener[0] == 4) {
                    seconds++;
                    counterListener[0] = 0;
                    if (seconds < 60) {
                        if (seconds < 10) {
                            textView.setText("00:0" + seconds);
                        } else {
                            textView.setText("00:" + seconds);
                        }
                    } else {
                        int min = seconds / 60;
                        int remainingSeconds = seconds - (min * 60);
                        if (remainingSeconds < 10) {
                            textView.setText(min + ":0" + remainingSeconds);
                        } else {
                            textView.setText(min + ":" + remainingSeconds);
                        }
                    }
                }
                counterListener[0]++;
            }

            public void onFinish() {
            }
        }.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                seekBar.setProgress(seekBar.getMax());
                notifyDataSetChanged();
                stopAudio();
            }
        });
    }

    private int pauseAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private Bitmap convertToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str.substring(base64Str.indexOf(",") + 1), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public interface OnClickListener {
        void onClick(JSONObject jsonObject, int position);

        void onLongClick(JSONObject jsonObject, int position);
    }

    private class RecyclerViewHolders extends RecyclerView.ViewHolder {

        RelativeLayout itemListPostSplitDateRL;
        RoundCornerProgressBar itemListPostAudioSB;
        LinearLayout itemListPostMainLL, itemListPostContentImage4LL;
        CardView itemListPostContentVideoCV, itemListPostContentAudioCV, itemListPostContentDocCV,
                itemListPostMainCV, itemListPostContentViewMoreCV, itemListPostImage42CV, itemListPostImage43CV,
                itemListPostImage44CV, itemListPostImage45CV, itemListPostImage46CV, itemListPostImage47CV,
                itemListPostImage48CV, itemListPostContentVideoIconCV, playAudioCv, playCv;
        TextView itemListPostCreatedAtTV, itemListPostContentTextTV, itemListPostSplitDateTV, documentTv,
                itemListPostMultipleImage4TV, itemListPostAudioTimeTV, itemListPostContentViewMoreTV;
        AppCompatImageView itemListPostAudioPlayIV, itemListPostVideoThumbIV,
                itemListPostImage41IV, itemListPostImage42IV, itemListPostImage43IV, itemListPostImage44IV,
                itemListPostImage45IV, itemListPostImage46IV, itemListPostImage47IV, itemListPostImage48IV;

        RecyclerViewHolders(View itemView) {
            super(itemView);

            itemListPostMainLL = itemView.findViewById(R.id.itemListPostMainLL);
            itemListPostMainCV = itemView.findViewById(R.id.itemListPostMainCV);
            itemListPostAudioSB = itemView.findViewById(R.id.itemListPostAudioSB);
            itemListPostImage42CV = itemView.findViewById(R.id.itemListPostImage42CV);
            itemListPostImage43CV = itemView.findViewById(R.id.itemListPostImage43CV);
            itemListPostImage44CV = itemView.findViewById(R.id.itemListPostImage44CV);
            itemListPostImage45CV = itemView.findViewById(R.id.itemListPostImage45CV);
            itemListPostImage46CV = itemView.findViewById(R.id.itemListPostImage46CV);
            itemListPostImage47CV = itemView.findViewById(R.id.itemListPostImage47CV);
            itemListPostImage48CV = itemView.findViewById(R.id.itemListPostImage48CV);
            itemListPostImage41IV = itemView.findViewById(R.id.itemListPostImage41IV);
            itemListPostImage42IV = itemView.findViewById(R.id.itemListPostImage42IV);
            itemListPostImage43IV = itemView.findViewById(R.id.itemListPostImage43IV);
            itemListPostImage44IV = itemView.findViewById(R.id.itemListPostImage44IV);
            itemListPostImage45IV = itemView.findViewById(R.id.itemListPostImage45IV);
            itemListPostImage46IV = itemView.findViewById(R.id.itemListPostImage46IV);
            itemListPostImage47IV = itemView.findViewById(R.id.itemListPostImage47IV);
            itemListPostImage48IV = itemView.findViewById(R.id.itemListPostImage48IV);
            itemListPostSplitDateRL = itemView.findViewById(R.id.itemListPostSplitDateRL);
            itemListPostSplitDateTV = itemView.findViewById(R.id.itemListPostSplitDateTV);
            itemListPostAudioPlayIV = itemView.findViewById(R.id.itemListPostAudioPlayIV);
            itemListPostCreatedAtTV = itemView.findViewById(R.id.itemListPostCreatedAtTV);
            itemListPostAudioTimeTV = itemView.findViewById(R.id.itemListPostAudioTimeTV);
            documentTv = itemView.findViewById(R.id.documentTv);
            itemListPostVideoThumbIV = itemView.findViewById(R.id.itemListPostVideoThumbIV);
            itemListPostContentDocCV = itemView.findViewById(R.id.itemListPostContentDocCV);
            itemListPostContentTextTV = itemView.findViewById(R.id.itemListPostContentTextTV);
            itemListPostContentAudioCV = itemView.findViewById(R.id.itemListPostContentAudioCV);
            itemListPostContentVideoCV = itemView.findViewById(R.id.itemListPostContentVideoCV);
            itemListPostContentImage4LL = itemView.findViewById(R.id.itemListPostContentImage4LL);
            itemListPostMultipleImage4TV = itemView.findViewById(R.id.itemListPostMultipleImage4TV);
            itemListPostContentViewMoreCV = itemView.findViewById(R.id.itemListPostContentViewMoreCV);
            itemListPostContentViewMoreTV = itemView.findViewById(R.id.itemListPostContentViewMoreTV);
            itemListPostContentVideoIconCV = itemView.findViewById(R.id.itemListPostContentVideoIconCV);
            playAudioCv = itemView.findViewById(R.id.playAudioCv);
            playCv = itemView.findViewById(R.id.playCv);

        }
    }

}