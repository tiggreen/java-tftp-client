/**
 * @author Tigran Hakobyan
 * txh7358@rit.edu
 * 
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class PktFactory {
	
	public final byte[] rOpCode = {0,1};
	public final byte[] wOpCode = {0,2};
	public final byte[] dataOpCode = {0,3};
	public final byte[] ackOpCode = {0,4};
	
	// by default our program uses octer mode.
	public static String currentMode = "octet";
	
	// termination bit in wrq/rrq packet.
	public final byte term = 0;
	
	private int pktLength;
	private InetAddress serverAddr;
	private int port;
	
	public PktFactory(int pktLength, InetAddress serverAddr, int port) {
		this.pktLength = pktLength;
		this.serverAddr = serverAddr;
		this.port = port;
	}
	
	public  DatagramPacket createReadRequestPacket(String strFileName){
		byte[] filename = strFileName.getBytes();	
		byte[] mode = currentMode.getBytes();
		int len = rOpCode.length + filename.length + mode.length + 2;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(len);
		try {
			outputStream.write(rOpCode);
			outputStream.write(filename);
			outputStream.write(term);
			outputStream.write(mode);
			outputStream.write(term);
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] readPacketArray = outputStream.toByteArray();
		DatagramPacket readPacket = new DatagramPacket(readPacketArray, readPacketArray.length, serverAddr, port);
		return readPacket;
	}
	
	public  DatagramPacket createAckPacket(byte[] blockNum, int prt){
		int len = ackOpCode.length + blockNum.length;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(len);
		try {
			outputStream.write(ackOpCode);
			outputStream.write(blockNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] ackPacketArray = outputStream.toByteArray();
		DatagramPacket ackPacket = new DatagramPacket(ackPacketArray, ackPacketArray.length, serverAddr, prt);
		return ackPacket;
	}

	public byte[] getOpCode(byte[] data) {
		byte[] opCode = new byte[2];
		opCode[0] = data[0];
		opCode[1] = data[1];
		return opCode;
	}
	public byte[] getBlockNum(byte[] data) {
		byte[] blockNum = new byte[2];
		blockNum[0] = data[2];
		blockNum[1] = data[3];
		return blockNum;
	}
	
	public byte[] getDataBytes(byte[] data) {
		// We sure that data bytes pkt is always 512 bytes.
		byte[] dataVal = new byte[pktLength-4];
		int j = 0;
		for(int i = 4; i < data.length; i++) {
			dataVal[j++] = data[i]; 
		}
		return dataVal;
	}
	
	public String getErrorMessage(byte[] data) {
		byte[] errorMsgBytes = new byte[data.length-5];
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(errorMsgBytes.length);
		int j = 0;
		for(int i = 4; i < data.length - 1; i++) {
			errorMsgBytes[j++] = data[i]; 
		}
		try {
			outputStream.write(errorMsgBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return outputStream.toString();
	}
	
}
