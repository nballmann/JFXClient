package org.nic.jfxclient.controller;

import java.net.InetAddress;

import org.nic.jfxclient.JFXClient;
import org.nic.jfxclient.util.ControllerInterface;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class InitFragmentController implements ControllerInterface {

	private JFXClient mainApp;

	@FXML
	private TextField usernameTextField;

	@FXML
	private TextField ipTextField;

	@FXML
	public void init() {

		String local;

		try {

			local = InetAddress.getLocalHost().getHostAddress() + ":" + JFXClient.PORT;

		} catch (Exception e) {
			local = "Network Error";
		}

		ipTextField.setText(local);
		
		usernameTextField.setText(System.getProperty("user.name"));

	}

	@FXML
	private void handleConnectButton() {

		if(usernameTextField.textProperty().get() != "" && ipTextField.getText() != "Network Error") 
		{
			mainApp.connectToServer(usernameTextField.getText(), ipTextField.getText());
		}

	}

	@Override
	public void setMainApp(JFXClient mainApp) {
		this.mainApp = mainApp;
	}


}
