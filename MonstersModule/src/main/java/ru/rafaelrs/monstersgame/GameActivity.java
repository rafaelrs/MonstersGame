package ru.rafaelrs.monstersgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import ru.rafaelrs.monstersgame.model.PlayField;
import ru.rafaelrs.monstersgame.model.PlaySquare;
import ru.rafaelrs.monstersgame.view.MonstersView;

public class GameActivity extends Activity {

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
                    square.setOccupancy(!square.getOccupancy());
                    mPlayField.setFieldSquareData(squarex, squarey, square);
                    break;
            }
            return false;
        }

    }
    /** Called when the activity is first created. */
    private PlayField fieldModel;
    private MonstersView monstersView;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);

        // install the view
        setContentView(R.layout.main);

        // measure optimal field size
        float displayWidth = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().xdpi;
        float displayHeight = getResources().getDisplayMetrics().heightPixels / getResources().getDisplayMetrics().ydpi;
        int fieldXSize = (int)(displayWidth / 0.3);
        int fieldYSize = (int)((displayHeight - 0.5) / 0.3);

        // creating field
        fieldModel = new PlayField(fieldXSize, fieldYSize);

        // setting created play field as data source for game view
        monstersView = (MonstersView) findViewById(R.id.playfield);
        monstersView.setPlayField(fieldModel);

        monstersView.setOnTouchListener(new TrackTouchListener(fieldModel, monstersView));

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

/*
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                int squarex = monstersView.getXFieldPosition(event.getX());
                int squarey = monstersView.getYFieldPosition(event.getY());
                fieldModel.getFieldSquareData(squarex, squarey).setOccupancy(!fieldModel.getFieldSquareData(squarex, squarey).getOccupancy());
                break;
        }
        return false;
    }
*/
}
