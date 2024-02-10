# Exceptions package
This package is aimed at providing basic custom exceptions to be thrown by the user of this framework.

## DeserializationException.java
Public runtime exception that can be thrown when any request deserialization error is encountered.

## GenericException.java
Public runtime exception that serves as the root of the custom exception hierarchy. This exception includes the `failure_message` parameter that can be used to hold an optional failure message to send back to the client.

## PersistenceException.java
Public runtime exception indicating an error while comunicating with the database.

## ResultSetMappingException.java
Public runtime exception indicating an error during the result set to entity mapping process.
