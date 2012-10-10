package Mahjong.Irc;
/*
Copyright 2004 Steve Jolly

This file is part of Pony

Pony is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; specifically version 2 of the License.

Pony is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Pony; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


import java.io.IOException;

import org.jibble.pircbot.*;



public class MahjongBot extends PircBot
{

	public String ServerName;
	public int ServerPort;
	public String ServerPassword;
	public String BotName;
	public IrcConsole con;
	public String channel;
	

	public boolean begining;
	public MahjongBot(String CServerName, int CServerPort, String CServerPassword, String CBotName, IrcConsole c, String chan)
	{
		ServerName = CServerName;
		ServerPort = CServerPort;
		ServerPassword = CServerPassword;
		BotName = CBotName;
		this.setName(BotName);
		this.setLogin("KedamaBotsInc");
		con = c;
		channel = chan;
		c.SetBot(this);
		
		
		
		
	}
	public void onMessage(String channel, String sender, String login, String hostname, String mess)
	{
		this.channel = channel;
		onmess(channel, login + hostname, mess);
	}

	public void onmess(String sendera, String hostmask, String message)
	{	
		if (message.indexOf('!') == 0)
		{
				con.parseIrc(sendera,message.substring(1));
		}
		
	}

	public void onPrivateMessage(String sender, String login, String hostname, String mess)
	{
		onmess(sender, login + hostname, mess);

	}

	public void onAction(String sender, String login, String hostname, String target, String action)
	{

	}

	protected void onDisconnect()
	{
		int tries = 0;
		while (tries < 1000 && !isConnected())
		{
			try
			{
				connect(ServerName, ServerPort, ServerPassword);
			} catch (Exception e)
			{
				this.log("Connection attempt " + tries + " failed.");
				tries++;
				if (tries >= 1000000)
					System.exit(0);
			}
		} // while
	}

	protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason)
	{
		if (recipientNick.equals(BotName))
		{
			joinChannel(channel);
		}
	}

	// irc game methods
	

	public void getrecordrequest(String sender, String[] command)
	{
		//TODO, after changing how records work
		if(command[1].equalsIgnoreCase("game"))
		{
			
		}
	}
	
	
	public void Reconnect(){
		try
		{
			this.reconnect();
		} catch (NickAlreadyInUseException e)
		{
			con.PrintMessage("Nick already in use");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IrcException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.joinChannel(channel);
	}
}
