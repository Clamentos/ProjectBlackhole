# Tasks package
This package is aimed at providing low-level socket and stream handling logic.

## ConnectionTask.java
Public class that manages a single client socket. A new instance of this class is created by the `ServerTask` for every incoming socket. Connections must be in an "proper" condition in order to have its requests serviced:

- The actual TCP connection must exist and be valid.
- Connections have a fixed number of requests before being automatically terminated.
- The client, during request transmission, cannot transmit bytes at more than a certain (configurable) amount of milliseconds apart.

`ConnectionTask` has the duty of listening for new requests and spawn a new `RequestTask` for each one of them. This class can also handle requests directly if a particular "streaming" mode is specified, which designed to be used when transmitting large amounts of data, in order to reduce the server's memory usage.

## RequestTask.java
Public class that manages a single request. This class is spawned by `ConnectionTask` and has the duty of handling requests by dispatching them to the appropriate servlet, as well as responding to the client. As mentioned earlier, this class only handles "normal" mode requests.

## ServerTask.java
Public class that accepts the incoming client sockets on a specific port. Usually only one is instantiated, but it's possible to have many (on different ports). This class has the duty of listening for new sockets and accept them. If the  accept is successfull, then a new `ConnectionTask` will be instantiated. IP addresses have a fixed number of sockets that they can concurrently hold. In any other case, the socket is rejected.