package ru.rafaelrs.monstersgame.model;

import android.content.Context;
import android.support.v7.appcompat.R;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Created by 1111 on 10.12.13.
 */
public class MonsterUnit extends Thread implements Runnable {
    private boolean vulnerable;
    private int xpos, ypos;
    private int xshadow, yshadow;
    private PlayField pobject;

    public MonsterUnit(int x, int y, boolean vulnerable, PlayField pobject) {
        this.vulnerable = vulnerable;
        xpos = x;
        ypos = y;
        xshadow = -1;
        yshadow = -1;
        this.pobject = pobject;
    }

    public boolean isVulnerable() { return vulnerable; };
    public void setVulnerable(boolean vulnerable) { this.vulnerable = vulnerable; };
    public int getX() { return xpos; };
    public int getY() { return ypos; };
    public int getShadowX() { return xshadow; };
    public int getShadowY() { return yshadow; };

    public void moveMonster(int newx, int newy) {
        xshadow = xpos;
        yshadow = ypos;
        xpos = newx;
        ypos = newy;
    }

    @Override
    public void run() {
        Random randInt = new Random();
        while (true) {
            for (int i = 0; i < 5;  i++) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (pobject.monstersOnField) {

                    if (i == 4) {
                        class Directions { public int x, y; Directions(int x, int y) { this.x = x; this.y = y; }}
                        Directions[] directions = {
                                new Directions(-1, -1), new Directions(0, -1), new Directions(1, -1),
                                new Directions(-1, 0),                         new Directions(1,  0),
                                new Directions(-1, 1),  new Directions(0, 1),  new Directions(1, 1)
                        };
                        ArrayList<Directions> availDirections = new ArrayList<Directions>();
                        for (Directions dir : directions) {
                            int x = getX() + dir.x;
                            int y = getY() + dir.y;
                            if (x >= 0 && y >=0 && x < pobject.getWidth() && y < pobject.getHeight() && pobject.getFieldSquareData(x, y).getMonsterUnit() == null) {
                                availDirections.add(dir);
                            }
                        }
                        availDirections.add(new Directions(0, 0));
                        Directions generatedDir = availDirections.get(randInt.nextInt(availDirections.size() - 1));
                        moveMonster(getX() + generatedDir.x, getY() + generatedDir.y);
                    }

                    int generatedVulnerable = randInt.nextInt(99);
                    if (generatedVulnerable < 70) { setVulnerable(true); } else { setVulnerable(false); }
                    pobject.fieldHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            pobject.syncPlayField();
                            pobject.notifyListener(getX(), getY());
                            pobject.notifyListener(getShadowX(), getShadowY());
                        }
                    });
                }
            }
        }
    }
}
