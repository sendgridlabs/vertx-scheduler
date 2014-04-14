# vertx-scheduler

A module to implement scheduling on top of Vertx timers.

Initially this includes a weekly schedule, with configurable behavior on DST changes.

## Setup

## Use

### Set a one shot timer to fire at next occurrence of time

    Scheduler scheduler = new Scheduler(vertx, TimeZone.getTime("America/New_York"));

    WeekTimeSpec time = new WeekTimeSpec(Day.TUE, 2, 30, 0, 0);
    long id = scheduler.setTimer(time, new Handler<long>() {
        public void handle(long timerId) {
            // ...
        }
    });

### Set a periodic timer to fire on every occurrence of time

    Scheduler scheduler = new Scheduler(vertx, TimeZone.getTime("America/New_York"));

    WeekTimeSpec time = new WeekTimeSpec(Day.TUE, 2, 30, 0, 0);
    long id = scheduler.setPeriodic(time, new Handler<long>() {
        public void handle(long timerId) {
            // ...
        }
    });

### Cancel a timer

    scheduler.cancelTimer(timerId);

### Daylight Saving Time

Behavior on DST changes is configurable.  Default is to skip when time changes ahead and to run both when time changes back.

    Scheduler scheduler = new Scheduler(vertx, TimeZone.getTime("America/New_York"), DstAheadBehavior.DST_AHEAD_SKIP, DstBackBehavior.DST_BACK_BOTH_HOURS);

#### Time changes ahead
The scheduler can either skip any events during the missing hour (DST_AHEAD_SKIP), or run them on the next hour (DST_AHEAD_NEXT_HOUR).

#### Time changes back
The scheduler can callback on the first occurrence of the duplicated hour (DST_BACK_FIRST_HOUR), just the second (DST_BACK_SECOND_HOUR), or both (DST_BACK_BOTH_HOURS).



