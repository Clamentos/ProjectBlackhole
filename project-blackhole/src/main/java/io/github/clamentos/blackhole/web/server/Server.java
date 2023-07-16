package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Eager-loaded singleton.</b></p>
 * <p>Web server.</p>
 * This class manages the {@link ServerTask} along with its worker {@link Thread}.
*/
public class Server {
    
    private static final Server INSTANCE = new Server();
    private ServerTask server_task;

    //____________________________________________________________________________________________________________________________________

    private Server() {

        server_task = new ServerTask();
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the {@link Server} instance created during class loading.
     * @return The {@link Server} instance.
    */
    public static Server getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * <p>Start the {@link ServerTask}.</p>
     * This method will not block the calling thread but simply return immediately.
    */
    public void start() {

        Thread.ofVirtual().start(server_task);
    }

    public void stop() {

        server_task.stop();

        while(server_task.isStoppingCompleted() == false) {

            try {

                Thread.sleep(500);
            }

            catch(InterruptedException exc) {

                //...
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
