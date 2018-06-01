package openadmin.view.edu.yaml;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.model.yamlview.ElementTypeEdu;
import openadmin.model.yamlview.EventTypeEdu;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLEventEdu  implements Serializable{

	@Getter @Setter
	private EventTypeEdu event=null; 
	
	@Getter @Setter
	private String element= "form";  
	
	@Getter @Setter
	private ElementTypeEdu elementType= ElementTypeEdu.FORM; 
		
	@Getter @Setter
	private String action= null; 
	
	// Elements to refresh with ajax (fields, tabs, panels etc)
	@Getter @Setter
	private List<String> refresh= null; 
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
