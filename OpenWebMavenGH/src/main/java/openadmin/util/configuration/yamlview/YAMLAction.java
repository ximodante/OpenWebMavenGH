package openadmin.util.configuration.yamlview;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.model.yamlform.ButtonType;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLAction implements Serializable {
	
	@Getter @Setter
	private String name=null;
	
	@Getter @Setter
	private ButtonType type=ButtonType.CommandButton;
		
	@Getter @Setter
	private String parent= "form";  
		
	@Getter @Setter
	private String pack= null; 
				
	@Getter @Setter
	//Full class name with package
	private String klass= null; 
		
	@Getter @Setter
	private String method= null; 
		
	// Comma separated fields 
	@Getter @Setter
	private String refresh= null; 
	
	@Getter @Setter
	private String icon=null; //Button icon
	
	// Group of roles that can execute the action
	@Getter @Setter
	private String roleGroup= null; 
	
	// Only default actions (New, Edit, Copy Delete and Serach) are defaultActions
	@Getter @Setter
	private boolean isDefaultAction=false;
	
}
