package openadmin.util.configuration.yaml;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class YAMLMenuItem implements Serializable{

	private static final long serialVersionUID = 20180204L;
	
	@Getter @Setter
	private String program = "";        // 
	
	@Getter @Setter
	private String description = "";        // 
	
	@Getter @Setter
	private String icon = null;        // 
	
	@Getter @Setter
	private String className = "";        // 
	
	@Getter @Setter
	private boolean defaultActions = true;        // 
	
	@Getter @Setter
	private String type = "defaultView";          // 0=Submenu 1=action/command 2=default view, 3=custom view, 4=yaml view
	
	//@Getter @Setter 
	//private String parameter= null;             // class method to execute or YAML file of a view
	
	//@Getter @Setter
	//private List<String> roles=null;           // List of roles allowed to execute the view
	
	@Getter @Setter
	private List<YAMLAction> actions= null;        // Actions
	
	
	@Getter @Setter
	private List<YAMLMenuItem> menuItems= null;        // MenuItems
	
	// Optional, name of the roleGroup that can access a menuitem
	// Usually used only in YAMLForms
	@Getter @Setter
	private String roleGroup=null; 
	
	
	/*
	public boolean isDefaultActions() {
		if (this.defaultActions) {
			String vType = this.viewType.toLowerCase().trim();
			if (vType.equals("submenu") || vType.equals("action") )
					this.defaultActions=false;
		}
		return this.defaultActions;
	}
	*/
	
	public byte getNodeType() {
		byte myType=2; // Default view
		switch (this.type.toLowerCase()) {
			case "0":
			case "submenu":
			case "parent":	
				myType = 0;
				break;	
		
		    case "1":
			case "action":
			case "command":	
				myType = 1;
				break;
				
			case "2":
			case "default":
			case "defaultview":	
				myType = 2;
				break;
				
			case "3":
			case "custom":
			case "customview":	
				myType = 3;
				break;	
				
			case "4":
			case "yaml":
			case "yamlview":	
				myType = 4;
				break;	
			
			default:
	             throw new IllegalArgumentException("Invalid MenuItemType: " + type);	
				
		}
		return myType;
	}
	
	
	public String getClassSimpleName() {
		return StringUtils.substringAfterLast(className, ".").trim();
	}

	public String getClassPackage() {
		return StringUtils.substringBeforeLast(className, ".").trim();
	}
}
