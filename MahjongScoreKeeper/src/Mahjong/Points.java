package Mahjong;

public class Points
{
	public String owner;
	public int amount;
	public Points(String s, int a)
	{
		owner=s;
		amount=a;
	}
	public String toString()
	{
		return owner+" "+amount;
	}
}
