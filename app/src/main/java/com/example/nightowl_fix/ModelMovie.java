package com.example.nightowl_fix;

import java.io.Serializable;

import io.realm.RealmObject;

public class ModelMovie extends RealmObject implements Serializable {
    private int Id;
    private String Judul;
    private double Penilaian;
    private String Overview;
    private String Poster;
    private String Backdrop;

    public ModelMovie() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getJudul() {
        return Judul;
    }

    public void setJudul(String judul) {
        Judul = judul;
    }

    public double getPenilaian() {
        return Penilaian;
    }

    public void setPenilaian(double penilaian) {
        Penilaian = penilaian;
    }

    public String getOverview() {
        return Overview;
    }

    public void setOverview(String overview) {
        Overview = overview;
    }


    public String getPoster() {
        return Poster;
    }

    public void setPoster(String poster) {
        Poster = poster;
    }

    public String getBackdrop() {
        return Backdrop;
    }

    public void setBackdrop(String backdrop) {
        Backdrop = backdrop;
    }

}

