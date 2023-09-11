# Transfer package
This package is aimed at implementing all the various representation objects of data that will be flowing through the network.

## Request.java
Public record class that contains all the necessary information for a request. This class carries data such as:

- `resource`: The target resource of the request.
- `method`: The method of the request.
- `session_id`: The session id, used for authentication and authorization.
- `data`: The actual data.

## Response.java
Public record class that contains all the necessary information for a request. This class carries data such as:

- `response_status`: The response status of the associated request.
- `remaining_requests`: The number of remaining requests associated to the current connection.
- `data`: The actual data.

## The protocol
The protocol used by the network layer is a custom one designed to be extremely simple and minimal. Data flowing through the socket streams is considered "unstructured", data contained within the `Request` and `Response` objects is considered "semi-structured", while data embedded in other objects (such as table entities) is considered as "structured".

In order to send requests, a client must first open a socket towards the server. That socket will have a fixed lifetime and a fixed number of requests allowed before getting closed.

### Data layout
The client must send data in a specific format which closely resembles the TLV format (Type Length Value). Data is composed of entries which are composed of an ordered triple:

1. **Type**: The type of data that the entry holds. This is a single byte.
2. **Length**: This is an optional signed 4-bytes long parameter that indicates the length of the following data.
3. **Value**: This is the actual data that is simply appended after the Type and Length.

### Request message
In order to send a valid request, the client must send some metadata before transmitting the actual data formatted according to the previous section. The metadata includes:

1. **Message length**: Signed 4-byte parameter to specify the total length of the message, including the metadata in bytes. A length of 0 orders the server to close the associated socket connection and all the streams. A negative length signifies that the request should be handled in a "streaming" mode. This is useful for transfering large amounts of data and reducing the memory footprint.
2. **Resource**: Signed 1-byte parameter to specify the target resource.
3. **Method**: Signed 1-byte parameter to specify the method of the request (similar to HTTP).
4. **Session-id**: 32 bytes for the session-id.

After the metadata, the actual data can be simply appended.

### Response message
The server will send a response with the following metadata:

1. **Message length**: Signed 4-byte parameter to specify the total length of the message, including the metadata in bytes. A negative length signifies that the request should be handled in a "streaming" mode. This is useful for transfering large amounts of data and reducing the memory footprint. This parameter will never be equal to 0, only strictly positive or negative.
2. **Remaining requests**: Signed 4-byte parameter to specify the number of remaining requests that the client can send on the associated socket.
3. **Response status**: Signed 1-byte parameter to specify the response status (similar to HTTP).

> For more information on all the constants, please see the **components** sub-package.