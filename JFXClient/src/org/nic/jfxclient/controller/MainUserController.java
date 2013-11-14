package org.nic.jfxclient.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    private Label userLabel = new Label();

    public StringProperty clientUsername = new SimpleStringProperty("");

    public Map<String, Rectangle> users = Collections.synchronizedMap(new HashMap<String, Rectangle>());
    public Map<String, Label> userLabels = Collections.synchronizedMap(new HashMap<String, Label>());

    private double deltaX = 0;
    private double deltaY = 0;

    public StringProperty clientUsernameProperty() { return clientUsername; }

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
		    userPane.getChildren().remove(userLabels.get(username));
		}

	    });

	    users.remove(username);
	    userLabels.remove(username);

	}

    }

    @Override
    public void setMainApp(JFXClient mainApp) {

	this.mainApp = mainApp;

	this.clientUsername.set(mainApp.username);

    }

    public synchronized void checkUserExistance(final List<DataPackage> dataList) {

	ArrayList<String> activeUsers = new ArrayList<>();

	for(DataPackage  dp : dataList) {

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

			users.get(dp.username).setX(dp.x);
			users.get(dp.username).setY(dp.y);

		    }

		});

	    }
	    else {

		final Rectangle rect = new Rectangle(dp.x, dp.y, 15, 15);
		rect.setFill(Color.RED);
		rect.setX(dp.x);//setLayoutX(dp.x);
		rect.setY(dp.y);//setLayoutY(dp.y);

		addUser(dp.username, rect);

		Platform.runLater(new Runnable() {

		    @Override
		    public void run() {

			userPane.getChildren().add(users.get(dp.username));
			addUserLabel(dp.username, users.get(dp.username));

			userRect.toFront();
			userLabel.toFront();
		    }

		});

	    }

	}

    }

    private void addUserLabel(final String userName, final Rectangle userRect) {

	Platform.runLater(new Runnable() {

	    @Override
	    public void run() {

		final Label label = new Label(userName);
		label.setMouseTransparent(true);
		label.setTextFill(Color.RED);
		//				label.layoutXProperty().bindBidirectional(userRect.xProperty());

		userRect.xProperty().addListener(new ChangeListener<Number>() {

		    @Override
		    public void changed(ObservableValue<? extends Number> arg0,
			    Number arg1, Number arg2) {

			if(userRect.xProperty().get() > userPane.getPrefWidth()/2) {
			    label.layoutXProperty().set(userRect.xProperty().get()-(label.getWidth()-15));
			}
			else {
			    label.layoutXProperty().set(userRect.xProperty().get());
			}

		    }

		});

		userRect.yProperty().addListener(new ChangeListener<Number>() {

		    @Override
		    public void changed(ObservableValue<? extends Number> arg0,
			    Number arg1, Number newValue) {

			if(newValue.intValue() > userPane.getPrefHeight()/2) {
			    label.layoutYProperty().set(newValue.doubleValue()-25);
			}
			else {
			    label.layoutYProperty().set(newValue.doubleValue()+18);
			}

		    }

		});

		userLabels.put(userName, label);
		userPane.getChildren().add(label);

	    }

	});

    }

    public void init() {

	userRect.layoutXProperty().bindBidirectional(mainApp.xProperty());
	userRect.layoutYProperty().bindBidirectional(mainApp.yProperty());
	userLabel.setTextFill(Color.BLUE);
	userLabel.mouseTransparentProperty().set(true);
	userLabel.textProperty().bindBidirectional(clientUsername);
	//		userLabel.layoutXProperty().bindBidirectional(userRect.layoutXProperty());

	userRect.layoutXProperty().addListener(new ChangeListener<Number>() {

	    @Override
	    public void changed(ObservableValue<? extends Number> observable,
		    Number oldValue, Number newValue) {

		if(userRect.layoutXProperty().get() > userPane.getPrefWidth()/2) {
		    userLabel.layoutXProperty().set(userRect.layoutXProperty().get()-(userLabel.getWidth()-15));
		}
		else {
		    userLabel.layoutXProperty().set(userRect.layoutXProperty().get());
		}

	    }

	});

	userRect.layoutYProperty().addListener(new ChangeListener<Number>() {

	    @Override
	    public void changed(ObservableValue<? extends Number> observable,
		    Number oldValue, Number newValue) {

		if(newValue.intValue() > (userPane.getPrefHeight()/2)) {
		    userLabel.layoutYProperty().set(newValue.doubleValue()-25);
		}
		else {
		    userLabel.layoutYProperty().set(newValue.doubleValue()+25);
		}

	    }

	});

	userPane.getChildren().add(userRect);
	userPane.getChildren().add(userLabel);

	userRect.setOnMousePressed(new EventHandler<MouseEvent>() {

	    @Override
	    public void handle(MouseEvent e) {

		if(e.getButton() == MouseButton.PRIMARY) {
		    deltaX = userRect.getLayoutX() - e.getSceneX();
		    deltaY = userRect.getLayoutY() -  e.getSceneY();

		    System.out.println(mainApp.getX() + " : " + mainApp.getY());
		}
	    }

	});

	userRect.setOnMouseDragged(new EventHandler<MouseEvent>() {

	    @Override
	    public void handle(MouseEvent e) {

		if(e.getButton() == MouseButton.PRIMARY) {

		    //					System.out.println( deltaX + " : " + deltaY );
		    userRect.setLayoutX(e.getSceneX() + deltaX);
		    userRect.setLayoutY(e.getSceneY() + deltaY);

		    if(userRect.layoutXProperty().get() < 0) {
			mainApp.setX(0);
		    }
		    if(userRect.layoutXProperty().get() > (userPane.getPrefWidth()-15)) {
			mainApp.setX((int)userPane.getPrefWidth()-15);
		    }
		    if(userRect.layoutYProperty().get() < 0) {
			mainApp.setY(0);
		    }
		    if(userRect.layoutYProperty().get() > (userPane.getPrefHeight()-15)) {
			mainApp.setY((int)userPane.getPrefHeight()-15);
		    }
		}

	    }

	});

    }

}
