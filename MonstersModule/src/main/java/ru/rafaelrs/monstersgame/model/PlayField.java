package ru.rafaelrs.monstersgame.model;

import java.util.LinkedList;

public class PlayField {
    /** FieldChangeListener. */
    public interface FieldChangeListener {
        /** @param x, y coordinates of changed field */
        void onFieldChange(int x, int y);
    }

    private PlaySquare[] squares;
    private int width, height;
    //private LinkedList;

    private FieldChangeListener FieldChangeListener;

    public PlayField(int fwidth, int fheight) {
        width = fwidth;
        height = fheight;
        squares = new PlaySquare[fwidth * fheight];
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                setFieldSquare(x, y, new PlaySquare());
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    /** @param flistener set the change listener. */
    public void setFieldChangeListener(FieldChangeListener flistener) {
        FieldChangeListener = flistener;
    }

    /** @return square from the field by x, y coordinates. */
    private PlaySquare getFieldSquare(int x, int y) {
        return (squares == null) ? null : squares[y * width + x];
    }

    private void setFieldSquare(int x, int y, PlaySquare square) {
        if (squares == null) return;
        squares[y * width + x] = square;
        notifyListener(x, y);
    }

    public PlaySquare getFieldSquareData(int x, int y) {
        if (squares == null) return null;
        return new PlaySquare(getFieldSquare(x, y).getOccupancy());
    }

    public void setFieldSquareData(int x, int y, PlaySquare square) {
        if (squares == null) return;
        getFieldSquare(x, y).setOccupancy(square.getOccupancy());
        notifyListener(x, y);
    }

    public PlaySquare[] getField() {
        return squares;
    }

    private void notifyListener(int x, int y) {
        if (null != FieldChangeListener) {
            FieldChangeListener.onFieldChange(x, y);
        }
    }

}
