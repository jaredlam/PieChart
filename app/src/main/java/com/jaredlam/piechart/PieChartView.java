package com.jaredlam.piechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jaredluo on 15/12/24.
 */
public class PieChartView extends View {


    private RectF mOuterOval;
    private RectF mInnerOval;
    private Path mOuterAPath;
    private Path mOuterBPath;
    private Path mOuterCPath;
    private Paint mBluePaint;
    private Paint mGreenPaint;
    private Paint mRedPaint;
    private Paint mWhitePaint;
    private Point mCenter;
    private Timer mTimer;
    private float mCurrentAngle;
    private Handler mHandler;

    private float mTotal = 56f;
    private float mPartA = 28;
    private float mPartB = 14;
    private float mPartC = 14;
    private float mADegree;
    private float mBDegree;
    private float mCDegree;
    private Paint mPaint;

    private boolean mDrawing = false;
    private int mStep = 5;
    private int mThickness;


    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mADegree = mPartA / mTotal * 360;
        mBDegree = mADegree + mPartB / mTotal * 360;
        mCDegree = mBDegree + mPartC / mTotal * 360;

        mThickness = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getContext().getResources()
                .getDisplayMetrics());

        mCenter = new Point();
        mOuterOval = new RectF();
        mInnerOval = new RectF();

        mOuterAPath = new Path();
        mOuterBPath = new Path();
        mOuterCPath = new Path();

        mGreenPaint = new Paint();
        mGreenPaint.setColor(Color.GREEN);
        mGreenPaint.setAntiAlias(true);

        mBluePaint = new Paint();
        mBluePaint.setColor(Color.BLUE);
        mBluePaint.setAntiAlias(true);

        mRedPaint = new Paint();
        mRedPaint.setColor(Color.RED);
        mRedPaint.setAntiAlias(true);


        mWhitePaint = new Paint();
        mWhitePaint.setColor(Color.WHITE);
        mWhitePaint.setAntiAlias(true);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mDrawing) {
                    invalidate();
                    setTimer();
                }
            }
        };

        mHandler.sendEmptyMessage(0);
        mDrawing = true;

    }

    private void setTimer() {
        long delay = (long) (5.0f * (mCurrentAngle / 360.0f));
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, delay);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mDrawing) {
            return;
        }

        if (mCurrentAngle < mADegree) {
            if (mPaint == null) {

                mCenter.set(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
                float outerRadius = getMeasuredWidth() / 2;
                mOuterOval.set(mCenter.x - outerRadius, mCenter.y - outerRadius, mCenter.x + outerRadius, mCenter.y + outerRadius);
                float innerRadius = getMeasuredWidth() / 2 - mThickness;
                mInnerOval.set(mCenter.x - innerRadius, mCenter.y - innerRadius, mCenter.x + innerRadius, mCenter.y + innerRadius);

                mOuterAPath.moveTo(mCenter.x, mCenter.y);
            }
            mPaint = mGreenPaint;
            mOuterAPath.arcTo(mOuterOval, mCurrentAngle, mStep);
            canvas.drawPath(mOuterAPath, mPaint);
        } else if (mCurrentAngle < mBDegree) {
            if (mPaint == mGreenPaint) {
                mOuterBPath.moveTo(mCenter.x, mCenter.y);
            }
            mPaint = mBluePaint;
            mOuterBPath.arcTo(mOuterOval, mCurrentAngle, mStep);
            canvas.drawPath(mOuterAPath, mGreenPaint);
            canvas.drawPath(mOuterBPath, mPaint);
        } else {
            if (mPaint == mBluePaint) {
                mOuterCPath.moveTo(mCenter.x, mCenter.y);
            }
            mPaint = mRedPaint;
            mOuterCPath.arcTo(mOuterOval, mCurrentAngle, mStep);
            canvas.drawPath(mOuterAPath, mGreenPaint);
            canvas.drawPath(mOuterBPath, mBluePaint);
            canvas.drawPath(mOuterCPath, mPaint);
        }

        if (mCurrentAngle + mStep >= 360) {
            mStep = (int) (360 - mCurrentAngle);
            mDrawing = false;
        } else {
            mCurrentAngle += mStep;
        }
        canvas.drawOval(mInnerOval, mWhitePaint);
    }
}
