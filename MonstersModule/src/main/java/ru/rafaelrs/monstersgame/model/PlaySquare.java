package ru.rafaelrs.monstersgame.model;

import android.support.v7.appcompat.R;

public final class PlaySquare {

    private boolean occupied;

    public PlaySquare() {
        this.occupied = true;
    }

    public PlaySquare(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean getOccupancy() { return occupied; }
    public void setOccupancy(boolean occupied) { this.occupied = occupied; }

}
