package stock.android.chess.watch;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import stock.android.chess.R;

public class WatchActivity extends AppCompatActivity {
    public static String api_key_youtube = "AIzaSyBk9IJATvKbgEKrpaHt0JSck4RZFUR48q4";
    private String id_channel_youtube = "UCHz5JQAUSkjxrosDIWCtEdw";
    private String id_playlist_youtube = "PLaAXmN_SIW5NPJ5vJKXh8WiV9Q_FMtDx8";
    private String maxResults = "50";

    //json playlist
    //private String urlGetJson = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + id_playlist_youtube + "&key=" + api_key_youtube + "&maxResults=" + maxResults;

    //json channel
    private String urlGetJson = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + id_channel_youtube + "&key=" + api_key_youtube + "&maxResults=" + maxResults;
    RecyclerView rvVideo;
    ArrayList<VideoItem> arrayVideo;
    VideoRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.watch_activity);
        rvVideo = (RecyclerView) findViewById(R.id.recyclerViewVideo);
        rvVideo.setLayoutManager(new LinearLayoutManager(this)); // Thêm dòng này để set LayoutManager
        arrayVideo = new ArrayList<>();

        adapter = new VideoRecyclerAdapter(this, R.layout.item_video, arrayVideo);
        rvVideo.setAdapter(adapter);

        GetJsonYoutube(urlGetJson);
    }

    private void GetJsonYoutube(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonItems = response.getJSONArray("items");
                            String title = "";
                            String url = "";
                            String idVideo = "";
                            for (int i = 0; i < jsonItems.length(); i++) {
                                /*
                                //get video info in playlist
                                JSONObject jsonItem = jsonItems.getJSONObject(i);

                                JSONObject jsonSnippet = jsonItem.getJSONObject("snippet");

                                title = jsonSnippet.getString("title");

                                JSONObject jsonThumbnail = jsonSnippet.getJSONObject("thumbnails");

                                JSONObject jsonMedium = jsonThumbnail.getJSONObject("medium");
                                url = jsonMedium.getString("url");

                                JSONObject jsonResourceID = jsonSnippet.getJSONObject("resourceId");
                                idVideo = jsonResourceID.getString("videoId");
                                */
                                //get video info in channel
                                JSONObject jsonItem = jsonItems.getJSONObject(i);

                                JSONObject jsonId = jsonItem.getJSONObject("id");
                                if(jsonId == null || !jsonId.has("videoId")) {
                                    continue;
                                }
                                idVideo = jsonId.getString("videoId");

                                JSONObject jsonSnippet = jsonItem.getJSONObject("snippet");
                                if(jsonSnippet == null || !jsonSnippet.has("title")) {
                                    continue;
                                }
                                title = jsonSnippet.getString("title");

                                JSONObject jsonThumbnail = jsonSnippet.getJSONObject("thumbnails");
                                JSONObject jsonMedium = jsonThumbnail.getJSONObject("medium");
                                url = jsonMedium.getString("url");

                                arrayVideo.add(new VideoItem(idVideo, title, url));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WatchActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
