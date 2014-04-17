package com.sendgrid.labs.vertx.schedule;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;

/**
 * Interface to schedule/cancel timers
 */
abstract public class Scheduler {

    /**
     * Stop processing all timers
     */
    abstract public void stop();

    /**
     * Set a one-shot timer to fire at next occurance of specified time, at which point {@code handler} will be called with
     * the id of the timer.
     * @return the timer instance
     */
    abstract public Timer setTimer(TimeOfWeek time, Handler<Timer> handler);

    /**
     * Set a periodic timer to fire on every occurance of specified time, at which point {@code handler} will be called with
     * the id of the timer.
     * @return the timer instance
     */
    abstract public Timer setPeriodic(TimeOfWeek time, Handler<Timer> handler);

    /**
     * Cancel the specified {@code timer}.
     */
    abstract public void cancelTimer(Timer timer);

    /**
     * Creates a new scheduler
     * @return The new scheduler
     */
    public static Scheduler create(Vertx vertx) { return new SchedulerImpl(vertx); }


    protected Scheduler() {}
}

