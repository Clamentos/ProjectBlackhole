# Tasks package
This package is aimed at providing the basic support for launching and managing virtual threads and tasks, as well as starting and stopping the whole system.

## ApplicationStarter.java
Public static class that starts and stops the whole system. This can be done by calling the `start(ApplicationContext context)` method. Once launched, this method will block the running thread waiting for quit command from standard in. 

## ContinuousTask.java
Public abstract class that enforces common behaviour among all tasks that execute continuously. The method `stop()` will gracefully terminate the stopped task.

## Task.java
Public abstract class that enforces common behaviour among all tasks that execute once. This task, unlike `ContinuousTask` cannot be explicitly stopped.

## TaskManager.java
Public singleton class that manages all tasks and virtual threads in the system. The method `launchThread(Runnable runnable, String name)` can be used to run the specified runnable on a new virtual thread. Tasks will remove themselves from the managing buffers by calling the protected method `remove(Runnable runnable)` just before terminating. The protected method `shutdown()` is used by `ApplicationStarter` to stop all tasks once the quit command is issued.
