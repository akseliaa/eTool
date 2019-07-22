package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {
	
	
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();		//Create new loader for fxml.
		loader.setLocation(Main.class.getResource("Gui.fxml"));		//Initialize loader with fxml-file.
		
		AnchorPane root = (AnchorPane) loader.load();		//Get root element from fxml-file.
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("eTool");
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
