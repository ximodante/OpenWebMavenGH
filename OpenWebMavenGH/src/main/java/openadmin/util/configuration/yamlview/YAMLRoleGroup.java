package openadmin.util.configuration.yamlview;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLRoleGroup implements Serializable {
	@Getter @Setter
	private String name=null; //name of the group
	
	@Getter @Setter
	private List<String> roles= null; // Name of roles of this group
	
	@Getter @Setter
	private int roleCount;
	
}
