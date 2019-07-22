package application;

public class ScriptRunner {
	
	private Console console;		//Output console
	
	public ScriptRunner(Console console) {
		this.console = console;
	} 
	
	public boolean runPowerSelect(ParameterHandler handler) {		//Run power mode selection script
		try {
			run(handler.getPowerSelectCmd());
			console.logSuccess("Power select command completed");
			return true;
		} catch (Exception e) {
			console.logError("Power select command failed");
			return false;
		}
	}
	
	public boolean runModeSelect(ParameterHandler handler) {		//Run mode selection script
		try {
			run(handler.getModeSelectCmd());
			console.logSuccess("Mode select command completed");
			return true;
		} catch (Exception e) {
			console.logError("Mode select command failed");
			return false;
		}
	}
	
	public boolean runAddressRead(ParameterHandler handler) {		//Run address read script
		try {
			run(handler.getAddressReadCmd());
			console.logSuccess("Address read command completed");
			return true;
		} catch (Exception e) {
			console.logError("Address read command failed");
			return false;
		}
	}
	
	public boolean runDataRead(ParameterHandler handler) {		//Run data read script
		try {
			run(handler.getDataReadCmd());
			console.logSuccess("Data read command completed");
			return true;
		} catch (Exception e) {
			console.logError("Data read command failed");
			return false;
		}
	}
	
	private void run(String cmd[]) throws Exception {		//Run commands given in parameters
		for(String item : cmd) {
			System.out.println(item);
		}
		Runtime.getRuntime().exec(cmd);		//Execute command array
	}
}
