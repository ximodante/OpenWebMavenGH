package openadmin.view.edu.yaml;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import openadmin.view.edu.ButtonType;
import openadmin.view.edu.ElementTypeEdu;

@SuppressWarnings("serial")
@ToString
public class YAMLActionEdu  implements Serializable{

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
	
	@Getter @Setter
	private List<String>roles=null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
