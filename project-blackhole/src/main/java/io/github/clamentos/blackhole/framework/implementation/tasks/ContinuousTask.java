package io.github.clamentos.blackhole.framework.implementation.tasks;

///
import io.github.clamentos.blackhole.framework.implementation.logging.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.LogPrinter;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ExceptionFormatter;

///..
import io.github.clamentos.blackhole.framework.scaffolding.task.Stoppable;

///.
import java.io.PrintWriter;
import java.io.StringWriter;

///..
import java.util.Objects;

///
/**
 * <h3>Continuous Task</h3>
 * Enforces common behaviour and structure for any runnable that executes for an indefinite amount of time.
*/
public abstract class ContinuousTask implements Stoppable {

    ///
    /** The status flag of {@code this}. */
    private volatile byte status;

    ///
    /** Instantiates a new {@code ContinuousTask} object. */
    public ContinuousTask() {

        status = 0;
    }

    ///
    /**
     * Method to perform initialization operations before entering the continuous loop.
     * @throws Throwable If any exception occurs.
    */
    public abstract void initialize() throws Throwable;

    ///..
    /**
     * Method to perform the main operations while in the continuous loop.
     * @throws Throwable If any exception occurs.
    */
    public abstract void work() throws Throwable;

    ///..
    /**
     * Method to perform cleanup operations after the the continuous loop.
     * @throws Throwable If any exception occurs.
    */
    public abstract void terminate() throws Throwable;

    ///
    /** @return The stopped status flag. */
    public boolean isStopped() {

        return(status == 2);
    }

    ///..
    /**
     * <p>Main execution method.</p>
     * This method will perform the following:
     * <blockquote><pre>
     *TaskManager.getInstance().add(this);
     *initialize();
     *
     *while(status == 0) {
     *     
     *    work();
     *}
     * 
     *terminate();
     *TaskManager.getInstance().remove(this);
     *status = 2;
     * </pre></blockquote>
    */
    @Override
    public void run() {

        try {

            TaskManager.getInstance().add(this);
            initialize();

            while(status == 0) {

                try {

                    work();
                }

                catch(Throwable exc) {

                    printException(exc);
                }
            }

            terminate();
            TaskManager.getInstance().remove(this);
            status = 2;
        }

        catch(Throwable exc2) {

            printException(exc2);
        }
    }

    ///..
    /** Sets the stopped flag of {@code this}. */
    @Override
    public void stop() {

        status = 1;
    }

    ///.
    private void printException(Throwable exc) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        exc.printStackTrace(pw);

        LogPrinter.getInstance().logToFile(

            ExceptionFormatter.format(

                "Uncaught exception in task " + Objects.toIdentityString(this) + " [",
                exc, "] >> Stack trace: " + sw.toString()
            ),

            LogLevels.ERROR
        );
    }

    ///
}
