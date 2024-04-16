package site;

public class WordReader {
	public String str;
	public int curIdx;
	
	public String readWord()
	{
		int i = curIdx;

		EatSpace();
		if(!Character.isLetter(str.charAt(i)))
		{
			return String.valueOf(GetChar());
		}

		int wordStart = i;

		while(i < str.length() && (Character.isAlphabetic(str.charAt(i)) || Character.isDigit(str.charAt(i))))
		{
			i++;
		}
		
		int wordEnd = i;
		
		curIdx = i;

		
		return str.substring(wordStart, wordEnd);
	}
	public char GetChar()
	{
		char ret = str.charAt(curIdx);
		curIdx++;
		return ret;
	}
	public void EatSpace()
	{
		while(curIdx < str.length() && (str.charAt(curIdx) == ' '))
		{
			curIdx++;
		}
	}
	public char CurChar()
	{
		return str.charAt(curIdx);
	}
	public char CharAt(int i)
	{
		return str.charAt(i);
	}
	public WordReader(String inStr)
	{
		str = inStr;
	}
}
