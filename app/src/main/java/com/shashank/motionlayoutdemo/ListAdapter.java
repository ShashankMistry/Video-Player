package com.shashank.motionlayoutdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<File> {
    Context context;
    ArrayList<File> files = new ArrayList<>();


    public ListAdapter(@NonNull Context context,ArrayList<File> files) {
        super(context, 0,files);
        this.context = context;
        this.files = files;
    }

    private static class ViewHolder {
        ImageView img;
        TextView name;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//
        ViewHolder holder;
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.cutom_view, parent, false);
            holder = new ListAdapter.ViewHolder();
            holder.img = convertView.findViewById(R.id.image);
            holder.name = convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
            holder.name.setText(files.get(position).getName());
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(files.get(position).getPath(),
                    MediaStore.Images.Thumbnails.MINI_KIND);
            holder.img.setImageBitmap(thumb);

        return convertView;
    }

}
