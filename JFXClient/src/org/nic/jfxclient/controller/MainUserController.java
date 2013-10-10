package org.nic.jfxclient.controller;

import java.util.ArrayList;
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

	private double deltaX = 0;
	private double deltaY = 0;

	public void init() {

		userRect.xProperty().bindBidirectional(mainApp.xProperty());
		userRect.yProperty().bindBidirectional(mainApp.yProperty());

		userPane.getChildren().add(userRect);		

		userRect.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if(e.getButton() == MouseButton.PRIMARY) {
					deltaX = userRect.getLayoutX() - e.getSceneX();
					deltaY = userRect.getLayoutY() -  e.getSceneY();

					System.out.println(e.getSceneX() + " : " + e.getSceneY());
					System.out.println(mainApp.getX() + " : " + mainApp.getY());
				}
			}

		});

		userRect.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if(e.getButton() == MouseButton.PRIMARY) {

					System.out.println( deltaX + " : " + deltaY );
					userRect.setLayoutX((int)(e.getSceneX() + deltaX));
					userRect.setLayoutY((int)(e.getSceneY() + deltaY));

					if(userRect.xProperty().get() < 0) {
						userRect.xProperty().set(0);
					}
					if(userRect.xProperty().get() > (userPane.getPrefWidth()-15)) {
						userRect.setLayoutX((int)userPane.getPrefWidth()-15);
					}
					if(userRect.yProperty().get() < 0) {
						userRect.yProperty().set(0);
					}
					if(userRect.yProperty().get() > (userPane.getPrefHeight()-15)) {
						userRect.setLayoutY((int)userPane.getPrefHeight()-15);
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

		if(users.containsKey(username)) {

			Platform.runLater(new Runnable() {

				@Override
				public void run() {

					userPane.getChildren().remove(users.get(username));
				}

			});

			users.remove(username);

		}

	}

	@Override
	public void setMainApp(JFXClient mainApp) {

		this.mainApp = mainApp;

		this.clientUsername = mainApp.username;

	}

	public void checkUserExistance(final ArrayList<DataPackage> dpList) {

		ArrayList<String> activeUsers = new ArrayList<>();

		for(DataPackage  dp : dpList) {

			activeUsers.add(dp.username);

		}

		for(String key : users.keySet()) {

			if(!activeUsers.contains(key)) {

				removeUser(key);

			}

		}

	}

	public void addUserRect(final DataPackage dp) {

		if(!mainApp.username.equals(dp.username)) 
		{
			if(users.keySet().contains(dp.username)) {

				Platform.runLater(new Runnable() {

					@Override
					public void run() {

						users.get(dp.username).setLayoutX((int) dp.x);
						users.get(dp.username).setLayoutY((int) dp.y);

					}

				});

			}
			else {

				final Rectangle rect = new Rectangle(dp.x, dp.y, 15, 15);
				rect.setFill(Color.RED);

				addUser(dp.username, rect);

				Platform.runLater(new Runnable() {

					@Override
					public void run() {

						userPane.getChildren().add(users.get(dp.username));

					}

				});

			}

		}


	}

}
