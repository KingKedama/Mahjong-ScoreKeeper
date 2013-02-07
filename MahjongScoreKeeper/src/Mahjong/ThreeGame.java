package Mahjong;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Vector;


public class ThreeGame extends FreeplayGame {

	public ThreeGame(Player east, Player south, Player west, Player north,
			Console c, String n, boolean r) {
		super(null, null, null, null, c, n, false);
		players=new Player[3];
		players[EAST] = east;
		players[SOUTH] = south;
		players[WEST] = west;
		riichis = new ArrayList<Player>();
		prev = new int[3];
		prev[0] = players[0].score;
		prev[1] = players[1].score;
		prev[2] = players[2].score;
		printgamestatus();
		updategame();
		printnextround();
	}
	
	public ThreeGame(Player east,Player south,Player west,Console c,String n)
	{
		super(null,null,null,null,c,n,false);
		players=new Player[3];
		players[EAST] = east;
		players[SOUTH] = south;
		players[WEST] = west;
		riichis = new ArrayList<Player>();
		prev = new int[3];
		prev[0] = players[0].score;
		prev[1] = players[1].score;
		prev[2] = players[2].score;
		start=prev.clone();
		printgamestatus();
		updategame();
		printnextround();
	}

	public void updategame()
	{
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(namedate, true));
			out.write("\r\n"+pround+","+players[EAST].name+","+players[SOUTH].name+","+players[WEST].name);
			out.write("\r\n"+"change"+","+(players[EAST].score-prev[EAST])+","+(players[SOUTH].score-prev[SOUTH])+","+(players[WEST].score-prev[WEST]));
			out.write("\r\n"+"result"+","+players[EAST].score+","+players[SOUTH].score+","+players[WEST].score+","+bonus+","+riichi);
			out.close();
		} catch (IOException eee) {
			System.out.println("failed to write to "+namedate);
			return;
		}
	}
	
	public void printgamestatus() {
		Formatter fmt = new Formatter();
		fmt.format("%12s %12s %12s %12s", pround, players[0].name,
				players[1].name, players[2].name + "\r\n");
		fmt.format("%12s %12s %12s %12s", "change", players[0].score
				- prev[0], players[1].score - prev[1], players[2].score
				- prev[2] + "\r\n");
		fmt.format("%12s %12s %12s %12s", "", players[0].score,
				players[1].score, players[2].score + "  ");
		System.out.println(fmt);
	}
	
	public void tenpai(String[] ten)
	{
		finalizeriichi(null);
		Player[] tenpai =new Player[ten.length];
		boolean move = true;
		for(int i=0;i < ten.length;i++)
		{
			tenpai[i]=players[position(ten[i])];
			if(tenpai[i]==players[dealer])
				move=false;
		}
		if(tenpai.length==1)
		{
			take(tenpai[0], players[0], 1000);
			take(tenpai[0], players[1], 1000);
			take(tenpai[0], players[2], 1000);
		}
		else if(tenpai.length==2)
		{
			Player loser;
			if(players[0]!=tenpai[0] &&players[0] !=tenpai[1])
				loser=players[0];
			else if(players[1]!=tenpai[0] &&players[1] !=tenpai[1])
				loser=players[1];
			else
				loser=players[2];
			take(tenpai[0],loser,1000);
			take(tenpai[1],loser,1000);
		}
		if(move)
			nexthand();
		bonus++;
		update();
		printnextround();
	}
	
	public void nexthand()
	{
		if (dealer == WEST) {
			round++;
			dealer = EAST;
		} else
			dealer++;
	}
	
	//TODO handvalue?
}
