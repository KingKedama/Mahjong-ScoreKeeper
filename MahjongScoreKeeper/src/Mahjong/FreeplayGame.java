package Mahjong;

public class FreeplayGame extends Game{

	public FreeplayGame(Player east, Player south, Player west, Player north,
			Console c, String n, boolean r) {
		super(east, south, west, north, c, n, r);
		switched=true;
	}
	
	
	public void tenpai (String[] ten)
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
		
		if (tenpai.length == 1) 
		{
			take(tenpai[0], players[0], 1000);
			take(tenpai[0], players[1], 1000);
			take(tenpai[0], players[2], 1000);
			take(tenpai[0], players[3], 1000);
		}
		else if(tenpai.length==2)
		{
			Player lose1=null,lose2=null;
			for(int i =0;i < 4;i++)
				if(players[i]!=tenpai[0] && players[i]!=tenpai[1])
					if(lose1==null)
						lose1=players[i];
					else
						lose2=players[i];
			take(tenpai[0],lose1,1500);
			take(tenpai[1],lose2,1500);
		}
		else if(tenpai.length==3)
		{
			Player p = null;
			if (players[0] != tenpai[0] && players[0] != tenpai[1]
					&& players[0] != tenpai[2])
				p = players[0];
			else if (players[1] != tenpai[0] && players[1] != tenpai[1]
					&& players[1] != tenpai[2])
				p = players[1];
			else if (players[2] != tenpai[0] && players[2] != tenpai[1]
					&& players[2] != tenpai[2])
				p = players[2];
			else if (players[3] != tenpai[0] && players[3] != tenpai[1]
					&& players[3] != tenpai[2])
				p = players[3];
			take(tenpai[0], p, 1000);
			take(tenpai[1], p, 1000);
			take(tenpai[2], p, 1000);
		}
		if(move)
			nexthand();
		bonus++;
		update();
		printnextround();
	}

	protected void take(Player winner,Player loser, int amount)
	{
		if (amount % 100 != 0)
			amount = amount + 100 - (amount % 100);
		winner.score = winner.score + amount;
		loser.score = loser.score - amount;
	}
	
	public void finalizeriichi(Player winner)
	{
		for (Player p : riichis) {
				p.score = p.score - 1000;
				riichi++;
		}
		riichis.clear();
		if(winner !=null)
		{
		winner.score = winner.score + 1000 * riichi;
		riichi = 0;
		}
	}
	
	public boolean riichi(String player)
	{
		
		Player p=players[position(player)];
		if(riichis.contains(p))
		{
			System.out.println(player+"has already riichied");
			return false;
		}
			riichis.add(p);
			System.out.println(p.name + " riichied");
			return true;
		
	}
}
