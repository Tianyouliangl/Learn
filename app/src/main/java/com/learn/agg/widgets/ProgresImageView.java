package com.learn.agg.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.makeramen.roundedimageview.RoundedImageView;

public class ProgresImageView extends RoundedImageView {
    private Paint mPaint;
    private int mProgress;

    public ProgresImageView(Context context) {
        super(context,null);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public ProgresImageView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public ProgresImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();
        mPaint.setColor(Color.parseColor("#70000000"));// 半透明
        canvas.drawRect(0, 0, getWidth(), getHeight() - getHeight() * mProgress
                / 100, mPaint);// 绘制上半部分的半透明矩形区域（左上，右下，画笔）

        mPaint.setColor(Color.parseColor("#00000000"));// 全透明
        canvas.drawRect(0, getHeight() - getHeight() * mProgress / 100,
                getWidth(), getHeight(), mPaint);// 绘制下半部分的全透明矩形区域

        mPaint.setTextSize(45);
        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setStrokeWidth(2);

//        Rect rect = new Rect();
//        mPaint.getTextBounds("100%", 0, "100%".length(), rect);// 确定文字区域
//        canvas.drawText(mProgress + "%", getWidth() / 2 - rect.width() / 2,
//                getHeight() / 2, mPaint);//绘制中间上传百分比文字
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        postInvalidate();//刷新界面，重新运行onDraw()方法
    }
}
