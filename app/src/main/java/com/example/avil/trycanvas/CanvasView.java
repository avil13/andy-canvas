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
    volatile private Matrix matrixStiker;
    private Matrix matrixRotate;

    private Bitmap bitmap;
    private Canvas canvas;
    private float mX, mY;
    private float sX, sY;
    private double startAngle;

    private Path path;
    private Paint paint;
    private static final float TOLERANCE = 5;
    private Context context;

    private volatile long lastCalled = 0;
    private int interval = 200;
    private double oldX, oldY;
    int rotate = 0;

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
        matrixRotate = new Matrix();
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

//        canvas.drawPath(path, paint);
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
                if (event.getPointerCount() == 1) {
                    setXY(x, y);
                }
//                startTouch(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {
//                    if (lastCalled + interval < System.currentTimeMillis()) {
//                        lastCalled = System.currentTimeMillis();
//
//                        getAngle(x, y);
//                    }
                } else {
                    setXY(x, y);
                }
//                moveTouch(x, y);
                break;


            case MotionEvent.ACTION_UP:
//                setXY(x, y);
                //                upTouch();

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
//        bg = getResizedBitmap(bg, canvas.getHeight(), canvas.getWidth());

        canvas.drawBitmap(background, matrixBg, null);
        invalidate();
    }


    public void setXY(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            this.sX = x;
            this.sY = y;
//            invalidate();
        }
    }


    public void drawStiker(Canvas canvas, float x, float y) {
        float nX = x - (stiker.getWidth() / 2);
        float nY = y - (stiker.getHeight() / 2);

        matrixStiker.setTranslate(nX, nY);
        matrixStiker.postRotate(rotate, x, y);

        canvas.drawBitmap(stiker, matrixStiker, null);
    }

    //
    private void rotateDialer(float degrees) {
        rotate = (int) degrees;
        invalidate();
        //        matrixRotate.reset();
//        matrixRotate.postRotate(degrees);
//        stiker = Bitmap.createBitmap(stiker, 0, 0, stiker.getWidth(), stiker.getHeight(), matrixRotate, false);
        //        dialer.setImageBitmap(Bitmap.createBitmap(imageScaled, 0, 0, imageScaled.getWidth(), imageScaled.getHeight(), matrix, true));
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


    public void rotate(boolean r){
        if(r){
            rotate += 15;
        }else{
            rotate -= 15;
        }
    }

    /**
     * @return The angle of the unit circle with the image view's center
     */
    private void getAngle(double xTouch, double yTouch) {
        if ((oldX + 40d) < xTouch) {
            oldX = xTouch;
            rotate  = 90;
            return;
        }
        if ((oldX + 20d) < xTouch) {
            oldX = xTouch;
            rotate = 45;
            return;
        }
        if ((oldX - 40d) > xTouch) {
            oldX = xTouch;
            rotate  = 270;
            return;
        }
        if ((oldX - 20d) > xTouch) {
            oldX = xTouch;
            rotate  = 315;
            return;
        }
        rotate = 0;

//        double x = xTouch - (stiker.getWidth() / 2d);
//        double y = stiker.getHeight() - yTouch - (stiker.getHeight() / 2d);

//
//        double res = 0d;
//
//        switch (getQuadrant(x, y)) {
//            case 1:
//                res = Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
//                break;
//            case 2:
//                res = 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
//                break;
//            case 3:
//                res = 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
//                break;
//            case 4:
//                res = 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
//                break;
//        }
//        Log.i("===", String.valueOf(res));
//        return res;
    }

    /**
     * @return The selected quadrant.
     */
    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

}







