package com.deorabanna1925.postory.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.deorabanna1925.postory.R;
import com.deorabanna1925.postory.databinding.ItemGradientSingleColorBinding;

import java.util.ArrayList;

public class GradientColorAdapter extends RecyclerView.Adapter<GradientColorAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> arrayList;

    public GradientColorAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGradientSingleColorBinding binding = ItemGradientSingleColorBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String code = arrayList.get(position);
        holder.code.setText(code);

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_circle_24);
        assert unwrappedDrawable != null;
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(code));
        holder.color.setBackground(wrappedDrawable);

        holder.code.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("hexCode", code);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copy to Clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView color;
        public TextView code;

        public ViewHolder(@NonNull ItemGradientSingleColorBinding binding) {
            super(binding.getRoot());

            color = binding.color;
            code = binding.code;

        }

    }
}
