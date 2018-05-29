package openadmin.view.edu.yaml;

import java.io.Serializable;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * Tab elements (subviews)
 * @author eduard
 *
 */
@ToString
public class YAMLTabEdu  implements Serializable{

	private static final long serialVersionUID = 20180204L;
	
	@Getter @Setter
	private String name=null; //tab name
	
	@Getter @Setter
	private String klass= null; // detail (for instance lines of a invoice)
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
