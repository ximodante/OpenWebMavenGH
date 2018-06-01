package openadmin.model.yamlview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class ActionEdu implements Serializable{
	
	@Getter @Setter
	private ComponentEdu parent; // Component that has the event
		
	@Getter @Setter
	private String klass=null; //class that has the method to execute
	
	@Getter @Setter
	private String method=null; //method from the class to execute
		
	@Getter @Setter
	private List<String> lstAffectedFields=new ArrayList<String>(); // names of the affected fields
	
	@Getter @Setter
	private String icon=null; //Button icon
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
