package io.github.clamentos.blackhole.analytics;

//________________________________________________________________________________________________________________________________________

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;

//________________________________________________________________________________________________________________________________________

/**
 * Simple immutable class that holds some application statistics, such as memory and thread info.
*/
public class Status {

    private MemoryUsage heap_info;
    private MemoryUsage non_heap_info;
    private ThreadInfo[] thread_infos;

    //____________________________________________________________________________________________________________________________________

    /**
     * Aquire an snapshot of the current application status.
    */
    public Status() {

        heap_info =  ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        non_heap_info = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        thread_infos = ManagementFactory.getThreadMXBean().getThreadInfo(ManagementFactory.getThreadMXBean().getAllThreadIds());
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the {@link MemoryUsage} object for the heap.
     * @return heap memory information.
     */
    public MemoryUsage getHeapInfo() {

        return(heap_info);
    }

    /**
     * Get the {@link MemoryUsage} object for non heap areas.
     * @return non heap memory information.
     */
    public MemoryUsage getNonHeapInfo() {

        return(non_heap_info);
    }

    /**
     * Get the {@link ThreadInfo} objects about the current threads.
     * @return array of thread information.
     */
    public ThreadInfo[] getThreadInfos() {

        return(thread_infos);
    }

    //____________________________________________________________________________________________________________________________________
}
