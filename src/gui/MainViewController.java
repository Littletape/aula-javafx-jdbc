package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;

	@FXML
	private MenuItem menuItemDepartment;

	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			// injeta dependencia ao SellerService no SellerListController
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemDepartmentAction() {
		// loadView("/gui/DepartmentList.fxml");
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			// injeta dependencia ao DepartmentService no DepartmentListController
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {
			// express�o lambda vazia
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	// Carrega a view recebendo o caminho completo (url) do fxml
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializeAction) {
		// synchronized, garante processamento n�o interronpido durante o multi thread
		// Consumer para carregar uma express�o lambda como um parametro
		// lembrando que T � um tipo generico

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); // Carrega a url
			VBox newVbox = loader.load(); // Informa que a tela carregada sera do tipo VBox

			Scene mainScene = Main.getMainScene(); // guarda uma referencia para a cena principal

			// percorre os conteudos dos nodos/tags <ScrollPane>, <Content> e <VBox> da
			// MainView.fxml
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0); // guarda o <children> onde esta o <MenuBar> da MainView.fxml
			mainVBox.getChildren().clear(); // limpa os <children> da MainView.fxml
			mainVBox.getChildren().add(mainMenu); // Adiciona o conteudo na <VBox> da MainView.fxml
			mainVBox.getChildren().addAll(newVbox.getChildren()); // Adiciona o conteudo na <VBox> da MainView.fxml

			// executa a express�o lambda que foi declarada como parametro
			T controller = loader.getController();
			initializeAction.accept(controller);

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}

	}

}
