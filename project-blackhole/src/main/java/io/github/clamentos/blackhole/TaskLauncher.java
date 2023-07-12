package io.github.clamentos.blackhole;

@SuppressWarnings("preview")
public class TaskLauncher {
    
    public static Thread launch(Runnable task) {

        return(Thread.ofVirtual().start(task));
    }
}
