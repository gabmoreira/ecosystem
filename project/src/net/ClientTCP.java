package net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import conversions.Conversions;

/**
 * This class specifies the client implemented in the SimulationManager to communicate the data to the Server.
 * The client tries to connect with the server. As the server might be handling another client at that time, connection might not be possible.
 * If that is the case, the client tries recursively until the server accepts the connection. 
 * This might cause some freezes in the simulation, specially if many clients are connected to the server.
 * 
 * @author MOREIRA Gabriel
 *
 */
public class ClientTCP {
	
	static int port = 6789;
	static Socket clientSocket;
	static double[] val = null;
	
	public static double[] communicateWithServer(byte[] data) {
		try {		
			connection(data);
		} catch (UnknownHostException e) {
			System.out.println("Simulation> Unknown host. Shutting down simulation.");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Simulation> Impossible to establish connection: Server busy. Trying again...");
			
			/** Tries to connect recursively until it succeeds */
			val = communicateWithServer(data);
		}
		return val;
	}
	
	/**
	 * Establishes the connection.
	 * @param data to communicate.
	 * @throws UnknownHostException.
	 * @throws IOException is handled in the communicateWithServer method by calling the method recursively until in no longer throws the exception.
	 * 
	 */
	public static void connection(byte[] data) throws UnknownHostException, IOException {
		clientSocket = new Socket(InetAddress.getLocalHost(), port);
		OutputStream outputStream = clientSocket.getOutputStream();
		InputStream inputStream = clientSocket.getInputStream();
		outputStream.write(data);
		byte[] dataReceived = new byte[4096*8]; 
		inputStream.read(dataReceived);
		val = Conversions.byteToDouble(dataReceived);
		clientSocket.close();
	}

}
