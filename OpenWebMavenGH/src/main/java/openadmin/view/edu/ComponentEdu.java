package openadmin.view.edu;

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
public class ComponentEdu implements Serializable{
	
	@Getter @Setter
	private String name=null; //
	
	@Getter @Setter
	private String description=null; //header or tooltiptext
	
	@Getter @Setter
	private String klass=null; // Class to display
	
	@Getter @Setter
	private ComponentEdu parent=null; // Parent component container
	
	@Getter @Setter
	private List<ActionEdu> lstActions=new ArrayList<ActionEdu>(); // detail of the Actions included in the tab
	
	@Getter @Setter
	private List<EventEdu> lstEvents=new ArrayList<EventEdu>(); // detail of the Events included in the tab
	
}
