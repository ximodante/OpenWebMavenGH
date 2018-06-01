package openadmin.model.yamlview;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class ViewEdu extends ComponentEdu {

	@Getter @Setter 
	private String rsbundle=null; // Resource Bundle Key
	
	@Getter @Setter
	private List<List<ComponentEdu>> lines= null; // Distribution of panels and tabs (and maybe fields)
	
	
}
