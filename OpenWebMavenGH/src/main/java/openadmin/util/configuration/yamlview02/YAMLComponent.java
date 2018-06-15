package openadmin.util.configuration.yamlview02;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.TreeSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.util.edu.YAMLUtilsEdu;

@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YAMLComponent implements Serializable{
	
	@Getter @Setter
	private String name="form"; //To identify component
	
	@Getter @Setter
	private String description=null; // Description of the main form or unique description for the rest
	
	
	// simple package name of the class (control, vehicles, ..)
	// to get the model package  you should add "openadmin.model"  to the pack
	// to get the action package you should add "openadmin.action" to the pack
	@Getter @Setter 
	private String pack=null; 
	
	@Getter @Setter 
	private String klass=null; // Class to edit fields
	
	@Getter @Setter 
	private String attribute=null;
	
	// Name of the program from control.Program.descripton
	@Getter @Setter
	private String program=null; // Description of the program
	
	@Getter @Setter
	private String rsbundle=null; // Name of the resource bundle for i18n
	
	@Getter @Setter
	private boolean defaultFormActions=true;
	
	@Getter @Setter
	private boolean defaultRoleGroups=true;
	
	@Getter @Setter
	private String comment=null; //Comments, remarks etc. 
	
	//Distribution of components
	@Getter @Setter
	private List<List<String>> lines= null; 
	
	@Getter @Setter
	//Properties pair name value
	private Set <YAMLProperty> properties=new TreeSet<>();
	
	@Getter @Setter
	//Definition of all components of the form
	private Set <YAMLComponent> components=new TreeSet<>();
	
	@Getter @Setter
	//All the events of the form and children
	private Set <YAMLEvent> events=new TreeSet<>();
	
	@Getter @Setter
	//All the actions of the form and children
	private Set <YAMLAction> actions=new TreeSet<>();
	
	@Getter @Setter
	//All the actions of the form and children
	private Set <YAMLRoleGroup> roleGroups=new TreeSet<>();
	
	public static void main(String[] args) throws JsonProcessingException {
		
		YAMLComponent yc=null;
		InputStream iin = YAMLComponent.class.getResourceAsStream("/view/user.yaml");
		try {
			yc = YAMLUtilsEdu.YAMLFileToObject(iin, YAMLComponent.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(yc.toString());
		
	}
}
