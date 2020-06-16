package com.module.ui.widget.favor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

import com.module.ui.R;
import com.module.ui.util.BitmapUtils;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * 飘星星控件
 *
 */
public class FavorWidget3 extends SurfaceView implements View.OnClickListener {
    private static final int MAX_FAVOR_COUNT = 24;

    private BezierController3 mBezierController;
    private Bitmap[] mBitmaps;
    //    private boolean mUsingRemoteIcons;// 是否已经使用后台配置的icon
    private Random mRandom;
    private FavorSender mFavorSender;
    private long mLastClickTime;// 上一次点击时间
    private int mSendingCount = 0;// 单个发送点赞时间窗口内，点击的次数
    private boolean mIsPaused;

    private FavorHandler mFavorHandler;
    private Handler mHandler = new Handler();

    public FavorWidget3(Context context) {
        this(context, null, 0);
    }

    public FavorWidget3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FavorWidget3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setZOrderOnTop(true);
        setOnClickListener(this);

        mFavorHandler = new FavorHandler(this);

        mBezierController = new BezierController3(getHolder(), new Paint(Paint.ANTI_ALIAS_FLAG));
        mBezierController.setSleepDuration(BitmapBezierBean3.TIME_CYCLE);
        mRandom = new Random();


    }

    public void showFlyingAnim() {
        mHandler.removeCallbacksAndMessages(null);
        totalCount = 0;
        displayRandomFavorPassively(1);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayRandomFavorPassively(1);
            }
        }, 500);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayRandomFavorPassively(1);
            }
        }, 1000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayRandomFavorPassively(1);
            }
        }, 1500);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mBezierController != null) {
            mBezierController.stop();
        }

        if (mFavorHandler != null) {
            mFavorHandler.stop();
            mFavorHandler = null;
        }
        if (mBitmaps != null) {
            for (Bitmap bitmap : mBitmaps) {
                BitmapUtils.recycle(bitmap);
            }
        }
        mBitmaps = null;

        mRandom = null;
        mFavorSender = null;
        mHandler.removeCallbacksAndMessages(null);
    }

    public void setBitmaps(Bitmap[] bitmaps, boolean isRemoteIcons) {
        mBitmaps = bitmaps;
//        mUsingRemoteIcons = isRemoteIcons;
    }

    /**
     * addFavor方法，均是主动点击进行点赞
     */
    private void addRandomFavor() {
        if (mBitmaps == null || mBitmaps.length == 0) {
            return;
        }
        if (mBitmaps.length == 1) {
            addFavor(mBitmaps[0]);
            return;
        }
        if (mRandom == null) {
            mRandom = new Random();
        }
        Bitmap bitmap = null;
        while (bitmap == null) {
            bitmap = mBitmaps[mRandom.nextInt(mBitmaps.length)];
        }
        addFavor(bitmap);
    }

    private void addFavor(Bitmap bitmap) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        width = width <= 0 ? 40 : width;
        int startX = width / 2;
        int startY = height - bitmap.getHeight() / 2;
        int endX = mRandom.nextInt(width);
        int endY = 0;
        FavorBean favor = new FavorBean(mBezierController,
                new Point(startX, startY), new Point(endX, endY), bitmap, mRandom);
        addFavor(favor);
    }

    private void addFavor(FavorBean favor) {
        if (mBezierController.getBeanCount() < MAX_FAVOR_COUNT) {
            mBezierController.addBean(favor);
        }
    }

    /**
     * 被动点赞，断续抛出爱心
     *
     * @param count
     */
    private int totalCount = 0;

    public void displayRandomFavorPassively(int count) {
        if (mIsPaused) {
            return;
        }

        totalCount += count;
        if (totalCount > 64) {
            totalCount = 64;
        }
        if(mFavorHandler != null){
            mFavorHandler.removeMessages(0);
            mFavorHandler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onClick(View v) {
//        // 本地可以连续点赞，但每隔250ms才给远端点赞
//        addRandomFavor();
//        long now = System.currentTimeMillis();
//        mSendingCount++;
//        if (now - mLastClickTime >= 2000L) {
//            mLastClickTime = now;
//            if (mFavorSender != null) {
//                mFavorSender.onSendFavor(mSendingCount);
//            }
//            mSendingCount = 0;
//        }
    }

    public void pause() {
        mIsPaused = true;
        if (mBezierController != null) {
            mBezierController.pause(true);
        }
    }

    public void resume() {
        mIsPaused = false;
    }

    public void setFavorSender(FavorSender sender) {
        mFavorSender = sender;
    }

    public interface FavorSender {
        void onSendFavor(int count);
    }

    public static class FavorBean extends BitmapBezierBean3 {

        FavorBean(BezierController3 controller, Point start, Point end, Bitmap bitmap,
                  Random random) {
            super(controller, start, end, bitmap);
            mRandom = random;
            start();
        }

        @Override
        protected void onStart() {
            Random random = mRandom;
            if (random == null) {
                return;
            }

            // 平移
            int centerX = random.nextInt(mStart.x * 2);
            int centerY = Math.abs(mEnd.y - mStart.y) / 2;
            mCenter = new Point(centerX, centerY);
        }
    }

    private static class FavorHandler extends Handler {
        private WeakReference<FavorWidget3> mFavorWidget;

        public FavorHandler(FavorWidget3 widget) {
            mFavorWidget = new WeakReference<>(widget);
        }

        public void stop() {
            mFavorWidget = null;
        }

        @Override
        public void handleMessage(Message msg) {
            FavorWidget3 widget;
            if (mFavorWidget != null && (widget = mFavorWidget.get()) != null) {
                Bitmap bitmap1 = BitmapFactory.decodeResource(widget.getResources(), R.drawable.love_zone_pic_sweetness_love1, null);
                Bitmap bitmap2 = BitmapFactory.decodeResource(widget.getResources(), R.drawable.love_zone_pic_sweetness_love2, null);
                Bitmap bitmap3 = BitmapFactory.decodeResource(widget.getResources(), R.drawable.love_zone_pic_sweetness_love3, null);
                Bitmap bitmap4 = BitmapFactory.decodeResource(widget.getResources(), R.drawable.love_zone_pic_sweetness_love4, null);
                Bitmap[] remoteIcons = {bitmap1, bitmap2, bitmap3, bitmap4};
                if (remoteIcons != null) {
                    widget.setBitmaps(remoteIcons, true);
                }

                if (!widget.mIsPaused && widget.totalCount > 0) {
                    widget.addRandomFavor();

                    sendEmptyMessageDelayed(0, 200L);

                    --widget.totalCount;
                }
            }
        }
    }
}