package com.example.game2048;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


public class GameItem extends View {
    private int mNumber;
    private String mNumberVal;
    private Paint mPaint;
    private Rect mRect;//绘制文字区域

    public GameItem(Context context) {
        this(context,null);
    }

    public GameItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GameItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String mBgColor = "#CCC0B3";
        switch (mNumber)
        {
            case 0:
                mBgColor = "#CCC0B3";
                break;
            case 2:
                mBgColor = "#EEE4DA";
                break;
            case 4:
                mBgColor = "#EDE0C8";
                break;
            case 8:
                mBgColor = "#F2B179";
                break;
            case 16:
                mBgColor = "#F49563";
                break;
            case 32:
                mBgColor = "#F5794D";
                break;
            case 64:
                mBgColor = "#F55D37";
                break;
            case 128:
                mBgColor = "#EEE863";
                break;
            case 256:
                mBgColor = "#EDB04D";
                break;
            case 512:
                mBgColor = "#ECB04D";
                break;
            case 1024:
                mBgColor = "#EB9437";
                break;
            case 2048:
                mBgColor = "#EA7821";
                break;
            default:
                mBgColor = "#EA7821";
                break;
        }
        mPaint.setColor(Color.parseColor(mBgColor));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);//宽高由layout决定

        if (mNumber != 0){
            drawText(canvas);
        }
    }

    private void drawText(Canvas mCanvas){
        mPaint.setColor(Color.BLACK);
        float x = (getWidth() - mRect.width()) / 2;
        float y = (getHeight() + mRect.height()) / 2;
        //值得注意的是，y是text的下边际，x为起始位置
        mCanvas.drawText(mNumberVal,x,y,mPaint);
    }

    public void setNumber(int number){
        this.mNumber = number;
        mNumberVal = mNumber + "";
        mPaint.setTextSize(30.0f);
        mRect = new Rect();
        mPaint.getTextBounds(mNumberVal, 0, mNumberVal.length(), mRect);
        invalidate();
    }

    public int getNumber(){
        return mNumber;
    }
}
