package com.example.avil.trycanvas;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by avil on 08.09.17.
 */

public class CanvasView extends View {

    public int width;
    public int height;


    private Bitmap background;
    private Bitmap stiker;
    private Matrix matrixBg;
    private Matrix matrixStiker;

    private Bitmap bitmap;
    private Canvas canvas;
    private float mX, mY;
    private float sX, sY;

    private Path path;
    private Paint paint;
    private static final float TOLERANCE = 5;
    private Context context;

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        path = new Path();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4f);

        matrixBg = new Matrix();
        matrixStiker = new Matrix();
//        matrixStiker.reset();
//        matrixStiker.setScale(0.2f, 0.2f);

        stiker = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_8);

        stiker = getResizedBitmap(
                stiker,
                stiker.getHeight() / 4,
                stiker.getWidth() / 4
                );

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBgImg(canvas);
        drawStiker(canvas, sX, sY);
        invalidate();

        canvas.drawPath(path, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        background = getResizedBitmap(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_stars_center),
                canvas.getHeight(),
                canvas.getWidth()
        );
    }

    private void startTouch(float x, float y) {
        path.moveTo(x, y);
        mX = x;
        mY = y;
    }


    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }


    public void clearCanvas() {
        path.reset();
        invalidate();
    }


    private void upTouch() {
        path.lineTo(mX, mY);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setXY(x, y);
//                startTouch(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                setXY(x, y);
//                moveTouch(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                setXY(x, y);
                //                upTouch();
                invalidate();
                break;

        }
        return true;
    }


    // перемещение стикера
    // определяем коснулся ли тач стикера
    // если да, то находим координату которой коснулся
    // если идет претягивание то смещаем стикер на разницу координат при перетягивании

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void drawBgImg(Canvas canvas) {
//        Bitmap bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_stars_center);
//
//        bg = getResizedBitmap(bg, canvas.getHeight(), canvas.getWidth());

        canvas.drawBitmap(background, matrixBg, null);
        invalidate();
    }


    public void setXY(float x, float y) {
        this.sX = x;
        this.sY = y;
    }


    public void drawStiker(Canvas canvas, float x, float y) {
        float nX = x - (stiker.getWidth() / 2);
        float nY = y - (stiker.getHeight() / 2);

        matrixStiker.reset();
        matrixStiker.setTranslate(nX, nY);

        canvas.drawBitmap(stiker, matrixStiker, null);
    }


    //
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;

    }

}







