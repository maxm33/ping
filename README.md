# Ping

The famous UNIX command ping implemented in Java. This version uses UDP instead of ICMP protocol.

## Usage

- Compile

```
javac ./src/*.java
```

- Run the client

```
java src.Client <hostname>:<port> <#packets>
```

> [!IMPORTANT]
> If hostname is _localhost_, a local server will be started and tested.
