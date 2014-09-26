/**
 * TFTPClient.java 
 * 
 * @author Tigran Hakobyan
 * txh7358@rit.edu
 * 
 */
import java.net.*;
import java.io.*;

public class TFTPClient {
	
	static String ftpHandle = "tftp> ";
	static InetAddress hostname;
	static int tftpPort = 69;
	static boolean isConnected = false;
	static int pktLength = 512;

	public static boolean isInputOK(String userInput) {
		String[] userInputArr = userInput.trim().split(" ");
		if (userInputArr.length > 2) {
			return false;
		}
		String cmnd = userInputArr[0].trim();
		
		if ((cmnd.equals("connect") || cmnd.equals("get")) && userInputArr.length != 2) {
			return false;
		}
		if ((cmnd.equals("quit") || cmnd.equals("?")) && userInputArr.length != 1) {
			return false;
		}
		return true;
	}
	
	public static void showHelp() {
		System.out.print("Commands are: \n" +
				"connect 		connect to remote tftp \n" +
				"mode			set the transfer mode \n" +
				"get			receive file \n" +
				"quit			exit tftp \n" +
				"?			print help information \n");
	}

	public static String getInputCommand(String userInput) {
		return userInput.split(" ")[0].trim();
	}
	
	public static String getInputHost(String userInput) {
		return userInput.split(" ")[1].trim();
	}
	
	public static String getUserGivenFilename(String userInput) {
		return userInput.split(" ")[1].trim();
	}
	
	public static void setMode(String userInput) {
		String[] st = userInput.trim().split(" ");
		if(st.length == 2) {
			if (st[1].equals("netascii")) {
				PktFactory.currentMode = "netascii";
			} else {
				System.out.println("usage: mode [ netascii | octet ]");
			}
		} else {
			PktFactory.currentMode = "octet";
		}
		System.out.println("mode " + PktFactory.currentMode);
	}
	
	
	public static void disconnect() {
		ftpHandle = "tftp> ";
		isConnected = false;		
	}
	
	public static void main(String[] args) {
		
		try {
			
			PktFactory pktFactory = null;
			DatagramSocket clientSocket = null;
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(System.in));
			byte[] buf = new byte[pktLength];
			DatagramPacket sendingPkt;
			DatagramPacket receivedPkt;
			showHelp();
			
			while (true) {
				System.out.print(ftpHandle);
				String userInput = bufReader.readLine().toLowerCase();

				if (isInputOK(userInput)) {
					String inputCommandString = getInputCommand(userInput).trim();
					
					if (inputCommandString.equals("connect")) {
						if(isConnected) {
							System.out.println("You're already connected to " + hostname.getCanonicalHostName());
							continue;
						}
						clientSocket = new DatagramSocket(0);
						try {
							hostname = InetAddress.getByName(getInputHost(userInput));
							if (!hostname.isReachable(4000)) {
								System.out.println("Hostname you provided is not responding. Try again.");
								continue;
							}
						} catch (UnknownHostException e) {
							System.out.println("tftp: nodename nor servname provided, or not known");
							continue;
						}

						pktFactory = new PktFactory(pktLength+4, hostname, tftpPort);
						System.out.println("Connecting " + 
								hostname.getCanonicalHostName() + " at the port number " + tftpPort);
						isConnected = true;
						ftpHandle = "tftp@" + hostname.getCanonicalHostName() + "> ";
							
					} else if (inputCommandString.equals("mode")) {
							setMode(userInput);
							
					} else if (inputCommandString.equals("get")) {
						if (!isConnected) {
							System.out.println("You must be connected first!");
							continue;
						}
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						String filename = getUserGivenFilename(userInput);
						buf = new byte[pktLength+4];
						
						/** Sending the reading request with the filename to the server. **/
						try {
							/** Sending a RRQ with the filename. **/
							sendingPkt = pktFactory.createReadRequestPacket(filename);
							clientSocket.send(sendingPkt);
							clientSocket.setSoTimeout(4500);
							} catch (Exception e) {
								e.printStackTrace();
								System.out.println("Can't connect to the server. Please try again.");
								continue;
							}
						boolean receivingMessage = true;
						while (true) {
							
							try {
								receivedPkt  = new DatagramPacket(buf, buf.length);
								clientSocket.setSoTimeout(15000);
								clientSocket.receive(receivedPkt);
								
								byte[] dPkt = receivedPkt.getData();
								byte[] ropCode = pktFactory.getOpCode(dPkt);
								
								/** rPkt either a DATA or an ERROR pkt. If an error then print the error message and
								 * terminate the program finish get command. **/
								if (ropCode[1] == 5) {
									String errorMsg = pktFactory.getErrorMessage(dPkt);
									System.out.println(errorMsg);
									break;
								}

								if (receivedPkt.getLength() < pktLength + 4  && ropCode[1] == 3 ) {
									
									FileOutputStream fstream = new FileOutputStream(filename);
									// Let's get the last data pkt for the current transfering file.
									byte[] fileDataBytes = pktFactory.getDataBytes(dPkt);
									outputStream.write(fileDataBytes);
									fstream.write(outputStream.toByteArray());
									fstream.close();
									
									// It's time to send the last ACK message before Normal termination.
									byte[] bNum = pktFactory.getBlockNum(dPkt);
									DatagramPacket sPkt = pktFactory.createAckPacket(bNum, receivedPkt.getPort());
									clientSocket.send(sPkt);
									
									System.out.println("File transfer is finished.");
									break;		
								}
								

								if (ropCode[1] == 3) {
									if (receivingMessage) {
									System.out.println("Receiving the file now..");
									receivingMessage = false;
									}
									byte[] fileDataBytes = pktFactory.getDataBytes(dPkt);
									outputStream.write(fileDataBytes);
									
									/** For each received DATA pkt we need to send ACK pkt back. **/
									byte[] bNum = pktFactory.getBlockNum(dPkt);
									DatagramPacket sPkt = pktFactory.createAckPacket(bNum, receivedPkt.getPort());
									clientSocket.send(sPkt);
									
								}
							} catch (SocketTimeoutException e) {
								System.out.println("Server didn't respond and timeout occured.");
								break;	
							}
					}
				} else if (inputCommandString.equals("quit")) {
						if (isConnected) {
							System.out.println("Disconnecting from " + hostname.getCanonicalHostName());
							disconnect();
						} else {
							System.out.println("Exiting the tftp client. Bye.");
							break;
						}
				} else if (userInput.equals("?")) {
						showHelp();
						}
				} else {
					System.out.println("?Invalid command");
					continue;
			}
		}
		} catch (Exception e) {
			System.out.println("Unknown error occured.");
		}
	}
}