package stock.android.chess.watch;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import stock.android.chess.R;

import java.util.List;

public class VideoRecyclerAdapter extends RecyclerView.Adapter<VideoRecyclerAdapter.ViewHolder> {

    private Context context;

    private int layout;
    private List<VideoItem> videoItemList;


    public VideoRecyclerAdapter(Context context, int layout, List<VideoItem> videoItemList) {
        this.context = context;
        this.layout = layout;
        this.videoItemList = videoItemList;
    }

    @Override
    public int getItemCount() {
        return videoItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView imgThumbnail;
        public LinearLayout item_video;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.video_title);
            imgThumbnail = itemView.findViewById(R.id.video_thumbnail);
            item_video = itemView.findViewById(R.id.item_video);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoItem video = videoItemList.get(position);
        holder.title.setText(video.getTitle());
        Picasso.get().load(video.getThumbnailUrl()).into(holder.imgThumbnail);

        holder.item_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGoToPlayVideo(video.getVideoId());
            }
        });
    }

    private void onClickGoToPlayVideo(String idVideo) {
        Intent intent = new Intent(context, PlayVideoActivity.class);
        intent.putExtra("idVideoYoutube", idVideo);
        context.startActivity(intent);
    }
}
