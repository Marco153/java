package site;
import java.io.*;



import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class WebSocket extends Thread{
	public class Client
	{
		Socket s;
		int clienId;
	}
	public List<Client> clients = new ArrayList<Client>();
	//public atic Map<String, User> users;
	public void SendImgTo(String message, String userName, Map<String, User> users)
	{
		for (Map.Entry<String, User> pair : users.entrySet())
		{
			User c = pair.getValue();
			System.out.println("iterating "+c.name +" and target is "+userName + ", its socket "+ c.socketImg);
			if(c.name.equals(userName) && c.socketImg != null)
			{
				System.out.println("found "+c.name);
				try
				{
					OutputStream out = c.socketImg.getOutputStream();
					WebSocket.sendMessage(out, message);
				} catch (IOException ex) {
					System.out.println("Server exception: " + ex.getMessage());
					ex.printStackTrace();
				}
				break;
			}
		}
	}
	public void SendMessageTo(String message, String userName, Map<String, User> users)
	{
		for (Map.Entry<String, User> pair : users.entrySet())
		{
			User c = pair.getValue();
			System.out.println("iterating "+c.name +" and target is "+userName);
			if(c.name.equals(userName) && c.socket != null)
			{
				System.out.println("found "+c.name);
				try
				{
					OutputStream out = c.socket.getOutputStream();
					WebSocket.sendMessage(out, message);
				} catch (IOException ex) {
					System.out.println("Server exception: " + ex.getMessage());
					ex.printStackTrace();
				}
				break;
			}
		}
	}
	public void BroadCast(String str)
	{
		System.out.println("total clients is " + clients.size());
		for(int i = 0; i < clients.size(); i++)
		{
			Client c = clients.get(i);
			try
			{
				OutputStream out = c.s.getOutputStream();
				WebSocket.sendMessage(out, str);
			} catch (IOException ex) {
				System.out.println("Server exception: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}
	public void run()
	{
		InetAddress addr = null;
		try
		{
			addr = InetAddress.getByName(main.IP);
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
		}
		try (ServerSocket serverSocket = new ServerSocket(main.port + 1, 51, addr)) {

			while (true) {
				Socket socket = serverSocket.accept();
				InputStream in = socket.getInputStream();
				Client c = new Client();
				c.s = socket;
				clients.add(c);

				byte[] buffer = new byte[1024 * 1028 * 6];
				int bytesRead;
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

				bytesRead = in.read(buffer, 0, 1024 * 1024 * 6);
				byteArray.write(buffer, 0, bytesRead);
				

				String req = byteArray.toString();
				System.out.println("******msg in ws: "+ req);
				//System.out.println("new con req"+req);

				HttpRequest reqHttp = new HttpRequest(req);

				OutputStream out = socket.getOutputStream();
				
				User u = main.GetUserFromCookie(socket, reqHttp);
				if(u == null)
				{
					socket.close();
					break;
				}

				if(reqHttp.url.equals("/img"))
				{
					u.socketImg = socket;
					System.out.println("socket img "+u.socketImg);
				}
				else
					u.socket = socket;

				String secKey = reqHttp.headers.get("Sec-WebSocket-Key");

				
				byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
					+ "Connection: Upgrade\r\n"
					+ "Upgrade: websocket\r\n"
					+ "Sec-WebSocket-Accept: "
					+ Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((secKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
					+ "\r\n\r\n").getBytes("UTF-8");
				  out.write(response, 0, response.length);
				  out.flush();
				//out.flush();
				//System.out.println("address: "+ socket.getRemoteSocketAddress().toString());
				//sendMessage(out, "hello bro");
				
				
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void sendMessage(OutputStream out, String message) throws IOException {
		byte[] messageBytes = message.getBytes("UTF-8");
		int messageLength = messageBytes.length;

		ByteArrayOutputStream frame = new ByteArrayOutputStream();
		frame.write(0x81); // FIN bit set and text frame opcode

		if (messageLength <= 125) {
			frame.write(messageLength);
			System.out.println("125");
		} else if (messageLength <= 65535) {
			frame.write(126);
			frame.write((messageLength >> 8) & 0xFF);
			frame.write(messageLength & 0xFF);
			System.out.println("65535");
		} else {
			System.out.println("grande " + messageLength);
			frame.write(127);
			frame.write((messageLength >> 56) & 0xFF);
			frame.write((messageLength >> 48) & 0xFF);
			frame.write((messageLength >> 40) & 0xFF);
			frame.write((messageLength >> 32) & 0xFF);
			frame.write((messageLength >> 24) & 0xFF);
			frame.write((messageLength >> 16) & 0xFF);
			frame.write((messageLength >> 8) & 0xFF);
			frame.write(messageLength & 0xFF);
		}

		frame.write(messageBytes);
		out.write(frame.toByteArray());
		out.flush();
	}
}
