package com.kittelson;

import io.micrometer.core.annotation.Timed;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Logic {

    @Timed
    public void randomRunner() throws InterruptedException {
        int sleepDuration = (int) (Math.random() * 1_000);
        log.debug("sleeping for {} ms", sleepDuration);
        Thread.sleep(sleepDuration);
    }
}
