package openadmin.util.configuration.yamlview;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.model.yamlform.ElementTypeEdu;
import openadmin.model.yamlform.EventTypeEdu;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLVwEvent  implements IYAMLElement<EventTypeEdu>, Serializable{

	@Getter @Setter
	//private EventTypeEdu event=null; 
	private EventTypeEdu name=null;
	
	@Getter @Setter
	private String element= "form";  
	
	@Getter @Setter
	private ElementTypeEdu elementType= ElementTypeEdu.FORM; 
		
	@Getter @Setter
	private String action= null; 
	
	// Elements to refresh with ajax (fields, tabs, panels etc)
	@Getter @Setter
	private List<String> refresh= null; 
		

}
