package openadmin.view.edu.yaml;

import java.io.Serializable;
import java.util.List;

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
	private List<String> panels = null; // Each tab has a panel or a listPanel
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
