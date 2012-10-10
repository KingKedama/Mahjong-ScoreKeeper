package Mahjong.sim;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
public class PlayerData
{
	private LinkedList<String> games;
	private LinkedList<String> tournyscore;
	public static final String CORRECTION="CORRECTION";
	private final String OUT="-100";
	public String name;
	public PlayerData(String n)
	{
		name=n;
		games=new LinkedList<String>();
		tournyscore=new LinkedList<String>();
		if (new File(n).exists())
		{
			try
			{
				BufferedReader in = new BufferedReader(new FileReader(n));
				String str;
				in.readLine();
				while ((str = in.readLine()) != null)
				{
					String[] stuff=str.split(",");
					if(stuff.length > 1)
					{
						tournyscore.add(stuff[1]);
						if(stuff.length > 3)
						{
							games.add(stuff[3]);
						}
						else
						{
							games.add(CORRECTION);
						}
					}
				}
				in.close();
			
			} catch (IOException e)
			{
				System.out.println("ERROR: failed to read " + n);
			}
		}
		else
		{
			System.out.println("ERROR: "+n+" does not exist");
		}
	
	}
	public boolean InTourny()
	{
		return !tournyscore.getFirst().equals(OUT);
	}
	public int tScore()
	{
		return new Integer(tournyscore.getFirst()).intValue();
	}
	public String getGame()
	{
		return games.getFirst();
	}
	public void advance()
	{
		tournyscore.removeFirst();
		games.removeFirst();
	}
	public boolean hasNext()
	{
		return !games.isEmpty();
	}
	public void swap(String s)
	{
		
		int i=1;
		for(String g : games)
		{
			if(g.equals(s))
			{
				i=games.indexOf(g);
				break;
			}
		}
		games.addFirst(games.remove(i));
		tournyscore.addFirst(tournyscore.remove(i));
	}
}
