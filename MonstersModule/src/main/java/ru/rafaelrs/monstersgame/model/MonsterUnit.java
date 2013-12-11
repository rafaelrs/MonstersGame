package ru.rafaelrs.monstersgame.model;

import android.support.v7.appcompat.R;

/**
 * Created by 1111 on 10.12.13.
 */
public class MonsterUnit {
    private boolean vulnerable;
    private int xpos, ypos;
    private int xshadow, yshadow;

    public MonsterUnit(int x, int y, boolean vulnerable) {
        this.vulnerable = vulnerable;
        xpos = x;
        ypos = y;
        xshadow = -1;
        yshadow = -1;
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
}
