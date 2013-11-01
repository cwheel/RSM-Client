package com.nosedive25.rsml;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Observable;
import java.util.Observer;

public class RSMClientDriver implements Observer {
	
	//Example game setup
	public void startGame() throws UnknownHostException, IOException, InterruptedException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		
		  //Start a client
		  RSMClient client = new RSMClient("localhost", 34674, "client", "testclient");
		  
		  //Add the current object as an observer to receive data from the server and other clients
		  client.addObserver(this);
		  
		  //Wait a bit (Bug needs to be fixed on server)
		  Thread.sleep(5000);
		  
		  //Set the human readable client id, a username
		  client.setPlayerID("testuser");
		  
		  System.out.println("Client Driver starting!");

		  //Ask the server for the current games it's running
		  client.refreshGameList();
		  
		  //Same bug
		  Thread.sleep(5000);
		  
		  //Send one player update. Do something similar each time the players information changed (X/Y cords, animation state etc.)
		  
		  for (int i  = 0; i < 1800; i++) {
			  Thread.sleep(33);
			  client.setKey("test", Integer.toString(i));
			  client.sendPlayerData();
		  }
	 }

	@Override
	public void update(Observable o, Object arg) {
		//Receive the last response from the server and process it
		if (((RSMClient) o).lastResponce().responceType() == RSMServerResponce.Command) {
			System.out.println("Server replyed with command: " + ((RSMClient) o).lastResponce().command());
		} else if (((RSMClient) o).lastResponce().responceType() == RSMServerResponce.PlayerUpdate) {
			System.out.println("Server replyed with playerupdate: " + ((RSMClient) o).lastResponce().playerTable().toString());
		}
		
	}
}