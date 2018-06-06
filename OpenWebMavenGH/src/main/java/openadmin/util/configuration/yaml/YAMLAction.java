package openadmin.util.configuration.yaml;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;;

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
	


}
