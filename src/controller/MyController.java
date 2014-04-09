package controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;


public class MyController implements Initializable {

	PathTreeItem selectedItem;
	
	@FXML
	private Button delete;
	@FXML
	private TreeView<Path> myTree;
	@FXML
	private Button addFile;
	@FXML
	private Button addDir;
	@FXML
	private TextField textField;
	@FXML
	private TextField serverName;
	@FXML
	private TextField portNumber;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button sendButton;
	@FXML
	private Label statLabel;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		//----------Wybór folderu startowego
		//Stage stage = new Stage();
		//DirectoryChooser directoryChooser = new DirectoryChooser();
		//directoryChooser.setTitle("Open Resource File");
		//String filePath = directoryChooser.showDialog(stage).getAbsolutePath();
		//System.out.println(filePath);
		String filePath = "D:\\Drive (2011)";
		Path file = Paths.get(filePath);
		
		//---------Budowa drzewa
		PathTreeItem rootItem = null;
		try {
			rootItem = new PathTreeItem(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
        rootItem.setExpanded(true);
        myTree.setRoot(rootItem);
        
        //---------Zmiana nazwy folderu
        myTree.setCellFactory(new Callback <TreeView<Path>, TreeCell<Path>>() {
        	@Override
        	public TreeCell<Path> call(TreeView<Path> myTree) {
        		return new nameCell();
        	}
        });
        
        //--------Lapanie wybranego elementu
        myTree.getSelectionModel().selectedItemProperty().addListener( new ChangeListener() {

            @SuppressWarnings("unchecked")
			@Override
            public void changed(ObservableValue observable, Object oldValue,
                    Object newValue) {

                selectedItem = (PathTreeItem) newValue;
            }
        });
	}
	
	public void deleteObject(ActionEvent event) {
		selectedItem.delete();
	}
	
	public void addFile(ActionEvent event) {
		selectedItem.create(textField.getText(), true);
	}
	
	public void addDir(ActionEvent event) {
		selectedItem.create(textField.getText(), false);
	}
	
	public void send() {
		//SendTask task = new SendTask(serverName.getText(), portNumber.getText(), selectedItem.getFullPath());
		SendTask task = new SendTask("localhost", "112", selectedItem.getFullPath());
		Thread th = new Thread(task);
		th.setDaemon(true);
		progressBar.progressProperty().bind(task.progressProperty()); //progress bar bind
		statLabel.textProperty().bind(task.messageProperty());
		th.start();
	}
}
