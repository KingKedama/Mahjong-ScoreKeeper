package Mahjong;

public class Moneys
{
	public String owner;
	public int amount;
	public Moneys(String s, int a)
	{
		owner=s;
		amount=a;
	}
	public String toString()
	{
		return owner+" "+amount;
	}
}
