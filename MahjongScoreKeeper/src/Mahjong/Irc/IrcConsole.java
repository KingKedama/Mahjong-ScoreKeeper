package Mahjong.Irc;

import Mahjong.Console;

public class IrcConsole extends Console {
	
	public MahjongBot bot;
	public String sender="";
	public static void main(String[] args) {
		if (args.length < 3){
			System.out.println("Syntax: [nick] [server] [channel]");
			return;
		}
		IrcConsole c = new IrcConsole();
		for(String s:args){
			System.out.println(s);
		}
		String name= args[0];
		String server=args[1];
		String channel=args[2];
		
		MahjongBot b = new MahjongBot(server,6667,null,name,c,channel);
		boolean done=false;
		while(!done){
			try{
			b.connect(b.ServerName, b.ServerPort, b.ServerPassword);
			done=true;
			}
			catch(Exception e){}
		}
		done=false;
		while(!done){
			try{
				b.joinChannel(b.channel);
			done=true;
			}
			catch(Exception e){}
		}
		c.SetBot(b);
		c.mainLoop();
		
		
		
		
	}
	
	public void PrintMessage(String mess)
	{
		if(mess!=null){
			mess=mess.replace("Syntax:", "Syntax:!");
		}
		else{
			new Exception().printStackTrace();
		}
		if(bot != null)
		{
			if(sender==null){
			bot.sendMessage(bot.channel, mess);
			}
			else{
				bot.sendMessage(sender, mess);
			}
		}
		System.out.println(mess);
	}
	
	
	public void SetBot(MahjongBot b)
	{
		bot=b;
	}
	
	public void mainLoop()
	{
		done = false;
		while (!done)
		{
			System.out.print("#$?");
			String command = in.nextLine();
			if(command.indexOf("reconnect")==0){
				bot.Reconnect();
			}
			else
				parseCommand(command, true);
		}
	}
	synchronized public void parseIrc(String sender,String command){
		this.sender=sender;
		this.parseCommand(command, true);
	}
}
