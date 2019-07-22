package application;

import java.util.ArrayList;

public class ParameterHandler {
	
	private SystemVariables sv;			//Class to handle all system variables defined in configuration file
	private Console console;			//Output console
	
	private String comPort;
	private String mode;
	private String writeAddress;
	private String readAddress;
	private String highAddress;
	private String lowAddress;
	private String size;
	private String filepath;

	private String pullup;
	private String pullupState;
	private String powerState;
	
	private String param1;
	private String param2;
	private String param3;
	private String param4;
	private String param5;
	private String param6;
	
	public ParameterHandler(Console console) throws Exception {
		this.console = console;
		sv = new SystemVariables();
	}
	
	public String[] getPowerSelectCmd() {		//Create power mode select command
		String path = sv.getCommonFunction("powerSelect");		//Get power mode selection script path
		if(
			path == null ||
			comPort == null ||
			pullup == null ||
			pullupState == null ||
			powerState == null
		) {
			console.logError("Error creating power mode selection command.");
			return null;
		}
		String cmd[] = {
			"python",
			path,
			"-c",
			comPort,
			"-r",
			pullup,
			"-e",
			pullupState,
			"-p",
			powerState
		};
		return cmd;
	}
	
	public String[] getModeSelectCmd() {		//Create mode select command
		String path = sv.getCommonFunction("modeSelect");		//Get mode selections script path
		if(path == null || mode == null || comPort == null) {
			console.logError("Error creating mode selection command.");
			return null;
		}
		
		String baseArray[] = {
				"python",
				path,
				"-p",
				comPort,
				"-m",
				mode
		};
		
		switch (mode) {		//Execute commands required by mode
		case "i2c":
			validateParams(2);
			String paramArray[] = getParams(2);
			String modeArray[] = combine(baseArray, paramArray);
			return modeArray;
		default:
			console.logError("No parameters defined for mode:" + mode);
			return null;
		}
	}
	
	public String[] getAddressReadCmd() {		//Create address read command
		if (mode == null) {
			console.logError("No mode selected");
			return null;
		}
		String path = sv.getModeFunction(mode, "addressRead");		//Get mode's address read script path
		if(path == null || comPort ==  null) {
			console.logError("Error address read command.");
			return null;
		}
		String cmd[] = {
				"python",
				path,
				"-c",
				comPort
		};
		return cmd;
	}
	
	public String[] getDataReadCmd() {		//Create data read command
		if(mode == null) {
			console.logError("No mode selected");
			return null;
		}
		String path = sv.getModeFunction(mode, "dataRead");		//Get mode's data read script path
		if(path == null || comPort == null) {
			console.logError("Error creating data read command.");
			return null;
		}
		String cmd[] = {
				"python",
				path,
				"-c",
				comPort,
				"-f",
				filepath,
				"-w",
				writeAddress,
				"-r",
				readAddress,
				"-h",
				highAddress,
				"-l",
				lowAddress,
				"-s",
				size
		};
		return cmd;
	}
	
	public void setComPort(String comPort) {
		this.comPort = comPort;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public void setPullup(String pullup) {
		this.pullup = pullup;
	}
	
	public void setPullupState(String pullupState) {
		this.pullupState = pullupState;
	}
	
	public void setPowerState(String powerState) {
		this.powerState = powerState;
	}
	
	public void setParam1(String param) {
		param1 = param;
	}
	
	public void setParam2(String param) {
		param2 = param;
	}
	
	public void setParam3(String param) {
		param3 = param;
	}
	
	public void setParam4(String param) {
		param4 = param;
	}
	
	public void setParam5(String param) {
		param5 = param;
	}
	
	public void setParam6(String param) {
		param6 = param;
	}
	
	public void setFilePath(String filepath) {
		this.filepath = filepath;
	}
	
	public void setWriteAddress(String writeAddress) {
		this.writeAddress = writeAddress;
	}
	
	public void setReadAddress(String readAddress) {
		this.readAddress = readAddress;
	}
	
	public void setHighAddress(String highAddress) {
		this.highAddress = highAddress;
	}
	
	public void setLowAddress(String lowAddress) {
		this.lowAddress = lowAddress;
	}
	
	public void setSize(String size) {
		this.size = size;
	}
	
	public String getMode() {
		return mode;
	}
	
	private String[] combine(String array1[], String array2[]) {		//Combine two arrays together
		int length = array1.length + array2.length;
		String result[] = new String[length];
		System.arraycopy(array1, 0, result, 0, array1.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}
	
	private boolean validateParams(int amount) {		//Validate required amount of parameters
		String paramArray[] = {param1,param2,param3,param4,param5,param6};
		for(int i = 0; i < amount; i++) {
			if(paramArray[i] == null) {
				//Error
				return false;
			}
		}
		return true;
	}
	
	private String[] getParams(int amount) {		//Get required amount of parameters
		ArrayList<String> list = new ArrayList<String>();
		String paramArray[] = {param1,param2,param3,param4,param5,param6};
		String letters[] = {"a","b","c","d","e","f"};
		for(int i = 0; i < amount; i++) {
			list.add("-" + letters[i]);
			list.add(paramArray[i]);
		}
		return list.toArray(new String[0]);
	}
}
