package com.deorabanna1925.postory.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deorabanna1925.postory.R;
import com.deorabanna1925.postory.model.ThumbnailItem;
import com.deorabanna1925.postory.utils.ThumbnailCallback;
import com.nineoldandroids.view.ViewHelper;

import org.w3c.dom.Text;

import java.util.List;

/**
 * @author Varun on 01/07/15.
 */
public class ThumbnailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "THUMBNAILS_ADAPTER";
    private static int lastPosition = -1;
    private ThumbnailCallback thumbnailCallback;
    private List<ThumbnailItem> dataSet;

    public ThumbnailsAdapter(List<ThumbnailItem> dataSet, ThumbnailCallback thumbnailCallback) {
        Log.v(TAG, "Thumbnails Adapter has " + dataSet.size() + " items");
        this.dataSet = dataSet;
        this.thumbnailCallback = thumbnailCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.v(TAG, "On Create View Holder Called");
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.list_thumbnail_item, viewGroup, false);
        return new ThumbnailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int i) {
        final ThumbnailItem thumbnailItem = dataSet.get(i);
        Log.v(TAG, "On Bind View Called");
        ThumbnailsViewHolder thumbnailsViewHolder = (ThumbnailsViewHolder) holder;
        thumbnailsViewHolder.name.setText(thumbnailItem.name);
        thumbnailsViewHolder.thumbnail.setImageBitmap(thumbnailItem.image);
        thumbnailsViewHolder.thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        setAnimation(thumbnailsViewHolder.thumbnail, i);
        thumbnailsViewHolder.thumbnail.setOnClickListener(v -> {
            if (lastPosition != i) {
                thumbnailCallback.onThumbnailClick(thumbnailItem.filter);
                lastPosition = i;
            }
        });
    }

    private void setAnimation(View viewToAnimate, int position) {
        {
            ViewHelper.setAlpha(viewToAnimate, .0f);
            com.nineoldandroids.view.ViewPropertyAnimator.animate(viewToAnimate).alpha(1).setDuration(250).start();
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ThumbnailsViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView thumbnail;

        public ThumbnailsViewHolder(View v) {
            super(v);
            this.name = (TextView) v.findViewById(R.id.name);
            this.thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
        }
    }
}
