package com.sendgrid.labs.vertx.schedule;

import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;

class SchedulerImpl extends Scheduler {

    public SchedulerImpl(Vertx vertx) {
        this.vertx = vertx;
        this.timers = new HashMap<Timer, TimerData>();
    }

    public void stop() {
        for(TimerData timer : timers.values()) {
            vertx.cancelTimer(timer.vertxTimerId);
        }
        timers.clear();
    }

    public Timer setTimer(TimeOfWeek time, Handler<Timer> handler) {
        return start(false, time, handler);
    }

    public Timer setPeriodic(TimeOfWeek time, Handler<Timer> handler) {
        return start(true, time, handler);
    }

    public void cancelTimer(Timer timer) {
        if(timers.containsKey(timer)) {
            TimerData data = timers.get(timer);
            vertx.cancelTimer(data.vertxTimerId);
            timers.remove(timer);
        }
    }


    private Timer start(final boolean periodic, TimeOfWeek time, final Handler<Timer> handler) {
        final TimerData data = new TimerData();
        final Timer timer = new Timer();

        final SchedulerLogic logic = new SchedulerLogic(time.getTimeZone(), new Date(), time.getWeekMs(), time.aheadBehavior(), time.backBehavior());

        final Handler<java.lang.Long> timerCB = new Handler<java.lang.Long>() {
            public void handle(Long vertxTimerId) {
                if(periodic) {
                    timer.next = logic.next();
                    handler.handle(timer);
                    data.vertxTimerId = vertx.setTimer(Utils.getMsUntilDate(timer.next), this);
                } else {
                    timer.next = null;
                    handler.handle(timer);
                    timers.remove(timer);
                }
            }
        };

        timer.next = logic.next();
        data.vertxTimerId = vertx.setTimer(Utils.getMsUntilDate(timer.next), timerCB);
        timers.put(timer, data);

        return timer;
    }

    private class TimerData {
        Long vertxTimerId;
    };

    private Vertx vertx;
    private HashMap<Timer, TimerData> timers;
}

