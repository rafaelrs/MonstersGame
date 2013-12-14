package ru.rafaelrs.monstersgame.model;

import android.os.Handler;

import java.util.LinkedList;
import java.util.Random;

public class PlayField {

    /** FieldChangeListener. */
    public interface FieldChangeListener {
        /** @param x, y coordinates of changed field */
        void onFieldChange(int x, int y);
    }

    /** OnGameOverListener */
    public interface OnGameOverListener {
        void onGameOver();
    }

    private PlaySquare[] squares;
    private int width, height;
    public LinkedList<MonsterUnit> monstersOnField = new LinkedList<MonsterUnit>();
    public int gameLevel;

    private FieldChangeListener FieldChangeListener;
    private OnGameOverListener OnGameOverListener;

    final Handler fieldHandler = new Handler();

    public PlayField(int fwidth, int fheight) {
        width = fwidth;
        height = fheight;
        squares = new PlaySquare[fwidth * fheight];
        syncPlayField();
    }

    public void syncPlayField() {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                PlaySquare square = getFieldSquare(x, y);
                if (square == null) {
                    setFieldSquare(x, y, new PlaySquare());
                } else {
                    MonsterUnit mu = square.getMonsterUnit();
                    if (mu != null && (monstersOnField.indexOf(mu) == -1 || mu.getX() != x || mu.getY() != y)) {
                        square.setMonsterUnit(null);
                        notifyListener(x, y);
                    }
                }
            }
        }
        for (MonsterUnit mu : monstersOnField) {
            PlaySquare square =  getFieldSquare(mu.getX(), mu.getY());
            if (square.getMonsterUnit() != mu) {
                square.setMonsterUnit(mu);
                notifyListener(mu.getX(), mu.getY());
            }
        }
    }

    public void setGameLevel(int level) {
        gameLevel = level;
    }

    public void generateMonsters() {
        Random randInt = new Random();
        for (int i = 0; i < (int)(gameLevel * 1.5 + 5); i++) {
            boolean placeFounded = false;

            while (true) {
                int x = randInt.nextInt(getWidth() - 1);
                int y = randInt.nextInt(getHeight() - 1);
                PlaySquare square = getFieldSquare(x, y);
                if (square.getMonsterUnit() != null) continue;

                MonsterUnit newMonster = new MonsterUnit(x,y, true, this);
                monstersOnField.add(newMonster);
                newMonster.start();
                square.setMonsterUnit(newMonster);
                notifyListener(x, y);
                break;
            }
        }
        syncPlayField();
    }

    public void fieldClear() {
        monstersOnField.clear();
    }

    public void destroyMonster(int x, int y) {
        synchronized (monstersOnField) {
            PlaySquare square = getFieldSquare(x, y);
            monstersOnField.remove(square.getMonsterUnit());
            notifyListener(x, y);
            syncPlayField();
        }

        if (monstersOnField.isEmpty()) {
            //stopMonsters();
            OnGameOverListener.onGameOver();
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    /** @param flistener set the change listener. */
    public void setFieldChangeListener(FieldChangeListener flistener) {
        FieldChangeListener = flistener;
    }

    public void setOnGameOverListener(OnGameOverListener glistener) {
        OnGameOverListener = glistener;
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
        return new PlaySquare(getFieldSquare(x, y));
    }

    public void setFieldSquareData(int x, int y, PlaySquare square) {
        if (squares == null) return;
        getFieldSquare(x, y).fillSquareData(square);
        notifyListener(x, y);
    }

    public PlaySquare[] getField() {
        return squares;
    }

    public void notifyListener(int x, int y) {
        if (null != FieldChangeListener) {
            FieldChangeListener.onFieldChange(x, y);
        }
    }

}
