package application;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SystemVariables {
	
	private final Document document;
	
	public SystemVariables() throws Exception {
		String configPath = "src/application/config.xml";		//Configuration file path
		document = new XmlParser().parseXml(configPath);		//Parse configuration file to document
	}
	
	public String[] getWR() throws Exception {
		String addressPath = "address.xml";		//address-file path
		ArrayList<String> returnList = new ArrayList<>();
		Document adDoc = new XmlParser().parseXml(addressPath);		//Parse address-file to document
		NodeList adList = adDoc.getDocumentElement().getElementsByTagName("address");		//List containing read and write addresses
		for(int i = 0; i < adList.getLength(); i++) {		//Go throw all possible addresses given by Python script
			Element address = (Element)adList.item(i);		//Get address-element by its index
			Node writeNode = address.getElementsByTagName("write").item(0);		//Get node containing write address
			Node readNode = address.getElementsByTagName("read").item(0);		//Get node containing read address
			String write = writeNode.getTextContent();
			String read = readNode.getTextContent();
			String adString = "Write: " + write + " - Read: " + read;		//Create address string
			returnList.add(adString);		//Add address string to list
		}
		return returnList.toArray(new String[0]);		//Return all addresses
	}
	
	public String[] getModes() {		//Get all modes
		return getValues(document.getDocumentElement(), "mode");
	}
	
	public String[] getPower() {		//Get power options
		Node common = getCommon();
		Node power = getChild(common, "parameter", "power");
		return getValues(power, "option");
	}
	
	public String[] getParameters(String modeName) {		//Get mode's parameters
		Element mode = (Element) getMode(modeName);
		return getValues(mode, "parameter");
	}
	
	public String[] getOptions(String modeName, String paramName) {		//Get mode's parameter's options
		Node mode = getMode(modeName);
		Node param = getParam(mode, paramName);
		return getValues(param, "option");
	}
	
	public String getCommonFunction(String name) {		//Get common python function
		Node common = getCommon();
		Node function = getChild(common, "function", name);
		return function.getAttributes().getNamedItem("path").getNodeValue();
	}
	
	public String getModeFunction(String modeName, String funcName) {		//Get mode's python function
		Node mode = getMode(modeName);
		Node function = getChild(mode, "function", funcName);
		return function.getAttributes().getNamedItem("path").getNodeValue();
	}
	
	public String getOptionValue(String modeName, String paramName, String optionName) {		//Get option's value
		Node mode = getMode(modeName);
		Node parameter = getParam(mode, paramName);
		return getOptionValue(parameter, optionName);
	}
	
	private Node getMode(String modeName) {		//Get mode 
		NodeList modeList = document.getDocumentElement().getElementsByTagName("mode");
		for(int i = 0; i < modeList.getLength(); i++) {
			if(modeList.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(modeName)) {
				return modeList.item(i);
			}
		}
		return null;
	}
	
	private Node getChild(Node node, String tagName, String childName) {		//Get parent's child
		NodeList tagList = ((Element)node).getElementsByTagName(tagName);
		for(int i = 0; i < tagList.getLength(); i++) {
			if(tagName == null) {
				return tagList.item(0);
			}
			if(tagList.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(childName)) {
				return tagList.item(i);
			}
		}
		return null;
	}
	
	private String[] getValues(Node node, String tagName) {		//Get all values
		ArrayList<String> valueList = new ArrayList<String>();
		NodeList nodeList = ((Element) node).getElementsByTagName(tagName);
		for(int i = 0; i < nodeList.getLength(); i++) {
			String value = nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue();
			valueList.add(value);
		}
		return valueList.toArray(new String[0]);
	}
	
	private Node getCommon() {		//Get common element
		return document.getDocumentElement().getElementsByTagName("common").item(0);
	}
	
	private Node getParam(Node modeNode, String paramName) {		//Get parameter
		return getChild(modeNode, "parameter", paramName);
	}
	
	private Node getOption(Node paramNode, String optionName) {		//Get option
		return getChild(paramNode, "option", optionName);
	}
	
	private String getOptionValue(Node paramNode, String optionName) {		//Get option's value
		Node option = getOption(paramNode, optionName);
		return option.getAttributes().getNamedItem("value").getNodeValue();
	}
	
}
