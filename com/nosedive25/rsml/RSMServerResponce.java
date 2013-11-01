package com.nosedive25.rsml;

import java.util.Hashtable;

public class RSMServerResponce {
	public static final int Command = 1;
	public static final int PlayerUpdate = 0;
	private String cmd;
	private Hashtable<String, String> playerTable;
	private int dataType;
	private String player;
	
	public RSMServerResponce(String rawResponce) {
		player = rawResponce.split("LAYER_UPDATE\\(")[1].split("\\):")[0];
		
		if (rawResponce.contains("PLAYER_UPDATE")) {
			dataType = PlayerUpdate;
			playerTable = stringToHashtable(rawResponce.split("\\):")[1]);
		} else if (rawResponce.contains("GAME_CMD")) {
			dataType = Command;
			cmd = rawResponce.split("\\):")[1];
		}
	}
	
	private Hashtable<String, String> stringToHashtable(String hashstring) {
		hashstring = hashstring.replace("{", "").replace("}", "");
		String[] elements = hashstring.split(",");
		Hashtable<String, String> newTable = new Hashtable<String, String>();
		
		for (int i = 0; i < elements.length; i++) {
			String key = elements[i].split("=")[0];
			String value = elements[i].split("=")[1];
			
			newTable.put(key, value);
		}
		
		return newTable;
	}
	
	public int responceType() {
		return dataType;
	}
	
	public Hashtable<String, String> playerTable() {
		return playerTable;
	}
	
	public String command() {
		return cmd;
	}
	
	public String player() {
		return player;
	}

}
