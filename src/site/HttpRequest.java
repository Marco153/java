package site;
import java.io.*;
import java.util.Map;
import java.util.HashMap;

public class HttpRequest {
	private String reqLine;
	public String method;
	public String url;
	public String body;
	public Map<String, String> params;
	public Map<String, String> headers;
	
	public static Map<String, String> ParseParams(WordReader reader, char delimiter)
	{
		Map<String, String> ret = new HashMap<String, String>();
		reader.EatSpace();
		boolean firstTime = true;
		while(true)
		{
			String paramName = reader.readWord();
			reader.EatSpace();
			//System.out.println("got param "+ paramName);

			if(reader.isDone() || reader.CurChar() != '=')
			{
				System.out.println("param ret is "+ reader.CurChar());
				//System.out.println("param reqLine "+ reqLine);
				return ret;
			}
			
			reader.GetChar();

			String paramValue = reader.readWord();

			//System.out.println("got param val "+ paramValue);
			reader.EatSpace();
			ret.put(paramName, paramValue);

			firstTime = false;
			//System.out.println("param before loop end char"+ reader.CurChar());

			if(!reader.isDone() && reader.CurChar() == delimiter)
			{
				//System.out.println("it was & "+ reader.CurChar());
				reader.GetChar();
				//System.out.println("it was & after "+ reader.CurChar());
			}
			else
				break;
			//System.out.println("didnt break ");

		}
	
		return ret;
	}
	public HttpRequest(String req)
	{
		params = new HashMap<String, String>();
		headers = new HashMap<String, String>();

		BufferedReader bufReader = new BufferedReader(new StringReader(req));

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

				if(!Character.isLetter(wordReader.CurChar()))
					break;

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
				params = ParseParams(wordReader, '&');
			}
			
			reqLine = bufReader.readLine();
			while(reqLine != null && !reqLine.isEmpty())
			{
				wordReader = new WordReader(reqLine);
				

				String headerName = wordReader.readWordHader();
				// eating tho double colon
				wordReader.curIdx++;

				wordReader.EatSpace();
				String headerVal = wordReader.getRemaining();
				headers.put(headerName, headerVal);
				reqLine = bufReader.readLine();
			}

			reqLine = bufReader.readLine();
			body = "";
			while(reqLine != null && !reqLine.isEmpty())
			{
				body += reqLine;
				reqLine = bufReader.readLine();
			}
		}
		catch(IOException e)
		{
			
		}
	}
}
