package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;

	@FXML
	private MenuItem menuItemDepartment;

	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}

	@FXML
	public void onMenuItemDepartmentAction() {
		System.out.println("onMenuItemDepartmentAction");
	}

	@FXML
	public void onMenuItemAboutAction() {
		// System.out.println("onMenuItemAboutAction");
		loadView("/gui/About.fxml");
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
	}

	// Carrega a view recebendo o caminho completo (url) do fxml
	private synchronized void loadView(String absoluteName) {
		// a palavra esclusiva synchronized, garante que o processamento não seja
		// interronpido durante o multi thread
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); // Carrega a url
			VBox newVbox = loader.load(); // Informa que a tela carregada sera do tipo VBox

			Scene mainScene = Main.getMainScene(); // guarda uma referencia para a cena principal

			// percorre os conteudos dos nodos/tags <ScrollPane>, <Content> e <VBox> da MainView.fxml
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0); // guarda o <children> onde esta o <MenuBar> da MainView.fxml
			mainVBox.getChildren().clear(); // limpa os <children> da MainView.fxml
			mainVBox.getChildren().add(mainMenu); // Adiciona o conteudo na <VBox> da MainView.fxml
			mainVBox.getChildren().addAll(newVbox.getChildren()); // Adiciona o conteudo na <VBox> da MainView.fxml

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}

	}

}
