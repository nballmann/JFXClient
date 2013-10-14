package org.nic.jfxclient.controller;

import java.util.HashMap;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

import org.nic.jfxclient.JFXClient;
import org.nic.jfxclient.util.ControllerInterface;

public class MainViewController implements ControllerInterface {
	
	@FXML 
	private TextArea queryTextArea;
	
	@FXML
	private StackPane panelChanger;
	
	private JFXClient mainApp;
	
	private HashMap<String, Node> nodes = new HashMap<>();
	
	public void setMainApp(JFXClient mainApp) {
		
		this.mainApp = mainApp;
		
	}
	
	public void addTextAreaEntry(final String entry) {
		
		queryTextArea.appendText(entry);
		
	}
	
	public void clearTextArea() {
		
		queryTextArea.clear();
		
	}
	
	public void addPanel(final String panelName, Node node) {

		nodes.put(panelName, node);
		
	}
	
	public void showPanel(final String panelName) {
		

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				
				if(panelChanger.getChildren().size() > 0) 
				{
					panelChanger.getChildren().remove(0);
				}
				
				panelChanger.getChildren().add(nodes.get(panelName));
				
			}
			
		});
		
	}
	
	@FXML
	private void handleMenuClose() {
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				
				mainApp.cleanUpAndExit();
				
			}
			
		});
		
	}

}
