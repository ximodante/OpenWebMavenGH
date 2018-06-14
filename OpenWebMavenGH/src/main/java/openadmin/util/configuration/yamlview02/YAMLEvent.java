package openadmin.util.configuration.yamlview02;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.model.yamlform02.EventType;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLEvent implements Serializable {
	
	@Getter @Setter
	private EventType name=null;
	
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
		
}
