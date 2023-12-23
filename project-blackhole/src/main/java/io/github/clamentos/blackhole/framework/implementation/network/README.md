# Network package
This package is aimed at implementing the actual network layer of the framework and is composed of the following sub-packages:

- tasks.
- transfer.

## ServerContext.java
Public singleton class that keeps track of the currently connected sockets. This class is responsible for keeping a map that associates connected IP addresses with client sockets.

This class is used to impose limits on how many sockets each IP address can have, as well as, tracking the total maximum number of connected sockets.

## SocketDataProvider.java
Public class that wraps the socket input stream allowing a controlled but direct access to such input stream. This class allows reading from the socket in a "chunked" fashion via the `fill(byte[] chunk, int starting_position, int amount)` method.

### The protocol
The protocol used by the network layer is a custom one designed to be extremely simple and minimal. Data flowing through the socket streams is considered "unstructured", while data contained within the `NetworkRequest`, `NetworkResponse`, entities and DTO objects is considered "structured".

Once the socket is created, multiple requests can be sent one after the other in a "pipeline" fashion. Each request must have an id that will be echoed back by the server allowing the client to properly match the responses. This id is a single byte and it's recommended to not have more than 256 requests "in flight" as this will create issues client-side (the server doesn't actually care about the id value).

The socket will timeout and close forcibly, if no data is red by the server within a certain period of time (depends on server configuration and is usually around 10 or 20 seconds).

A Request that specifies a payload size of 0 is allowed and perfectly legal. A negative value is also allowed and has a special meaning in this protocol. Such case is used to tell the server to gracefully terminate the connection. Once this case is triggered, the server will stop accepting any new request and will finish processing all the currently in-flight requests from such connection and then close the associated socket.

#### Data layout
The client must send data in a specific format which closely resembles the TLV format (Type Length Value). Data is composed of entries which are composed of an ordered triple:

1. **Type**: The type of data that the entry holds. This is a single byte and the possible values are explained in the `transfer` sub-package.
2. **Length**: This is an optional signed 4-bytes long parameter that indicates the length of the following data. Only used in some types, explained in the `transfer` sub-package.
3. **Value**: This is the actual data that is simply appended after the type and length.

#### Request message
In order to send a valid request, the client must send the request headers first and then the payload if present. See the `transfer` sub-package for more information.

#### Response message
The server will send a response composed of the response headers and the payload if present. See the `transfer` sub-package for more information.
