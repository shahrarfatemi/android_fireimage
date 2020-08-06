package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private Context context;
    private List<ImageUpload> imageUploads;

    @NonNull
    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.sample, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
        ImageUpload imageUpload = imageUploads.get(position);
        holder.imageText.setText(imageUpload.getImageName());
        Picasso.get().load(imageUpload.getImageUri())
                .placeholder(R.drawable.shahrar)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUploads.size();
    }

    public CustomAdapter(Context context, List<ImageUpload> imageUploads) {
        this.context = context;
        this.imageUploads = imageUploads;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView imageText;
        ImageView imageView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            imageText = itemView.findViewById(R.id.nameId);
            imageView = itemView.findViewById(R.id.imageId);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
