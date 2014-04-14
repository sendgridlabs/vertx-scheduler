package com.sendgrid.labs.vertx.schedule.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;

import com.sendgrid.labs.vertx.schedule.Scheduler;
import com.sendgrid.labs.vertx.schedule.WeekTimeSpec;

public class SchedulerImpl extends Scheduler {

    public SchedulerImpl(Vertx vertx, TimeZone tz, DstAheadBehavior ahead, DstBackBehavior back) {
        this.vertx = vertx;
        this.tz = tz;
        this.nextTimerId = 0;
        this.timers = new HashMap<Long, TimerData>();
        this.aheadBehavior = ahead;
        this.backBehavior = back;
    }

    public void stop() {
        for(TimerData timer : timers.values()) {
            vertx.cancelTimer(timer.vertxTimerId);
        }
        timers.clear();
    }

    public long setTimer(WeekTimeSpec time, Handler<java.lang.Long> handler) {
        return start(false, time.getWeekMs(), handler);
    }

    public long setPeriodic(WeekTimeSpec time, Handler<java.lang.Long> handler) {
        return start(true, time.getWeekMs(), handler);
    }

    public void cancelTimer(long id) {
        if(timers.containsKey(id)) {
            TimerData timer = timers.get(id);
            vertx.cancelTimer(timer.vertxTimerId);
            timers.remove(id);
        }
    }


    private long start(final boolean periodic, int week_ms, final Handler<java.lang.Long> handler) {
        final TimerData data = new TimerData();
        final long myTimerId = nextTimerId++;

        final SchedulerLogic logic = new SchedulerLogic(tz, new Date(), week_ms, aheadBehavior, backBehavior);

        final Handler<java.lang.Long> timerCB = new Handler<java.lang.Long>() {
            public void handle(Long vertxTimerId) {
                handler.handle(myTimerId);
                if(periodic) {
                    Date d = logic.next();
                    data.vertxTimerId = vertx.setTimer(Utils.getMsUntilDate(logic.next()), this);
                } else {
                    timers.remove(myTimerId);
                }
            }
        };

        data.vertxTimerId = vertx.setTimer(Utils.getMsUntilDate(logic.next()), timerCB);
        timers.put(myTimerId, data);

        return myTimerId;
    }

    private class TimerData {
        Long vertxTimerId;
    };

    private Vertx vertx;
    private TimeZone tz;
    private long nextTimerId;
    private HashMap<Long, TimerData> timers;
    private DstAheadBehavior aheadBehavior;
    private DstBackBehavior backBehavior;
}

