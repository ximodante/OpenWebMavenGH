package openadmin.util.configuration.yamlview;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString @NoArgsConstructor
public class YAMLDefaultValues {
	@Getter @Setter
	private List<YAMLVwAction> formActions= null;
	
	@Getter @Setter
	private List<YAMLVwRoleGroup> roleGroups= null; 

}
