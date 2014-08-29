package com.sendgrid.labs.vertx.schedule;
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

import java.util.Calendar;

import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.logging.Logger;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

/**
 * Simple integration test for scheduler
 */
public class TestScheduler extends TestVerticle {

    @Test
    public void testTimer() {
        Scheduler scheduler = Scheduler.create(vertx);
        final Calendar target = Calendar.getInstance();
        target.setLenient(true);
        final Logger log = container.logger();
        log.info("Starting timer test at " + target.getTime().toString());
        target.add(Calendar.SECOND, 30);
        log.info("Waiting for callback at " + target.getTime().toString());

        scheduler.setTimer(getTimeOfWeek(target), new Handler<Timer>() {
            public void handle(Timer t) {
                assertTrue(closeEnough(target, Calendar.getInstance()));
                assertTrue(t.getNext() == null);
                testComplete();
            }
        });
    }

    @Test
    public void testPeriodic() {
        Scheduler scheduler = Scheduler.create(vertx);
        final Calendar target = Calendar.getInstance();
        target.setLenient(true);
        final Logger log = container.logger();
        log.info("Starting periodic test at " + target.getTime().toString());
        target.add(Calendar.SECOND, 30);
        log.info("Waiting for callback at " + target.getTime().toString());

        scheduler.setPeriodic(getTimeOfWeek(target), new Handler<Timer>() {
            public void handle(Timer t) {
                assertTrue(closeEnough(target, Calendar.getInstance()));
                Calendar next = (Calendar)target.clone();
                next.add(Calendar.DAY_OF_MONTH, 7);
                assertTrue(next.getTime().equals(t.getNext()));
                testComplete();
            }
        });
    }


    final int MS_IN_SECOND = 1000;
    final int MS_IN_MINUTE = 60*MS_IN_SECOND;
    final int MS_IN_HOUR = 60*MS_IN_MINUTE;
    final int MS_IN_DAY = 24*MS_IN_HOUR;

    private TimeOfWeek getTimeOfWeek(Calendar c) {
        return TimeOfWeek.create(
            (c.get(Calendar.DAY_OF_WEEK)-1) * MS_IN_DAY +
            c.get(Calendar.HOUR_OF_DAY) * MS_IN_HOUR +
            c.get(Calendar.MINUTE) * MS_IN_MINUTE +
            c.get(Calendar.SECOND) * MS_IN_SECOND +
            c.get(Calendar.MILLISECOND)
        );
    }

    private boolean closeEnough(Calendar target, Calendar actual) {
        final Logger log = container.logger();
        log.info("Checking that " + target.getTime().toString() + " and " + actual.getTime().toString() + " are very close");
        Calendar end = (Calendar)target.clone();
        end.add(Calendar.SECOND, 1);
        return actual.after(target) && actual.before(end);
    }

}
