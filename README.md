# A simple TFTP Client in java. 


## Running instructions

### Compile all the java files first:

```java
javac *.java
```

### Run TFTPClient.java which contains the main method.

```java
java TFTPClient.java <br>
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


connect  URL URL to remote tftp server (glados.cs.rit.edu on port 69) <br>
mode &nbsp;			  octet or netascii &nbsp;&nbsp;&nbsp;   set the transfer mode (default octet) <br>
get&nbsp;       filename&nbsp;&nbsp;&nbsp;			      receive file <br>
quit&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;		  	                    exit tftp <br>
? &nbsp;&nbsp;&nbsp;&nbsp;	                          print help information <br>

