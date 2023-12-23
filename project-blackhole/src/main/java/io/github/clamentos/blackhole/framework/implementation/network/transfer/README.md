# Transfer package
This package is aimed at implementing all the various representation objects of data that will be flowing through the network.

## NetworkRequest.java
Public record class that contains all the necessary information for a request. This class carries:

1. `headers`: The request headers.
2. `payload`: The request payload.

## NetworkResponse.java
Public record class that contains all the necessary information for a request. This class carries:

1. `headers`: The response headers.
2. `payload`: The response payload.

## TransferContext.java
Public record class that contains the shared objects between the `TransferTask.java` and the multiple `RequestTask.java`. They include:

- The client socket input and output streams.
- The output stream synchronization lock.
- The active `RequestTask` counter.
