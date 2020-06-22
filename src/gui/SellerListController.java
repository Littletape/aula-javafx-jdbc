package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {
	private SellerService service;

	@FXML
	private TableView<Seller> tableViewSeller;

	@FXML
	private TableColumn<Seller, Integer> tableColumId;

	@FXML
	private TableColumn<Seller, String> tableColumName;
	
	@FXML
	private TableColumn<Seller, String> tableColumEMail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumBaseSalary;

	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;

	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Seller> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event); // recebe o Palco pai
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage); // executa o modal
	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		// prepara as celulas da tabela para receber seus respectivos valores.
		tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumEMail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumBirthDate, "dd/MM/yyyy"); // formatação do tableColumBirthDate
		tableColumBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumBaseSalary, 2); // formatação do tableColumBaseSalary

		// ajusta o tamanho da tabela para o tamanho da janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		// excessão caso o programador esquecer de injetar a dependencia do serviço
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		// carrega todos os itens da lista de Vendedor dentro da ObservableList
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);

		tableViewSeller.setItems(obsList); // carrega os itens da ObservableList dentro do <TableView>

		initEditButtons(); // chama o metodo de inicialização do botão 'Edit'
		initRemoveButtons(); // chama o metodo de inicialização do botão 'Remove'
	}

	// modal do SellerForm
	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
		/*
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); // Carrega a url
			Pane pane = loader.load();

			// Carrega dados da classe Seller no formulario
			SellerFormController controller = loader.getController();
			controller.setSeller(obj);
			controller.setSellerService(new SellerService());
			controller.subscribeDataChangeListener(this); // recebe a notificação notificação do evento
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
		*/
	}

	// implementação do evento da interface DataChangeListener
	@Override
	public void onDataChange() {
		updateTableView();
	}

	// metodo para inicializar os botões 'Edit' em cada linha da tabela
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);

				if (obj == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);

				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}

		});
	}

	// metodo para inicializar os botões 'Remove' em cada linha da tabela
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	// exibe o alert de confirmação ao clicar no botão delete
	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e){
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
