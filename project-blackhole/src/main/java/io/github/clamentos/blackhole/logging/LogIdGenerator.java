package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import java.util.concurrent.atomic.AtomicLong;

//________________________________________________________________________________________________________________________________________

public class LogIdGenerator {

    //____________________________________________________________________________________________________________________________________
    
    private static final LogIdGenerator INSTANCE = new LogIdGenerator();
    private AtomicLong id;

    //____________________________________________________________________________________________________________________________________

    private LogIdGenerator() {

        id.set(0);
    }

    public static LogIdGenerator getInstance() {

        return(INSTANCE);
    }

    public long getNext() {

        return(id.getAndIncrement());
    }

    //____________________________________________________________________________________________________________________________________
}

/*
 * @LOOP:
 *  LL: x;
 *  ADI: x, 1;
 *  SC: x, r1;
 * BEQ: r1, 1, LOOP;
 * ...
*/