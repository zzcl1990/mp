package edu.illinois.cs.cs125.fall2020.mp.models;

import java.io.Serializable;

public class Rating implements Serializable {
    /*xxx*/
    public static final double NOT_RATED = -1.0;

    private String id;
    private double rating;

    public Rating(final String setId, final double setRating) {
        this.id = setId;
        this.rating = setRating;
    }

    public Rating(String s, int i, int i1) {

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public Double getRating() {
        return rating;
    }
}
