package site;

import java.io.*;



import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
class User
{
	int id;
	String name;
}
public class main {

	public static int secret;
	public static Map<Integer, User> users;

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
	public static void SendString(String str, Socket socket) throws IOException
	{
		OutputStream outputStream = socket.getOutputStream();
		String httpResponse = "HTTP/1.1 200 OK\r\n\r\n"+str;
		outputStream.write(httpResponse.getBytes("UTF-8"));
		outputStream.close();
	}
	public static void SendFile(String fileName, Socket socket) throws IOException
	{
		String file = new String(Files.readAllBytes(Paths.get((fileName))));
		OutputStream outputStream = socket.getOutputStream();
		String ext = getFileExtension(fileName);
		String contentType = "";
		
		if(ext.equals("png"))
		{
			contentType += "Content-Type: image/png\n";
		}
		if(ext.equals("js"))
		{
			contentType += "Content-Type: text/javascript\n";
		}
		String httpResponse = "HTTP/1.1 200 OK\r\n"+contentType+"\r\n"+file;

		outputStream.write(httpResponse.getBytes("UTF-8"));
		outputStream.close();
	}
	public static Connection con;
	public static void main(String[] args) throws JSONException {
		Random rand = new Random();
		users = new HashMap<Integer, User>();
		secret = rand.nextInt(999999999);


		String url = "jdbc:mysql://localhost:3306/products";
		String username = "java";
		String password = "1234";

		System.out.println("Connecting database ...");

		Mysql mysql = new Mysql("products", "java", "1234");
		try
		{
			mysql.UpdateQuery("update product set name =\"processador\" where id = 1");
			Map<String, List<String>> ret = mysql.ExecuteQuery("select * from product;");
			for(String s : ret.get("name"))
			{
				System.out.println("name: " + s);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		WebSocket ws= new WebSocket();
		ws.start();

		int port = 42069;
		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("Server is listening on port " + port);


			while (true) {
				Socket socket = serverSocket.accept();

				//System.out.println("New client connected");

				InputStream inputStream = socket.getInputStream();

				byte[] buffer = new byte[4096];
				int bytesRead;
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

				bytesRead = socket.getInputStream().read(buffer, 0, 4096);
				byteArray.write(buffer, 0, bytesRead);


				String req = byteArray.toString();

				HttpRequest reqHttp = new HttpRequest(req);


				//System.out.println("req is "+req);


				String ext = getFileExtension(reqHttp.url);

				if(reqHttp.url.equals("/panel"))
				{
					SendFile("panel.html", socket);
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
					SendFile("imgs/"+reqHttp.url.substring(1), socket);
				}
				else if(reqHttp.url.equals("/common.js"))
				{
					SendFile("common.js", socket);
				}
				else if(reqHttp.url.equals("/chat.js"))
				{
					SendFile("chat.js", socket);
				}
				else if(reqHttp.url.equals("/send"))
				{
					String m = reqHttp.params.get("m");
					ws.BroadCast(m);

					OutputStream outputStream = socket.getOutputStream();
					outputStream.write(("ok").getBytes("UTF-8"));
					outputStream.close();

				}
				else if(reqHttp.url.equals("/chat"))
				{
					SendFile("chat.html", socket);
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
					JSONArray ar = new JSONArray(reqHttp.body);

					String json = "INSERT INTO product(";
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
				else if(reqHttp.url.equals("/dball"))
				{
					Map<String, List<String>> ret = mysql.ExecuteQuery("select * from product;");
					List<String> ids = ret.get("id");
					Set<String> keySet = ret.keySet();


					String json = "{\"db\":[";
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
							if(column == "id")
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
					SendString(json, socket);
				}
				else if(reqHttp.url.equals("/dbadd"))
				{
					String id = reqHttp.params.get("id");
				}
				else if(reqHttp.url.equals("/chat"))
				{
					SendFile("chat.html", socket);
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
				else if(reqHttp.url.equals("/userpanel"))
				{
					JSONObject obj = new JSONObject(reqHttp.body);

					Integer i = Integer.parseInt(obj.getString("session"));
					System.out.println("user panel uid is " + i + " and its name is " + users.get(i).name);

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
						u.id = uid;
						users.put(uid, u);
						SendString(createUserRegResponse(1, Integer.toString(uid)), socket);
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
				else if(reqHttp.url.equals("/dbup"))
				{
					JSONArray ar = new JSONArray(reqHttp.body);

					String json = "INSERT INTO product(";
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
				else if(reqHttp.url.equals("/dball"))
				{
					Map<String, List<String>> ret = mysql.ExecuteQuery("select * from product;");
					List<String> ids = ret.get("id");
					Set<String> keySet = ret.keySet();


					String json = "{\"db\":[";
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
							if(column == "id")
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
					SendString(json, socket);
				}
				else if(reqHttp.url.equals("/dbadd"))
				{
					String id = reqHttp.params.get("id");
					if(id != null)
					{
						mysql.UpdateQuery("insert into product(id, name) values ("+id+", \"\");");
						SendString("added new column", socket);
					}
				}
				else if(reqHttp.url.equals("/dbrm"))
				{
					String id = reqHttp.params.get("id");
					if(id != null)
					{
						mysql.UpdateQuery("delete from product where id = "+id+";");
					SendString("removed", socket);
					}
				}
				else if(reqHttp.url.equals("/dbget"))
				{
					String name = reqHttp.params.get("name");
					if(name != null)
					{
						Map<String, List<String>> ret = mysql.ExecuteQuery("select * from product where name = \""+name+"\";");
					SendString(ret.get("id").get(0), socket);
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
