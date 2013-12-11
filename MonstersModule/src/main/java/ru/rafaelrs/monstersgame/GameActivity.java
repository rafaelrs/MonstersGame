package ru.rafaelrs.monstersgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import ru.rafaelrs.monstersgame.model.PlayField;
import ru.rafaelrs.monstersgame.model.PlaySquare;
import ru.rafaelrs.monstersgame.view.MonstersView;

public class GameActivity extends Activity implements PlayField.OnGameOverListener  {

    private static class TrackTouchListener implements MonstersView.OnTouchListener {
        private final PlayField mPlayField;
        private final MonstersView mView;

        TrackTouchListener(PlayField playField, MonstersView mView) {
            mPlayField = playField;
            this.mView = mView;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    int squarex = mView.getXFieldPosition(event.getX());
                    int squarey = mView.getYFieldPosition(event.getY());
                    PlaySquare square = mPlayField.getFieldSquareData(squarex, squarey);
                    if (square.getMonsterUnit() == null || !square.getMonsterUnit().isVulnerable()) break;
                    mPlayField.destroyMonster(squarex, squarey);
                    break;
            }
            return false;
        }

    }
    /** Called when the activity is first created. */
    private PlayField fieldModel;
    private MonstersView monstersView;
    private int currentLevel = 1;
    private int playerScore = 0;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);

        // install the view
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        InitGame();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_NewGame) {
            fieldModel.stopMonsters();
            currentLevel = 1;
            playerScore = 0;
            InitGame();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitGame() {

        // measure optimal field size
        float displayWidth = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().xdpi;
        float displayHeight = getResources().getDisplayMetrics().heightPixels / getResources().getDisplayMetrics().ydpi;
        int fieldXSize = (int)(displayWidth / 0.3);
        int fieldYSize = (int)((displayHeight - 0.3) / 0.3);

        // creating field
        fieldModel = new PlayField(fieldXSize, fieldYSize);
        fieldModel.generateMonsters(currentLevel);
        fieldModel.setOnGameOverListener(this);
        setTitle("Monsters - Level " + currentLevel + ", score: " + playerScore);

        // setting created play field as data source for game view
        monstersView = (MonstersView) findViewById(R.id.playfield);
        monstersView.setPlayField(fieldModel);

        monstersView.setOnTouchListener(new TrackTouchListener(fieldModel, monstersView));

        fieldModel.startMonsters();

    }

    @Override
    public void onGameOver() {
        currentLevel++;
        InitGame();
    }

}
