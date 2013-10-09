package org.nic.jfxclient.controller;

import java.util.HashMap;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.nic.jfxclient.JFXClient;
import org.nic.jfxclient.model.DataPackage;
import org.nic.jfxclient.util.ControllerInterface;

public class MainUserController implements ControllerInterface {
	
	@FXML
	private AnchorPane userPane;
	
	private JFXClient mainApp;
	
	private String clientUsername = "";
	
	public HashMap<String, Rectangle> users = new HashMap<>();
	
	public void addUser(final String username, Rectangle rect) {
		
		if(username != "" && rect != null) {
			
			users.put(username, rect);
			
		}
		
	}
	
	public void removeUser(final String username) {
		
		if(users.containsKey(username))
			users.remove(username);
		
	}

	@Override
	public void setMainApp(JFXClient mainApp) {

		this.mainApp = mainApp;
		
		this.clientUsername = mainApp.username;
		
	}
	
	public void addUserRect(DataPackage dp) {
		
		final Rectangle rect = new Rectangle(dp.x, dp.y, 15, 15);
		if(mainApp.username.equals(dp.username)) {
			
			rect.setFill(Color.BLUE);
			
		}
		else {
			
			rect.setFill(Color.RED);
			
		}
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				
				userPane.getChildren().add(rect);
				
			}
			
			
		});
		
	}
	
	

}
