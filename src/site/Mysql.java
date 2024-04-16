package site;

import java.sql.*; 
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class Mysql {
	public String url;
	public String username;
	public String password;
	String QueryOnDuplicateValString(String val)
	{
		//letter case
		if(Character.isLetter(val.charAt(0)))
		{
			return "\""+val+"\"";
		}
		// maybe number
		else
		{
			return val;
		}
	}
	void UpdateDb(String dbString)
	{
		Object obj = JSONValue.parse(dbString);
		JSONArray array = (JSONArray)obj;
		String query = "INSERT INTO product(";
		List<String> insertIntoVals = new ArrayList<String>();
		//List<String> onDuplicateVals = new ArrayList<String>();

		JSONObject dummyJson = (JSONObject)array.get(0);
		Set<String> dummyKeysSet = dummyJson.keySet();
		
		String onDuplicateEnding = "";

		int curKey = 0;
		for(String column : dummyKeysSet)
		{
			query += column;
	 		onDuplicateEnding += column + " = " + "new."+column;

			if(curKey < (dummyKeysSet.size() - 1))
			{
				query += ", ";
				 onDuplicateEnding += ", ";
			}
			curKey += 1;
		}
		query += ") VALUES";

		for(int i = 0; i < array.size(); i++)
		{
			JSONObject json = (JSONObject)array.get(i);
			query += "(";
			
			Set<String> keysSet = json.keySet();
			
			
			curKey = 0;
			String insertIntorow = "";
			//String onDuplicaterow = "";
			for(String column : keysSet)
			{
				String jsonColumnVal = json.get(column).toString();
				jsonColumnVal.replace("\n", "\\n");
				insertIntorow  += QueryOnDuplicateValString(jsonColumnVal);
				//onDuplicaterow += column + "="+;
				if(curKey < (keysSet.size() - 1))
				{
					insertIntorow += ", ";
				}
				curKey++;
			}
			query += insertIntorow;
			query += ")";

			if(i < (array.size() - 1))
			{
				query += ",";
			}
		}
		query += "as new ON DUPLICATE KEY UPDATE " + onDuplicateEnding;
		UpdateQuery(query);
		
	}
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
	
		try(Connection con = DriverManager.getConnection(url, username, password))
		{
			Statement st = con.createStatement();
			ResultSet res = st.executeQuery("select * from product;");
			while(res.next())
			{
				idColumn.add(Integer.toString(res.getInt("id")));
				nameColumn.add(res.getString("name"));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		ret.put("id", idColumn);
		ret.put("name", nameColumn);
		
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
