package com.sendgrid.labs.vertx.schedule;

import java.util.TimeZone;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;

import com.sendgrid.labs.vertx.schedule.impl.SchedulerImpl;

abstract public class Scheduler {

    public enum DstAheadBehavior { DST_AHEAD_SKIP, DST_AHEAD_NEXT_HOUR };
    public enum DstBackBehavior { DST_BACK_BOTH_HOURS, DST_BACK_FIRST_HOUR, DST_BACK_SECOND_HOUR };

    /**
     * Stop processing all timers
     */
    abstract public void stop();

    /**
     * Set a one-shot timer to fire at next occurance of specified time, at which point {@code handler} will be called with
     * the id of the timer.
     * @return the unique ID of the timer
     */
    abstract public long setTimer(WeekTimeSpec time, Handler<java.lang.Long> handler);

    /**
     * Set a periodic timer to fire on every occurance of specified time, at which point {@code handler} will be called with
     * the id of the timer.
     * @return the unique ID of the timer
     */
    abstract public long setPeriodic(WeekTimeSpec time, Handler<java.lang.Long> handler);

    /**
     * Cancel the timer with the specified {@code id}. Returns {@code} true if the timer was successfully cancelled, or
     * {@code false} if the timer does not exist.
     */
    abstract public void cancelTimer(long id);

    /**
     * Creates a new schedule using the given {@code tz}
     * @return The new scheduler
     */
    public static Scheduler create(Vertx vertx, TimeZone tz) { return new SchedulerImpl(vertx, tz, DstAheadBehavior.DST_AHEAD_SKIP, DstBackBehavior.DST_BACK_BOTH_HOURS); }

    /**
     * Creates a new schedule using the given {@code tz}
     * @return The new scheduler
     */
    public static Scheduler create(Vertx vertx, TimeZone tz, DstAheadBehavior ahead, DstBackBehavior back) { return new SchedulerImpl(vertx, tz, ahead, back); }

}

