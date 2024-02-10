package io.github.clamentos.blackhole;

///
import io.github.clamentos.blackhole.framework.implementation.tasks.ApplicationStarter;

///..
import io.github.clamentos.blackhole.framework.scaffolding.ApplicationContext;

///
// TODO: use the service provider api instead of relying on the user to manually inject.

/**
 * <h3>App</h3>
 * This is where the main method is located to start the whole application.
*/
public class App {

    ///
    /**
     * <p>Starts the whole application.</p>
     * This method will call the {@code .start(...)} method of the application starter passing the user-defined application context.
     * <p><b>NOTE: The user of this framework must pass to the start method the concrete application context implementation.</b></p>
     * @param args : command-line arguments (currently not used).
     * @see ApplicationStarter
     * @see ApplicationContext
    */
    public static void main(String[] args) {

        ApplicationStarter.start(new Prova());
    }

    ///
}

// Compile the project : mvn compile
// Run the project     : mvn exec:exec
// Test the project    : mvn test
