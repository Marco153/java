package site;

import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Mysql {
	public String url;
	public String username;
	public String password;
	void UpdateQuery(String query)
	{
		try(Connection con = DriverManager.getConnection(url, username, password))
		{
			Statement st = con.createStatement();
			st.executeUpdate(query);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
	}
	Map<String, List<String>> ExecuteQuery(String query) throws SQLException
	{
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		List<String> idColumn = new ArrayList<String>();
		List<String> nameColumn = new ArrayList<String>();
		List<String> imageColumn = new ArrayList<String>();
	
		try(Connection con = DriverManager.getConnection(url, username, password))
		{
			Statement st = con.createStatement();
			ResultSet res = st.executeQuery("select * from product;");
			while(res.next())
			{
				idColumn.add(Integer.toString(res.getInt("id")));
				nameColumn.add(res.getString("name"));
				imageColumn.add(res.getString("image"));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		ret.put("id", idColumn);
		ret.put("name", nameColumn);
		ret.put("image", imageColumn);
		
		return ret;
	}
	public Mysql(String database, String userName, String passWord)
	{
		url = "jdbc:mysql://localhost:3306/"+database;
		username = userName;
		password = passWord;

		System.out.println("Connecting database ...");
	}
}
