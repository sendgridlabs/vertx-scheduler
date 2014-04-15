package com.sendgrid.labs.vertx.schedule;

import java.util.TimeZone;

import com.sendgrid.labs.vertx.schedule.impl.Utils;

/** Represents a weekly time (i.e. Monday at 7:00 PM in EDT)
 */
public class WeekTimeSpec {

    public enum Day { SUN, MON, TUE, WED, THU, FRI, SAT };
    public enum DstAheadBehavior { DST_AHEAD_SKIP, DST_AHEAD_NEXT_HOUR };
    public enum DstBackBehavior { DST_BACK_BOTH_HOURS, DST_BACK_FIRST_HOUR, DST_BACK_SECOND_HOUR };

    /** Construct UTC time using number of milliseconds since the beginning
      * of the week (Sunday 12:00 AM)
     */
    public static WeekTimeSpec create(int weekMs) {
        return create(TimeZone.getTimeZone("UTC"), weekMs);
    }

    /** Construct time in timezone using number of milliseconds since the beginning
      * of the week (Sunday 12:00 AM)
     */
    public static WeekTimeSpec create(TimeZone tz, int weekMs) {
        return create(tz, weekMs, DstAheadBehavior.DST_AHEAD_SKIP, DstBackBehavior.DST_BACK_BOTH_HOURS);
    }

    /** Construct time in timezone using number of milliseconds since the beginning
      * of the week (Sunday 12:00 AM) with DST behavior
     */
    public static WeekTimeSpec create(TimeZone tz, int weekMs, DstAheadBehavior ahead, DstBackBehavior back) {
        WeekTimeSpec w = new WeekTimeSpec();
        w.weekMs = weekMs;
        w.tz = tz;
        w.ahead = ahead;
        w.back = back;
        return w;
    }

    /** Construct UTC time using individual components
     */
    public static WeekTimeSpec create(Day day, int hour, int minute, int sec, int ms) {
        return create(TimeZone.getTimeZone("UTC"), Utils.convertWeekTime(day, hour, minute, sec, ms));
    }

    /** Construct time in timezone using individual components
     */
    public static WeekTimeSpec create(TimeZone tz, Day day, int hour, int minute, int sec, int ms) {
        return create(tz, Utils.convertWeekTime(day, hour, minute, sec, ms));
    }

    /** Construct time in timezone using individual components with DST behavior
     */
    public static WeekTimeSpec create(TimeZone tz, Day day, int hour, int minute, int sec, int ms, DstAheadBehavior ahead, DstBackBehavior back) {
        return create(tz, Utils.convertWeekTime(day, hour, minute, sec, ms), ahead, back);
    }

    /* Return number of milliseconds since the beginning of the week.
     */
    public int getWeekMs() {
        return weekMs;
    }

    /* Timezone associated with time
     */
    public TimeZone getTimeZone() {
        return tz;
    }

    /* Behavior on DST changes ahead
     */
    public DstAheadBehavior aheadBehavior() {
        return ahead;
    }

    /* Behavior on DST changes behind
     */
    public DstBackBehavior backBehavior() {
        return back;
    }

    private WeekTimeSpec() { }

    private int weekMs;
    private TimeZone tz;
    private DstAheadBehavior ahead;
    private DstBackBehavior back;
}

