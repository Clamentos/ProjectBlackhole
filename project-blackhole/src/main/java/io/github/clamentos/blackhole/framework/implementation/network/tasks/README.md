# Tasks package
This package is aimed at providing low-level socket and stream handling logic for network requests and responses.

## RequestTask.java
Public class that manages a single request. This class is spawned by `TransferTask` and has the duty of handling requests by dispatching them to the appropriate user-defined servlet as well as responding to the client.

## ServerTask.java
Public class that accepts the incoming client sockets on a specific port. This class has the duty of listening for new sockets and accept them. If the accept is successfull, then a new `TransferTask` will be instantiated. IP addresses have a fixed number of sockets that they can concurrently hold. In any other case, the socket is rejected.

## TransferTask.java
Public class that manages a single client socket. A new instance of this class is created by the `ServerTask` for every incoming socket.
`TransferTask` has the duty of listening for new requests and spawn a new `RequestTask` for each one of them.
