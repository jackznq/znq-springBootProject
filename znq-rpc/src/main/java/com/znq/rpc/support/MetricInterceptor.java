package com.znq.rpc.support;


import com.google.common.collect.Maps;
import com.znq.common.codec.Request;
import com.znq.common.interceptor.AbstractInvokerInterceptor;
import com.znq.common.util.ForestUtil;
import com.znq.rpc.ForestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by znq on 2016/12/7.
 */
public class MetricInterceptor extends AbstractInvokerInterceptor
{

    private final static Logger LOGGER = LoggerFactory.getLogger(MetricInterceptor.class);

    private final static Map<String, Metric> metricsMap = Maps.newConcurrentMap();
    private final static String BEG_TIME = "begTime";

    final static ScheduledFuture<?> scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
            for (Map.Entry<String, Metric> stringMetricEntry : metricsMap.entrySet()) {
                Metric value = stringMetricEntry.getValue();
                LOGGER.info(" methodName:{}, current tps:{}, avgTime:{}, maxTime:{}, minTime:{} ",
                        stringMetricEntry.getKey(), value.getAndSet(), value.totalTime / 60, value.maxTime, value.minTime);
            }
        }
    }, 0, 1, TimeUnit.SECONDS);

    @Override
    public boolean beforeInvoke(Object target, Method method, Object... args) {
        ForestContext.putAttr(BEG_TIME, String.valueOf(System.currentTimeMillis()));
        return true;
    }


    @Override
    public boolean afterInvoke(Object target, Method method, Object result) {
        Long beginTime = Long.valueOf(ForestContext.getAttr(BEG_TIME));
        long exeTime = System.currentTimeMillis() - beginTime;
        Object content = ForestContext.getMessage().getContent();
        if (!(content instanceof Request)) {
            return true;
        }
        String key = ForestUtil.buildUri(((Request) content).getServiceName(), ((Request) content).getMethodName());
        Metric metric = metricsMap.get(key);
        if (metric == null) {
            synchronized (this) {
                metric = metricsMap.get(key);
                if (metric == null) {
                    metric = new Metric();
                    metricsMap.put(key, metric);
                }
            }
        }
        metric.incrementAndGetTPS();
        metric.exeTime(exeTime);
        return true;
    }

    class Metric {
        private int minTime;
        private int maxTime;
        private int totalTime;
        private AtomicLong tps = new AtomicLong(0);

        public long incrementAndGetTPS() {
            return tps.incrementAndGet();
        }


        public long getAndSet() {
            totalTime = 0;
            return tps.getAndSet(0);
        }

        public synchronized void exeTime(long currentTime) {
            if (currentTime < minTime || minTime == 0) {
                minTime = (int) currentTime;
            }
            if (currentTime > maxTime) {
                maxTime = (int) currentTime;
            }
            totalTime += currentTime;
        }

        public void setTotalTime(int totalTime) {
            this.totalTime = totalTime;
        }
    }

}

