package openadmin.util.configuration.yamlview;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.model.yamlview.EventTypeEdu;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLVwListPanel implements IYAMLElement<String>, Serializable{
	@Getter @Setter
	private String name=null; //panel name
	
	@Getter @Setter
	private String description=null; //Header of the list view
	
	@Getter @Setter
	private String klass=null; // Class to display
	
	@Getter @Setter
	private List<String> fields = null; // Attrributes of the class to display
	
	public static void main(String[] args) {  
		// TODO Auto-generated method stub

	}

}
