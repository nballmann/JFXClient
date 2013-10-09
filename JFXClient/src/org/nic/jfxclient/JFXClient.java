package org.nic.jfxclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.nic.jfxclient.controller.InitFragmentController;
import org.nic.jfxclient.controller.MainUserController;
import org.nic.jfxclient.controller.MainViewController;
import org.nic.jfxclient.model.DataPackage;

public class JFXClient extends Application{

	public static final int PORT = 15001;
	public static final String HOST = "localhost";

	private MainViewController mainController;
	private InitFragmentController initController;
	private MainUserController userController;

	private Socket socket;

	private ArrayList<DataPackage> others = new ArrayList<>();

	private int state = 0;
	private boolean connected = true;

	public String username = "";

	private int x = 0;
	private int y = 0;

	private Runnable send = new Runnable() {

		@Override
		public void run() {

			ObjectOutputStream oos;

			while(connected) {

				synchronized (this) {

					if(socket != null) {

						try {

							DataPackage dp = new DataPackage();

							dp.x = x;
							dp.y = y;
							dp.username = username;

							oos = new ObjectOutputStream(socket.getOutputStream());
							oos.writeObject(state);
							//						System.out.println("Send: state");

							oos = new ObjectOutputStream(socket.getOutputStream());
							oos.writeObject(dp);

							//						System.out.println("Send: DataPackage");

							if (state == 1) { // client disconnected

								connected = false;
								socket = null;
								mainController.showPanel("InitFragment");

								mainController.addTextAreaEntry("\r\nClient Disconnected");

							}

						} catch (Exception e) {
							connected = false;
							socket = null;
							mainController.showPanel("InitFragment");
//							e.printStackTrace();
						}

					}
					else { // temporary
						break;
					}

				}

			}
		}

	};

	private Runnable receive = new Runnable() {

		@Override
		public void run() {

			ObjectInputStream ois;

			while(connected) {

				synchronized (this) {

					try {

						ois = new ObjectInputStream(socket.getInputStream());
						int receiveState = (Integer) ois.readObject();

						//					System.out.println("Client/receive: state");

						if(receiveState == 1) { // disconnected by server

							connected = false;
							socket = null;
							mainController.showPanel("InitFragment");

							mainController.addTextAreaEntry("\r\nDisconnected by Server");
							
							break;

						}
						else if(receiveState == 2) { // server disconnected

							connected = false;
							socket = null;
							mainController.showPanel("InitFragment");

							mainController.addTextAreaEntry("\r\nServer Disconnected");

							break;
						}

						ois = new ObjectInputStream(socket.getInputStream());
						@SuppressWarnings("unchecked")
						ArrayList<DataPackage> dataList = (ArrayList<DataPackage>) ois.readObject();

						//					System.out.println("Client/receive: dataList");

						for(int i = 0; i < dataList.size(); i++) {

							DataPackage dp = dataList.get(i);

							if(dataList.size() != others.size()) {

								if(dataList.size() > others.size()) {

									others.add(dp);

								}

								if(dataList.size() < others.size()) {

									others.remove(0);

								}
							}
							else {

								others.set(i, dp);

							}

							userController.addUserRect(dp);

						}

					} catch (Exception e) { 
						connected = false;
						socket = null;
						mainController.showPanel("InitFragment");
						e.printStackTrace(); 
					}

				}

			}

		}

	};

	@Override
	public void start(Stage stage) throws Exception {


		FXMLLoader loader = new FXMLLoader(getClass().getResource("view/main_view.fxml"));

		Parent parent = (Parent) loader.load();

		mainController = loader.getController();
		mainController.setMainApp(this);

		loadInitFragment();
		initController.init();

		loadUserFragment();

		Scene scene = new Scene(parent);

		stage.setScene(scene);

		stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent e) {

				if ( socket != null ) {

					connected = false;
					state = 1;

				}
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				receive = null;
				send = null;

				System.exit(0);
			}

		});


		stage.setTitle("JFX Client");
		stage.show();

	}

	private void loadInitFragment() throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("view/init_fragment_pane.fxml"));

		AnchorPane pane = (AnchorPane) loader.load();

		initController = loader.getController();
		initController.setMainApp(this);

		mainController.addPanel("InitFragment", pane);
		mainController.showPanel("InitFragment");


	}

	private void loadUserFragment() throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("view/main_user_fragment.fxml"));

		AnchorPane pane = (AnchorPane) loader.load();

		userController = loader.getController();
		userController.setMainApp(this);

		mainController.addPanel("UserFragment", pane);


	}

	public void connectToServer(String username, String ipString) {

		int port = Integer.parseInt(ipString.substring(ipString.indexOf(":") + 1));
		String ip = ipString.substring(0, ipString.indexOf(":"));
		this.username = username;

		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;

		try {
			socket = new Socket(ip, port);

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(username);

			ois = new ObjectInputStream(socket.getInputStream());
			String response = (String) ois.readObject();

			mainController.addTextAreaEntry(response);

			if(response.equals("Welcome to the Server!")) {

				mainController.showPanel("UserFragment");

				state = 0;
				connected = true;

				new Thread(send).start();
				new Thread(receive).start();

			}

		}
		catch (ConnectException e) {
			mainController.addTextAreaEntry("Server unreachable");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		launch();

	}

}
