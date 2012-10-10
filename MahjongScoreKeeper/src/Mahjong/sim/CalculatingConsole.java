package Mahjong.sim;
import Mahjong.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;



public class CalculatingConsole extends Console
{
	public int count;
	public static void main(String[] args) 
	{
		HashMap<Integer,GameData> g=new HashMap<Integer,GameData>();
		Vector<Integer> index=new Vector<Integer>();
		CalculatingConsole c = new CalculatingConsole();
		HashMap<String,PlayerData> p =new HashMap<String,PlayerData>();
		for(File f: new File("./").listFiles())
		{
			String s=f.getName();
			if(s.endsWith("detail.txt"))
			{
				int i = new Integer(s.substring(s.indexOf("detail.txt")-13, s.indexOf("detail.txt")).replace("-", ""));
				System.out.println(i);
				index.add(i);
				g.put(i, new GameData(s));		
			}
			if(s.endsWith(".csv")&& !(new File(s.substring(0, s.indexOf(".csv"))+"detail.txt").exists()))
			{
				p.put(s.substring(0, s.indexOf(".csv")), new PlayerData(s));
			}
		}
		//at this point, all the data should be in the appropriate places
		Collections.sort(index);
		c.dostuff(g,index,c,p);
		
		
	}
	public HashMap<Integer,GameData> game;
	public CalculatingConsole con;
	public HashMap<String,PlayerData> players;
	public Player correction;
	public void dostuff(HashMap<Integer,GameData> g,Vector<Integer> index,CalculatingConsole c,HashMap<String,PlayerData> p)
	{
		game=g;
		con=c;
		players=p;
		
		LinkedList<Moneys> tem=new LinkedList<Moneys>();
		tem.add(new Moneys("Correct",TOURNY));
		correction=new Player("Correct",25000,tem,this);
		con.everyone.put("Correct", correction);
		for(Integer i:index)
		{
			evaluateGame(g.get(i));
		}
		for(Player pi:everyone.values())
		{
			pi.writestack();
		}
		done = false;
		while (!done)
		{
			System.out.print("#$?");
			String command = in.nextLine();
			if(con.everyone.containsKey(command))
			{
			Player play=(Player)(con.everyone.get(command));
			System.out.println(play.name+": "+play.score);
			System.out.println(play.money.toString());
			
			for(String name:everyone.keySet())
			{
				
				int temp=0;
				for(Moneys m:play.money)
				{
					if(m.owner.equals(name))
						temp+=m.amount;
				}
				System.out.println(name+": "+temp);
				}
			
			}
		}
	
	}
	public void evaluateGame(GameData g)
	{
		while(g.handsleft())
		{
			System.out.print("-");
			System.out.println(g.getcurrent());
			System.out.println(g.filename);
			String[] split =g.getcurrent().split(" ");
			if(split[0].equals("game"))
			{
				
				if(con.games.containsKey(split[1]))
				{
					con.parseCommand("end "+split[1], true);
				}
				for(int i=2;i < 6;i++)
				{
					updatePlayer(players.get(split[i]),g);
				}
				con.parseCommand(g.getcurrent(), true);
				g.advance();
			}
			else if (split[0].equals("ron") || split[0].equalsIgnoreCase("doubleron") || split[0].equalsIgnoreCase("tsumo") || split[0].equalsIgnoreCase("tenpai"))
			{
				TournyGame t=(TournyGame)(con.games.get(split[1]));
				for(Player p :t.players )
				{
					updatePlayer(players.get(p.name),g);
					players.get(p.name).advance();
				}
				con.parseCommand(g.getcurrent(), true);
				g.advance();
			}
			else if(split[0].equalsIgnoreCase("riichi"))
			{
				updatePlayer(players.get(split[2]),g);
				con.parseCommand(g.getcurrent(), true);
				g.advance();
			}
			else if (split[0].equalsIgnoreCase("switch"))
			{
				if(!con.everyone.containsKey(split[3]))
				{
					con.parseCommand("player "+split[3], true);
				}
				con.parseCommand(g.getcurrent(), true);
				g.advance();
			}
			else if(split[0].equalsIgnoreCase("dealer") || split[0].equalsIgnoreCase("bonus") || split[0].equalsIgnoreCase("round"))
			{
				con.parseCommand(g.getcurrent(), true);
				g.advance();
			}
			else
			{
				System.out.println("unhandled command: "+g.getcurrent());
				while(g.handsleft()){
					g.advance();}
			}
			
		}
	}
	public void updatePlayer(PlayerData p,GameData g)
	{
		System.out.println(p.name);
		while(!p.getGame().equalsIgnoreCase(g.filename.substring(0, g.filename.indexOf("detail.txt"))+".csv") )
		{
			Player play =(Player)(con.everyone.get(p.name.substring(0, p.name.indexOf(".csv"))));
			if(play ==null)
			{
				con.parseCommand("player "+p.name.substring(0, p.name.indexOf(".csv")), true);
			}
			else if(play.score < 0 && p.InTourny())
			{
				if(!play.in)
					con.parseCommand("tadd "+play.name, true);
				p.advance();
			}
			else if(p.getGame().equalsIgnoreCase(PlayerData.CORRECTION))
			{
				if(play.score > p.tScore())
				{
					int value=play.score-p.tScore();
					correction.give(play.take(value,correction.name, ""),"");
					//play.take(value,play.name);
					
				}
				else if(play.score < p.tScore())
				{
					int value=p.tScore()-play.score;
					/*
					LinkedList<Moneys> temp=new LinkedList<Moneys>();
					temp.add(new Moneys(play.name,value));
					play.give(temp);*/
					play.give(correction.take(value, play.name,""), "");
				}
				p.advance();
			}
			else
			{
				p.swap(g.filename.substring(0, g.filename.indexOf("detail.txt"))+".csv");
			}
		}
	}
	
	
	public CalculatingConsole()
	{
		everyone = new HashMap();
		games = new HashMap();
		count=0;
		grabdata();
		//norder = new Vector<Player>();
	}
	
	public void grabdata()
	{
		if(!everyone.containsKey(POT))
		{
			newplayer(new String[]{"",POT});
			
		}
		pot=everyone.get(POT);
	}//overwriten in case java calls the super constructor
	public void newplayer(String[] split)
	{
		for (int i = 1; i < split.length; i++)
		{
			String name = split[i];
			Player p = new Player(name,25000,null,this);
			everyone.put(name, p);
		}
		
	}
	
	//may have to make versions of this for other game types, making them use count
	public void newgame(String[] split, String command)//changed so that data isn't recorded
	{
		if (split.length == 6)
		{
			String name = split[1];
			String east = split[2];
			String south = split[3];
			String west = split[4];
			String north = split[5];
			Player e = (Player) everyone.get(east);
			Player s = (Player) everyone.get(south);
			Player w = (Player) everyone.get(west);
			Player n = (Player) everyone.get(north);
			if (e != null && s != null && w != null && n != null)
			{
				SimpleDateFormat f = new SimpleDateFormat("yy-MM-dd-HHmm");

				String filename = name +count+ ".csv";
				if (new File(filename).exists())
				{
					PrintMessage(filename + " already exists");
					return;
				}
				File file = new File(filename);
				try
				{
					file.createNewFile();
				} catch (IOException ee)
				{
					PrintMessage("failed to create " + filename);
					return;
				}

				File detail = new File(filename.substring(0, filename.length() - 4) + "detail.txt");
				try
				{
					file.createNewFile();
				} catch (IOException ee)
				{
					PrintMessage("failed to create " + filename.split(".")[0] + "detail.txt");
				}
				try
				{
					BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
					out.write("hand,East,South,West,North,Bonus,Riichi");
					out.close();
				} catch (IOException eee)
				{
					PrintMessage("failed to write to " + filename);
					return;
				}
				if (!games.containsKey(name))
				{
					TournyGame m = new TournyGame(e, s, w, n, this, filename, false);
					games.put(name, m);
					m.updatedetail(command);
				}
				else
				{
					PrintMessage(name + " is a currently running game");
					return;
				}
			}
			else
			{
				if (e == null)
					PrintMessage(split[2] + "does not exist");
				if (s == null)
					PrintMessage(split[3] + "does not exist");
				if (w == null)
					PrintMessage(split[4] + "does not exist");
				if (n == null)
					PrintMessage(split[5] + "does not exist");
				return;
			}
		}
		else
		{
			PrintMessage("Syntax:!game [name] [eastplayer] [southplayer] [westplayer] [northplayer]");
		}
		count++;
	}

	public void tournyadd(String[] split)//modified to not write to player files
	{
		for (int i = 1; i < split.length; i++)
		{
			Player p = (Player) everyone.get(split[i]);
			if (p != null)
			{
				if (!p.in)
				{
					LinkedList<Moneys> money=new LinkedList<Moneys>();
					money.add(new Moneys(p.name,TOURNY));
					p.in=true;
					p.give(money,"");
					money=new LinkedList<Moneys>();
					money.add(new Moneys(p.name,TOPOT));
					pot.give(money,"");
					PrintMessage("added " + p.name + " to tourny");
				}
				else
				{
					PrintMessage(p.name + " is already in the tourny");
				}
			}
			else
			{
				PrintMessage(split[i] + " does not exist");

			}
		}
	}
}
