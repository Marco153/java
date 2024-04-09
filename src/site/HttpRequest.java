package site;
import java.io.*;
import java.util.Map;
import java.util.HashMap;

public class HttpRequest {
	private String reqLine;
	public String method;
	public String url;
	public Map<String, String> params;
	
	public void ParseParams(WordReader reader)
	{
		reader.EatSpace();
		boolean firstTime = true;
		while(firstTime || reader.CurChar() == '&')
		{
			String paramName = reader.readWord();
			reader.EatSpace();

			if(reader.CurChar() != '=')
				return;
			
			reader.GetChar();

			String paramValue = reader.readWord();

			reader.EatSpace();
			params.put(paramName, paramValue);

			firstTime = false;
		}
	}
	public HttpRequest(String req)
	{
		BufferedReader bufReader = new BufferedReader(new StringReader(req));

		params = new HashMap<String, String>();
		try
		{
			reqLine = bufReader.readLine();
			WordReader wordReader = new WordReader(reqLine);
			
			method = wordReader.readWord();

			wordReader.EatSpace();

			url = "/";
			while(wordReader.CurChar() == '/')
			{
				wordReader.GetChar();

				url += wordReader.readWord();
				if(wordReader.CurChar() == '.')
				{
					url += wordReader.GetChar();
					url += wordReader.readWord();
				}
			}
			if(wordReader.CurChar() == '?')
			{
				wordReader.GetChar();
				ParseParams(wordReader);
			}

		}
		catch(IOException e)
		{
			
		}
	}
}
