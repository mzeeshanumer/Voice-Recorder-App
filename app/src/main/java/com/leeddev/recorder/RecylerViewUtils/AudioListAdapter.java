package com.leeddev.recorder.RecylerViewUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;


import com.leeddev.recorder.AppUtils.utils;
import com.leeddev.recorder.R;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {
    private ShareActionProvider mShareActionProvider;
    private File[] allFiles;
   // private TimeAgo timeAgo;
    LinearLayout btn_share;
    LinearLayout btn_delete;
    LinearLayout btn_details;
    Animation fadeInAnimation;
    Context context;
    private onItemListClickbabu onItemListClick;
    public AudioListAdapter(Context context, File[] allFiles, onItemListClickbabu onItemListClick) {
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;
        this.context=context;
    }
    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_item,parent,false);
        //timeAgo = new TimeAgo();

        return new  AudioViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.list_title.setText(allFiles[position].getName());
        holder.list_date.setText(getTimeAgo(allFiles[position].lastModified()));
        holder.duration.setText(getDuration(allFiles[position]));
        holder.btnMenu.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
            View dialogView = LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.menu_dialogbox, null);
            fadeInAnimation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fade_in);
            btn_share= dialogView.findViewById(R.id.layout_share);
            btn_delete = dialogView.findViewById(R.id.layout_delet);
            btn_details = dialogView.findViewById(R.id.layout_details);
            dialog.setView(dialogView);
            dialog.setCancelable(true);
            dialog.show();

            btn_share.setOnClickListener(view1 -> {
                btn_share.startAnimation(fadeInAnimation);
                Uri imageUri = FileProvider.getUriForFile(
                        context,
                        "com.leeddev.recorder.provider", //(use your app signature + ".provider" )
                        allFiles[position]);
                String sharePath = allFiles[position].getPath();
                Uri uri = Uri.parse("file://" +sharePath);
                Intent share = new Intent(Intent.ACTION_SEND);
                if(allFiles[position].exists()) {
                    share.setType("audio/*");
                    share.putExtra(Intent.EXTRA_STREAM, imageUri);
                    context.startActivity(Intent.createChooser(share, "Share Sound File"));
                }
                dialog.dismiss();
            });


            btn_delete.setOnClickListener(view12 -> {
                btn_delete.startAnimation(fadeInAnimation);
                Toast.makeText(context.getApplicationContext(), "Delete Successfully", Toast.LENGTH_SHORT).show();
                if(allFiles[position].exists()) {
                    allFiles[position].delete();

                }
                dialog.dismiss();
            });
            btn_details.setOnClickListener(view13 -> {
                Toast.makeText(context.getApplicationContext(),"Coming Soon", Toast.LENGTH_SHORT).show();
//                    Intent intent= new Intent(context.getApplicationContext(), DetailsActivity.class);
//                    context.startActivity(intent);
            dialog.dismiss();

            });
        });
    }
    private static String getDuration(File file) {
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
            String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return utils.formateMilliSeccond(Long.parseLong(durationStr));
        } catch (IllegalArgumentException e) {
            return "";
        }
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
        private ImageButton btnMenu;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            list_image = itemView.findViewById(R.id.list_image_view);
            list_date = itemView.findViewById(R.id.tv_list_date);
            list_title = itemView.findViewById(R.id.tv_list_title);
            duration = itemView.findViewById(R.id.duration);
             btnMenu = itemView.findViewById(R.id.image_btn_menu);
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
    public String getTimeAgo(long duration){
        Date now = new Date();
        long longNow = now.getTime();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(longNow-duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(longNow-duration);
        long hours = TimeUnit.MILLISECONDS.toHours(longNow-duration);
        long days = TimeUnit.MILLISECONDS.toDays(longNow-duration);

        if(seconds<60)
        {return  "just now";}

        else if (minutes==1)
        {
            return "a minute ago";
        }

        else if (minutes>1&&minutes<60)
        {
            return minutes+" minutes ago";
        }

        else if (hours==1)
        {
            return "an hour ago";
        }
        else if (hours>1&&hours<24)
        {
            return hours + " hours ago";
        }

        else if (days==1)
        {
            return "a day ago";
        }
        else {
            return days + " days ago";
        }
//conditions ended
    }

}
