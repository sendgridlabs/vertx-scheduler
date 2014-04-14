package com.sendgrid.labs.vertx.schedule;

import com.sendgrid.labs.vertx.schedule.impl.Utils;

/** Represents a weekly time (i.e. Monday at 7:00 PM)
 */
public class WeekTimeSpec {

    public enum Day { SUN, MON, TUE, WED, THU, FRI, SAT };

    /** Construct using number of milliseconds since the beginning
      * of the week (Sunday 12:00 AM)
     */
    public WeekTimeSpec(int weekMs) {
        this.weekMs = weekMs;
    }

    /** Construct using individual components
     */
    public WeekTimeSpec(Day day, int hour, int minute, int sec, int ms) {
        this.weekMs = Utils.convertWeekTime(day, hour, minute, sec, ms);
    }

    /* Return number of milliseconds since the beginning of the week.
     */
    public int getWeekMs() {
        return weekMs;
    }


    private int weekMs;
}

