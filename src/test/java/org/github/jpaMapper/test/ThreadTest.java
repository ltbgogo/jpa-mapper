package org.github.jpaMapper.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ThreadTest {

    public static void main(String[] args) throws InterruptedException {
        Map map = new HashMap();
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    map.put(j, new Date());
                }
            }).start();
        }
        TimeUnit.HOURS.sleep(1);
    }
}
