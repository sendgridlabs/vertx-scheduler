package com.sendgrid.labs.vertx.schedule;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;

import com.sendgrid.labs.vertx.schedule.impl.SchedulerImpl;

abstract public class Scheduler {

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
     * Creates a new scheduler
     * @return The new scheduler
     */
    public static Scheduler create(Vertx vertx) { return new SchedulerImpl(vertx); }

}

