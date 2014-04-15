package com.sendgrid.labs.vertx.schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

class EventInfo {
    public Date date;
    public boolean isDstAheadHour;
    public boolean isDstBackHour1;
    public boolean isDstBackHour2;
};

class WeekTracker {
    public WeekTracker(TimeZone tz, Date start, int weekMs) {
        calendar = Calendar.getInstance(tz);
        calendar.setLenient(true);
        calendar.setTime(start);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        this.weekMs = weekMs;

        loadInitialEvents(start);
    }

    public void advance() {
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        events = calculateThisWeeksEvents();
    }

    public LinkedList<EventInfo> get() {
        return events;
    }

    private void loadInitialEvents(Date start) {
        events = calculateThisWeeksEvents();

        // eliminate events in this week that are already in the past
        while(events.size() > 0 && events.peek().date.before(start)) {
            events.pop();
        }

        // if no events left for this week, advance to next week
        if(events.size() < 1) {
            advance();
        }
    }

    private LinkedList<EventInfo> calculateThisWeeksEvents() {
        LinkedList<EventInfo> ret = new LinkedList<EventInfo>();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        Calendar event = (Calendar)calendar.clone(); event.add(Calendar.MILLISECOND, weekMs);
        Calendar event_before = (Calendar)event.clone(); event_before.add(Calendar.MINUTE, -60);
        Calendar event_after = (Calendar)event.clone(); event_after.add(Calendar.MINUTE, 60);

        int start_of_week_dst_offset = calendar.get(Calendar.DST_OFFSET);
        int event_dst_offset = event.get(Calendar.DST_OFFSET);
        int hour_before_dst_offset = event_before.get(Calendar.DST_OFFSET);
        int hour_after_dst_offset = event_after.get(Calendar.DST_OFFSET);

        if(hour_before_dst_offset < event_dst_offset) {
            // this is move ahead hour
            EventInfo e = new EventInfo();
            e.date = event.getTime();
            e.isDstAheadHour = true;
            e.isDstBackHour1 = false;
            e.isDstBackHour2 = false;
            ret.add(e);
        } else if(hour_before_dst_offset > event_dst_offset) {
            // this is move back hour
            EventInfo e1 = new EventInfo();
            e1.date = event.getTime();
            e1.isDstAheadHour = false;
            e1.isDstBackHour1 = true;
            e1.isDstBackHour2 = false;
            ret.add(e1);
            EventInfo e2 = new EventInfo();
            event.add(Calendar.MINUTE, 60);
            e2.date = event.getTime();
            e2.isDstAheadHour = false;
            e2.isDstBackHour1 = false;
            e2.isDstBackHour2 = true;
            ret.add(e2);
        } else {
            // not dst hour, but be sure to adjust for dst offset (in case this was a dst week)
            EventInfo e = new EventInfo();
            event.add(Calendar.MILLISECOND, start_of_week_dst_offset - event_dst_offset);
            e.date = event.getTime();
            e.isDstAheadHour = false;
            e.isDstBackHour1 = false;
            e.isDstBackHour2 = false;
            ret.add(e);
        }

        return ret;
    }

    private Calendar calendar;
    private LinkedList<EventInfo> events;
    private int weekMs;
};

