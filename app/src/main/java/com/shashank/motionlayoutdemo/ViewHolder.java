package com.shashank.motionlayoutdemo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shashank.motionlayoutdemo.RecyclerViewAdapter.OnClickListener;

import org.w3c.dom.Text;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView name;
    ImageView img;
    OnClickListener onClickListener;
    public ViewHolder(@NonNull  View itemView, OnClickListener mOnClickListener) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        img = itemView.findViewById(R.id.image);
        onClickListener = mOnClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onClickListener.onItemClickListener(getAdapterPosition());
    }
}
