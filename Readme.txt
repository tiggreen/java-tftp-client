Running instructions - 

Compile all the java files first. 
javac *.java 


Run TFTPClient containing the main method. 
java TFTPClient 

TFTP Client Usage instructions. 

Available commands: 



connect		URL     	 	URL to remote tftp server (glados.cs.rit.edu on port 69) 
mode   		octet or netascii     	set the transfer mode (default octet) 
get  		filename    		receive file 
quit      				exit tftp 
?      					print help information

Help to get started: 

connect glados.cs.rit.edu
get file.txt
get the-brothers-karamazov.pdf
?
get images.jpeg


