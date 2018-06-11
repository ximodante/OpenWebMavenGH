package openadmin.util.configuration.yamlview;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.model.yamlform.EditorTypeEdu;
import openadmin.model.yamlform.EventTypeEdu;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLVwField  extends YAMLVwComponent implements IYAMLElement<String>, Serializable{
	/*
	@Getter @Setter
	private String name=null;  
	
	@Getter @Setter
	private String klass=null;  
	*/
	
	@Getter @Setter
	private String attribute=null;  
	
	@Getter @Setter
	private EditorTypeEdu editor= null; 
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
