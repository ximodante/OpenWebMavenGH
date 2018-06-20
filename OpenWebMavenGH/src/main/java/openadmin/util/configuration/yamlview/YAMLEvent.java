package openadmin.util.configuration.yamlview;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.model.yamlform.EventType;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLEvent implements Serializable {
	
	@Getter @Setter
	private EventType type=null;
	
	@Getter @Setter
	private String parent= "form";  
	
	@Getter @Setter
	private String pack= null; 
			
	// Full class name with package
	@Getter @Setter
	private String klass= null; 
	
	@Getter @Setter
	private String method= null; 
	
	// Comma separated fields 
	@Getter @Setter
	private String refresh= null; 
		
}
