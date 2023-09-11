# Exceptions package
This package is aimed at providing global error and exception signaling features for all other classes and packages.

## Failures.java
Public enumeration that contains all the possible failures that can be encountered, such has **network**, **persistence**, **formatting**, **security** errors and many others.

> For a list of all the errors, please see the **JavaDocs** in the class itself.

## FailuresWrapper.java
Public class that extends `Throwable` that is used to make `Failures` a throwable object. This can be used as a constructor parameter in any regular exception that allows it.

## GlobalExceptionHandler.java
Public singleton class that extends `UncaughtExceptionHandler` in order to catch and hande uncaught exceptions. Every thread should set this class as its default uncaught exception handler with

> `Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance())`