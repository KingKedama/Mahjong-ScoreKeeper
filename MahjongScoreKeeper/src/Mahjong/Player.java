package Mahjong;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
public class Player {
	public String name;
	public int score; 
	public LinkedList<Points> points;
	private Console printer;
	public boolean in;
	
	public int first;
	public int second;
	public int third;
	public int forth;
	public int winnnings;//not always accurate, only updated when topn is called
	public int prize;
	
	public Player(String n,int s,LinkedList<Points> m,Console c)
	{
		printer=c;
		name=n;
		score=s;
		points=m;
		in=true;
		if(m==null){
			in=false;
			points=new LinkedList<Points>();
		}
		else if(score >=0)
		{
			score=0;//should be unnessary, but checks that the entered score was accurate
			for(Points mon : m)
			{
				score+=mon.amount;
			}
		}
		
		first=0;
		second=0;
		third=0;
		forth=0;
		if (new File(Console.RESULT).exists())// get score from file
		{
			BufferedReader in;
			try
			{
				in = new BufferedReader(new FileReader(Console.RESULT));
				String str;
				while ((str = in.readLine()) != null)
				{
					String[] split=str.split(" ");
					
					
					if(split.length>=9){
						if(split[2].equals(name)){
							first++;
						}
						if(split[4].equals(name)){
							if(split[3].equalsIgnoreCase("1")){
								first++;
							}
							else{
								second++;
							}
						}
						if(split[6].equals(name)){
							if(split[5].equalsIgnoreCase("1")){
								first++;
							}
							else if(split[5].equalsIgnoreCase("2")){
								second++;
							}
							else{
								third++;
							}
						}
						if(split[8].equals(name)){
							if(split[7].equalsIgnoreCase("1")){
								first++;
							}
							else if(split[7].equalsIgnoreCase("2")){
								second++;
							}
							else if(split[7].equalsIgnoreCase("3")){
								third++;
							}
							
							forth++;
						}
					}
					
				}
				in.close();
			}
			catch (IOException e)
			{
				c.PrintMessage("failed to read " + Console.RESULT);
			}
		}
	}
	public double GPA(){
		double tmp=first*4+ second*3 + third*2+forth;
		tmp=tmp/(first+second+third+forth);
		if(Double.isNaN(tmp)|| Double.isInfinite(tmp))
			return 0;
		return 5-tmp;
	}
	
	public int ownMoney()
	{
		if(points.isEmpty())
			return 0;
		if(!points.getFirst().owner.equals(name))
			return 0;
		return points.getFirst().amount;
	}
	//return the linked list containing the Moneys(value and original owner) taken for tourny purposes
	public LinkedList<Points> take(int am,String player,String game)
	{
		if(this==printer.pot && score < am)//the pot will always give itself money out of air rather than go negative
		{
			LinkedList<Points> temp=new LinkedList<Points>();
			temp.add(new Points(this.name,am-score));
			this.give(temp,game);
		}
		LinkedList<Points> ret=new LinkedList<Points>();
		score-=am;
			int amount=am;
			while(amount > 0)
			{
				if(!points.isEmpty())
				{
					Points take=points.getLast();
					for(Points m : points)
						if(m.owner.equals(player))
							take=m;
					if(take.amount > amount)
					{
						ret.add(new Points(take.owner,amount));
						take.amount-=amount;
						amount=0;
					}
					else
					{
						ret.add(take);
						points.remove(take);
						amount-=take.amount;
					}
				}
				else
				{
					ret.addAll(printer.pot.take(amount, player,game));
					amount =0;
				}
			}
			if(in)
				writestack();
		return ret;
	}
	
	public void give(LinkedList<Points> points,String game)
	{
		if(!in)
		{
			for(Points m:points)
			{
				score+=m.amount;
			}
			System.out.println(name);
			printer.pot.give(points,game);
			
			
		}
		else
		while(!points.isEmpty())
		{
			int amount=points.getFirst().amount;
			if(score < 0)
			{
				int debt= -score;
				
				score+=amount;
				if(debt >= amount)//this only makes you less negative
				{
					LinkedList<Points> temp=new LinkedList<Points>();
					temp.add(points.removeFirst());
					printer.pot.give(temp,game);
				}
				else //you are being pushed positive
				{
					LinkedList<Points> temp=new LinkedList<Points>();
					temp.add(new Points(points.getFirst().owner,debt));
					printer.pot.give(temp,game);
					score=0;
					points.addFirst(new Points(points.removeFirst().owner,amount-debt));
				}
			}
			else
			{
				score+=points.getFirst().amount;
				if (points.getFirst().owner.equals(name))// it is your money
				{
					if (!points.isEmpty() && points.getFirst().owner.equals(name))// you
																				// still
																				// have
																				// some
																				// of
																				// your
																				// own
																				// money
					{
						points.getFirst().amount += points.getFirst().amount;
					}
					else
					{
						points.addFirst(points.getFirst());
					}
				}
				else
				{
					if (!points.isEmpty() && points.getFirst().owner.equals(points.getLast().owner))
					{
						points.getLast().amount += points.getFirst().amount;
					}
					else
					{
						points.add(points.getFirst());
					}
				}
				points.removeFirst();
			}
		}
		if(in)
			writestack();
	}
	
	public void writestack()
	{
		File f=new File(name+".stack.back");
		if (f.exists())
			if (!f.delete())
			{
				printer.PrintMessage("failed to delete "+name+".stack.back");
				return;
			}
		f= new File(name+".stack");
		if (f.exists())
		{
			if (!f.renameTo(new File(name+".stack.back")))
			{
				printer.PrintMessage("failed to rename "+name+".stack");
				return;
			}
		}
		f= new File(name+".stack");
		try
		{
			f.createNewFile();
		} catch (IOException e)
		{
			printer.PrintMessage("failed to create "+name+".stack");
			return;
		}
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(name+".stack", true));
			for(Points m: points)
			{
				out.write(m.owner+" "+m.amount+"\r\n");
			}
			out.close();
			f=new File(name+".stack.back");
			if (f.exists())
				if (!f.delete())
				{
					printer.PrintMessage("failed to delete "+name+".stack.back");
					return;
				}
		}
		catch (IOException e)
		{
			printer.PrintMessage("failed to write to "+name+".stack");
		}
	}
	
	public void writecsv(String round, String game)
	{
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(name + ".csv", true));
			out.write("\r\n" + score + "," + round + "," + game);
			out.close();
		} catch (IOException e)
		{
			printer.PrintMessage("failed to write to " + name + ".csv");
		}
	}

	//statistics section
	//
	public int handsIn;//total number of hands this player has been in
	public void addHand(boolean won,boolean riichi,boolean lost,boolean tsumo,boolean tenpai)
	{
		
	}
	public void fixvalues(String game){
		for(Game g: printer.games.values()){
			if(g.namedate !=game){
				if(g.position(name) !=-1){
					g.start[g.position(name)]=score-g.prev[g.position(name)]+g.start[g.position(name)];
					g.prev[g.position(name)]=score;
				}
			}
		}
	}

}
