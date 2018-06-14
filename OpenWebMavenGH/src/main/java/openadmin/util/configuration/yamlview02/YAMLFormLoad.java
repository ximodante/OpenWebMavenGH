package openadmin.util.configuration.yamlview02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import openadmin.model.yamlform02.ElementType;
import openadmin.model.yamlform02.YComponent;

public class YAMLFormLoad {

	@Getter @Setter
	YAMLComponent form=null;
	
	/**
	 * Receive default falues from YAMLDefaultValues that load data from "view/_defaultValues.yaml" 
	 * @param yDV
	 */
	public void setDefaultValues(YAMLDefaultValues yDV) {
		//1. Set default actions
		if (form.isDefaultFormActions()) 
			yDV.getFormActions().stream().forEach(e ->this.form.getActions().add(e));
		
		//1. Set default role groups
		if (form.isDefaultRoleGroups())
			yDV.getRoleGroups().stream().forEach(e ->this.form.getRoleGroups().add(e));
	}
	
	@Getter
	YComponent yForm=new YComponent();
	
	/*******************************************************************
	 * 1. HELPER INFO STRUCTURE
	 *******************************************************************/
	//Helper to find easily all components
	private Map<String,YAMLComponent> hYAMLCom= new HashMap<>();
	
	//Helper to find easily all events
	private Map<String,YAMLEvent> hYAMLEve= new HashMap<>();
	
	//Helper to find easily all actions
	private Map<String,YAMLAction> hYAMLAct= new HashMap<>();
	
	//Helper to find easily all roleGroups
	private Map<String,YAMLRoleGroup> hYAMLRgr= new HashMap<>();
		
	//Helper to find easily all role names
	private Set<String> sYAMLRnm;
		
	/*******************************************************************
	 * 2. INITIALIATION:
	 * 2.1 First load YAMLComponent form
	 * 2.2 Set Default Actions
	 * 2.3 Execute init()
	 *******************************************************************/
	
	private void init() {
		
		//1. Fill the helpers
		this.fillHelpers();
		
		//2. Fill the DB structure
		this.fillYForm();
	}
	
	/**
	 * Fill the helpers
	 */
	private void fillHelpers() {
		//1. Components
		this.form.getComponents().stream()
			.forEach( e-> this.hYAMLCom.put(e.getName(), e));
		
		//2. Events
		this.form.getEvents().stream()
			.forEach( e-> this.hYAMLEve.put(e.getName().toString(), e));
		
		//3. Actions
		this.form.getActions().stream()
			.forEach( e-> this.hYAMLAct.put(e.getName().toString(), e));

		//4. Role groups
		this.form.getRoleGroups().stream()
			.forEach( e-> this.hYAMLRgr.put(e.getName().toString(), e));
		
		//5. Role Names
		this.sYAMLRnm=this.form.getRoleGroups().stream()
			.flatMap( roleGroup-> roleGroup.getRoles().stream())
			.collect(Collectors.toSet());
			
	}
	
	/*******************************************************************
	 * 3. TRANSLATE INFORMATION From YAMLComponent to Component
	 * 2.1 
	 *******************************************************************/
	
	
	private void fillYForm() {
		this.yForm.setDescription(this.form.getDescription());
		this.yForm.setType(ElementType.FORM);
		this.yForm.setName("form");
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
