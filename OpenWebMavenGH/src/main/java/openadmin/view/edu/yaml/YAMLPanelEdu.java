package openadmin.view.edu.yaml;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Panel in a View
 * A panel can has collection and we should supply the class
 * 
 * @author eduard
 *
 */
@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLPanelEdu  implements Serializable {

	@Getter @Setter
	private String name=null; //panel name
	
	@Getter @Setter
	private String description=null; //panel header
	
	@Getter @Setter
	private List<List<String>> lines= null; // lines of the panel, each line has a list of fields
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}  

}
