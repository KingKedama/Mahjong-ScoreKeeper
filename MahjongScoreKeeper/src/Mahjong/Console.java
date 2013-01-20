package Mahjong;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

public class Console 
{
	public Scanner in;
	public HashMap<String, Player> everyone;
	public HashMap<String, TournyGame> games;
	public Vector<Player> norder;
	public Player pot;
	public static String POT = "Void";
	public boolean done;
	public final static int POTBUFFER = 170000;// because it's own 30000 will be
	// added as well when it is
	// created
	public final static int TOURNY = 25000;
	public final static int TOPOT = 0;//no point currently
	
	public final static String RESULT ="results.txt"; 
	public final static int TOTAL = 30;
	public final static int FIRST = 6;
	public final static int SECOND = 4;
	public final static int THIRD = 2;
	
	public static void main(String[] args)
	{
		Console c = new Console();
		c.mainLoop();

	}//blah

	public Console()
	{
		everyone = new HashMap<String, Player>();
		games = new HashMap<String, TournyGame>();
		norder = new Vector<Player>();
		if (!new File("masterlist.txt").exists())
		{
			File file = new File("masterlist.txt");
			try
			{
				file.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if (!new File(RESULT).exists())
		{
			File file = new File(RESULT);
			try
			{
				file.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		grabdata();
		in = new Scanner(System.in);
	}

	public void mainLoop()
	{
		done = false;
		while (!done)
		{
			System.out.print("#$?");
			String command = in.nextLine();
			parseCommand(command, true);
		}
	}

	public void parseCommand(String command, boolean allowed)
	{
		String[] split = command.split(" ");
		if (split[0].equalsIgnoreCase("player"))
			newplayer(split);
		else if (split[0].equalsIgnoreCase("game"))
			newgame(split, command);
		else if (split[0].equalsIgnoreCase("tadd"))
			tournyadd(split);
		else if (split[0].equalsIgnoreCase("tsumo"))
			tsumo(split, command);
		else if (split[0].equalsIgnoreCase("ron"))
			ron(split, command);
		else if (split[0].equalsIgnoreCase("doubleron"))
			doubleron(split, command);
		else if (split[0].equalsIgnoreCase("tenpai"))
			tenpai(split, command);
		else if (split[0].equalsIgnoreCase("riichi"))
			riichi(split, command);
		else if (split[0].equalsIgnoreCase("switch"))
			changeplayer(split, command);
		else if (split[0].equalsIgnoreCase("round"))
			changeround(split, command);
		else if (split[0].equalsIgnoreCase("bonus"))
			changebonus(split, command);
		else if (split[0].equalsIgnoreCase("dealer"))
			changedealer(split, command);
		else if (split[0].equalsIgnoreCase("freeplay"))
			freegame(split, command);
		else if (split[0].equalsIgnoreCase("score"))
			printscore(split);
		else if (split[0].equalsIgnoreCase("top"))
			topn(split);
		else if (split[0].equalsIgnoreCase("3game"))
			ThreePlayer(split, command);
		else if (split[0].equalsIgnoreCase("end"))
			this.endgame(split);
		else if (split[0].equalsIgnoreCase("help"))
			help(split);
		else if (split[0].equalsIgnoreCase("take"))
			this.take(split);
		else if (split[0].equalsIgnoreCase("printstack"))
			this.printstack(split);
		else if (split[0].equalsIgnoreCase("stacksum"))
			this.stacksum(split);
		else if (split[0].equalsIgnoreCase("findmoney"))
			this.findmoney(split);
		else if (split[0].equalsIgnoreCase("status")){
			printStatus(split);
		}
		else if (command.trim().equals(""))
		{
		}
		else
		{
			PrintMessage(split[0] + " is not a valid command");
		}
	}

	// loads info from files
	public void grabdata()
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader("masterlist.txt"));
			String str;
			while ((str = in.readLine()) != null)
			{
				Player p = readprofile(str);
				if (p != null)
				{
					everyone.put(str.toLowerCase(), p);
					norder.add(p);
				}
			}
			in.close();
			if (getPlayer(POT)==null)
			{
				newplayer(new String[]
				{ "", POT });
				pot = getPlayer(POT);
				tournyadd(new String[]
				{ "", POT });
				give(new String[]
				{ "", POT, "" + POTBUFFER, POT });
			}
			pot = getPlayer(POT);
		} catch (IOException e)
		{
			PrintMessage("failed to read masterlist.txt");
			return;
		}
	}

	public Player readprofile(String name)
	{
		int score = 25000;
		BufferedReader in;
		if (new File(name + ".csv").exists())// get score from file
		{
			try
			{
				in = new BufferedReader(new FileReader(name + ".csv"));
				String prev = "", str;
				while ((str = in.readLine()) != null)
				{
					prev = str;
				}
				in.close();
				String[] s = prev.split(",");
				if (s.length == 0)
				{
					PrintMessage(name + ".csv contains errors");
					return null;
				}
				try
				{
					score = new Integer(s[0]);

				} catch (NumberFormatException e)
				{
					PrintMessage(name + ".csv contains errors");
					return null;
				}
			} catch (IOException e)
			{
				PrintMessage("failed to read " + name + ".csv");
				return null;
			}
		}
		else
		// create .csv: this is a new player
		{
			File file = new File(name + ".csv");
			try
			{
				file.createNewFile();
			} catch (IOException e)
			{
				PrintMessage("failed to create " + name + ".csv");
				return null;
			}
			try
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(name + ".csv", true));
				out.write("regular,round,game\r\n" + score);
				out.close();
				return new Player(name, score, null, this);
			} catch (IOException e)
			{
				PrintMessage("failed to write to " + name + ".csv");
				return null;
			}
		}
		// at this point score should be either 25000 for a new player or
		// whatever was writen to their .csv last
		if (!new File(name + ".stack").exists())
		{
			return new Player(name, score, null, this);
		}
		try
		{
			in = new BufferedReader(new FileReader(name + ".stack"));
			String str;
			LinkedList<Moneys> m = new LinkedList<Moneys>();
			while ((str = in.readLine()) != null)
			{
				String[] split = str.split(" ");
				if (split.length == 2)
				{
					try
					{
						m.add(new Moneys(split[0], new Integer(split[1])));
					} catch (NumberFormatException e)
					{
						PrintMessage("failed to read " + name + ".stack: " + "file contains errors");
						return null;
					}
				}
			}
			in.close();
			return new Player(name, score, m, this);

		} catch (IOException e)
		{
			PrintMessage("failed to read " + name + ".stack");
			return null;
		}
	}

	public final String PLAYERSYNTAX = "Syntax:player [list of names]";
	public void newplayer(String[] split)
	{
		for (int i = 1; i < split.length; i++)
		{
			String name = split[i];
			if (new File(name + ".csv").exists())
			{
				PrintMessage(name + ".csv already exists");
				return;
			}
			Player p = readprofile(name);
			try
			{
				BufferedWriter out = new BufferedWriter(new FileWriter("masterlist.txt", true));
				out.write(name + "\r\n");
				out.close();
			} catch (IOException e)
			{
				PrintMessage("failed to write to masterlist.txt");
			}
			everyone.put(name.toLowerCase(), p);
			norder.add(p);// not sure this will be still necessary
			PrintMessage("created player: " + name);

		}
	}

	public final String GAMESYNTAX="Syntax:game [name] [eastplayer] [southplayer] [westplayer] [northplayer]";
	public void newgame(String[] split, String command)
	{
		if (split.length != 6)
		{
			PrintMessage(GAMESYNTAX);
			return;
		}
		String name = split[1];
		String east = split[2];
		String south = split[3];
		String west = split[4];
		String north = split[5];
		Player e = (Player) getPlayer(east);
		Player s = (Player) getPlayer(south);
		Player w = (Player) getPlayer(west);
		Player n = (Player) getPlayer(north);
		if (e == null || s == null || w == null || n == null)
		{
			if (e == null)
				PrintMessage(split[2] + " does not exist");
			if (s == null)
				PrintMessage(split[3] + " does not exist");
			if (w == null)
				PrintMessage(split[4] + " does not exist");
			if (n == null)
				PrintMessage(split[5] + " does not exist");
			return;
		}
		if(e==s || e==w || e==n 
	    || s==w || s==n
	    || w==n){
			PrintMessage("invalid game: duplicate players");
			return;
		}
		if (games.containsKey(name))
		{
			PrintMessage(name + " is a currently running game");
			return;
		}
		SimpleDateFormat f = new SimpleDateFormat("yy-MM-dd-HHmm");
		String filename = name + f.format(new Date()) + ".csv";
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

		file = new File(filename.substring(0, filename.length() - 4) + "detail.txt");
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
		TournyGame m = new TournyGame(e, s, w, n, this, filename, true);
		games.put(name, m);
		m.updatedetail(command);

	}

	public void tournyadd(String[] split)
	{
		System.out.println(split[1]);
		for (int i = 1; i < split.length; i++)
		{
			Player p = (Player) getPlayer(split[i]);
			if (p == null)
			{
				PrintMessage(split[i] + " does not exist");
				return;
			}
			if (p.in)
			{
				PrintMessage(p.name + " is already in the tourny");
				return;
			}
			p.in = true;
			LinkedList<Moneys> money = new LinkedList<Moneys>();
			p.score = 0;
			money.add(new Moneys(p.name, TOURNY));
			p.give(money,"");
			money = new LinkedList<Moneys>();
			money.add(new Moneys(p.name, TOPOT));
			pot.give(money,"");
			p.writecsv("tourny", "tourny");
			PrintMessage("added " + p.name + " to tourny");

		}
	}

	public void tsumo(String[] split, String command)
	{
		if (split.length != 5)
		{
			PrintMessage("Syntax:!tsumo [game] [winner] [hou] [han|y|dy]");
			return;
		}
		TournyGame m = (TournyGame) games.get(split[1]);
		if (m == null)
		{
			PrintMessage(split[1] + " is not a game");
			return;
		}
		String p = split[2];
		if (!m.exists(new String[]
		{ split[2] }))
		{
			return;
		}
		try
		{
			int hou = new Integer(split[3]);
			if (split[4].equalsIgnoreCase("y"))
			{
				m.tsumo(p, hou, -1);
			}
			else if (split[4].equalsIgnoreCase("dy"))
			{
				m.tsumo(p, hou, -2);
			}
			else
			{
				int han = new Integer(split[4]);
				m.tsumo(p, hou, han);
			}
		} catch (NumberFormatException e)
		{
			PrintMessage("Syntax:!tsumo [game] [winner] [hou] [han|y|dy]");
			return;
		}
		m.updatedetail(command);
	}

	public void ron(String[] split, String command)
	{

		if (split.length != 6)
		{
			PrintMessage("Syntax:!ron [game] [winner] [hou] [han|y|dy] [loser]");
			return;
		}
		TournyGame m = (TournyGame) games.get(split[1]);
		if (m == null)
		{
			PrintMessage(split[1] + " is not a valid game");
			return;
		}
		String p = split[2];
		String p2 = split[5];
		if (!m.exists(new String[]
		{ p, p2 }))
		{
			return;
		}
		try
		{
			int hou = new Integer(split[3]);
			if (split[4].equalsIgnoreCase("y"))
				m.ron(p, p2, hou, -1);
			else if (split[4].equalsIgnoreCase("dy"))
				m.ron(p, p2, hou, -2);
			else
			{
				int han = new Integer(split[4]);
				m.ron(p, p2, hou, han);
			}
		} catch (NumberFormatException e)
		{
			PrintMessage("Syntax:!ron [game] [winner] [hou] [han|y|dy] [loser]");
			return;
		}
		m.update();
		m.printnextround();
		m.updatedetail(command);

	}

	public void tenpai(String[] split, String command)
	{
		TournyGame m = (TournyGame) games.get(split[1]);
		if (m == null)
		{
			PrintMessage(split[1] + " is not a game");
			return;
		}
		String[] play = new String[split.length - 2];
		for (int i = 2, j = 0; i < split.length; i++, j++)
		{
			play[j] = split[i];
		}
		if (!m.exists(play))
		{
			return;
		}
		m.tenpai(play);
		m.updatedetail(command);

	}

	public void riichi(String[] split, String command)
	{
		if (split.length != 3)
		{
			PrintMessage("Syntax:!riichi [game] [player]");
			return;
		}
		TournyGame m = (TournyGame) games.get(split[1]);
		if (m == null)
		{
			PrintMessage(split[1] + "is not a game");
			return;
		}
		String p = split[2];

		if (!m.exists(new String[]
		{ p }))
		{
			return;
		}
		if (m.riichi(p))
		{
			m.updatedetail(command);
		}
	}

	public void changeplayer(String[] split, String command)
	{
		if (split.length != 4)
		{
			PrintMessage("Syntax:!switch [game] [oldplayer] [newplayer]");
			return;
		}
		TournyGame m = (TournyGame) games.get(split[1]);
		if (m == null)
		{
			PrintMessage(split[1] + " is not an existing game");
			return;
		}
		String p = split[2];
		if (!m.exists(new String[]
		{ p }))
		{
			return;
		}
		if (!(m instanceof NTGame))
		{
			Player p2 =getPlayer(split[3]);
			if (p2 == null)
			{
				PrintMessage(split[3] + " does not exist");
				return;
			}
			if (p2 == m.players[0] || p2 == m.players[1] || p2 == m.players[2] || p2 == m.players[3])
			{
				PrintMessage(split[3] + " is already in " + split[1]);
				return;
			}

			m.changeplayer(p, p2);
		}
		else
			m.changeplayer(p, new Player(split[3], 25000, null, null));
		m.printgamestatus();
		m.updatedetail(command);

	}

	public void doubleron(String[] split, String command)
	{
		if (split.length != 9)
		{
			System.out.println("Syntax:!doubleron [game] [winner1] [hou] [han|y|dy] [winner2] [hou] [han|y|dy] [loser]");
			return;
		}
		TournyGame m = (TournyGame) games.get(split[1]);
		if (m == null)
		{
			PrintMessage(split[1] + " is not an existing game");
			return;
		}
		String winner1 = split[2];
		String winner2 = split[5];
		String loser = split[8];
		if (!m.exists(new String[]
		{ winner1, winner2, loser }))
		{
			return;
		}
		int hou1, han1, hou2, han2;
		try
		{
			hou1 = new Integer(split[3]);
			if (split[4].equalsIgnoreCase("y"))
				han1 = -1;
			else if (split[4].equalsIgnoreCase("dy"))
				han1 = -2;
			else
			{
				han1 = new Integer(split[4]);
			}
		} catch (NumberFormatException e)
		{
			System.out.println("Syntax:!doubleron [game] [winner1] [hou] [han|y|dy] [winner2] [hou] [han|y|dy] [loser]");
			return;
		}
		try
		{
			hou2 = new Integer(split[6]);
			if (split[7].equalsIgnoreCase("y"))
				han2 = -1;
			else if (split[7].equalsIgnoreCase("dy"))
				han2 = -2;
			else
			{
				han2 = new Integer(split[7]);
			}
		} catch (NumberFormatException e)
		{
			System.out.println("Syntax:!doubleron [game] [winner1] [hou] [han|y|dy] [winner2] [hou] [han|y|dy] [loser]");
			return;
		}
		m.doubleron(winner1, winner2, loser, hou1, han1, hou2, han2);
		m.updatedetail(command);

	}

	public void changeround(String[] split, String command)
	{
		if (split.length != 3)
		{
			PrintMessage("Syntax:!round [game] [east|south|west|north|#]");
			return;
		}
		TournyGame m = (TournyGame) games.get(split[1]);
		if (m == null)
		{
			PrintMessage(split[1] + " is not an existing game");
			return;
		}
		if (split[2].equalsIgnoreCase("east"))
			m.setround(0);
		else if (split[2].equalsIgnoreCase("south"))
			m.setround(1);
		else if (split[2].equalsIgnoreCase("west"))
			m.setround(2);
		else if (split[2].equalsIgnoreCase("north"))
			m.setround(3);
		else
			try
			{

				m.setround(new Integer(split[2]) - 1);
			} catch (NumberFormatException e)
			{
				System.out.println("Syntax:!round [game] [east|south|west|north|#]");
				return;
			}
		m.printnextround();
		m.updatedetail(command);
	}

	public void changebonus(String[] split, String command)
	{
		if (split.length != 3)
		{
			PrintMessage("Syntax:!bonus [game] [#]");
			return;
		}
		TournyGame m = (TournyGame) games.get(split[1]);
		if (m == null)
		{
			PrintMessage(split[1] + " is not an existing game");
			return;
		}
		try
		{
			int b = new Integer(split[2]);
			if (b < 0)
			{
				PrintMessage("bonus cannot be negative");
				return;
			}
			m.setbonus(b);
		} catch (NumberFormatException e)
		{
			PrintMessage("Syntax:!bonus [game] [#]");
			return;
		}
		m.printnextround();
		m.updatedetail(command);
	}

	public void changedealer(String[] split, String command)
	{
		if (split.length != 3)
		{
			PrintMessage("Syntax:!dealer [game] [name]");
			return;
		}
		TournyGame m = (TournyGame) games.get(split[1]);
		if (m == null)
		{
			PrintMessage(split[1] + " is not an existing game");
			return;
		}
		if (!m.exists(new String[]
		{ split[2] }))
		{
			return;
		}
		m.setdealer(split[2]);
		m.printnextround();
		m.updatedetail(command);
	}

	public void freegame(String[] split, String command)
	{
		if (split.length != 6)
		{
			PrintMessage("Syntax:!freeplay [name] [eastplayer] [southplayer] [westplayer] [northplayer]");
			return;
		}
		String name = split[1];
		String east = split[2];
		String south = split[3];
		String west = split[4];
		String north = split[5];
		Player e = new Player(east, 25000, null, this);
		Player s = new Player(south, 25000, null, this);
		Player w = new Player(west, 25000, null, this);
		Player n = new Player(north, 25000, null, this);
		if (games.containsKey(name))
		{
			PrintMessage(name + " is a currently running game");
			return;
		}
		SimpleDateFormat f = new SimpleDateFormat("yy-MM-dd-HHmm");
		String filename = name + f.format(new Date()) + ".csv";
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

		file = new File(filename.substring(0, filename.length() - 4) + "detail.txt");
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

		TournyGame m = new NTGame(e, s, w, n, this, filename, false);
		games.put(name, m);
		m.updatedetail(command);

	}

	public void endgame(String[] split)
	{
		if (split.length != 2)
		{
			PrintMessage("Syntax:!end [game]");
			return;
		}
		if (!games.containsKey(split[1]))
		{
			PrintMessage(split[1] + " is not a current game");
			return;
		}

		PrintMessage("ending game " + split[1]);
		TournyGame game=games.get(split[1]);
		if (game.riichi > 0){
			pot.give(game.triichi,"");
		}
		if(!game.switched && game.over()){
			
			Player[] players=game.players.clone();
			for(int i=0;i < 4;i++){
				for(int j=i+1; j< 4;j++ ){
					if(game.gamescore(players[j])>game.gamescore(players[i])){
						Player tmp=players[i];
						players[i]=players[j];
						players[j]=tmp;
					}
				}
			}
			try
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(RESULT, true));
				
				out.write(game.namedate);
				int place=0;
				for(int i=0;i < players.length;i++){
					if(i == 0 || game.gamescore(players[i]) != game.gamescore(players[i-1])){
						place=i+1;
					}
					out.write(" "+place);
					out.write(" "+players[i].name);
					if(place==1){
						players[i].first++;
					}
					else if(place==2){
						players[i].second++;
					}
					else if(place==3){
						players[i].third++;
					}
					else if(place==4){
						players[i].forth++;
					}
					else{
						PrintMessage("recording game placements failed");
					}
				}
				out.write("\r\n");
				out.close();
			} catch (IOException eee)
			{
				PrintMessage("failed to write to " + RESULT);
				return;
			}
		}
		
		games.remove(split[1]);
		
	}

	public void help(String[] split)
	{
		if (split.length == 1)
		{
			PrintMessage("Commands:player,game,tadd," + "tsumo,ron,doubleron,tenpai,riichi," + "switch,round,bonus,dealer,freeplay," + "end,help,3game" + ",score,top,printstack,stacksum,findmoney," + "take");

			PrintMessage("type help [command] for details");
		}
		else if (split.length == 2)
		{
			if (split[1].equals("player"))
			{
				PrintMessage(PLAYERSYNTAX);
				PrintMessage("creates a new player and file for each name in [list of names]. " + "names must contain only characters valid in file names");
			}
			else if (split[1].equals("game"))
			{
				PrintMessage(GAMESYNTAX);
				PrintMessage("creates a game. all players must exist");
			}
			else if (split[1].equals("tadd"))
			{
				PrintMessage("Syntax:!tadd [list of players]");
				PrintMessage("adds each player in [list of players] to the tournament, giving them 25000 starting points and adding 5000 to the pot.");
			}
			else if (split[1].equals("tsumo"))
				PrintMessage("Syntax:!tsumo [game] [winner] [hou] [han|y|dy]");
			else if (split[1].equals("ron"))
				PrintMessage("Syntax:!ron [game] [winner] [hou] [han|y|dy] [loser]");
			else if (split[1].equals("doubleron"))
				PrintMessage("Syntax:!doubleron [game] [winner1] [hou] [han|y|dy] [winner2] [hou] [han|y|dy] [loser]");
			else if (split[1].equals("tenpai"))
			{
				PrintMessage("Syntax:!tenpai [game] [list of names]");
				PrintMessage("in the case where 2 people are in tenpai and exactly 3 of the players in [game] are in the tourny, players normal scores are calculated as usual but tournament scores are calculated based on a payout of 2000 from losers to winners ignoring the non-tournament player.");
			}
			else if (split[1].equals("riichi"))
			{
				PrintMessage("Syntax:!riichi [game] [player]");
			}
			else if (split[1].equals("switch"))
			{
				PrintMessage("Syntax:!switch [game] [oldplayer] [newplayer]");
				PrintMessage("takes [oldplayer] out of [game] and puts [newplayer] in at [oldplayer's position." + "  the position of dealer, bonus rounds, and riichies on the table does not change.");
			}
			else if (split[1].equals("round"))
			{
				PrintMessage("Syntax:!round [game] [east|south|west|north|#]");
				PrintMessage("manually changes the round of [game]." + "1-4 =east-north, 5 and higher are ???(#-4) (so 5 is ???1");
			}
			else if (split[1].equals("bonus"))
			{
				PrintMessage("Syntax:!bonus [game] [#]\n" + "sets the number of bonus rounds to [#]");
			}
			else if (split[1].equals("dealer"))
			{
				PrintMessage("Syntax:!dealer [game] [name]");
				PrintMessage("sets the dealer to the player [name]," + " but does not change number of bonus rounds");
			}
			else if (split[1].equals("freeplay"))
			{
				PrintMessage("Syntax:!freeplay [name] [eastplayer] [southplayer] [westplayer] [northplayer]");
				PrintMessage("creates a freeplay game that does not count toward overall tournament or regular scores. " + "each player starts with 25000, and need not exist in the masterlist. " + "game summery and detail files are still created");
			}
			else if (split[1].equals("end"))
			{
				PrintMessage("Syntax:!end [game]\n");
				PrintMessage("frees up a game's name, so it can be used again");
			}
			else if (split[1].equals("help"))
			{
				PrintMessage("Syntax:!help | help [command]");
			}
			else if (split[1].equals("3game"))
			{
				PrintMessage("Syntax:!3game [name] [eastplayer] [southplayer] [westplayer]");
				PrintMessage("creates a 3 player game. player's overall scores are not affected");
			}
			else if(split[1].equals("score")){
				
			}
			else if(split[1].equals("printstack")){
				
			}
			else if(split[1].equals("stacksum")){
				
			}
			else if(split[1].equals("findmoney")){
				
			}
			else if(split[1].equals("take")){
				
			}
			else if(split[1].equals("top")){
				
			}
			else
			{
				PrintMessage(split[1] + " is not a command");
			}
		}
	}

	public void ThreePlayer(String[] split, String command)
	{
		if (split.length != 5)
		{
			PrintMessage("Syntax:!freeplay [name] [eastplayer] [southplayer] [westplayer] [northplayer]");
			return;
		}
		String name = split[1];
		String east = split[2];
		String south = split[3];
		String west = split[4];
		Player e = new Player(east, 35000, null, this);
		Player s = new Player(south, 35000, null, this);
		Player w = new Player(west, 35000, null, this);
		if (games.containsKey(name))
		{
			PrintMessage(name + " is a currently running game");
			return;
		}
		SimpleDateFormat f = new SimpleDateFormat("yy-MM-dd-HHmm");
		String filename = name + f.format(new Date()) + ".csv";
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

		file = new File(filename.substring(0, filename.length() - 4) + "detail.txt");
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
			out.write("hand,East,South,West,Bonus,Riichi");
			out.close();
		} catch (IOException eee)
		{
			PrintMessage("failed to write to " + filename);
			return;
		}

		TournyGame m = new ThreeGame(e, s, w, this, filename);
		games.put(name, m);
		m.updatedetail(command);

	}

	public void PrintMessage(String mess)
	{
		System.out.println(mess);
	}

	public void printscore(String[] split)
	{
		if (split.length != 2)
		{
			PrintMessage("Syntax:!score [player]");
			return;
		}
		Player p = (Player) getPlayer(split[1]);
		if (p == null)
		{
			PrintMessage(split[1] + " does not exist");
			return;
		}
		if (p.in){
			PrintMessage(p.name + " is a tournament player");
			topn(new String[]{"","0"});
			PrintMessage("score:"+p.score+" 1st:"+p.first+" 2nd:"+p.second+" 3rd:"+p.third+" 4th:"+p.forth+" RV:"+p.winnnings+"GPA: "+p.GPA());
		}
		else
			PrintMessage("score: " + p.score);

	}
	public Player getPlayer(String name){
		return everyone.get(name.toLowerCase());
	}
	public final int LINESIZE = 10;

	public void printstack(String[] split)
	{
		if (split.length != 2)
		{
			PrintMessage("Syntax:!score [player]");
			return;
		}
		Player p = (Player) getPlayer(split[1]);
		if (p == null)
		{
			PrintMessage(split[1] + " does not exist");
			return;
		}
		int cur = 0;
		String s = "";
		for (Moneys m : p.money)
		{
			if (cur >= LINESIZE)
			{
				PrintMessage(s);
				cur = 0;
				s = "";
			}
			s += " [" + m.toString() + "] ";
			cur++;
		}
		if (!s.equals(""))
			PrintMessage(s);

	}

	public void stacksum(String[] split)// prints the total from each person of
	// a person's stack
	{
		if (split.length != 2)
		{
			PrintMessage("Syntax:!score [player]");
			return;
		}
		Player p = (Player) getPlayer(split[1]);
		if (p == null)
		{
			PrintMessage(split[1] + " does not exist");
			return;
		}
		for (String name : everyone.keySet())
		{
			int temp = 0;
			for (Moneys m : p.money)
			{
				if (m.owner.equals(name))
					temp += m.amount;
			}
			if (temp > 0)
				PrintMessage(name + ": " + temp);
		}
	}

	public void findmoney(String[] split)
	{
		if (split.length != 2)
		{
			PrintMessage("Syntax:!findmoney [player]");
			return;
		}
		String name = split[1];
		int total = 0;
		for (Player p : everyone.values())
		{
			int temp = 0;
			for (Moneys m : p.money)
			{
				if (m.owner.equalsIgnoreCase(name))
					temp += m.amount;
			}
			if (temp != 0)
				PrintMessage(p.name + ": " + temp);
			total += temp;
		}
		PrintMessage("total: " + total);
	}

	public void printStatus(String[] split){
		if (split.length != 2){
			//TODO print syntax
			return;
		}
		TournyGame g= games.get(split[1]);
		if(g== null){
			PrintMessage(split[1]+" is not a current game");
			return;
		}
		g.printStatus();
	}
	// take [receiver] [amount] [giver]
	public final String TAKESYNTAX = "Syntax:take [game] [receiver] [amount] [giver]";
	public void take(String[] split)
	{
		if (split.length != 5)
		{
			PrintMessage(TAKESYNTAX);
			return;
		}
		Player receiver, giver;
		
			receiver = getPlayer(split[2]);
		
		if(receiver == null)
		{
			PrintMessage(split[2] + " does not exist");
			return;
		}
		
			giver = getPlayer(split[4]);
		
		if(giver == null)
		{
			PrintMessage(split[4] + " does not exist");
			return;
		}
		String game;
		if(games.containsKey(split[1])){
			game=games.get(split[1]).namedate;
		}
		else{
			PrintMessage(split[1]+ " is not a currently running game");
			return;
		}
		try
		{
			int amount = new Integer(split[3]);
			if (amount < 0)
			{
				PrintMessage("amounts must be positive");
				return;
			}
			receiver.give(giver.take(amount, receiver.name,game),game);
			receiver.writecsv("take", amount + " from " + giver.name);
			giver.writecsv("gave", amount + " to " + receiver.name);
			PrintMessage(receiver.name+"got "+amount+" from "+giver.name);
		} catch (NumberFormatException e)
		{
			PrintMessage(split[2] + " is not a number");
			return;
		}

	}

	// give [receiver] [amount] [owner]
	public final String GIVESYNTAX = "Syntax:give [receiver] [amount] [owner]";

	public void give(String[] split)
	{
		if (split.length != 4)
		{
			PrintMessage(GIVESYNTAX);
			return;
		}
		Player receiver;
		receiver = getPlayer(split[1]);
		
		if(receiver== null)
		{
			PrintMessage(split[1] + " does not exist");
			return;
		}
		try
		{
			int amount = new Integer(split[2]);
			if (amount < 0)
			{
				PrintMessage("amounts must be positive");
				return;
			}
			LinkedList<Moneys> temp = new LinkedList<Moneys>();
			temp.add(new Moneys(split[3], amount));
			receiver.writecsv("give", temp.toString());
			receiver.give(temp,"");
		} catch (NumberFormatException e)
		{
			PrintMessage(split[2] + " is not a number");
			return;
		}
	}

	// destroy [giver] [amount] [destroyer]
	public final String DESTROYSYNTAX = "Syntax:destroy [giver] [amount] [destroyer]";

	public void destroy(String[] split)
	{
		if (split.length != 4)
		{
			PrintMessage(DESTROYSYNTAX);
			return;
		}
		Player giver;
		
			giver = getPlayer(split[1]);
		
		if (giver==null)
		{
			PrintMessage(split[1] + " does not exist");
			return;
		}
		try
		{
			int amount = new Integer(split[2]);
			if (amount < 0)
			{
				PrintMessage("amounts must be positive");
				return;
			}
			LinkedList<Moneys> temp = giver.take(amount, split[3],"");
			giver.writecsv("destroy", amount + ":" + temp.toString());
			PrintMessage(temp.toString() + " was destroyed");
		} catch (NumberFormatException e)
		{
			PrintMessage(split[2] + " is not a number");
			return;
		}
	}

	public void topn(String[] split)
	{

		int n = 0;
		try
		{
			n = new Integer(split[1]);
		} catch (NumberFormatException e)
		{
			return;
		}
		if (n < 0)
			return;
		List<Player> intourny=getTournyPlayers();
		//calculate total money
		double total=0;//double so casting later isn't needed
		for(Player p:intourny){
			if(p.score>0 && p!=pot){
				total+=p.score;
			}
		}
		
		int tmoney=(TOTAL-FIRST-SECOND-THIRD)*intourny.size();
		Player[] top = new Player[n];
		for (Player p : intourny)
		{
			
			p.winnnings= (int) ((p.score / total) *tmoney);
			if (p != pot)
			{
				for (int i = 0; i < n; i++)
				{
					if (top[i] == null)
					{
						top[i] = p;
						break;
					}
					if (top[i].winnnings < p.winnnings)
					{
						Player temp = top[i];
						top[i] = p;
						p = temp;
					}
				}
			}
		}
		for (int i = 0; i < n; i++)
		{
			if (top[i] == null){
				break;
			}
			PrintMessage(String.format("%d. %s:score: %d GPA: %1.3f RV: %d", i+1,top[i].name,top[i].score,top[i].GPA(),top[i].winnnings));
		}

	}

	private List<Player> getTournyPlayers()
	{
		List<Player> players=new ArrayList<Player>();
		for(Player p : (Collection<Player>) everyone.values()){
			if(p.in && p!=pot){
				players.add(p);
			}
		}
		return players;
	}
}
