package com.sendgrid.labs.vertx.schedule.impl;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.TimeZone;

import com.sendgrid.labs.vertx.schedule.WeekTimeSpec.DstAheadBehavior;
import com.sendgrid.labs.vertx.schedule.WeekTimeSpec.DstBackBehavior;

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
public class SchedulerLogicTest {

    @Test
    public void testSpringAhead() {
        TimeZone tz = TimeZone.getTimeZone("America/New_York");
        Calendar c = Calendar.getInstance(tz);
        c.set(2014, 2, 2, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);

        SchedulerLogic logic;
        int weekMs = 2*60*60*1000 + 30*60*1000;

        System.out.println("Testing skip");
        logic = new SchedulerLogic(tz, c.getTime(), weekMs, DstAheadBehavior.DST_AHEAD_SKIP, DstBackBehavior.DST_BACK_BOTH_HOURS);
        assert(checkDate(logic.next(), date(1393745400)));
        assert(checkDate(logic.next(), date(1394951400)));

        System.out.println("Testing next hour");
        logic = new SchedulerLogic(tz, c.getTime(), weekMs, DstAheadBehavior.DST_AHEAD_NEXT_HOUR, DstBackBehavior.DST_BACK_BOTH_HOURS);
        assert(checkDate(logic.next(), date(1393745400)));
        assert(checkDate(logic.next(), date(1394350200)));
        assert(checkDate(logic.next(), date(1394951400)));
    }

    @Test
    public void testFallBack() {
        TimeZone tz = TimeZone.getTimeZone("America/New_York");
        Calendar c = Calendar.getInstance(tz);
        c.set(2014, 9, 26, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);

        SchedulerLogic logic;
        int weekMs = 2*60*60*1000 + 30*60*1000;

        System.out.println("Testing first hour");
        logic = new SchedulerLogic(tz, c.getTime(), weekMs, DstAheadBehavior.DST_AHEAD_SKIP, DstBackBehavior.DST_BACK_FIRST_HOUR);
        assert(checkDate(logic.next(), date(1414305000)));
        assert(checkDate(logic.next(), date(1414909800)));
        assert(checkDate(logic.next(), date(1415518200)));

        System.out.println("Testing second hour");
        logic = new SchedulerLogic(tz, c.getTime(), weekMs, DstAheadBehavior.DST_AHEAD_SKIP, DstBackBehavior.DST_BACK_SECOND_HOUR);
        assert(checkDate(logic.next(), date(1414305000)));
        assert(checkDate(logic.next(), date(1414913400)));
        assert(checkDate(logic.next(), date(1415518200)));

        System.out.println("Testing both hours");
        logic = new SchedulerLogic(tz, c.getTime(), weekMs, DstAheadBehavior.DST_AHEAD_SKIP, DstBackBehavior.DST_BACK_BOTH_HOURS);
        assert(checkDate(logic.next(), date(1414305000)));
        assert(checkDate(logic.next(), date(1414909800)));
        assert(checkDate(logic.next(), date(1414913400)));
        assert(checkDate(logic.next(), date(1415518200)));
    }

    private Date date(int epoch_secs) {
        return new Date(epoch_secs * 1000L);
    }

    private boolean checkDate(Date one, Date two) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        System.out.println("    -> " + one.getTime()/1000 + " " + df.format(one));
        return two.equals(one);
    }
}

