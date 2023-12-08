package io.github.clamentos.blackhole;

///
import io.github.clamentos.blackhole.framework.implementation.tasks.ApplicationStarter;

///..
import io.github.clamentos.blackhole.framework.scaffolding.ApplicationContext;

///
/**
 * <h3>App</h3>
 * This is where the main method is located to start the whole application.
*/
public class App {

    ///
    /**
     * <p>Starts the whole application.</p>
     * This method will call the {@code .start(...)} method of the application starter passing a user-defined application context.
     * @param args : command-line arguments (not used).
     * @see ApplicationStarter
     * @see ApplicationContext
    */
    public static void main(String[] args) {

        // TODO: pass the actual provider
        ApplicationStarter.start(null);
    }

    ///
}

// Compile the project : mvn compile
// Run the project     : mvn exec:exec
// Test the project    : mvn test
