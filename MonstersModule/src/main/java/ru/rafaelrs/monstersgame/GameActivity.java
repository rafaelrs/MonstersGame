package ru.rafaelrs.monstersgame;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import ru.rafaelrs.monstersgame.model.PlayField;
import ru.rafaelrs.monstersgame.model.PlaySquare;
import ru.rafaelrs.monstersgame.view.MonstersView;

public class GameActivity extends Activity implements PlayField.OnGameOverListener  {

    private class TrackTouchListener implements MonstersView.OnTouchListener {
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
                    GameActivity.this.monstersScore = (int)(GameActivity.this.monstersScore + 100 * GameActivity.this.measureScoreMultiplier());
                    GameActivity.this.publishGameState();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.destroy);
                            mediaPlayer.start();
                            while (mediaPlayer.isPlaying());
                            mediaPlayer.release();
                        }
                    }).start();
                    mPlayField.destroyMonster(squarex, squarey);
                    break;
            }
            return false;
        }

    }
    /** Called when the activity is first created. */
    private PlayField fieldModel;
    private MonstersView monstersView;
    public int currentLevel = 1;
    public int playerScore = 0, monstersScore = 0;
    private long levelStartTime;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);

        // install the view
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
            currentLevel = 1;
            playerScore = 0;
            fieldModel.fieldClear();
            InitGame();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitGame() {

        if (currentLevel % 2 == 0) {
            findViewById(R.id.root).setBackgroundResource(R.drawable.background2);
        } else {
            findViewById(R.id.root).setBackgroundResource(R.drawable.background1);
        }

        // measure optimal field size
        float displayWidth = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().xdpi;
        float displayHeight = getResources().getDisplayMetrics().heightPixels / getResources().getDisplayMetrics().ydpi;
        int fieldXSize = (int)(displayWidth / 0.3);
        int fieldYSize = (int)((displayHeight - 0.3) / 0.3);

        // creating field
        fieldModel = new PlayField(fieldXSize, fieldYSize);
        fieldModel.setGameLevel(currentLevel);
        fieldModel.generateMonsters();
        fieldModel.setOnGameOverListener(this);

        // setting created play field as data source for game view
        monstersView = (MonstersView) findViewById(R.id.playfield);
        monstersView.setPlayField(fieldModel);

        monstersView.setOnTouchListener(new TrackTouchListener(fieldModel, monstersView));

        levelStartTime = System.currentTimeMillis();
        monstersScore = 0;
        publishGameState();
    }

    public void publishGameState() {
        TextView levelView = (TextView) findViewById(R.id.current_level);
        levelView.setText("" + currentLevel);
        TextView scoreView = (TextView) findViewById(R.id.game_score);
        int summaryScore = playerScore + monstersScore;
        scoreView.setText("" + summaryScore);
    }

    public float measureScoreMultiplier() {
        float timeMultiplier = (((5 + (float)currentLevel) * 10000) / (1 + System.currentTimeMillis() - levelStartTime)) / 20; // On more level more time starting from 60 seconds
        float levelMultiplier = ((5 + (float)currentLevel) / 5); // On more level more score starting from 1.2
        return timeMultiplier * levelMultiplier;
    }

    private void showMessage(String title, String msg) {
        Intent descriptionIntent = new Intent(this, MessageActivity.class);
        descriptionIntent.putExtra(MessageActivity.KEY_TITLE, title);
        descriptionIntent.putExtra(MessageActivity.KEY_MESSAGE, msg);
        startActivity(descriptionIntent);
    }

    @Override
    public void onGameOver() {
        int levelBonus = (int)(1000 * GameActivity.this.measureScoreMultiplier());
        playerScore = playerScore + levelBonus + monstersScore;
        currentLevel++;
        InitGame();
    }

}
