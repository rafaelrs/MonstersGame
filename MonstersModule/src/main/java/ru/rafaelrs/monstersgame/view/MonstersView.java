package ru.rafaelrs.monstersgame.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import ru.rafaelrs.monstersgame.R;
import ru.rafaelrs.monstersgame.model.PlayField;
import ru.rafaelrs.monstersgame.model.PlaySquare;

public class MonstersView extends View implements PlayField.FieldChangeListener {

    private volatile PlayField playField;
    private float squareWidth;
    private float squareHeight;

    /**
     * @param context the rest of the application
     */
    public MonstersView(Context context) {
        super(context);
        setFocusableInTouchMode(true);
    }

    /**
     * @param context
     * @param attrs
     */
    public MonstersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusableInTouchMode(true);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public MonstersView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusableInTouchMode(true);
    }

    /**
     * @param playField
     */
    public void setPlayField(PlayField playField) {
        this.playField = playField;
        this.playField.setFieldChangeListener(this);
        invalidate();
    }

    public int getXFieldPosition(float x) {
        return (int)(squareWidth == 0 ? 0 : x / squareWidth);
    }

    public int getYFieldPosition(float y) {
        return (int)(squareHeight == 0 ? 0 : y / squareHeight);
    }

    public void onFieldChange(int x, int y) {
        int rectLeft    = (int)(x * squareWidth);
        int rectTop     = (int)(y * squareHeight);
        int rectRight   = rectLeft + (int)squareWidth;
        int rectBottom  = rectTop + (int)squareHeight;

        Rect rectToModify = new Rect(rectLeft, rectTop, rectRight, rectBottom);
        invalidate(rectToModify);

        /*this.post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        squareWidth = getWidth() / playField.getWidth();
        squareHeight = getHeight() / playField.getHeight();
    }

    /**
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override protected void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(hasFocus() ? Color.BLUE : Color.GRAY);
        canvas.drawRect(0, 0, getWidth() - 1, getHeight() -1, paint);

        if (null == playField) { return; }

        /*paint.setColor(Color.BLACK);
        for (int y = 0; y < playField.getHeight(); y++) {
            canvas.drawLine(0, y * squareHeight, getWidth(), y * squareHeight, paint);
        }
        for (int x = 0; x < playField.getWidth(); x++) {
            canvas.drawLine(x * squareWidth, 0, x * squareWidth, getHeight(), paint);
        }*/

        paint.setStyle(Paint.Style.FILL);
        for (int y = 0; y < playField.getHeight(); y++) {
            for (int x = 0; x < playField.getWidth(); x++) {
                PlaySquare square = playField.getFieldSquareData(x, y);
                if (square != null && square.getMonsterUnit() != null) {
                    if (square.getMonsterUnit().isVulnerable()) {
                        paint.setColor(Color.YELLOW);
                    } else {
                        paint.setColor(Color.GREEN);
                    }
                    float monsterX = x * squareWidth;
                    float monsterY = y * squareHeight;
                    canvas.drawRect(monsterX + 1, monsterY + 1, monsterX + squareWidth - 1, monsterY + squareHeight - 1, paint);
                    Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                    canvas.drawBitmap(mBitmap, monsterX + 1, monsterY + 1, paint);
                }
            }
        }
    }
}
