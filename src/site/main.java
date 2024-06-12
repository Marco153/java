package site;

import java.io.*;



import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.sql.*;
public class main {

	public static int secret;
	public static String IP;
	public static int port;
	public static String productTableSchema;
	public static Map<String, String> tablesSchemas;
	public static Map<String, String> waitingImages;

	public static Map<String, User> users;

	public static void DbUpLogic(HttpRequest reqHttp, Mysql mysql, Socket socket) throws SQLException, JSONException
	{
		String tb = reqHttp.params.get("tb");
		if(tb == null)
			return;

		JSONArray ar = new JSONArray(reqHttp.body);

		String json = "INSERT INTO "+ tb +"(";
		String allColumnNames = "";
		String queryUpdateEnd = "";
		String values = "";
		for(int i = 0; i < ar.length(); i++)
		{

			JSONObject obj = ar.getJSONObject(i);
			int namesLen = obj.names().length();
			values += "(";
			for(int j = 0; j < namesLen; j++)
			{
				String columnName = obj.names().getString(j);
				if(i == 0)
				{
					allColumnNames += columnName;
					queryUpdateEnd += columnName + " = VALUES(" +columnName+")";
				}

				String valStr = obj.getString(columnName);

				if(!valStr.isEmpty() && Character.isDigit(valStr.charAt(0)))
				{
					values += valStr;
				}
				else
				{
					values += "\"" + valStr + "\"";
				}


				if(j < (namesLen - 1))
				{
					if(i == 0)
					{
						allColumnNames += ", ";
						queryUpdateEnd += ", ";
					}
					values += ", ";
				}
			}
			values += ")";
			if(i < (ar.length() - 1))
			{
				values += ", ";
			}
		}
		json += allColumnNames + ") VALUES"+values;
		json += " ON DUPLICATE KEY UPDATE ";
		json += queryUpdateEnd + ";";


		System.out.println("update query is " + json);

		mysql.UpdateQuery(json);
	}
	public static String DbAllLogic(HttpRequest reqHttp, Mysql mysql, Socket socket, String query) throws SQLException
	{
		String tb = reqHttp.params.get("tb");
		if(tb == null)
			return createUserRegResponse(0, "error");

		
		query = query.replace("NAME_HERE", tb);

		Map<String, List<String>> ret = mysql.ExecuteQuery(query);
		System.out.println("map is " + ret);
		List<String> ids = ret.get("id");
		Set<String> keySet = ret.keySet();


		String json = "{\"ok\" : 1, \"db\":[";
		int idsLen = ids.size();
		int i = 0;
		for(String curId : ids)
		{
			json += "{";
			json += "\"id\" :\"" + curId +"\",";
			int columnIdx = 0;
			int columnLen = keySet.size();
			for(String column : keySet)
			{
				//System.out.println("cur column is " + column);
				if(column.equals("id"))
					continue;
				List<String> columnValues = ret.get(column);
				json += " \"" + column +"\" :\"" + columnValues.get(i) + "\"";

				if(columnIdx < (columnLen - 2))
				json += ",";
				json += "";
				columnIdx++;

			}
			//json.
			json += "}";
			if(i < (idsLen - 1))
				json += ",";
			i++;
		}

		json += "]}";
		return json;
	}
	public static String createUserRegResponse(int ok, String err ) 
	{
		return "{ \"ok\": " + ok+", \"info\" :\""+ err + "\"}";
	}
	public static String getFileExtension(String str) {
		int lastIndexOf = str.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return str.substring(lastIndexOf + 1);
	}
	public static void SendStringHeader(String str, String header, Socket socket) throws IOException
	{
		OutputStream outputStream = socket.getOutputStream();
		String httpResponse = "HTTP/1.1 200 OK\r\n"+ header + "\r\n\r\n"+str;
		outputStream.write(httpResponse.getBytes("UTF-8"));
		outputStream.close();
	}
	public static void SendString(String str, Socket socket) throws IOException
	{
		OutputStream outputStream = socket.getOutputStream();
		String httpResponse = "HTTP/1.1 200 OK\r\n\r\n"+str;
		outputStream.write(httpResponse.getBytes("UTF-8"));
		outputStream.close();
	}
	public static void SendFile(String fileName, Socket socket)
	{

		String file = "";
		try{
			file = new String(Files.readAllBytes(Paths.get((fileName))));
			OutputStream outputStream = socket.getOutputStream();
			String ext = getFileExtension(fileName);
			String contentType = "";
			
			if(ext.equals("png") || ext.equals("jpeg"))
			{
				contentType += "Content-Type: text/json\n";
				byte[] fileContent = Files.readAllBytes(Paths.get((fileName)));
				String encodedString = "data:image/"+ext+";base64, " + Base64.getEncoder().encodeToString(fileContent);

				String httpResponse = "HTTP/1.1 200 OK\r\n"+contentType+"\r\n"+encodedString;
				//System.out.println("png img is " + httpResponse);

				outputStream.write(httpResponse.getBytes("UTF-8"));
				outputStream.close();
				return;
	}
			if(ext.equals("js"))
			{
				contentType += "Content-Type: text/javascript\n";
			}
			String httpResponse = "HTTP/1.1 200 OK\r\n"+contentType+"\r\n"+file;

			outputStream.write(httpResponse.getBytes("UTF-8"));
			outputStream.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public static Connection con;
	public static User GetUserFromCookie(Socket socket, HttpRequest reqHttp)
	{
		String cookieVal = reqHttp.headers.get("Cookie");
		if(cookieVal == null || cookieVal.isEmpty())
			return null;

		WordReader cookieParam = new WordReader(cookieVal);
		Map<String, String> cookies = HttpRequest.ParseParams(cookieParam, ';');
		String uid = cookies.get("uid");
		System.out.println("up got uid  "+ uid);

		if(uid == null || uid.isEmpty())
			return null;

		return users.get(uid);
	}
	public static void TestUidCookieSondPage(Socket socket, HttpRequest reqHttp, String page)
	{
		User user = GetUserFromCookie(socket, reqHttp);
		if(user != null)
		{
			SendFile(page, socket);
			System.out.println("user panel uid is " + user.id); 
		}
		else
		{
			//SendString(createUserRegResponse(0, "usuario usuario nao encontrado"), socket);
			SendFile("denied.html", socket);
		}
	}

	public static String GetBodyRemainingBedy(Socket socket, HttpRequest reqHttp)
	{

		String req = "";
		try
		{
			int bodyLen = reqHttp.body.length();
			String conLenStr = reqHttp.headers.get("Content-Length");
			System.out.println("on remaining body conLenStr "+conLenStr + " bodylen "+ bodyLen);
				
			if(conLenStr == null && bodyLen == 0)
				return "";
			Integer number;

			// limit of all packets combined
			int actualConLen = 1024 * 1024 * 4;

			if(conLenStr != null)
			{
				try {
				    actualConLen = Integer.valueOf(conLenStr);
				} catch (NumberFormatException e) {
				    System.out.println("Invalid integer input");
				}
			}

			
			int stillToGet = actualConLen - bodyLen;

			System.out.println("remaining body bytes "+stillToGet);

			InputStream inputStream = socket.getInputStream();


			byte[] buffer = new byte[1024 * 1024 * 6];
			int curBytesRead = 0;
			
			socket.setSoTimeout(1000);

			while(curBytesRead < stillToGet)
			{
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
				int bytesRead = socket.getInputStream().read(buffer, 0, 1024 * 1024 * 4);

				byteArray.write(buffer, 0, bytesRead);

				req += byteArray.toString();

				curBytesRead += bytesRead;

			}
			PrintStream out = new PrintStream(new File("base64file"));
			out.println(req);
			out.close();
			//System.out.println("body req is "+req);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return req;
		//socket.setSoTimeout(0);
	}
	public static void ChatPage(Socket socket, HttpRequest reqHttp)
	{
		try
		{
			JSONObject obj = new JSONObject(reqHttp.body);

			String uid = obj.getString("session");

			User user = users.get(uid);
			if(user != null)
			{
				System.out.println("user panel uid is " + uid); 
				SendFile("chat.html", socket);
			}
			else
			{
				//SendString(createUserRegResponse(0, "usuario usuario nao encontrado"), socket);
				SendFile("denied.html", socket);
			}
		}
		catch(JSONException e)
		{
			SendFile("denied.html", socket);
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws JSONException {
		IP = "192.168.1.2";
		port = 42069;
		Random rand = new Random();
		users = new HashMap<String, User>();
		tablesSchemas = new HashMap<String, String>();
		waitingImages = new HashMap<String, String>();

		secret = rand.nextInt(999999999);

		tablesSchemas.put("product", "select * from product;");
		tablesSchemas.put("promos", "select * from promos");

		String url = "jdbc:mysql://localhost:3306/products";
		String username = "java";
		String password = "1234";

		System.out.println("Connecting database ...");

		Mysql mysql = new Mysql("products", "java", "1234");
		InetAddress addr = null;
		try
		{
			mysql.UpdateQuery("update product set name =\"processador\" where id = 1");
			Map<String, List<String>> ret = mysql.ExecuteQuery(tablesSchemas.get("product"));
			for(String s : ret.get("name"))
			{
				System.out.println("name: " + s);
			}
			addr = InetAddress.getByName(IP);
		}
		catch(SQLException | UnknownHostException e)
		{
			e.printStackTrace();
		}
		WebSocket ws= new WebSocket();
		ws.start();

		try (ServerSocket serverSocket = new ServerSocket(port, 50, addr)) {

			System.out.println("Server is listening on port " + port);
			 //System.out.println("Working Directory = " + System.getProperty("user.dir"));


			while (true) {
				Socket socket = serverSocket.accept();

				//System.out.println("New client connected");

				InputStream inputStream = socket.getInputStream();

				String req = "";
				int attempts = 0;

				byte[] buffer = new byte[1024 * 1024 * 6];
				int bytesRead;
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
				
				bytesRead = socket.getInputStream().read(buffer, 0, 1024 * 1024 * 4);
				//System.out.println("bytes are "+buffer);

				byteArray.write(buffer, 0, bytesRead);


				req += byteArray.toString();

				HttpRequest reqHttp = new HttpRequest(req);

				reqHttp.body += GetBodyRemainingBedy(socket, reqHttp);

				//if(appType



				System.out.println("req is "+req);


				String ext = getFileExtension(reqHttp.url);

				if(reqHttp.url.equals("/panel"))
				{
					TestUidCookieSondPage(socket, reqHttp, "panel.html");
				}
				else if(reqHttp.url.equals("/login"))
				{
					SendFile("login.html", socket);
				}
				else if(ext.equals("js"))
				{
					SendFile(reqHttp.url.substring(1), socket);
					//System.out.println("css file: " );
				}
				else if(ext.equals("css"))
				{
					SendFile(reqHttp.url.substring(1), socket);
					//System.out.println("css file: " );
				}
				else if(ext.equals("jpeg"))
				{
					String img = "imgs/"+reqHttp.url.substring(1);
					System.out.println("loading img: "+ img);
					SendFile(img, socket);
				}
				else if(ext.equals("png"))
				{
					int lastIndexOf = reqHttp.url.lastIndexOf(".");

					//String fileName = fileName.substring(0, lastIndexOf);
					SendFile("imgs/"+reqHttp.url, socket);
				}
				else if(reqHttp.url.equals("/common.js"))
				{
					SendFile("common.js", socket);
				}
				else if(reqHttp.url.equals("/uploadimg"))
				{
					System.out.println("req is upimg"+req);
					SendFile("chat.js", socket);
				}
				else if(reqHttp.url.equals("/chat.js"))
				{
					SendFile("chat.js", socket);
				}
				else if(reqHttp.url.equals("/getwaitingimg"))
				{
					String imgId = reqHttp.params.get("id");
					
					String ret = null;

					String send = "";
					if(imgId != null)
						ret = waitingImages.get(imgId);

					if(ret != null)
						send = createUserRegResponse(1, ret);
					else
						send = createUserRegResponse(0, "error");

					SendString(send, socket);

				}
				else if(reqHttp.url.equals("/sendimg"))
				{
					String userName = reqHttp.params.get("u");
					String message = reqHttp.body;
					//message = "data:image/jpg;base64,"+ Base64.getEncoder().encodeToString(message.getBytes("UTF-8"));
					message = message;
					int uid = rand.nextInt(999999999);
					String key = Integer.toString(uid) + 'i';
					waitingImages.put(key, message);


					System.out.println("u  "+ userName + ", message " + message);

					ws.SendImgTo(key, userName, users);

					OutputStream outputStream = socket.getOutputStream();
					outputStream.write(("ok from server").getBytes("UTF-8"));
					outputStream.close();

				}
				else if(reqHttp.url.equals("/send"))
				{
					String userName = reqHttp.params.get("to");
					String message = reqHttp.body;


					System.out.println("to  "+ userName + ", message " + message);

					ws.SendMessageTo(message, userName, users);

					OutputStream outputStream = socket.getOutputStream();
					outputStream.write(("ok").getBytes("UTF-8"));
					outputStream.close();

				}
				else if(reqHttp.url.equals("/chat"))
				{
					TestUidCookieSondPage(socket, reqHttp, "chat.html");
					/*
					JSONObject obj = new JSONObject(reqHttp.body);

					Integer uid = Integer.parseInt(obj.getString("session"));

					User user = users.get(uid);
					if(user != null)
					{
						System.out.println("chat uid is " + uid); 
						SendFile("chat.html", socket);
					}
					else
					{
						SendFile("denied.html", socket);
					}
					*/
				}
				else if(reqHttp.url.equals("/"))
				{
					SendFile("index.html", socket);
				}
				else if(reqHttp.url.equals("/index.js"))
				{
					SendFile("index.js", socket);
				}
				else if(reqHttp.url.equals("/main.js"))
				{
					SendFile("main.js", socket);
				}
				else if(reqHttp.url.equals("/dbup"))
				{
					DbUpLogic(reqHttp, mysql, socket);
				}
				else if(reqHttp.url.equals("/dbpromo"))
				{
					String val = DbAllLogic(reqHttp, mysql, socket, 
					"select product.id, product.name, product.image, product.price, promos.new_price from product inner join promos on promos.pid = product.id;");

					SendString(val, socket);
				}
				else if(reqHttp.url.equals("/dball"))
				{
					String val = DbAllLogic(reqHttp, mysql, socket, "select * from NAME_HERE;");
					SendString(val, socket);
				}
				else if(reqHttp.url.equals("/"))
				{
					SendFile("index.html", socket);
				}
				else if(reqHttp.url.equals("/index.js"))
				{
					SendFile("index.js", socket);
				}
				else if(reqHttp.url.equals("/main.js"))
				{
					SendFile("main.js", socket);
				}
				else if(reqHttp.url.equals("/usersinfo"))
				{
					// TODO: ensure user has permissions to see the database
					
					SendString(DbAllLogic(reqHttp, mysql, socket, "select name, id from users;"), socket);
				}
				else if(reqHttp.url.equals("/userpanel"))
				{
					System.out.println("headers is "+ reqHttp.headers);
					User user = GetUserFromCookie(socket, reqHttp);
					if(user != null)
					{
						if(user.name.equals("adm"))
						{
							SendFile("admpanel.html", socket);
						}
						else
						{
							SendFile("user.html", socket);
						}
					}
					else
					{
						SendFile("denied.html", socket);
					}
				}
				else if(reqHttp.url.equals("/auth"))
				{
					String user = reqHttp.params.get("u");
					String pw = reqHttp.params.get("pw");

					if(!mysql.LoginUser(user, pw))
					{
						System.out.println("usuario ou senha nao batem");
						SendString(createUserRegResponse(0, "usuario ou senha nao batem"), socket);
					}
					else
					{
						System.out.println("logando usuario");

						int uid = rand.nextInt(999999999);
						User u = new User();
						u.name = user;
						u.id = Integer.toString(uid);
						users.put(u.id, u);
						SendStringHeader(createUserRegResponse(1, u.id), "Set-Cookie: uid="+u.id+";", socket);
					}
				}
				else if(reqHttp.url.equals("/cad"))
				{

					String user = reqHttp.params.get("u");
					String pw = reqHttp.params.get("pw");
					System.out.println("user pw is "+ pw);
					System.out.println("reqHttp is "+ reqHttp.params);


					if(!mysql.RegisterUser(user, pw))
					{
						System.out.println("usuario cadastrado");
						SendString(createUserRegResponse(0, "usuario ja cadastrado"), socket);
					}
					else
					{
						System.out.println("criando usuario");
						SendString(createUserRegResponse(1, "cadastrado com sucesso"), socket);
					}
				}
				else if(reqHttp.url.equals("/user"))
				{
				}
				else if(reqHttp.url.equals("/dbadd"))
				{
					String id = reqHttp.params.get("id");
					String tb = reqHttp.params.get("tb");
					System.out.println("got id "+ id + " tb "+tb);
					if(id != null && tb != null)
					{

						mysql.UpdateQuery("insert into " + tb +"(id) values ("+id+");");
						SendString("added new column", socket);
					}
				}
				else if(reqHttp.url.equals("/dbrm"))
				{
					String id = reqHttp.params.get("id");
					String tb = reqHttp.params.get("tb");
					if(id != null && tb != null)
					{
						mysql.UpdateQuery("delete from "+tb +" where id = "+id+";");
					SendString("removed", socket);
					}
				}
				else if(reqHttp.url.equals("/dbget"))
				{
					String name = reqHttp.params.get("name");
					if(name != null)
					{
						//Map<String, List<String>> ret = mysql.ExecuteQuery(productTableSchema);
					//SendString(ret.get("id").get(0), socket);
					}
				}



				//System.out.println("Client disconnected");

				socket.close();
			}

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
