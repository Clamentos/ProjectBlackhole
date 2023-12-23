# Components package
This package is aimed at providing the building blocks of the `NetworkRequest.java` and `NetworkResponse.java` classes in the parent package.

## RequestHeaders.java
Public record class that holds information about the associated `NetworkRequest` sent by the client, which includes:

1. `payload_size`: The size of the payload (can be 0).
2. `id`: The identifier to be echoed back in the response.
3. `flags`: The request flag bits:
    1. _compression_: `true` if the client desires compression, `false` otherwise.
4. `cache_timestamp`: The timestamp used for caching. The client can specify a value `<= 0` if it's not availabe yet.
5. `method`: The request method (not all resources will allow every method).
    1. _CREATE_.
    2. _READ_.
    3. _UPDATE_.
    4. _DELETE_.
    5. _LOGIN_.
    6. _LOGOUT_.
6. `target_resource`: The target resource on which the request will operate on.
7. `session_id`: The unique client session identifier (not needed for login type requests).

## ResponseHeaders.java
Public record class that holds information about the associated `NetworkResponse` sent by the server, which includes:

1. `payload_size`: The size of the payload (can be 0).
2. `id`: The identifier to be echoed back.
3. `flags`: The response flag bits:
    1. _compression_: `true` if the response payload is compressed, `false` otherwise.
4. `cache_timestamp`: The timestamp used for caching. Value `<= 0` if the response is not cacheable.
5. `response_status`: The response status of this response.

## ResponseStatuses.java
Public enumeration containing all possible response statuses:

_WIP_

## SimpleDto.java
Public record class used as a simple response data transfer object mainly for error responses. This class is streamable and contains a simple string message and a timestamp.

## Types.java
Public enumeration containing all the possible data types for requests and responses:

 1. `BYTE`: 1 byte signed integer.
 2. `SHORT`: 2 byte signed integer.
 3. `INT`: 4 byte signed integer.
 4. `LONG`: 8 byte signed integer.
 5. `FLOAT`: 4 byte floating point number.
 6. `DOUBLE`: 8 byte floating point number.
 7. `STRING`: UTF-8 encoded string.
 8. `RAW`: Raw bynary data.
 9. `NULL`: Signifies a `null` value. 1 byte size.
10. `BEGIN`: Specifies the beginning of an array (also used as a message control sequence). 1 byte size.
11. `END`: Specifies the end of an array (also used as a message control sequence). 1 byte size.
