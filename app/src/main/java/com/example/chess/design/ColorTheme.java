package com.example.chess.design;

public class ColorTheme {
    private int lightSquare;
    private int darkSquare;
    private int activeLightSquare;
    private int activeDarkSquare;

    public ColorTheme(int lightSquare, int darkSquare, int activeLightSquare, int activeDarkSquare) {
        this.lightSquare = lightSquare;
        this.darkSquare = darkSquare;
        this.activeLightSquare = activeLightSquare;
        this.activeDarkSquare = activeDarkSquare;
    }

    public int getLightSquare() {
        return lightSquare;
    }

    public void setLightSquare(int lightSquare) {
        this.lightSquare = lightSquare;
    }

    public int getDarkSquare() {
        return darkSquare;
    }

    public void setDarkSquare(int darkSquare) {
        this.darkSquare = darkSquare;
    }

    public int getActiveLightSquare() {
        return activeLightSquare;
    }

    public void setActiveLightSquare(int activeLightSquare) {
        this.activeLightSquare = activeLightSquare;
    }

    public int getActiveDarkSquare() {
        return activeDarkSquare;
    }

    public void setActiveDarkSquare(int activeDarkSquare) {
        this.activeDarkSquare = activeDarkSquare;
    }
}
