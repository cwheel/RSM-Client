package com.nosedive25.rsml;

import java.util.UUID;

public class RSMGame {
	private String name;
	private String motd;
	private String id;

	/*@desc RSMGame constructor, takes the games name*/
	public RSMGame(String n) {
		id = UUID.randomUUID().toString();
		name = n;
	}
	
	/*@desc Set the name of the game*/
	public void setName(String n) {
		name = n;
	}
	
	/*@desc Set the games message of the day or description*/
	public void setMotd(String m) {
		motd = m;
	}
	
	/*@desc Set the games name*/
	public String name() {
		return name;
	}
	
	/*@desc Returns the games message of the day*/
	public String motd() {
		return motd;
	}
	
	/*@desc Sets the games id*/
	public void setID(String newID) {
		id = newID;
	}
	
	/*@desc Converts the game object to a string*/
	public String toString() {
		return "{name=" + name + ",motd=" + motd + ",id=" + id + "}";
	}
}
