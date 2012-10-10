package Mahjong.sim;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GameData
{
	public String filename; 
	private LinkedList<String> data;//east south west north changeeast changesouth changewest changenorth detail
	public GameData(String g)
	{
		filename=g;
		data = new LinkedList<String>();
		//TODO read in data from file
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(g));
			String str;
			while ((str = in.readLine()) != null)
			{
				data.add(str);
			}
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean handsleft()
	{
		return !data.isEmpty();
	}
	
	public String getcurrent()
	{
		return data.getFirst();
	}
	
	public void advance()
	{
		data.removeFirst();
	}
	
}
