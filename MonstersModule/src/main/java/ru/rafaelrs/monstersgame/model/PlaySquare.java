package ru.rafaelrs.monstersgame.model;

public final class PlaySquare {

    private MonsterUnit monsterUnit;

    public PlaySquare() {
        monsterUnit = null;
    }

    public PlaySquare(PlaySquare ps) {
        monsterUnit = ps.monsterUnit;
    }

    public PlaySquare(MonsterUnit monsterUnit) {
        this.monsterUnit = monsterUnit;
    }

    public void fillSquareData(PlaySquare ps) {
        monsterUnit = ps.monsterUnit;
    }

    public MonsterUnit getMonsterUnit() { return monsterUnit; }
    public void setMonsterUnit(MonsterUnit monsterUnit) { this.monsterUnit = monsterUnit; }
}
