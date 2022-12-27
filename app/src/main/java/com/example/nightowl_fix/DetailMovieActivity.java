package com.example.nightowl_fix;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailMovieActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView tvJudul, tvName, tvPenilaian, tvOverview;
    ImageView imgCover, imgPhoto;
    RatingBar ratingBar;
    String NameFilm, Overview, Poster, Thumbnail, movieURL;
    int Id;
    double Rating;
    ModelMovie modelMovie;
    ProgressDialog progressDialog;
    YouTubePlayerView ytPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = findViewById(R.id.dt_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menampilkan trailer");

        ratingBar = findViewById(R.id.ratingBar);
        imgCover = findViewById(R.id.imgCover);
        imgPhoto = findViewById(R.id.imgPhoto);
        tvJudul = findViewById(R.id.tvTitle);
        tvName = findViewById(R.id.tvName);
        tvPenilaian = findViewById(R.id.tvRating);
        tvOverview = findViewById(R.id.tvOverview);
        ytPlayer =findViewById(R.id.ytTrailer);

        modelMovie = (ModelMovie) getIntent().getSerializableExtra("detailMovie");
        if (modelMovie != null) {

            Id = modelMovie.getId();
            NameFilm = modelMovie.getJudul();
            Rating = modelMovie.getPenilaian();
            Overview = modelMovie.getOverview();
            Poster = modelMovie.getBackdrop();
            Thumbnail = modelMovie.getPoster();
            movieURL = ApiEndpoint.URLFILM + "" + Id;

            tvJudul.setText(NameFilm);
            tvName.setText(NameFilm);
            tvPenilaian.setText(Rating + "/10");
            tvOverview.setText(Overview);
            tvJudul.setSelected(true);
            tvName.setSelected(true);

            float newValue = (float)Rating;
            ratingBar.setNumStars(5);
            ratingBar.setStepSize((float) 0.5);
            ratingBar.setRating(newValue / 2);

            //MELAKUKAN LOAD IMAGE
            Glide.with(this)
                    .load(ApiEndpoint.URLIMAGE + Poster)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgCover);

            Glide.with(this)
                    .load(ApiEndpoint.URLIMAGE + Thumbnail)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgPhoto);

            getLifecycle().addObserver(ytPlayer);
            getTrailer();
        }
    }

    private void getTrailer() {
        progressDialog.show();
        //KONEKSI INTERNET MENGGUNAKAN FAST ANDROID NETWORKING
        AndroidNetworking.get(ApiEndpoint.BASEURL + ApiEndpoint.MOVIE_VIDEO + ApiEndpoint.APIKEY + ApiEndpoint.LANGUAGE)
                .addPathParameter("id", String.valueOf(Id))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //MENGAMBIL KEY DARI VIDEO TRAILER
                                final String key;
                                key = jsonObject.getString("key");
                                //MENAMPILKAN VIDEO BERDASARKAN KEY KE YOUTUBE PLAYER
                                ytPlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                                    @Override
                                    public void onReady(YouTubePlayer youTubePlayer) {
                                        String videoId = key;
                                        youTubePlayer.cueVideo(videoId, 0);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetailMovieActivity.this,
                                    "Gagal menampilkan data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(DetailMovieActivity.this,
                                "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
