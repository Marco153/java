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

import java.sql.*;
	

public class main {
	class HelloWorld {
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
			String httpResponse = "HTTP/1.1 200 OK\r\n\r\n"+file;

			outputStream.write(httpResponse.getBytes("UTF-8"));
			outputStream.close();
		}
		public static Connection con;
	    public static void main(String[] args) throws JSONException {
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

	    	
	    	int port = 42069;
	    	try (ServerSocket serverSocket = new ServerSocket(port)) {
	    		 
	            System.out.println("Server is listening on port " + port);
	            
	            
	            while (true) {
	                Socket socket = serverSocket.accept();
	 
	                System.out.println("New client connected");
	                
	                InputStream inputStream = socket.getInputStream();
	                
	                byte[] buffer = new byte[4096];
	                int bytesRead;
	                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

	                bytesRead = socket.getInputStream().read(buffer, 0, 4096);
					byteArray.write(buffer, 0, bytesRead);
					

	                String req = byteArray.toString();

					HttpRequest reqHttp = new HttpRequest(req);
					
					
	                System.out.println("req is "+req);
	                
	                if(reqHttp.url.equals("/"))
	                {
						SendFile("index.html", socket);
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
								
								if(Character.isDigit(valStr.charAt(0)))
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
						Map<String, List<String>> ret = mysql.ExecuteQuery("select * from product");
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
								json += " \"" + column +"\" :\"" + columnValues.get(Integer.parseInt(curId) - 1) + "\"";
								
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
	                else if(reqHttp.url.equals("/dbget"))
	                {
	                	String name = reqHttp.params.get("name");
	                	if(name != null)
	                	{
	                		Map<String, List<String>> ret = mysql.ExecuteQuery("select * from product where name = \""+name+"\";");
	                		SendString(ret.get("id").get(0), socket);
	                	}
	                }



	                System.out.println("Client disconnected");
	 
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
}
