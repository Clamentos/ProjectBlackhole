package io.github.clamentos.blackhole.analytics;

//________________________________________________________________________________________________________________________________________

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;

//________________________________________________________________________________________________________________________________________

/**
 * Simple record that holds some application statistics, such as memory and thread info.
*/
public record Status(

    MemoryUsage heap_info,
    MemoryUsage non_heap_info,
    ThreadInfo[] threads_info

) {

    //____________________________________________________________________________________________________________________________________

    /**
     * Aquire an snapshot of the current application status.
    */
    public Status {

        heap_info =  ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        non_heap_info = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        threads_info = ManagementFactory.getThreadMXBean().getThreadInfo(ManagementFactory.getThreadMXBean().getAllThreadIds());
    }

    //____________________________________________________________________________________________________________________________________
}
