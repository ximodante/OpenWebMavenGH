package openadmin.view.edu;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
public class TabGroupEdu extends ComponentEdu {
	@Getter @Setter
	private List<TabElementEdu> tabs  = null; //Collection of tabs
	
}
