package openadmin.util.configuration.yamlview;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
public class YAMLVwRoleGroup implements IYAMLElement<String>, Serializable {
	@Getter @Setter
	private String name=null; //name of the group
	
	@Getter @Setter
	private String description=null; //description
	
	@Getter @Setter
	private List<String> roles= null; // Name of roles of this group
	
}
