package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import gnu.io.CommPortIdentifier;

public class Controller {
	
	private ScriptRunner sr;			//Python script runner
	ParameterHandler paramHandler;		//Class to handle all parameter options
	private SystemVariables sv;			//Class to handle all system variables defined in configuration file
	public Console console;				//Output console
	final DirectoryChooser dirChooser = new DirectoryChooser();		//Directory chooser for data file
	public File outputDirectory;		
	
    @FXML
    private URL location;
     
    @FXML
    private ResourceBundle resources;
    
    @FXML
    private AnchorPane root;
    
    @FXML
    private ChoiceBox<String> comPorts;
     
    @FXML
    private ChoiceBox<String> modes;
    
    @FXML
    private ChoiceBox<String> pullups;
    
    @FXML
    private Label param1_lab;
    
    @FXML
    private Label param2_lab;
    
    @FXML
    private Label param3_lab;
    
    @FXML
    private Label param4_lab;
    
    @FXML
    private Label param5_lab;
    
    @FXML
    private Label param6_lab;
    
    @FXML
    private ChoiceBox<String> param1_val;
    
    @FXML
    private ChoiceBox<String> param2_val;
    
    @FXML
    private ChoiceBox<String> param3_val;
    
    @FXML
    private ChoiceBox<String> param4_val;
    
    @FXML
    private ChoiceBox<String> param5_val;
    
    @FXML
    private ChoiceBox<String> param6_val;
    
    @FXML
    private RadioButton pu_state_e;
    
    @FXML
    private RadioButton pu_state_d;
    
    @FXML
    private RadioButton po_state_e;
    
    @FXML
    private RadioButton po_state_d;
    
    @FXML
    private ToggleGroup pullup_state;
    
    @FXML
    private ToggleGroup power_state;
    
    @FXML
    private TextFlow consoleArea;
    
    @FXML
    private GridPane modeSettings;
    
    @FXML
    private TextField textField;
    
    @FXML
    private TextField fileName;
    
    @FXML
    private ChoiceBox<String> addressSelect;
    
    @FXML
    private TextField highAddress;
    
    @FXML
    private TextField lowAddress;
    
    @FXML
    private TextField size;
    
    @FXML
    private ChoiceBox<String> format;
    
    public Controller() {
    }
    
    @FXML
    private void setMode() {		//Set mode
    	if(!validateCommonParameters()) {
    		console.logError("General settings setup failed");
    		return;
    	} 
    	setCommonParameters();
    	if(!validateModeParameters()) {
    		console.logError("Mode settings setup failed");
    		return;
    	}
    	setModeParameters();
    	sr.runModeSelect(paramHandler);		//Run mode select command
    }
    
    @FXML
    private void setPower() {		//Set power mode
    	if(!validateCommonParameters()) {
    		console.logError("General settings setup failed");
    		return;
    	}
    	setCommonParameters();
    	if(!validatePowerParameters()) {
    		console.logError("Power settings setup failed");
    		return;
    	}
    	setPowerParameters();
    	sr.runPowerSelect(paramHandler);	//Run power select command
    }
    
    @FXML
    private void read() {		//Read data from EEPROM
    	if(!validateCommonParameters()) {
    		console.logError("General settings setup failed");
    		return;
    	}
    	setCommonParameters();
    	if(!validateReadParameters()) {
    		console.logError("Read settings setup failed");
    		return;
    	}
    	setReadParameters();
    	sr.runDataRead(paramHandler);		//Run data read command
    }
    
    @FXML
    private void readAddress() {	//Read EEPROM read and write addresses
    	if(!validateCommonParameters()) {
    		console.logError("General settings setup failed");
    		return;
    	}
    	setCommonParameters();
    	if(!validate(paramHandler.getMode())) {
    		console.logError("Mode has not been set");
    		return;
    	}
    	sr.runAddressRead(paramHandler);	//Run address read command
    	try {
    		String list[] = sv.getWR();		//Get read and write addresses
    		addressSelect.setItems(FXCollections.observableArrayList(list));	//Set possible read and write addresses to choiceBox.
		} catch (Exception e) {
			System.out.println("failed wr");
		}
    }
    
    @FXML
    private void set() {
    	setWR();
    }
    
    @FXML
    private void openChooser() {		//Show directory chooser
    	outputDirectory = dirChooser.showDialog(root.getScene().getWindow());
    }

     
    @FXML
    private void initialize() throws Exception {		//Initialize at startup
    	console = new Console(consoleArea);		//New output console
    	sr = new ScriptRunner(console);			//New python script runner
    	
    	dirChooser.setTitle("Select output folder");	//Set directory chooser's title
    	dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));	//Define directory choosers default directory
    	
    	paramHandler = new ParameterHandler(console);	//New parameter handler
    	
    	outputDirectory = null;
    	
    	try {
			sv = new SystemVariables();
		} catch (Exception e) {
			console.logError("Configuration file was not found");
		}
    	
    	updateComPorts();		//Get available COM ports
    	modeSettings.setVisible(false);		//Hide mode settings because mode is not selected
    	
    	modes.setItems(FXCollections.observableArrayList(sv.getModes()));	//Set modes defined in configuration file to choiceBox
    	modes.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {		//Add listener to listen mode type changes
    		@Override
    	      public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
    	        String mode = modes.getItems().get((Integer) number2);
    	        updateModeParameters(mode);
    	      }
		});
    	
    	String formatList[] = {"hex","iHex","BOTH"};		//Output file types
    	format.setItems(FXCollections.observableArrayList(formatList));		//Set output file types to choiceBox
    	
    	pullups.setItems(FXCollections.observableArrayList(sv.getPower()));		//Set pullup options to choiceBox
    	
    	console.log("setup ready");
    }
    
    private void setReadParameters() {		//Set chosen read parameters
    	if(validateReadParameters()) {		//Check if parameters are valid
    		String givenName = fileName.getText();
        	String directoryPath = outputDirectory.getAbsolutePath();
        	String wrAddress = addressSelect.getValue();
        	String hAddress = highAddress.getText();
        	String lAddress = lowAddress.getText();
        	String rSize = size.getText();
        	String fileFormat = format.getValue();
        	
        	String[] wrSplit = wrAddress.split("-");		//Split address
        	String wAddress = null;
        	String rAddress = null;
        	for(String item : wrSplit) {
        		if(item.contains("Write")) {
        			wAddress = item.split(":")[1].trim();	//Set write address
        		} else if(item.contains("Read")) {
        			rAddress = item.split(":")[1].trim();	//Set read address
        		}
        	}
        	
        	if(!validate(wAddress)) {		//Validate write address
        		console.logError("Write address is invalid");
        		return;
        	} else if(!validate(rAddress)) {		//Validate read address
        		console.logError("Read address is invalid");
        		return;
        	}
        	
        	String path = directoryPath + "\\" + givenName + ".hex";		//Output path
        	
        	paramHandler.setFilePath(path);
        	paramHandler.setReadAddress(rAddress);
        	paramHandler.setWriteAddress(wAddress);
        	paramHandler.setLowAddress(lAddress);
        	paramHandler.setHighAddress(hAddress);
        	paramHandler.setSize(rSize);
    	} else {
    		console.log("Read setting are invalid");
    	}
    }
    
    private void setWR() {		//Set addresses
    	try {
			String list[] = sv.getWR();		//Get all possible WR-addresses
			addressSelect.setItems(FXCollections.observableArrayList(list));		//set addresses to choiceBox
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private boolean validateReadParameters() {		//Validate parameters needed for reading
    	boolean fail = false;
		if(!validate(fileName.getText())) {
    		fail = true;
    		console.logWarning("Filename is empty");
		}
    	
    	try {
			if(!validate(outputDirectory.getAbsolutePath())) {
    		fail = true;
    		console.logWarning("Output directory is not valid");
			}
		} catch (Exception e) {
			fail = true;
    		console.logWarning("Output directory is not valid");
		}

		if(!validate(addressSelect.getValue())) {
			fail = true;
			console.logWarning("W/R address is not selected");
		}
		
		if(!validate(highAddress.getText())) {
			fail = true;
			console.logWarning("High address is empty");
		}
			
		if(!validate(lowAddress.getText())) {
    		fail = true;
    		console.logWarning("Low address is empty");
    	}

		if(!validate(size.getText())) {
    		fail = true;
    		console.logWarning("Reading size is empty");
		}

    	
		if(!validate(format.getValue())) {
    		fail = true;
    		console.logWarning("Format is not valid");
		}

    	if(fail) {
    		return false;
    	}
    	return true;
    }
    
    private boolean validate(String value) {		//Validate given parameter
    	try {
    		if(value == null || value.isEmpty()) {
        		return false;
        	}
		} catch (Exception e) {
			return true;
		}
    	return true;
    }
    
    private void setCommonParameters() {		//Set common parameters
    	paramHandler.setComPort(comPorts.getValue());		//Set chosen COM port
    	paramHandler.setMode(modes.getValue());				//Set chosen mode
    }
    
    private void setPowerParameters() {			//Set power parameters
	paramHandler.setPullup(pullups.getValue());
	    	
    	if(pu_state_e.isSelected()) {				//Check if pullup enabled RadioButton is selected
    		paramHandler.setPullupState("on");
    	} else if (pu_state_d.isSelected()) {
    		paramHandler.setPullupState("off");		//Check if pullup disabled RadioButton is selected
    	}
    	
    	if(po_state_e.isSelected()) {				//Check if power enabled RadioButton is selected
    		paramHandler.setPowerState("on");
    	} else if (po_state_d.isSelected()) {
    		paramHandler.setPowerState("off");		//Check if power disabled RadioButton is selected
    	}
    }
    
    private boolean validateCommonParameters() {	//Validate common parameters
    	boolean fail = false;
    	if(!validate(comPorts.getValue())) {
    		fail = true;
    		console.logWarning("Selected comport is invalid");
    	}
    	if(!validate(modes.getValue())) {
    		fail = true;
    		console.logWarning("Mode is invalid");
    	}
    	if(fail) {
    		return false;
    	}
    	return true;
    }
    
    private boolean validatePowerParameters() {		//Validate power parameters
    	boolean fail = false;
    	if(!validate(pullups.getValue())) {
    		fail = true;
    		console.logWarning("Pullup is invalid");
    	}
    	if(pullup_state.getSelectedToggle() == null) {
    		fail = true;
    		console.logWarning("Pullup state is not selected");
    	}
    	if(power_state.getSelectedToggle() == null) {
    		fail = true;
    		console.logWarning("Power state is not selected");
    	}
    	if(fail) {
    		return false;
    	}
    	return true;
    }
    
    private boolean validateModeParameters() {		//Validate mode parameters
    	boolean fail = false;
    	int params = 0;		//Parameter count
    	switch (modes.getValue()) {		//Set needed amount of parameters
			case "i2c":
				params = 2;
				break;
			default:
				break;
		}
    	if(!validate(param1_val.getValue()) && params >=1) {
    		fail = true;
    		console.logWarning(param1_lab + " is empty");
    	}
    	if(!validate(param2_val.getValue()) && params >= 2) {
    		fail = true;
    		console.logWarning(param2_lab + " is empty");
    	}
    	if(!validate(param3_val.getValue()) && params >= 3) {
    		fail = true;
    		console.logWarning(param3_lab + " is empty");
    	}
    	if(!validate(param4_val.getValue()) && params >= 4) {
    		fail = true;
    		console.logWarning(param4_lab + " is empty");
    	}
    	if(!validate(param5_val.getValue()) && params >= 5) {
    		fail = true;
    		console.logWarning(param5_lab + " is empty");
    	}
    	if(!validate(param6_val.getValue()) && params >= 6) {
    		fail = true;
    		console.logWarning(param6_lab + " is empty");
    	}
    	
    	if(fail) {
    		return false;
    	}
    	return true;
    }
    
    private void setModeParameters() {		//Set all parameters
    	paramHandler.setParam1(getParamValue(1));
    	paramHandler.setParam2(getParamValue(2));
    	paramHandler.setParam3(getParamValue(3));
    	paramHandler.setParam4(getParamValue(4));
    	paramHandler.setParam5(getParamValue(5));
    	paramHandler.setParam6(getParamValue(6));
    }
    
    private String getParamValue(int paramNumber) {		//Get parameter value by it's number
    	int index = paramNumber - 1;
    	Label labelList[] = {param1_lab,param2_lab,param3_lab,param4_lab,param5_lab,param6_lab};
    	ChoiceBox boxList[] = {param1_val,param2_val,param3_val,param4_val,param5_val,param6_val};
    	try {
    		return sv.getOptionValue(paramHandler.getMode(), labelList[index].getText(), (String) boxList[index].getValue());	//Get parameter value
		} catch (Exception e) {
			return null;
		}
    }
    
    private void updateModeParameters(String mode) {	//Update mode parameters
    	modeSettings.setVisible(true);		//Set mode parameters visible
    	Label labelList[] = {param1_lab,param2_lab,param3_lab,param4_lab,param5_lab,param6_lab};
    	ChoiceBox boxList[] = {param1_val,param2_val,param3_val,param4_val,param5_val,param6_val};
    	String parameters[] = sv.getParameters(mode);	//Get mode's parameters
    	for(int i = 0; i < parameters.length; i++) {	//Go throw mode's parameters
    		String param = parameters[i];
    		
    		Label label = labelList[i];
    		label.setVisible(true);		//Set parameter's label visible
    		label.setText(param);		//Set parameter's text
    		
    		String options[] = sv.getOptions(mode, param);		//Get parameter's options
			ChoiceBox<String> cBox = boxList[i];
    		cBox.setVisible(true);		//Set choiceBox visible
    		cBox.setItems(FXCollections.observableArrayList(options));	//Set options to choiceBox
    	}
    	for(int i = parameters.length; i < labelList.length; i++) {		//Hide unused parameters
    		labelList[i].setVisible(false);
    		boxList[i].setVisible(false);
    	}
    }
     
    @FXML
    private void updateComPorts() {		//Update all unused COM ports
    	Enumeration pIList = CommPortIdentifier.getPortIdentifiers();		//Get ports
    	ArrayList<String> pList = new ArrayList<>();
    	while(pIList.hasMoreElements()) {	//Add ports to the port-list
    		CommPortIdentifier cpi = (CommPortIdentifier) pIList.nextElement();
    		pList.add(cpi.getName());
    	}
        comPorts.setItems(FXCollections.observableArrayList(pList));	//Set ports to choiceBox
    }
    
}
