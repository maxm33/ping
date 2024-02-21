# Ping

The famous ping command implemented in Java. This version uses UDP instead of ICMP protocol.

## Usage

- Compile

```
javac ./src/*.java
```

- Run the client

```
java src.Client <hostname>:<port> <#packets>
```

_If hostname is localhost, a local server will be started and tested._
