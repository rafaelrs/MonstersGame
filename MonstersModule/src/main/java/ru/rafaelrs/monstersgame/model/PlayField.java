package ru.rafaelrs.monstersgame.model;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import ru.rafaelrs.monstersgame.GameActivity;

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
    private LinkedList<MonsterUnit> monstersOnField = new LinkedList<MonsterUnit>();
    private boolean monstersThreadActive = false;

    private FieldChangeListener FieldChangeListener;
    private OnGameOverListener OnGameOverListener;

    public PlayField(int fwidth, int fheight) {
        width = fwidth;
        height = fheight;
        squares = new PlaySquare[fwidth * fheight];
        syncPlayField();
    }

    private void syncPlayField() {
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

    public void generateMonsters(int gameLevel) {
        Random randInt = new Random();
        for (int i = 0; i < (int)(gameLevel * 1.5 + 5); i++) {
            boolean placeFounded = false;

            while (true) {
                int x = randInt.nextInt(getWidth() - 1);
                int y = randInt.nextInt(getHeight() - 1);
                PlaySquare square = getFieldSquare(x, y);
                if (square.getMonsterUnit() != null) continue;

                MonsterUnit newMonster = new MonsterUnit(x,y, true);
                monstersOnField.add(newMonster);
                square.setMonsterUnit(newMonster);
                notifyListener(x, y);
                break;
            }
        }
        syncPlayField();
    }

    public void destroyMonster(int x, int y) {
        PlaySquare square = getFieldSquare(x, y);

        monstersLiveThread.cancel(true);
        while (!monstersLiveThread.finished) {
            monstersLiveThread.cancel(true);
        };

        monstersOnField.remove(square.getMonsterUnit());
        syncPlayField();

        if (monstersOnField.isEmpty()) {
            OnGameOverListener.onGameOver();
        } else {
            startMonsters();
        }
    }

    private MonstersLive monstersLiveThread;

    public void startMonsters() {
        monstersLiveThread = new MonstersLive();
        monstersLiveThread.execute(monstersOnField);
    }

    public void stopMonsters() {
        monstersLiveThread.cancel(true);
    }

    // Monsters lives here :)
    private class MonstersLive extends AsyncTask<LinkedList<MonsterUnit>, MonsterUnit, Void> {

        public boolean finished = false;

        protected Void doInBackground(LinkedList<MonsterUnit>... params) {
            if (params.length <= 0) return null;

            LinkedList<MonsterUnit> monstersList = params[0];
            Random randInt = new Random();

            while (!isCancelled()) {
                for (int i = 0; i < 5;  i++) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (MonsterUnit mu : monstersList) {

                        if (i == 4) {
                            class Directions { public int x, y; Directions(int x, int y) { this.x = x; this.y = y; }}
                            Directions[] directions = {
                                    new Directions(-1, -1), new Directions(0, -1), new Directions(1, -1),
                                    new Directions(-1, 0),                         new Directions(1,  0),
                                    new Directions(-1, 1),  new Directions(0, 1),  new Directions(1, 1)
                            };
                            ArrayList<Directions> availDirections = new ArrayList<Directions>();
                            for (Directions dir : directions) {
                                int x = mu.getX() + dir.x;
                                int y = mu.getY() + dir.y;
                                if (x >= 0 && y >=0 && x < getWidth() && y < getHeight() && getFieldSquare(x, y).getMonsterUnit() == null) {
                                    availDirections.add(dir);
                                }
                            }
                            availDirections.add(new Directions(0, 0));
                            Directions generatedDir = availDirections.get(randInt.nextInt(availDirections.size() - 1));
                            mu.moveMonster(mu.getX() + generatedDir.x, mu.getY() + generatedDir.y);
                        }

                        int generatedVulnerable = randInt.nextInt(99);
                        if (generatedVulnerable < 70) { mu.setVulnerable(true); } else { mu.setVulnerable(false); }
                        publishProgress(mu);
                    }
                }
            }
            finished = true;
            return null;
        }

        @Override
        protected void onProgressUpdate(MonsterUnit... mu) {
            super.onProgressUpdate(mu);
            syncPlayField();
            notifyListener(mu[0].getX(), mu[0].getY());
            notifyListener(mu[0].getShadowX(), mu[0].getShadowY());
        }
    }


/*    Thread monstersLiveThread = new Thread(this);

    public void startMonsters() {
        monstersThreadActive = true;
        monstersLiveThread.start();
    }

    @Override
    public void run() {
        Random randInt = new Random();
        while (monstersThreadActive) {
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (MonsterUnit mu : monstersOnField) {
                class Directions { public int x, y; Directions(int x, int y) { this.x = x; this.y = y; }}
                Directions[] directions = {
                        new Directions(-1, -1), new Directions(0, -1), new Directions(1, -1),
                        new Directions(-1, 0),                         new Directions(1,  0),
                        new Directions(-1, 1),  new Directions(0, 1),  new Directions(1, 1)
                };
                ArrayList<Directions> availDirections = new ArrayList<Directions>();
                for (Directions dir : directions) {
                    int x = mu.getX() + dir.x;
                    int y = mu.getY() + dir.y;
                    if (x >= 0 && y >=0 && x < getWidth() && y < getHeight() && getFieldSquare(x, y).getMonsterUnit() == null) {
                        availDirections.add(dir);
                    }
                }
                availDirections.add(new Directions(0, 0));
                Directions generatedDir = availDirections.get(randInt.nextInt(availDirections.size() - 1));
                mu.moveMonster(mu.getX() + generatedDir.x, mu.getY() + generatedDir.y);

                int generatedVulnerable = randInt.nextInt(99);
                if (generatedVulnerable < 70) { mu.setVulnerable(true); } else { mu.setVulnerable(false); }
                syncPlayField();
                notifyListener(mu.getX(), mu.getY());
            }
        }
    }
*/
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

    private void notifyListener(int x, int y) {
        if (null != FieldChangeListener) {
            FieldChangeListener.onFieldChange(x, y);
        }
    }

}
