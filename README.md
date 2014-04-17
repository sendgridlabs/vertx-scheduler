# vertx-scheduler

A module to implement scheduling on top of Vertx timers.

Initially this includes a weekly schedule with configurable behavior on DST changes.

## Installation

    ...
    dependencies {
      ...
      compile 'com.sendgrid:sendgrid-java:0.2.0'
    }

    repositories {
      mavenCentral()
    }
    ...

## Use

### Create a new scheduler

    Scheduler scheduler = new Scheduler(vertx);

### Create a time of week

    // Tuesday at 2:30 AM UTC
    TimeOfWeek time1 = new TimeOfWeek(Day.TUE, 2, 30, 0, 0);

    // Tuesday at 2:30 AM localtime
    TimeOfWeek time2 = new TimeOfWeek(TimeZone.getTimeZone("America/New_York"), Day.TUE, 2, 30, 0, 0);

    // Sunday at 01:01 AM UTC
    TimeOfWeek time3 = new TimeOfWeek(60*60*1000 + 60*1000);

    // Sunday at 01:01 AM localtime
    TimeOfWeek time4 = new TimeOfWeek(TimeZone.getTimeZone("America/New_York"), 60*60*1000 + 60*1000);

### Set a one shot timer on next occurrence of time

    Timer timer1 = scheduler.setTimer(time1, new Handler<Timer>() {
        public void handle(Timer t) {
            // ...
        }
    });

### Set a periodic timer to fire on every occurrence of time

    Timer timer2 = scheduler.setPeriodic(time2, new Handler<Timer>() {
        public void handle(Timer t) {
            // ...
        }
    });

### Cancel a timer

    scheduler.cancelTimer(timer1);

### Daylight Saving Time

Behavior on DST changes is configurable.  Default is to skip when time changes ahead and to run both when time changes back.

    TimeOfWeek time1 = new TimeOfWeek(TimeZone.getTime("America/New_York", Day.TUE, 2, 30, 0, 0, DstAheadBehavior.DST_AHEAD_SKIP, DstBackBehavior.DST_BACK_BOTH_HOURS);

#### Time change ahead
The scheduler can either skip any events during the missing hour (DST_AHEAD_SKIP), or run them on the next hour (DST_AHEAD_NEXT_HOUR).

#### Time change back
The scheduler can callback on the first occurrence of the duplicated hour (DST_BACK_FIRST_HOUR), just the second (DST_BACK_SECOND_HOUR), or both (DST_BACK_BOTH_HOURS).

### Dispose of scheduler

    scheduler.stop();


