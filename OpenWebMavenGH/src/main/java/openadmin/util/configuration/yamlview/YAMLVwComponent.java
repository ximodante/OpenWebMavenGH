package openadmin.util.configuration.yamlview;

import lombok.Getter;
import lombok.Setter;

public class YAMLVwComponent {
	@Getter @Setter
	private String name=null; //panel name
	
	@Getter @Setter
	private String description=null; //panel header
	
	@Getter @Setter
	private String klass=null; // ClassName 
	
}
