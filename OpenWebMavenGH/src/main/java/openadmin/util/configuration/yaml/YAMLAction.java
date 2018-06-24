package openadmin.util.configuration.yaml;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.util.configuration.yamlview.YAMLRoleGroup;;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class YAMLAction implements Serializable{

	private static final long serialVersionUID = 20180204L;
	
	@Getter @Setter
	private String name =null;        // 
	
	@Getter @Setter
	private String icon =null;        // 
	
	@Getter @Setter
	private Integer group =0;        // 
	
	@Getter @Setter
	private byte type =0;            // 0:Default 1: Custom
		
	//2018/06/06 removed
	//@Getter @Setter
	//private List<String> roles= null;        // Roles
	
	//2018/06/06 added
	@Getter @Setter
	private String roleGroup= null;        // Roles
	
	//2018/06/06 added for submenus
	@Getter @Setter
	private List<String> estimatedRoles= null;        // Roles calculated from roleGroup
	
	//2018/06/22 Only for sybmenus!!!
	//@Getter @Setter
	//private List<String> estimatedRoleGroups=null;
	
}
