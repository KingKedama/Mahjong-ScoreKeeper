package Mahjong.Commands;

import java.io.File;



import Mahjong.Console;

public abstract class Command {

	private Console c;
	private final String name;
	
	public Command(String name,Console c){
		this.name=name;
		this.c=c;
	

	}
	
	abstract public void execute();
}
