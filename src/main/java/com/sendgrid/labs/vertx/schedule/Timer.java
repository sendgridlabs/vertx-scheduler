package com.sendgrid.labs.vertx.schedule;

import java.util.Date;

/**
 * Represents a timer created by the Scheduler
 */
public class Timer {
    Date getNext() { return next; }

    Date next = null;
}

