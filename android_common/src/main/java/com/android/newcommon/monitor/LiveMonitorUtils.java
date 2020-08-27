package com.android.newcommon.monitor;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.R;
import com.android.newcommon.utils.DisplayUtil;

import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.concurrent.ScheduledExecutorService;

import androidx.core.content.ContextCompat;

/**
 * 直播监控显示
 */

public class LiveMonitorUtils {
    private static AppMonitor mAppMonitor;
    private static LinearLayout layoutLog;
    private static TextView tvFps, tvCpu, tvMemory;
    private static final DecimalFormat decimal = new DecimalFormat("#.0' fps '");
    private static final DecimalFormat cpuDecimal = new DecimalFormat("  cpu:#0.0' % '");
    private static final DecimalFormat memoryDecimal = new DecimalFormat("  memory:#.0' M'");

    /**
     * 打开性能显示
     *
     * @param context
     * @param mContentView
     */
    public static void startLiveMonitor(Context context, ViewGroup mContentView) {
        if (mAppMonitor == null) {
            mAppMonitor = new AppMonitor();
            layoutLog = new LinearLayout(context);
            layoutLog.setBackgroundColor(ContextCompat.getColor(context, R.color.black));

            int dp25 = DisplayUtil.dpToPx(context, 20);
            tvFps = new TextView(context);
            tvFps.setTextColor(ContextCompat.getColor(context, R.color.white));
            tvFps.setTextSize(16);
            tvFps.setPadding(dp25, dp25, 0, dp25);
            layoutLog.addView(tvFps);

            tvCpu = new TextView(context);
            tvCpu.setTextColor(ContextCompat.getColor(context, R.color.white));
            tvCpu.setTextSize(16);
            tvCpu.setPadding(0, dp25, 0, dp25);
            layoutLog.addView(tvCpu);

            tvMemory = new TextView(context);
            tvMemory.setTextColor(ContextCompat.getColor(context, R.color.white));
            tvMemory.setTextSize(16);
            tvMemory.setPadding(0, dp25, dp25, dp25);
            layoutLog.addView(tvMemory);

            mContentView.addView(layoutLog, 0);
        }

        mAppMonitor.startLogMonitorFps(new MonitorCallback() {
            @Override
            public void framePerSecond(double fps) {
                if (tvFps != null) {
                    Log.w("wangwei", "fps--" + fps);
                    tvFps.setText(decimal.format(fps));
                }
            }

            @Override
            public void cpuRate(final float rate) {
                Log.w("wangwei", "cpuRate--" + rate);
                if (tvCpu != null) {
                    tvCpu.post(new Runnable() {
                        @Override
                        public void run() {
                            if (tvCpu != null) {
                                tvCpu.setText(cpuDecimal.format(rate));
                            }
                        }
                    });
                }
            }

            @Override
            public void appMemory(final long memory) {
                Log.w("wangwei", "memory--" + memory);
                if (tvMemory != null) {
                    tvMemory.post(new Runnable() {
                        @Override
                        public void run() {
                            if (tvMemory != null) {
                                tvMemory.setText(memoryDecimal.format((double) memory / 1024));
                            }
                        }
                    });
                }
            }
        });
    }



    /**
     * 开始性能检测
     *
     * @param callback
     */
    public static void startAppMonitorCheck(CheckMonitorCallback callback) {
        if (mAppMonitor == null) {
            mAppMonitor = new AppMonitor();
        }
        mAppMonitor.startMonitorFps(callback);
    }








}
