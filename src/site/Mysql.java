package site;

import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

class ColumnAndType
{
	List<String> vals;
	String type;
}
public class Mysql {
	public String url;
	public String username;
	public String password;
	void UpdateQuery(String query)
	{
		try(Connection con = DriverManager.getConnection(url, username, password))
		{
			System.out.println("updating "+query);
			Statement st = con.createStatement();
			st.executeUpdate(query);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
	}
	boolean LoginUser(String user, String pw) throws SQLException
	{
		try(Connection con = DriverManager.getConnection(url, username, password))
		{
			Statement st = con.createStatement();
			ResultSet res = st.executeQuery("select * from users where name = \"" + user + "\" and pw = \""+ pw + "\";");
			if(!res.next())
			{
				return false;
			}
			return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	boolean RegisterUser(String user, String pw) throws SQLException
	{
		try(Connection con = DriverManager.getConnection(url, username, password))
		{
			Statement st = con.createStatement();
			ResultSet res = st.executeQuery("select * from users where name = \"" + user + "\";");

			if(res.next())
			{
				return false;
			}
			String q = "insert into users(name, pw) values(\"" + user + "\", \"" + pw + "\");";
			System.out.println("cad q is "+ q);
			st.executeUpdate(q);
			return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		
	}

	Map<String, List<String>> ExecuteQuery(String query) throws SQLException
	{
		String tableName = "";

		WordReader wReader = new WordReader(query);
		tableName = wReader.readWord();
		Map<String, ColumnAndType> columns = new HashMap<String, ColumnAndType>();

		System.out.println("the query is " + query);
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		

		List<String> idColumn = new ArrayList<String>();
		List<String> nameColumn = new ArrayList<String>();
		List<String> imageColumn = new ArrayList<String>();
		List<String> priceColumn = new ArrayList<String>();
	
		

		try(Connection con = DriverManager.getConnection(url, username, password))
		{
			Statement st = con.createStatement();
			ResultSet res = st.executeQuery(query);

			ResultSetMetaData md = res.getMetaData();
			int columnsCount = md.getColumnCount();
			for (int i = 1; i <= columnsCount; ++i) {
				ColumnAndType c = new ColumnAndType();
				c.vals = new ArrayList<String>();
				columns.put(md.getColumnName(i), c);
			}

			Set<String> keySet = columns.keySet();

			while(res.next())
			{
				//for
				for(String column : keySet)
				{
					ColumnAndType c = columns.get(column);
					c.vals.add(res.getString(column));
				}
				/*
				idColumn.add(Integer.toString(res.getInt("id")));
				nameColumn.add(res.getString("name"));
				imageColumn.add(res.getString("image"));
				priceColumn.add(res.getString("price"));
				*/
			}

			System.out.println(columns);
			for(String column : keySet)
			{
				ColumnAndType c = columns.get(column);
				ret.put(column, c.vals);
				
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		
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
