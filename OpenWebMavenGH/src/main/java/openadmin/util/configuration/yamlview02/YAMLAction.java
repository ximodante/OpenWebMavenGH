package openadmin.util.configuration.yamlview02;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.model.yamlform02.ButtonType;

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
	
}