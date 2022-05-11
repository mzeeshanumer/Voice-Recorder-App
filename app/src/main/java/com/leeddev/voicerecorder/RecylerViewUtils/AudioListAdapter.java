package com.leeddev.voicerecorder.RecylerViewUtils;

import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leeddev.voicerecorder.AppUtils.utils;
import com.leeddev.voicerecorder.R;
import com.leeddev.voicerecorder.AppUtils.TimeAgo;

import org.w3c.dom.Text;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {


    private File[] allFiles;
    private TimeAgo timeAgo;
    private onItemListClickbabu onItemListClick;

    public AudioListAdapter(File[] allFiles, onItemListClickbabu onItemListClick) {
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_item,parent,false);
        timeAgo = new TimeAgo();
        return new  AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
holder.list_title.setText(allFiles[position].getName());
holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
holder.duration.setText(getDuration(allFiles[position]));

    }
    private static String getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return utils.formateMilliSeccond(Long.parseLong(durationStr));
    }


    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView list_image;
        private TextView list_title;
        private TextView list_date;
        private TextView duration;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            list_image = itemView.findViewById(R.id.list_image_view);
            list_date = itemView.findViewById(R.id.tv_list_date);
            list_title = itemView.findViewById(R.id.tv_list_title);
            duration = itemView.findViewById(R.id.duration);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
              onItemListClick.onClickListener(allFiles[getAdapterPosition()],getAdapterPosition());

        }
    }

    public interface onItemListClickbabu {
        void onClickListener(File file,int position);

    }

}
