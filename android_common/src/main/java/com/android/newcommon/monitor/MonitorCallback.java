package com.android.newcommon.monitor;

/**
 * @author wangwei
 * @date 2020/8/27.
 */
public interface MonitorCallback {
    void framePerSecond(double fps);

    void cpuRate(float rate);

    void appMemory(long memory);
}
