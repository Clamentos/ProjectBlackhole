package io.github.clamentos.blackhole.framework.implementation.tasks;

///
import io.github.clamentos.blackhole.framework.implementation.logging.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.LogPrinter;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ExceptionFormatter;

///.
import java.io.PrintWriter;
import java.io.StringWriter;

///..
import java.util.Objects;

///
/**
 * <h3>Task</h3>
 * Enforces common behaviour and structure for any runnable that executes once and for a finite amount of time.
*/
public abstract class Task implements Runnable {

    ///
    /** Instantiates a new {@code Task} object. */
    public Task() {}

    ///
    /** Method to perform initialization operations. */
    public abstract void initialize();

    ///..
    /** Method to perform the operations. */
    public abstract void work();

    ///
    /**
     * <p>Main execution method.</p>
     * 
     * This method will perform the following:
     * <blockquote><pre>
     *TaskManager.getInstance().add(this);
     *initialize();
     *work();
     *TaskManager.getInstance().remove(this);
     * </pre></blockquote>
    */
    @Override
    public void run() {

        try {

            TaskManager.getInstance().add(this);
            initialize();
            work();
            TaskManager.getInstance().remove(this);
        }

        catch(Throwable exc) {

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

    ///
}
