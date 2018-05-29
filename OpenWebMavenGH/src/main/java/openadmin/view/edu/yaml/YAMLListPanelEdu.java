package openadmin.view.edu.yaml;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class YAMLListPanelEdu implements Serializable{

	private static final long serialVersionUID = 20180529L;

	@Getter @Setter
	private String name=null; //panel name
	
	@Getter @Setter
	private String field = null; // Attrribute that is a collection of a class
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
