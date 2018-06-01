package openadmin.model.yamlview;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class PanelEdu extends ComponentEdu{

	@Getter @Setter
	private List<List<ComponentEdu>> lines = null; // Attributes (fields) of the class to display in a row of the grid
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
