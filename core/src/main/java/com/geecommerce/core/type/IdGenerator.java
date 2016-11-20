package com.geecommerce.core.type;

import org.joda.time.DateTime;

import com.geecommerce.core.util.DateTimes;

public class IdGenerator {
    private static final int MAX_DATACENTER_ID = 9;

    private static final int MAX_WORKER_ID = 99;

    private static final long SUBTRACT_TIMESTAMP = new DateTime(2013, 01, 01, 0, 0, 0, 0).getMillis();

    private static long lastId = 0;

    private static long lastTimestamp = 0;

    private static long seqNumber = 0;

    public static final synchronized long nextId(int dataCenterId, int workerId) {
        if (workerId > MAX_WORKER_ID)
            throw new IllegalArgumentException(
                "Unable to generate next id because the worker id exceeds the limit of " + MAX_WORKER_ID);

        if (dataCenterId > MAX_DATACENTER_ID)
            throw new IllegalArgumentException(
                "Unable to generate next id because the datacenter id exceeds the limit of " + MAX_DATACENTER_ID);

        long millis = System.currentTimeMillis();

        if (millis < lastTimestamp) {
            throw new IllegalStateException(
                String.format("Unable to generate next id for %d milliseconds because the clock moved backwards",
                    lastTimestamp - millis));
        }

        long nextId = 0;

        int safeLoopInc = 0;

        // Loop until we have a new unique id.
        while (nextId == 0 || nextId == lastId) {
            // We can only have 99 sequence numbers, so if we reach this point
            // we wait 1 millisecond
            // so that the sequence number starts at zero again.
            if (seqNumber == 99) {
                try {
                    // Causes the sequence number to be reset in the next block.
                    Thread.sleep(1);
                    millis = System.currentTimeMillis();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // If the timestamp has not changed yet, increment the
            // sequence-number.
            if (lastTimestamp == millis) {
                seqNumber++;
            }
            // Otherwise use the new timestamp and reset the sequence-number.
            else {
                lastTimestamp = millis;
                seqNumber = 0;
            }

            StringBuilder newId = new StringBuilder();

            // Gain an extra 40 years by subtracting the unix timestamp of
            // 01.01.2013 from the 40 year old 01.01.1970
            // timestamp.
            // All timestamps are converted to UTC to keep ids unique globally.
            newId.append(DateTimes.newDate(millis).getTime() - DateTimes.newDate(SUBTRACT_TIMESTAMP).getTime());

            // Append the data-center-id. Mostly this will be just 1, but can be
            // changed for another data-center.
            newId.append(dataCenterId);

            // Append the workerId (could be a server-number) and make sure that
            // it consists of 2 digits.
            if (workerId < 10) {
                newId.append(0);
            }

            newId.append(workerId);

            // Append the sequence-number and make sure that it consists of 2
            // digits.
            // The sequence number gives us the possibility of creating 99
            // unique ids in 1 millisecond.
            if (seqNumber < 10) {
                newId.append(0);
            }

            newId.append(seqNumber);

            // Convert string to long
            nextId = Long.valueOf(newId.toString());

            // This will probably never happen, but just to be safe we break out
            // of loop
            // after 10000 iterations to avoid endless loop.
            if (safeLoopInc > 10000)
                break;

            safeLoopInc++;
        }

        // Remember the last id to make sure that we do not have any duplicates.
        lastId = nextId;

        return nextId;
    }
}
