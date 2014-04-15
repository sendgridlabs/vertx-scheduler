package com.sendgrid.labs.vertx.schedule;

import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;

class SchedulerImpl extends Scheduler {

    public SchedulerImpl(Vertx vertx) {
        this.vertx = vertx;
        this.nextTimerId = 0;
        this.timers = new HashMap<Long, TimerData>();
    }

    public void stop() {
        for(TimerData timer : timers.values()) {
            vertx.cancelTimer(timer.vertxTimerId);
        }
        timers.clear();
    }

    public long setTimer(TimeOfWeek time, Handler<java.lang.Long> handler) {
        return start(false, time, handler);
    }

    public long setPeriodic(TimeOfWeek time, Handler<java.lang.Long> handler) {
        return start(true, time, handler);
    }

    public void cancelTimer(long id) {
        if(timers.containsKey(id)) {
            TimerData timer = timers.get(id);
            vertx.cancelTimer(timer.vertxTimerId);
            timers.remove(id);
        }
    }


    private long start(final boolean periodic, TimeOfWeek time, final Handler<java.lang.Long> handler) {
        final TimerData data = new TimerData();
        final long myTimerId = nextTimerId++;

        final SchedulerLogic logic = new SchedulerLogic(time.getTimeZone(), new Date(), time.getWeekMs(), time.aheadBehavior(), time.backBehavior());

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
    private long nextTimerId;
    private HashMap<Long, TimerData> timers;
}

