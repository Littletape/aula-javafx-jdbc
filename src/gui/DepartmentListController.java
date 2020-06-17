package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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

}
