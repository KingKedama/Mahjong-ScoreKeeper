package Mahjong;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TournyGame
{
	public int riichi;
	public LinkedList<Moneys> triichi;
	public Player[] players; // east,south,west,north
	public static int EAST = 0;
	public static int SOUTH = 1;
	public static int WEST = 2;
	public static int NORTH = 3;
	public int[] prev;
	public int[] start;
	public String pround;
	public int round, dealer, bonus;
	public boolean record;
	public String namedate;
	public Console main;
	public ArrayList<Player> riichis;
	public List<String> commands;
	
	public boolean switched;

	public TournyGame(Player east, Player south, Player west, Player north, Console c, String n, boolean r)
	{
		players = new Player[4];
		namedate = n;
		players[EAST] = east;
		players[SOUTH] = south;
		players[WEST] = west;
		riichis = new ArrayList<Player>();
		commands=new ArrayList<String>();
		riichi=0;
		players[NORTH] = north;
		dealer = round = EAST;
		record = r;
		bonus = 0;
		triichi = new LinkedList<Moneys>();
		main = c;
		pround = "East 0";
		prev = new int[4];
		
		switched =false;
		try
		{
			prev[0] = players[0].score;
			prev[1] = players[1].score;
			prev[2] = players[2].score;
			prev[3] = players[3].score;
			start = prev.clone();
			printgamestatus();
			updategame();
			printnextround();
		} catch (Exception e)
		{
		}

	}

	public void updategame()
	{
		int e, s, w, n;
		e = players[EAST].score - start[EAST] + 25000;
		s = players[SOUTH].score - start[SOUTH] + 25000;
		w = players[WEST].score - start[WEST] + 25000;
		n = players[NORTH].score - start[NORTH] + 25000;
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(namedate, true));
			out.write("\r\n" + pround + "," + players[EAST].name + "," + players[SOUTH].name + "," + players[WEST].name + "," + players[NORTH].name);
			out.write("\r\n" + "change" + "," + (players[EAST].score - prev[EAST]) + "," + (players[SOUTH].score - prev[SOUTH]) + "," + (players[WEST].score - prev[WEST]) + "," + (players[NORTH].score - prev[NORTH]));
			out.write("\r\n" + "result" + "," + e + "," + s + "," + w + "," + n + "," + bonus + "," + riichi);
			out.close();
		} catch (IOException eee)
		{
			main.PrintMessage("failed to write to " + namedate);
			return;
		}
	}

	public void updatedetail(String command)
	{
		String filename = namedate.substring(0, namedate.length() - 4) + "detail.txt";
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
			out.write(command + "\r\n");
			out.close();
		} catch (IOException eee)
		{
			main.PrintMessage("failed to write to " + filename);
			return;
		}
	}

	public void update()
	{
		printgamestatus();
		if (record)
			for (int i = 0; i < 4; i++)
			{
				players[i].writecsv(pround, namedate);
			}
		updategame();
	}

	//has no 'change' or to 
	public void printStatus(){
		Formatter fmt = new Formatter();
		int e, s, w, n;
		e= gamescore(players[EAST]);
		s =gamescore(players[SOUTH]);
		w =gamescore(players[WEST]);
		n= gamescore(players[NORTH]);
		fmt.format("%12s %12s %12s %12s %12s", pround, players[0].name, players[1].name, players[2].name, players[3].name);
		main.PrintMessage(fmt.toString());
		fmt = new Formatter();
		fmt.format("%12s %12s %12s %12s %12s", "score", e, s, w, n);
		main.PrintMessage(fmt.toString());
		fmt = new Formatter();
		fmt.format("%12s %12s", "riichi " + riichi, "bonus " + bonus);
		main.PrintMessage(fmt.toString());
		for(Player p: players)
		{
			if(gamescore(p) < 1000)
			{
				main.PrintMessage(p.name+" cannot riichi");
			}
		}
	}
	public void printgamestatus()
	{
		Formatter fmt = new Formatter();
		int e, s, w, n;
		e= gamescore(players[EAST]);
		s =gamescore(players[SOUTH]);
		w =gamescore(players[WEST]);
		n= gamescore(players[NORTH]);

		fmt.format("%12s %12s %12s %12s %12s", pround, players[0].name, players[1].name, players[2].name, players[3].name);
		main.PrintMessage(fmt.toString());
		fmt = new Formatter();
		fmt.format("%12s %12s %12s %12s %12s", "change", players[0].score - prev[0], players[1].score - prev[1], players[2].score - prev[2], players[3].score - prev[3]);
		main.PrintMessage(fmt.toString());
		fmt = new Formatter();
		fmt.format("%12s %12s %12s %12s %12s", "game", e, s, w, n);
		main.PrintMessage(fmt.toString());
		if (record)
		{
			fmt = new Formatter();
			fmt.format("%12s %12s %12s %12s %12s", "score", players[0].score, players[1].score, players[2].score, players[3].score);
			main.PrintMessage(fmt.toString());
		}
		for(Player p: players)
		{
			if(gamescore(p) < 1000)
			{
				main.PrintMessage(p.name+" cannot riichi");
			}
		}
	}
	
	public int gamescore(Player p)
	{
		return p.score -start[position(p.name)]+25000;
	}

	public void updatepround()
	{
		if (round == 0)
			pround = "East " + (dealer + 1);
		else if (round == 1)
			pround = "South " + (dealer + 1);
		else if (round == 2)
			pround = "West " + (dealer + 1);
		else if (round == 3)
			pround = "North " + (dealer + 1);
		else
			pround = "???" + (round - 3) + " " + (dealer + 1);
		for (int i = 0; i < players.length; i++)
			prev[i] = players[i].score;
	}

	public void printnextround()
	{
		updatepround();
		Formatter fmt = new Formatter();
		fmt.format("%12s %12s %12s", pround, "riichi " + riichi, "bonus " + bonus);
		main.PrintMessage(fmt.toString());
	}

	public void tsumo(String win, int hou, int han)
	{
		Player winner = players[position(win)];
		finalizeriichi(winner);
		int value = handvalue(hou, han);
		if (players[dealer] == winner)
			value = 2 * value;
		for (int i = 0; i < players.length; i++)
		{
			if (i == dealer)
				take(winner, players[i], 2 * value + 100 * bonus,false);
			else
				take(winner, players[i], value + 100 * bonus,false);
		}
		if (players[dealer] == winner)
			bonus++;
		else
		{
			bonus = 0;
			nexthand();

		}
		update();
		printnextround();
	}

	public void ron(String win, String lose, int hou, int han)
	{
		Player winner = players[position(win)];
		Player loser = players[position(lose)];
		finalizeriichi(winner);
		int value = handvalue(hou, han);
		if (winner == players[dealer])
		{
			take(winner, loser, 6 * value + 300 * bonus,true);
			bonus++;
		}
		else
		{
			take(winner, loser, 4 * value + 300 * bonus,true);
			bonus = 0;
			nexthand();
		}

	}

	public void doubleron(String winner1, String winner2, String loser, int hou1, int han1, int hou2, int han2)
	{
		int tempr, tempd, tempb;// round,dealer,bonus
		tempr = round;
		tempd = dealer;
		tempb = bonus;
		int start;
		start = (position(loser) + 1) % 4;

		for (int i = 1; i < 4; i++, start = (start + 1) % 4)
		{
			if (players[start].name.equalsIgnoreCase(winner1))
			{
				this.ron(winner1, loser, hou1, han1);
				round = tempr;
				dealer = tempd;
				bonus = 0;
			}
			else if (players[start].name.equalsIgnoreCase(winner2))
			{
				this.ron(winner2, loser, hou2, han2);
				round = tempr;
				dealer = tempd;
				bonus = 0;
			}
		}
		bonus = tempb;
		if (players[dealer].name.equalsIgnoreCase(winner1) || players[dealer].name.equalsIgnoreCase(winner2))
			bonus++;
		else
		{
			bonus = 0;
			nexthand();
		}
		update();
		printnextround();
	}

	public void tenpai(String[] ten)
	{
		finalizeriichi(null);
		Player[] tenpai = new Player[ten.length];
		for (int i = 0; i < ten.length; i++)
			tenpai[i] = players[position(ten[i])];
		boolean move = true;
		if (tenpai.length == 1)
		{
			if (tenpai[0] == players[dealer])
				move = false;
			take(tenpai[0], players[0], 1000,false);
			take(tenpai[0], players[1], 1000,false);
			take(tenpai[0], players[2], 1000,false);
			take(tenpai[0], players[3], 1000,false);

		}
		else if (tenpai.length == 2)
		{
			if (tenpai[0] == players[dealer] || tenpai[1] == players[dealer])
				move = false;
			Player lose1 = null, lose2 = null;
			for (int i = 0; i < 4; i++)
			{
				if (players[i] != tenpai[0] && players[i] != tenpai[1])
					if (lose1 == null)
					{
						lose1 = players[i];
					}
					else
					{
						lose2 = players[i];
					}
			}
			take(tenpai[0], lose1, 1500,false);
			take(tenpai[1], lose2, 1500,false);
		}
		else if (tenpai.length == 3)
		{
			if (tenpai[0] == players[dealer] || tenpai[1] == players[dealer] || tenpai[2] == players[dealer])
				move = false;
			Player p = null;
			if (players[0] != tenpai[0] && players[0] != tenpai[1] && players[0] != tenpai[2])
				p = players[0];
			else if (players[1] != tenpai[0] && players[1] != tenpai[1] && players[1] != tenpai[2])
				p = players[1];
			else if (players[2] != tenpai[0] && players[2] != tenpai[1] && players[2] != tenpai[2])
				p = players[2];
			else if (players[3] != tenpai[0] && players[3] != tenpai[1] && players[3] != tenpai[2])
				p = players[3];
			take(tenpai[0], p, 1000,false);
			take(tenpai[1], p, 1000,false);
			take(tenpai[2], p, 1000,false);

		}
		else if (tenpai.length == 4)
		{
			move = false;
		}
		if (move)
		{
			nexthand();
		}
		bonus++;
		update();
		printnextround();
	}

	protected void take(Player winner, Player loser, int amount,boolean ron)
	{
		if (amount % 100 != 0)
			amount = amount + 100 - (amount % 100);
		String claimer=winner.name;
		if(!winner.in || !ron){
			claimer=Console.POT;
		}
		if(winner.score < 0){
			
			if(amount+winner.score <=0){
				claimer=Console.POT;
			}
			else{
				int a=-winner.score;
				winner.give(loser.take(a, Console.POT,namedate),namedate);
				amount=amount-a;
			}
		}
		winner.give(loser.take(amount, claimer,namedate),namedate);

	}

	public void changeplayer(String oldguy, Player newguy)
	{
		Player p = players[position(oldguy)];
		if (riichis.contains(p))
			riichis.remove(p);
		int index = position(oldguy);
		int gamescore = p.score - start[index] + 25000;

		if (!(this instanceof NTGame))
		{
			players[index] = newguy;
			prev[index] = newguy.score;
			start[index] = newguy.score - gamescore + 25000;
		}
		else
			players[index].name = newguy.name;

		updategame();
		switched=true;
	}

	public void setround(int r)
	{
		round = r;
		updatepround();
	}

	public void setbonus(int b)
	{
		if (b >= 0)
			bonus = b;
	}

	public void setdealer(String d)
	{
		dealer = position(d);
		updatepround();
	}

	// this function expects the base 2 to be included
	// yakuman is -1 double is -2
	public int handvalue(int hou, int han)
	{
		if (han == -2)
		{
			return 16000;
		}
		else if (han >= 15 || han == -1)
		{
			return 8000;
		}
		else if (han >= 13)
		{
			return 6000;
		}
		else if (han >= 10)
		{
			return 4000;
		}
		else if (han >= 8)
		{
			return 3000;
		}
		else if (han >= 7)
			return 2000;
		else if (han >= 3)
		{
			int value = hou * (int) Math.pow(2, han);
			if (value >= 2000)
				return 2000;
			else
				return value;
		}
		else
		{
			// error case
			// TODO print something useful?
			return 0;
		}
	}

	public void finalizeriichi(Player winner)
	{
		for (Player p : riichis)
		{
			
				LinkedList<Moneys> temp = p.take(1000, p.name,namedate);
				for (Moneys m : temp)
					triichi.add(m);
				riichi++;
		}
		riichis.clear();
		if (winner != null)
		{
			winner.give(triichi,namedate);
			triichi.clear();//should be anyway, but hey
			riichi=0;
		}
	}

	public boolean exists(String[] names)
	{
		boolean valid = true;
		boolean[] play =
		{ true, true, true, true };
		for (int i = 0; i < names.length; i++)
		{
			boolean exist = false;
			for (int j = 0; j < players.length; j++)
				if (players[j].name.equalsIgnoreCase(names[i]))
					if (play[j])
					{
						play[j] = false;
						exist = true;
					}
					else
					{
						main.PrintMessage(names[i] + " has already been used");
						valid = false;
					}
			if (!exist)
			{
				main.PrintMessage(names[i] + " is not in this game");
				valid = false;
			}
		}

		return valid;
	}

	public int position(String name)
	{
		for (int i = 0; i < players.length; i++)
			if (players[i].name.equalsIgnoreCase(name))
				return i;
		return -1;
	}

	public boolean riichi(String player)
	{

		Player p = players[position(player)];
		if (riichis.contains(p))
		{
			main.PrintMessage(player + "has already riichied");
			return false;
		}
		if (p.in)
		{
			if (p.score -start[position(player)] +25000 >=1000)
			{
				riichis.add(p);
				main.PrintMessage(p.name + " riichied");
				return true;
			}
			else
			{
				main.PrintMessage(p.name + " does not have enough points to riichi");
				return false;
			}
		}
		else
		{
			riichis.add(p);
			main.PrintMessage(p.name + " riichied");
			return true;
		}
	}

	public void nexthand()
	{
		if (dealer == NORTH)
		{
			round++;
			dealer = EAST;
		}
		else
			dealer++;
	}
	
	public void undo(){
		
	}

	public boolean over()
	{
		boolean over30000=false;
		boolean under0=false;
		for(Player p:players){
			if(gamescore(p) >=30000){
				over30000=true;
			}
			if(gamescore(p) < 0){
				under0=true;
			}
		}
		return over30000 && (round > EAST || under0);
	}
}
