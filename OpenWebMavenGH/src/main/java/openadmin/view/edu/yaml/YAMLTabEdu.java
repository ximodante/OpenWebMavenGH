package openadmin.view.edu.yaml;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * Tab elements (subviews)
 * @author eduard
 *
 */
@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLTabEdu  implements Serializable{  

	@Getter @Setter
	private String name=null; //tab name
	
	@Getter @Setter
	private String description=null; //Header of the tab container
		
	@Getter @Setter
	private List<String> containers = null; // Each tab has a panel or a listPanel
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
