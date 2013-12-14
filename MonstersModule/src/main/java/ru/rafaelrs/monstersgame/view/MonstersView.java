package ru.rafaelrs.monstersgame.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import ru.rafaelrs.monstersgame.GameActivity;
import ru.rafaelrs.monstersgame.R;
import ru.rafaelrs.monstersgame.model.PlayField;
import ru.rafaelrs.monstersgame.model.PlaySquare;

public class MonstersView extends View implements PlayField.FieldChangeListener {

    private volatile PlayField playField;
    private float squareWidth;
    private float squareHeight;
    private Bitmap mMonsterYellowImage;
    private Bitmap mMonsterGreenImage;
    private Bitmap mCellImage;

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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        squareWidth = getWidth() / playField.getWidth();
        squareHeight = getHeight() / playField.getHeight();

        if (squareHeight > 0 && squareWidth > 0) {
            mMonsterYellowImage = BitmapFactory.decodeResource(getResources(), R.drawable.monster_yellow);
            mMonsterGreenImage = BitmapFactory.decodeResource(getResources(), R.drawable.monster_green);
            int monsterHeight = (int)squareHeight - 1;
            int monsterWidth = (int)(monsterHeight * mMonsterYellowImage.getWidth() / mMonsterYellowImage.getHeight());
            mMonsterYellowImage = Bitmap.createScaledBitmap(mMonsterYellowImage, monsterWidth, monsterHeight, false);
            mMonsterGreenImage = Bitmap.createScaledBitmap(mMonsterGreenImage, monsterWidth, monsterHeight, false);
            mMonsterYellowImage.prepareToDraw();
            mMonsterGreenImage.prepareToDraw();

            mCellImage = BitmapFactory.decodeResource(getResources(), R.drawable.cell);
            mCellImage = Bitmap.createScaledBitmap(mCellImage, (int)(squareWidth - 2), (int)(squareHeight - 2), false);
            mCellImage.prepareToDraw();
        }
    }

    /**
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override protected void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        /*paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(new RectF(0, 0, getWidth() - 1, getHeight() - 1), 20, 20, paint);*/

        if (null == playField) { return; }

        /*paint.setColor(Color.BLACK);
        Paint cellPaint = new Paint(paint);
        cellPaint.setColor(Color.argb(255, 220, 220, 220));
        for (int y = 1; y < playField.getHeight(); y++) {
            canvas.drawLine(5, y * squareHeight, getWidth() - 6, y * squareHeight, paint);
            canvas.drawLine(5, y * squareHeight + 1, getWidth() - 6, y * squareHeight + 1, cellPaint);
        }
        for (int x = 1; x < playField.getWidth(); x++) {
            canvas.drawLine(x * squareWidth, 5, x * squareWidth, getHeight() - 6, paint);
            canvas.drawLine(x * squareWidth + 1, 5, x * squareWidth + 1, getHeight() - 6, cellPaint);
        }*/

        Paint cellPaint = new Paint(paint);
        cellPaint.setAlpha(76);
        /*paint.setStyle(Paint.Style.FILL);*/
        for (int y = 0; y < playField.getHeight(); y++) {
            for (int x = 0; x < playField.getWidth(); x++) {
                PlaySquare square = playField.getFieldSquareData(x, y);
                float monsterX = x * squareWidth;
                float monsterY = y * squareHeight;

                canvas.drawBitmap(mCellImage, monsterX + 1, monsterY + 1, cellPaint);
                if (square != null && square.getMonsterUnit() != null) {
                    Bitmap mBitmap;
                    if (square.getMonsterUnit().isVulnerable()) {
                        //paint.setColor(Color.YELLOW);
                        mBitmap = mMonsterYellowImage;
                    } else {
                        //paint.setColor(Color.GREEN);
                        mBitmap = mMonsterGreenImage;
                    }
                    //canvas.drawRect(monsterX + 1, monsterY + 1, monsterX + squareWidth - 1, monsterY + squareHeight - 1, paint);
                    canvas.drawBitmap(mBitmap, monsterX + 2 + (squareWidth - mBitmap.getWidth() - 1) / 2, monsterY + 2, paint);
                }
            }
        }
    }
}
