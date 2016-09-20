package com.monash.eric.mytestingdemo.Util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IBM on 14/09/2016.
 */
public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}