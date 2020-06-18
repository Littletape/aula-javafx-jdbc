package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {
	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumId;

	@FXML
	private TableColumn<Department, String> tableColumName;

	@FXML
	private Button btNew;

	private ObservableList<Department> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event); // recebe o Palco pai
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage); // executa o modal
	}

	public void seTDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		// prepara as celulas da tablela para receber seus respectivos valores.
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// ajusta o tamanho da tabela para o tamanho da janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		// excessão caso o programador esquecer de injetar a dependencia do serviço
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		// carrega todos os itens da lista de departamento dentro da ObservableList
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);

		// carrega os itens da ObservableList dentro do <TableView>
		tableViewDepartment.setItems(obsList);
	}

	// modal do DepartmentForm
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); // Carrega a url
			Pane pane = loader.load();

			// Carrega dados da classe Department no formulario
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.updateFormData();

			Stage dialogStage = new Stage(); // cria um novo palco sobrepondo o anterior
			dialogStage.setTitle("Enter department data"); // titulo do palco modal
			dialogStage.setScene(new Scene(pane)); // cria uma nova cena no palco modal
			dialogStage.setResizable(false); // desabilita redimencionamento
			dialogStage.initOwner(parentStage); // informa o Palco que o modal ira sobrepor
			dialogStage.initModality(Modality.WINDOW_MODAL); // informa que de que tipo será este modal
			dialogStage.showAndWait(); // exibe o modal e colaca as outras cenas em espera

		} catch (IOException e) {
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
