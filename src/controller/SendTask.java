package controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javafx.concurrent.Task;

public class SendTask extends Task {

	private String hostName;
	private int port;
	private String path;
	private String name;
	
	public SendTask(String hostName, String portNumber, String selected) {
		this.hostName = hostName;
		port = Integer.parseInt(portNumber);
		path = selected;
	}
	
	@Override
	protected Object call() throws Exception {
		
		try ( 	
			    Socket socket = new Socket(hostName, port);
			    
				//pobranie z pliku do bufora
				FileInputStream fis = new FileInputStream(path);
				//do przeliczenia
				FileInputStream cfis = new FileInputStream(path);
				//wyslanie z bufora w socket
				OutputStream os = socket.getOutputStream();
			) {
				name = path.substring(path.lastIndexOf('\\')+1);
				os.write(name.getBytes());
				os.flush();
			
				byte[] buffer = new byte[1024];
				long total = 0;
				updateMessage("Wysy³anie w toku..");
				
				while(cfis.read() > -1)
					total++;
				System.out.println("Bêdzie z tego "+total+" segmentów.");
				long current = 0;
				
				while(fis.read(buffer) > -1) {
					os.write(buffer);
					os.flush();
					updateProgress(current++,total);
					updateMessage(current+"//"+total);
				}
				
				//proces zamkniecia socketu
				AutoCloseable acos = os;
				AutoCloseable accfis = cfis;
				AutoCloseable acsock = socket;
				AutoCloseable acfis = fis;
				acos.close();
				accfis.close();
				acfis.close();
				acsock.close();
				updateMessage("Wysy³anie zakoñczone! "+name);
			}
			catch(IOException e) {
				System.out.println(e.toString());
			}
		
		return null;
	}
}