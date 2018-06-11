package openadmin.util.configuration.yamlview;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import openadmin.model.yamlform.ButtonType;
import openadmin.model.yamlform.ElementTypeEdu;

@SuppressWarnings("serial")
@ToString
public class YAMLVwAction  implements IYAMLElement<String>, Serializable{

	@Getter @Setter
	private String name=null; 
	
	@Getter @Setter
	private String element= "form"; 
	
	//Element level (form, tab, panel, field)
	@Getter @Setter
	private ElementTypeEdu elementType= ElementTypeEdu.FORM; 
	
	@Getter @Setter  
	private String action= null; //class.method
	
	@Getter @Setter
	private String icon= null; 
	
	@Getter @Setter 
	private ButtonType button=ButtonType.Button;
	
	// Elements to refresh with ajax (fields, tabs, panels etc)
	@Getter @Setter
	private List<String> refresh= null; 
	
	// Group of roles that are allowed to execute the action
	@Getter @Setter
	private String roleGroup=null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
