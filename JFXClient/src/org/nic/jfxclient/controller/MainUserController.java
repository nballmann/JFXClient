package org.nic.jfxclient.controller;

import java.util.HashMap;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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

	private Rectangle userRect = new Rectangle(15, 15, Color.BLUE);

	public String clientUsername = "";

	public HashMap<String, Rectangle> users = new HashMap<>();

	private double tempX = 0;
	private double tempY = 0;

	public void init() {

		userRect.layoutXProperty().bindBidirectional(mainApp.xProperty());
		userRect.layoutYProperty().bindBidirectional(mainApp.yProperty());

		userPane.getChildren().add(userRect);

		userRect.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if(e.getButton() == MouseButton.PRIMARY) {
//					tempX = e.getSceneX();
//					tempY = e.getSceneY();
				}
			}

		});

		userRect.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if(e.getButton() == MouseButton.PRIMARY) {
					mainApp.setX((int)(e.getSceneX() - tempX));
					mainApp.setY((int)(e.getSceneY() - tempY));

					if(userRect.xProperty().get() < 0) {
						mainApp.setX(0);
						userRect.xProperty().set(0);
					}
					if(userRect.xProperty().get() > (userPane.getPrefWidth()-15)) {
						mainApp.setX((int)userPane.getPrefWidth()-15);
						userRect.xProperty().set((int)userPane.getPrefWidth()-15);
					}
					if(userRect.yProperty().get() < 0) {
						mainApp.setY(0);
						userRect.yProperty().set(0);
					}
					if(userRect.yProperty().get() > (userPane.getPrefHeight()-15)) {
						mainApp.setY((int)userPane.getPrefHeight()-15);
						userRect.setY((int)userPane.getPrefHeight()-15);
					}
				}

			}

		});

	}

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

		if(!mainApp.username.equals(dp.username)) 
		{
			if(users.keySet().contains(dp.username)) {

				users.get(dp.username).xProperty().set((int) dp.x);
				users.get(dp.username).yProperty().set((int) dp.y);
				
			}
			else {
				
				final Rectangle rect = new Rectangle(dp.x, dp.y, 15, 15);
				rect.setFill(Color.RED);
				
				addUser(dp.username, rect);
				
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						
						userPane.getChildren().add(rect);
						
					}
					
				});
				
			}

		}


	}

}
