package view;

import java.util.function.UnaryOperator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import main.Main;
import service.ObjectService;
import model.Object;

public class HomePage {
	
	private Stage stage;
	private BorderPane root = new BorderPane();
	private GridPane gp = new GridPane();
	private Scene scene = new Scene(root, Main.WIDTH, Main.HEIGHT);
	private Label headerLbl = new Label("Manage & View PT Pudding's Item");
	private Label codeLbl = new Label("Item's Code");
	private Label nameLbl = new Label("Item's Name");
	private Label priceLbl = new Label("Item's Price");
	private Label stockLbl = new Label("Item's Stock");
	private TextField codeTf = new TextField();
	private TextField nameTf = new TextField();
	private TextField priceTf = new TextField();
	private Spinner<Integer> stockSp = new Spinner<>(0, 1000, 0); // Asumsi stock boleh 0 dan max 1000 (jika lebih dari 1000, maka nilai stock auto 1000)
	private TableView<Object> table = new TableView<>();
	private TableColumn<Object, String> codeCol = new TableColumn<>("Item's Code");
	private TableColumn<Object, String> nameCol = new TableColumn<>("Item's Name");
	private TableColumn<Object, Integer> priceCol = new TableColumn<>("Item's Price");
	private TableColumn<Object, Integer> stockCol = new TableColumn<>("Item's Stock");
	private Button addBtn = new Button("Add");
	private Button updateBtn = new Button("Update");
	private Button deleteBtn = new Button("Delete");
	private HBox buttonBox = new HBox(addBtn, updateBtn, deleteBtn);
	private ObservableList<Object> itemList = FXCollections.observableArrayList();
	private Object selectedItem;
	
	public HomePage(Stage stage) {
		this.stage = stage;
		this.setComponent();
		this.setStyle();
		this.setTableColumns();
		this.populateTable();
		this.handleButton();
		this.handleTableListener();
		this.validationPriceAndStock();
	}
	
	@SuppressWarnings("unchecked")
	private void setComponent() {
		gp.add(headerLbl, 0, 0, 2, 1);
		gp.add(codeLbl, 0, 1);
		gp.add(codeTf, 1, 1);
		gp.add(nameLbl, 0, 2);
		gp.add(nameTf, 1, 2);
		gp.add(priceLbl, 0, 3);
		gp.add(priceTf, 1, 3);
		gp.add(stockLbl, 0, 4);
		gp.add(stockSp, 1, 4);
		gp.add(buttonBox, 0, 5, 2, 1);
		table.getColumns().addAll(codeCol, nameCol, priceCol, stockCol);
		root.setTop(gp);
		root.setCenter(table);
		stage.setScene(scene);
	}
	
	@SuppressWarnings("deprecation")
	private void setStyle() {
		root.setPadding(new Insets(20));
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(15);
		gp.setVgap(15);
		GridPane.setHalignment(headerLbl, HPos.CENTER);
		GridPane.setHalignment(buttonBox, HPos.CENTER);
		BorderPane.setMargin(table, new Insets(20, 0, 0, 0));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		stockSp.setEditable(true);
		stockSp.setMaxWidth(Double.MAX_VALUE);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(20);
		addBtn.setMinWidth(100);
		updateBtn.setMinWidth(100);
		deleteBtn.setMinWidth(100);
		headerLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16");
	}
	
	private UnaryOperator<TextFormatter.Change> getNumberFilter() {
		return change -> {
			String newText = change.getControlNewText();
			if (newText.matches("\\d*")) {
				return change;
			} else {
				return null;
			}
		};
	}
	
	private void validationPriceAndStock() {
		priceTf.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, getNumberFilter()));
		stockSp.getEditor().setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, getNumberFilter()));
	}
	
	private void populateTable() {
		itemList = ObjectService.getAllItems();
		table.setItems(itemList);
		this.clearSelection();
	}
	
	private void clearSelection() {
		codeTf.clear();
		nameTf.clear();
		priceTf.clear();
		stockSp.getValueFactory().setValue(0);
	}
	
	private void setTableColumns() {
		codeCol.setCellValueFactory(new PropertyValueFactory<Object, String>("code"));
		nameCol.setCellValueFactory(new PropertyValueFactory<Object, String>("name"));
		priceCol.setCellValueFactory(new PropertyValueFactory<Object, Integer>("price"));
		stockCol.setCellValueFactory(new PropertyValueFactory<Object, Integer>("stock"));
	}
	
	private void handleButton() {
		addBtn.setOnAction(event ->{
			String code = codeTf.getText();
			String name = nameTf.getText();
			String price = priceTf.getText();
			String stock = stockSp.getEditor().getText().trim();
			
			if (code.isEmpty() || name.isEmpty() || price.isEmpty() || stock.isEmpty()) { // Boleh bernilai 0 api tidak boleh null
				alert(AlertType.ERROR, "Error", "Validation Error", "All fields must be filled!");
				return;
			}
			
			for(Object object : itemList) {
				if (object.getCode().equals(code)) {
					alert(AlertType.ERROR, "Error", "Validation Error", "Code must be unique!");
					return;
				}
			}
			
			if (!(code.matches("PD-\\d{3}"))) {
				alert(AlertType.ERROR, "Error", "Validation Error", "Code must start with PD and followed by nums (PD-XXX)!");
				return;
			}
			
			ObjectService.save(new Object(code, name, Integer.parseInt(price), Integer.parseInt(stock)));
			this.populateTable();
			alert(AlertType.INFORMATION, "Message", "Information", "Item Successfully Added!");
		});
		
		updateBtn.setOnAction(event ->{ // Update hanya melalui select code/name (tidak bisa melalui input manual), yang bisa diubah hanya price dan stock
			if (selectedItem == null) {
				alert(AlertType.ERROR, "Error", "Validation Error", "Please select an item to update!");
				return;
			}
			
			String code = selectedItem.getCode();
			String name = selectedItem.getName();
			String price = priceTf.getText();
			String stock = stockSp.getEditor().getText().trim();
			
			if (price.isEmpty() || stock.isEmpty()) {
				alert(AlertType.ERROR, "Error", "Validation Error", "All fields must be filled!");
				return;
			}
			
			ObjectService.update(new Object(code, name, Integer.parseInt(price), Integer.parseInt(stock)));
			this.populateTable();
			alert(AlertType.INFORMATION, "Message", "Information", "Item Successfully Updated!");
		});
		
		deleteBtn.setOnAction(event ->{ // Delete hanya berdasarkan item yang diselect
			if (selectedItem == null) {
				alert(AlertType.ERROR, "Error", "Validation Error", "Please select an item to delete!");
				return;
			}
			
			ObjectService.delete(selectedItem);
			this.populateTable();
			alert(AlertType.INFORMATION, "Message", "Information", "Item Successfully Deleted!");
		});
	}
	
	private void handleTableListener() {
		table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				selectedItem = newValue;
				codeTf.setText(newValue.getCode());
				nameTf.setText(newValue.getName());
				priceTf.setText(String.valueOf(newValue.getPrice()));
				stockSp.getValueFactory().setValue(newValue.getStock());
			}
		});
	}
	
	private void alert(AlertType alertType, String title, String header, String content) {
		Alert alert = new Alert(alertType);
		alert.initOwner(stage);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
		return;
	}
	
}