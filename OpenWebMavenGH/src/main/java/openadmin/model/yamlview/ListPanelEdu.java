package openadmin.model.yamlview;

import java.util.List; 

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
public class ListPanelEdu extends ComponentEdu {
	
	@Getter @Setter
	private List<List<FieldEdu>> lines= null; // Distribution of fields and maybe panels and tabs in rows or lines
	
}
