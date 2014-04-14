package com.sendgrid.labs.vertx.schedule.impl;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class WeekTrackerTest {

    @Test
    public void skipEventsInPast() {
        TimeZone tz = TimeZone.getTimeZone("America/New_York");
        WeekTracker wt;

        // choose a start date for scheduler between two dst back events
        // should return only the second
        Calendar c = Calendar.getInstance(tz);
        c.setTime(date(1415518200-1));
        wt = new WeekTracker(tz, c.getTime(), 2*60*60*1000 + 30*60*1000);
        assert(wt.get().size() == 1);
        assert(wt.get().peek().date.equals(date(1415518200)));

        // choose a start date for scheduler after two dst back events
        // should return only next week
        c.setTime(date(1415518200+1));
        wt = new WeekTracker(tz, c.getTime(), 2*60*60*1000 + 30*60*1000);
        assert(checkRegular(wt.get(), date(1416123000)));
    }


    @Test
    public void testSpringAhead() {
        TimeZone tz = TimeZone.getTimeZone("America/New_York");
        Calendar c = Calendar.getInstance(tz);
        c.set(2014, 2, 2, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);

        WeekTracker wt;

        System.out.println("Checking Sunday 1:30 AM");
        wt = new WeekTracker(tz, c.getTime(), 1*60*60*1000 + 30*60*1000);
        assert(checkRegular(wt.get(), date(1393741800)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1394346600)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1394947800)));

        System.out.println("Checking Sunday 2:00 AM");
        wt = new WeekTracker(tz, c.getTime(), 2*60*60*1000);
        assert(checkRegular(wt.get(), date(1393743600)));
        wt.advance();
        assert(checkAhead(wt.get(), date(1394348400)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1394949600)));

        System.out.println("Checking Sunday 2:30 AM");
        wt = new WeekTracker(tz, c.getTime(), 2*60*60*1000 + 30*60*1000);
        assert(checkRegular(wt.get(), date(1393745400)));
        wt.advance();
        assert(checkAhead(wt.get(), date(1394350200)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1394951400)));

        System.out.println("Checking Sunday 3:00 AM");
        wt = new WeekTracker(tz, c.getTime(), 3*60*60*1000);
        assert(checkRegular(wt.get(), date(1393747200)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1394348400)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1394953200)));

        System.out.println("Checking Sunday 3:30 AM");
        wt = new WeekTracker(tz, c.getTime(), 3*60*60*1000 + 30*60*1000);
        assert(checkRegular(wt.get(), date(1393749000)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1394350200)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1394955000)));

        System.out.println("Checking Friday 5:00 PM");
        wt = new WeekTracker(tz, c.getTime(), 5*24*60*60*1000 + 17*60*60*1000);
        assert(checkRegular(wt.get(), date(1394229600)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1394830800)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1395435600)));
    }

    @Test
    public void testFallBack() {
        TimeZone tz = TimeZone.getTimeZone("America/New_York");
        Calendar c = Calendar.getInstance(tz);
        c.set(2014, 9, 26, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);

        WeekTracker wt;

        System.out.println("Checking Sunday 1:30 AM");
        wt = new WeekTracker(tz, c.getTime(), 1*60*60*1000 + 30*60*1000);
        assert(checkRegular(wt.get(), date(1414301400)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1414906200)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1415514600)));

        System.out.println("Checking Sunday 2:00 AM");
        wt = new WeekTracker(tz, c.getTime(), 2*60*60*1000);
        assert(checkRegular(wt.get(), date(1414303200)));
        wt.advance();
        assert(checkBack(wt.get(), date(1414908000), date(1414911600)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1415516400)));

        System.out.println("Checking Sunday 2:30 AM");
        wt = new WeekTracker(tz, c.getTime(), 2*60*60*1000 + 30*60*1000);
        assert(checkRegular(wt.get(), date(1414305000)));
        wt.advance();
        assert(checkBack(wt.get(), date(1414909800), date(1414913400)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1415518200)));

        System.out.println("Checking Sunday 3:00 AM");
        wt = new WeekTracker(tz, c.getTime(), 3*60*60*1000);
        assert(checkRegular(wt.get(), date(1414306800)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1414915200)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1415520000)));

        System.out.println("Checking Sunday 3:30 AM");
        wt = new WeekTracker(tz, c.getTime(), 3*60*60*1000 + 30*60*1000);
        assert(checkRegular(wt.get(), date(1414308600)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1414917000)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1415521800)));

        System.out.println("Checking Friday 5:00 PM");
        wt = new WeekTracker(tz, c.getTime(), 5*24*60*60*1000 + 17*60*60*1000);
        assert(checkRegular(wt.get(), date(1414789200)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1415397600)));
        wt.advance();
        assert(checkRegular(wt.get(), date(1416002400)));
    }

    private Date date(int epoch_secs) {
        return new Date(epoch_secs * 1000L);
    }

    private boolean checkRegular(LinkedList<EventInfo> events, Date d) {
        return check(events, d, false, false, false);
    }

    private boolean checkAhead(LinkedList<EventInfo> events, Date d) {
        return check(events, d, true, false, false);
    }

    private boolean checkBack(LinkedList<EventInfo> events, Date d1, Date d2) {
        print(events);
        if(events.size() != 2) return false;
        EventInfo e1 = events.get(0);
        if(e1.isDstAheadHour != false || e1.isDstBackHour1 != true || e1.isDstBackHour2 != false || !e1.date.equals(d1))
            return false;
        EventInfo e2 = events.get(1);
        if(e2.isDstAheadHour != false || e2.isDstBackHour1 != false || e2.isDstBackHour2 != true || !e2.date.equals(d2))
            return false;
        return true;
    }

    private boolean check(LinkedList<EventInfo> events, Date d, boolean ahead, boolean back1, boolean back2) {
        print(events);
        if(events.size() != 1) return false;
        EventInfo e = events.get(0);
        if(e.isDstAheadHour != ahead || e.isDstBackHour1 != back1 || e.isDstBackHour2 != back2 || !e.date.equals(d))
            return false;
        return true;
    }

    private void print(LinkedList<EventInfo> events) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        for(EventInfo event : events) {
            System.out.print("    -> " + event.date.getTime()/1000 + " " + df.format(event.date));
            if(event.isDstAheadHour) System.out.print(" (ahead)");
            if(event.isDstBackHour1) System.out.print(" (back hour 1)");
            if(event.isDstBackHour2) System.out.print(" (back hour 2)");
            System.out.println("");
        }
    }
}

