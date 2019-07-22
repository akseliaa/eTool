package application;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Console {
	
	private final TextFlow console;

	public Console(TextFlow console) {
		this.console = console;
	}
	
	public void log(String message) {			//Log message
		Text t = new Text(message + " \n");
		print(t);
	}
	
	public void logError(String message) {		//Log error
		Text t = new Text(message + " \n");
		t.setFill(Color.RED);
		print(t);
	}
	
	public void logWarning(String message) {	//Log warning
		Text t = new Text(message + " \n");
		t.setFill(Color.ORANGE);
		print(t);
	}
	
	public void logSuccess(String message) {	//Log success
		Text t = new Text(message + " \n");
		t.setFill(Color.GREEN);
		print(t);
	}
	
	private void print(Text t) {		//Print text to the textFlow -element
		console.getChildren().add(t);
	}
	
}
