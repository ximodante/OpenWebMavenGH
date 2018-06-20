package openadmin.util.configuration.yamlview;


import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLProperty  implements Serializable{
	@Getter @Setter
	private String name=null; //property name
	
	@Getter @Setter
	private String value=null; //values
	
}
