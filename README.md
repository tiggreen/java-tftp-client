# A simple TFTP Client in java. 


## Running instructions

### Compile all the java files first:

```java
javac *.java
```

### Run TFTPClient.java which contains the main method.

```java
java TFTPClient.java
```

## TFTP Client Usage instructions.

### Available commands:

| Command        | args           | desc  |
| -------------  |:--------------:| -----:|
| connect        | URL            | URL to remote tftp server (e.g glados.cs.rit.edu on port 69) |
| mode           | octet or netascii      |    set the transfer mode (default octet) |
| get            | filename      |    receive file |
| quit           |               |    exit tftp <br> |
| ?              |               |    print help  |


