package openadmin.util.configuration.yamlview;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.model.yamlview.EditorTypeEdu;
import openadmin.model.yamlview.EventTypeEdu;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLVwField  implements IYAMLElement<String>,  Serializable{
	@Getter @Setter
	private String name=null;  
	
	@Getter @Setter
	private EditorTypeEdu editor= null; 
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
