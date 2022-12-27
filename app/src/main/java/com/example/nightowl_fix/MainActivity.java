package com.example.nightowl_fix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.onSelectData{

    ImageView imgLogout;
    FirebaseAuth mAuth;
    private MovieAdapter movieAdapter;
    private SearchView searchFilm;
    private RecyclerView rvFilmRecommend;
    private ProgressDialog progressDialog;
    private List<ModelMovie> moviePopular = new ArrayList<>();
    private static final String TAG = "MainActivity";
    private Activity mCurrentActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menampilkan data");

        //BUTTON LOGOUT
        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //Pencarian Film

        searchFilm = findViewById(R.id.searchFilm);
        searchFilm.setQueryHint(getString(R.string.search_film));
        searchFilm.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setSearchMovie(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals(""))
                    getMovie();
                return false;
            }
        });

        int searchPlateId = searchFilm.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchFilm.findViewById(searchPlateId);
        if (searchPlate != null) {
            searchPlate.setBackgroundColor(Color.TRANSPARENT);
        }

        //Film Rekomendasi
        rvFilmRecommend = findViewById(R.id.rvFilmRecommend);
        rvFilmRecommend.setLayoutManager(new LinearLayoutManager(this));
        rvFilmRecommend.setHasFixedSize(true);

        getMovie();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(this,RegisterActivity.class));
            finish();
        } else {}
    }

    //Pencarian Film
    private void setSearchMovie(String query) {
        progressDialog.show();
        AndroidNetworking.get(ApiEndpoint.BASEURL + ApiEndpoint.SEARCH_MOVIE
                + ApiEndpoint.APIKEY + ApiEndpoint.LANGUAGE + ApiEndpoint.QUERY + query)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            moviePopular = new ArrayList<>();
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ModelMovie dataApi = new ModelMovie();

                                dataApi.setId(jsonObject.getInt("id"));
                                dataApi.setJudul(jsonObject.getString("title"));
                                dataApi.setPenilaian(jsonObject.getDouble("vote_average"));
                                dataApi.setOverview(jsonObject.getString("overview"));
                                dataApi.setPoster(jsonObject.getString("poster_path"));
                                dataApi.setBackdrop(jsonObject.getString("backdrop_path"));
                                moviePopular.add(dataApi);
                                showMovie();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(getActivity(), "Gagal menampilkan data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        //Toast.makeText(getActivity(), "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Get Movie
    private void getMovie() {
        progressDialog.show();
        AndroidNetworking.get(ApiEndpoint.BASEURL + ApiEndpoint.MOVIE_POPULAR + ApiEndpoint.APIKEY + ApiEndpoint.LANGUAGE)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            moviePopular = new ArrayList<>();
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ModelMovie dataApi = new ModelMovie();

                                dataApi.setId(jsonObject.getInt("id"));
                                dataApi.setJudul(jsonObject.getString("title"));
                                dataApi.setPenilaian(jsonObject.getDouble("vote_average"));
                                dataApi.setOverview(jsonObject.getString("overview"));
                                dataApi.setPoster(jsonObject.getString("poster_path"));
                                dataApi.setBackdrop(jsonObject.getString("backdrop_path"));
                                moviePopular.add(dataApi);
                                showMovie();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(getActivity(), "Gagal menampilkan data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        //Toast.makeText(getActivity(), "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showMovie() {
        movieAdapter = new MovieAdapter(getApplicationContext(), moviePopular, this);
        rvFilmRecommend.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSelected(ModelMovie modelMovie) {
        Intent intent = new Intent(this, DetailMovieActivity.class);
        intent.putExtra("detailMovie", modelMovie);
        Log.i(TAG, "MyClass.getView() — get item number ");
        startActivity(intent);
        Log.i(TAG, "MyClass.getView() — get item number ");
    }

}