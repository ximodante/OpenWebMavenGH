package openadmin.view.edu.yaml;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.view.edu.EditorTypeEdu;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLFieldEdu  implements Serializable{
	@Getter @Setter
	private String name=null;  
	
	@Getter @Setter
	private EditorTypeEdu editor= null; 
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
