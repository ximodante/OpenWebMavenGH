package openadmin.util.configuration.yamlview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString @NoArgsConstructor
public class YAMLDefaultValues {
	@Getter @Setter
	private Set<YAMLAction> formActions= new HashSet<>();
	
	@Getter @Setter
	private Set<YAMLRoleGroup> roleGroups= new HashSet<>(); 

}
