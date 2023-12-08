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
 * <h3>Continuous task</h3>
 * Enforces common behaviour and structure for any runnable that executes for an indefinite amount of time.
*/
public abstract class ContinuousTask implements Stoppable {

    ///
    /** The stopped flag. */
    private volatile boolean stopped;

    ///
    /** Instantiates a new {@code ContinuousTask} object. */
    public ContinuousTask() {

        stopped = false;
    }

    ///
    /** Method to perform initialization operations before entering the continuous loop. */
    public abstract void initialize();

    ///..
    /** Method to perform the main operations while in the continuous loop. */
    public abstract void work();

    ///..
    /** Method to perform cleanup operations after the the continuous loop. */
    public abstract void terminate();

    ///
    /** @return The stopped status flag. */
    public boolean isStopped() {

        return(stopped);
    }

    ///
    /**
     * <p>Main execution method.</p>
     * This method will perform the following:
     * <blockquote><pre>
     *TaskManager.getInstance().add(this);
     *initialize();
     *
     *while(stopped == false) {
     *     
     *    work();
     *}
     * 
     *terminate();
     *TaskManager.getInstance().remove(this);
     * </pre></blockquote>
    */
    @Override
    public void run() {

        try {

            TaskManager.getInstance().add(this);
            initialize();

            while(stopped == false) {

                work();
            }

            terminate();
            TaskManager.getInstance().remove(this);
        }

        catch(Exception exc) {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            exc.printStackTrace(pw);

            LogPrinter.getInstance().logToFile(

                ExceptionFormatter.format(

                    "Uncaught exception in task " + Objects.toIdentityString(this) + " ",
                    exc, " >> Stack trace: " + sw.toString()
                ),

                LogLevels.ERROR
            );
        }
    }

    ///..
    /** Sets the stopped flag of {@code this}. */
    @Override
    public void stop() {

        stopped = true;
    }

    ///
}
