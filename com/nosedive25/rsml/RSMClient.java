package com.nosedive25.rsml;

import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Observable;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class RSMClient extends Observable {
	private SSLSocket server;
	private String clientID;
	private Hashtable<String, String> sendVals;
	private Hashtable<String, String> serverProperties;
	private ArrayList<RSMGame> activeGames = new ArrayList<RSMGame>(); 
	private DataOutputStream serverOut;;
	private String currentGame;
	private RSMServerResponce lastResponce;
	
	private KeyStore keyStore;
    private TrustManagerFactory tm;
    private KeyManagerFactory km;
	
	/*@desc Constructor for RSMClient, takes the server IP or hostname, port, keystore path and the keystore password*/
	public RSMClient(String host, int port, String keystorePath, String keystorePass)  throws UnknownHostException, IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
		 SecureRandom sr = new SecureRandom();
		 sr.nextInt();
	    	
		 keyStore = KeyStore.getInstance("JKS");
		 keyStore.load(new FileInputStream("clienttruststore"), keystorePass.toCharArray());
		 tm = TrustManagerFactory.getInstance("SunX509");
		 tm.init(keyStore);
		 km = KeyManagerFactory.getInstance("SunX509");
		 km.init(keyStore, keystorePass.toCharArray());
		 
		 SSLContext sslcon = SSLContext.getInstance("TLS");
		 sslcon.init(km.getKeyManagers(), tm.getTrustManagers(), sr);
		 server = (SSLSocket)sslcon.getSocketFactory().createSocket(host, port);
		 
		 sendVals = new Hashtable<String, String>();
		 serverOut = new DataOutputStream(server.getOutputStream());
		 
		 Runnable fd = new Runnable() {
			  public void run() {
				  try {
					getPlayerData();
				} catch (IOException e) {
					e.printStackTrace();
				}
			  }
			};
		 
		 Thread fetchData = new Thread(fd);
		 fetchData.start();
		 
		 sendServerCommand("REQUEST_SERVER_PROPERTIES");
	}
	
	/*@desc Adds a key to the table of player values*/
	public void setKey(String key, String value) {
		sendVals.put(key, value);
	}
	
	/*@desc Gets the currently active games from the server*/
	public void refreshGameList() throws IOException {
		sendServerCommand("REQUEST_ACTIVE_GAMES");
	}
	
	/*@desc Sets the human readable player id, a username*/
	public void setPlayerID(String id) throws IOException {
		clientID = id;
		sendServerCommand("SET_PLAYER_ID:" + id);
	}
	
	/*@desc Creates a new game on the server, if allowed*/
	public void createGame(String game, String motd) throws Exception {
		if (serverProperties.get("ClientsCanStartGames").equals("YES")) {
			sendServerCommand("CREATE_GAME:" + game + "," + motd);
		} else {
			throw new Exception("Invalid Server Properties");
		}
	}
	
	/*@desc Adds the client to the game with the matching game id*/
	public void joinGame(String id) throws IOException {
		currentGame = id;
		sendServerCommand("JOIN_GAME:" + id);
	}
	
	/*@desc Removes the client from the current game*/
	public void leaveGame() throws IOException {
		currentGame = "-1";
		sendServerCommand("LEAVE_GAME");
	}
	
	/*@desc Sends the player data table to the server and other clients in the game*/
	public void sendPlayerData() throws IOException {
		serverOut.writeBytes("PLAYER_UPDATE:" + sendVals.toString() + '\n');
		sendVals.clear();
	}
	
	/*@desc Sends a game command to the server and other clients in the game*/
	public void sendGameCommand(String cmd) throws IOException {
		sendServerCommand("GAME_CMD:" + cmd);
	}
	
	/*@desc Returns the current game*/
	public String currentGame() {
		return currentGame;
	}
	
	/*@desc Returns the server name*/
	public String serverName() {
		return serverProperties.get("ServerName");
	}
	
	/*@desc Returns the last update from the server*/
	public RSMServerResponce lastResponce() {
		return lastResponce;
	}
	
	/*@desc Returns an array list of the currently active games on the server*/
	public ArrayList<RSMGame> games() {
		return activeGames;
	}

	private void getPlayerData() throws IOException {
		while (true) {
			try {
				BufferedReader serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
				String input = serverIn.readLine();
				
				if (input.contains("SERVER_PROPERTIES")) {
					serverProperties = stringToHashtable(input.replace("SERVER_PROPERTIES:", ""));
				} else if (input.contains("ACTIVE_GAMES")) {
					activeGames = gamesFromServerString(input);
				} else if (input.contains("JOINED_GAME:")) {
					//TODO: Use in future implementations
				} else if (input.contains("LEFT_GAME:")) {
					//TODO: Use in future implementations
				} else {
					lastResponce = new RSMServerResponce(input);
					
					setChanged();
				    notifyObservers();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendServerCommand(String cmd) throws IOException {
		serverOut.writeBytes(cmd + '\n');
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
	
	private ArrayList<RSMGame> gamesFromServerString(String serverString) {
		ArrayList<RSMGame> serverGames = new ArrayList<RSMGame>();
		serverString = serverString.replace("{", "").replace("[", "").replace("]", "").replace("ACTIVE_GAMES:", "");
		String[] gameObjectStrings = serverString.split("},");
		
		for (int i = 0; i < gameObjectStrings.length; i++) {
			String[] gameObjectValues = gameObjectStrings[i].split(",");
			RSMGame newGame = new RSMGame(gameObjectValues[0].split("=")[1]);
			newGame.setMotd(gameObjectValues[1].split("=")[1].replace("}", ""));
			newGame.setID(gameObjectValues[2].split("=")[1]);
			
			serverGames.add(newGame);
		}
		
		return serverGames;
	}
}
