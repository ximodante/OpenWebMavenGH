package openadmin.util.configuration.yamlview02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import openadmin.dao.operation.DaoOperationFacadeEdu;
import openadmin.model.control.ClassName;
import openadmin.model.yamlform02.ButtonType;
import openadmin.model.yamlform02.ElementType;
import openadmin.model.yamlform02.YAction;
import openadmin.model.yamlform02.YComponent;
import openadmin.model.yamlform02.YEvent;
import openadmin.model.yamlform02.YProperty;

public class YAMLFormLoad {

	// YAML Form to read from a YAML File
	@Getter @Setter
	YAMLComponent form=null;
	
	@Setter
	private DaoOperationFacadeEdu connection = null; 	
	
	//The DB component
	@Getter
	YComponent yForm=new YComponent();
	
	// Sequence of loading a YAML File into the DB
	@Getter @Setter
	int fase=0;
	
	/*******************************************************************
	 * 1.A HELPER INFO STRUCTURE
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
	 * 1.B HELPER FOR DETECTED ERRORS
	 *******************************************************************/
	//Duplicated components
	private List<String> DupComponents= null;
	
	//Duplicated Events
	private List<String> DupEvents= null;
		
	//Duplicated Events
	private List<String> DupActions= null;
	
	//Duplicated Role Groups
	private List<String> DupRoleGroups= null;
		
	
	/*******************************************************************
	 * 2. INITIALIATION:
	 * 2.1 First load YAMLComponent form
	 * 2.2 Set Default Actions
	 * 2.3 Execute init()
	 *******************************************************************/
	/**
	 * 2.A INITIALIATION: Public methods
	 **/
	
	/**
	 * Receive default falues from YAMLDefaultValues that load data from "view/_defaultValues.yaml" 
	 * @param yDV
	 */
	public void setDefaultValues(YAMLDefaultValues yDV) {
		
		if (this.fase !=0) 
			throw new RuntimeException("Fase must be 0 when executing method 'setDefaultValues()'");
		
		if (this.form==null)
			throw new RuntimeException("YAML Form has not been loaded from YAML File when executing method 'setDefaultValues()'");
		
		this.fase=1;
		
		//1. Set default actions
		if (form.isDefaultFormActions()) 
			yDV.getFormActions().stream().forEach(e ->this.form.getActions().add(e));
		
		//1. Set default role groups
		if (form.isDefaultRoleGroups())
			yDV.getRoleGroups().stream().forEach(e ->this.form.getRoleGroups().add(e));
	}
	
	
	public void init() {
		if (this.fase!=1)
			throw new RuntimeException("Fase must be 1 when executing method 'init()'");
		
		this.fase=2;
		
		//1. Fill the helpers
		this.fillHelpers();
		
		//2. Fill the DB structure
		this.fillYForm();
	}
	
	/**
	 * Fill the helpers
	 */
	private void fillHelpers() {
		//1. Get a Hash Map of Components and fetch duplicates
		this.DupComponents=
			this.form.getComponents().stream()
				.map(e-> this.hYAMLCom.put(e.getName(), e))
				.filter(e-> e!=null)
				.map(e->e.getName())
				.collect(Collectors.toList());
		
		//2. Get a Hash Map of Events and fetch duplicates
		this.DupEvents=
			this.form.getEvents().stream()
				.map( e-> this.hYAMLEve.put(e.getParent()+"-" + e.getType().toString(), e))
				.filter(e-> e!=null)
				.map(e->e.getParent()+"-" + e.getType().toString())
				.collect(Collectors.toList());
		
		//3. Get a Hash Map of Actions and fetch duplicates
		this.DupActions=
			this.form.getActions().stream()
				.map(e-> this.hYAMLAct.put(e.getParent()+"-"+ e.getName(), e))
				.filter(e-> e!=null)
				.map(e-> e.getParent()+"-"+e.getName())
				.collect(Collectors.toList());
				
		
		//4. Get a Hash Map of Role groups and fetch duplicates
		this.DupRoleGroups=
			this.form.getRoleGroups().stream()
				.map( e-> this.hYAMLRgr.put(e.getName(), e))
				.filter(e-> e!=null)
				.map(e-> e.getName())
				.collect(Collectors.toList());
				
		
		//5. Role Names
		this.sYAMLRnm=this.form.getRoleGroups().stream()
			.flatMap( roleGroup-> roleGroup.getRoles().stream())
			.collect(Collectors.toSet());
			
	}
	
	/*******************************************************************
	 * 3. TRANSLATE INFORMATION From YAMLComponent to Component
	 * 2.1 
	 *******************************************************************/
	
	/**
	 * Convert YMLComponent general form to YComponent 
	 */
	private void fillYForm() {
		
		this.yForm.setDescription(this.form.getDescription());
		this.yForm.setType(ElementType.FORM);
		this.yForm.setName("form");
		this.yForm.setClassName(this.getClassName(this.form.getKlass()));
		this.yForm.setAttribute(null);
		
		this.yForm.setRow((byte)-1);
		this.yForm.setCol((byte)-1);
		this.yForm.setLevel((byte)0);
		this.yForm.setParent(null);
		
		this.yForm.setLstActions(this.getActions(this.yForm));
		this.yForm.setLstEvents(this.getEvents(this.yForm));
		this.yForm.setLstComponents(this.getComponents(this.form.getLines(),this.yForm));
		this.yForm.setLstProperties(this.getProperties(this.form.getProperties(),this.yForm));
	}
	
	
	/**
	 * Convert Set<YAMLAction> to List<YAction>
	 * @param myParent
	 * @return
	 */
	private List<YAction> getActions( YComponent myParent) {
		return this.form.getActions().stream()
			.filter(e -> e.getParent().equalsIgnoreCase(myParent.getName()))
			.map(e ->this.getAction(e, myParent))
			.collect(Collectors.toList());
	}	
	
	/**
	 * Convert Set<YAMLEvent> to List<YEvent>
	 * @param myParent
	 * @return
	 */
	private List<YEvent> getEvents( YComponent myParent) {
		return this.form.getEvents().stream()
			.filter(e -> e.getParent().equalsIgnoreCase(myParent.getName()))
			.map(e ->this.getEvent(e, myParent))
			.collect(Collectors.toList());
	}
	
	/**
	 * Convert Set<YMLComponent> to List<YComponent>
	 * @param ymlComponents
	 * @param myParent
	 * @return
	 */
	private List<YComponent> getComponents(List<List<String>> lines, YComponent myParent) {
		List<YComponent>lstComp=new ArrayList<>();
		
		byte row=0;
		for(List<String> myLine:lines) {
			byte col=0;
			for(String compName: myLine) {
				lstComp.add(this.getComponent(row, col++, compName, myParent));
			}
			row++;
		}
		return lstComp;
	}
	
	/**
	 * Convert Set<YMLProperty> to List<YProperty>
	 * @param ymlProperties
	 * @param myParent
	 * @return
	 */
	private List<YProperty> getProperties(Set<YAMLProperty> ymlProperties, YComponent myParent) {
		return ymlProperties.stream()
			.map(e ->this.getProperty(e, myParent))
			.collect(Collectors.toList());
	}
	
	/**
	 * Convert YAMLAction to YAction
	 * @param ymlAct
	 * @param myParent
	 * @return
	 */
	private YAction getAction (YAMLAction ymlAct, YComponent myParent) {
		
		YAction myAct =new YAction();
		
		myAct.setName(ymlAct.getName());
		myAct.setParent(myParent);
		myAct.setClassName(ymlAct.getKlass());
		myAct.setMethod(ymlAct.getMethod());
		myAct.setRefresh(ymlAct.getRefresh());
		myAct.setIcon(ymlAct.getIcon());
		
		myAct.setType(ymlAct.getType());
		myAct.setRoles(
			StringUtils.join(
				this.hYAMLRgr.get(ymlAct.getRoleGroup()),
				","));
		// Description updated by @PrePersist and @Preupdate menthod
		//myAct.setDescription(""+myParent.getId()+"-"+myAct.getName());
		
		return myAct;
	}
	
	/**
	 * Convert YMLEvent to YEvent
	 * @param ymlEve
	 * @param myParent
	 * @return
	 */
	private YEvent getEvent (YAMLEvent ymlEve, YComponent myParent) {
		
		YEvent myEve=new YEvent();
		
		myEve.setParent(myParent);
		myEve.setClassName(ymlEve.getKlass());
		myEve.setMethod(ymlEve.getMethod());
		myEve.setRefresh(ymlEve.getRefresh());
		
		myEve.setType(ymlEve.getType());
		
		// Description updated by @PrePersist and @Preupdate menthod
		//myEve.setDescription(""+myParent.getId()+"-"+myEve.getType());
		
			
		return myEve;
	}
	
	
	private YComponent getComponent (byte row, byte col, String compName, YComponent myParent) {
		
		YAMLComponent ymlComp=this.hYAMLCom.get(compName);
		YComponent myComp =new YComponent();
		
		myComp.setName(compName);
		myComp.setType(this.getCompType(compName));
		myComp.setClassName(this.getClassName(ymlComp.getKlass(), myParent));
		myComp.setAttribute(this.getAttribute(ymlComp));
		
		myComp.setRow(row);
		myComp.setCol(col);
		
		byte level=myParent.getLevel();
		myComp.setLevel(++level); 
		
		myComp.setParent(myParent);
		
		myComp.setLstActions(this.getActions(myComp));
		myComp.setLstEvents(this.getEvents(myComp));
		myComp.setLstComponents(this.getComponents(ymlComp.getLines(),myComp));
		myComp.setLstProperties(this.getProperties(ymlComp.getProperties(), myComp));
		
		// Description updated by @PrePersist and @Preupdate menthod
		//myComp.setDescription(this.form.getDescription());
		
		return myComp;
	}
	
	/**
	 * Convert YMLProperty to YProp
	 * @param ymlProp
	 * @param myParent
	 * @return
	 */
	private YProperty getProperty (YAMLProperty ymlProp, YComponent myParent) {
		
		YProperty myProp=new YProperty();
		
		myProp.setParent(myParent);
		myProp.setName(ymlProp.getName());
		myProp.setValue(ymlProp.getValue());
				
		// Description updated by @PrePersist and @Preupdate menthod
		//myProp.setDescription(""+myParent.getId()+"-"+myProp.getName());
		
			
		return myProp;
	}
	
	private ElementType getCompType(String compName) {
		ElementType myEl= null;
		switch (compName.substring(0,3)) {
			case "pn_": myEl= ElementType.PANEL;     break;
			case "gp_": myEl= ElementType.GRIDPANEL; break;
			case "tb_": myEl= ElementType.TAB;       break;
			case "tg_": myEl= ElementType.TABGROUP;  break;
			case "fl_": myEl= ElementType.FIELD;     break;
			case "for": myEl= ElementType.FORM;      break;
			default:                                 break;
			
		}
		return myEl;
	}
	/*******************************************************************
	 * 4. OBTAIN CONTROL INFORMATION
	 * 4.1 Get className
	 * 4.2 Get program 
	 *******************************************************************/
	
	
	/** 
	 * Gets a ClassName class from its description
	 * @param name
	 * @return
	 */
	private ClassName getClassName(String name) {
		
		String myStr=StringUtils.substringAfterLast(name, ".");
		System.out.println("Getting classname from " + name + "  " + myStr );
		return this.connection.findObjectDescription(new ClassName(myStr));
	}
	
	/** 
	 * Gets a ClassName class from its description.
	 * If name is null return parent's className attribute
	 * @param name
	 * @return
	 */
	private ClassName getClassName(String name, YComponent myParent) {
		if (name==null) return myParent.getClassName();
		else return getClassName(name);
	}
	/**
	 * 
	 * @param name
	 * @return
	 */
	private String getAttribute(YAMLComponent ymlComp) {
		String myAtt=ymlComp.getAttribute();
		if (myAtt==null) {
			String name=ymlComp.getName();
			if (name.substring(0, 3).equalsIgnoreCase("lf_"))
				myAtt=name.substring(3);
		}
		return myAtt;
	}
	
	/*******************************************************************
	 * 5. CHECK ERRORS
	 * 5.1 Check duplicated:  
	 * 5.1.1  Elements in lines
	 * 5.1.2  Components (and fields)
	 * 5.1.3  Actions in a component
	 * 5.1.4  Properties in a component
	 * 
	 * 5.2 Line components should exist in Components
	 * 5.3 Actions' RoleGroup should exist in RoleGroups 
	 *******************************************************************/
	public String checkErrors(boolean verbose) { 
		if (this.fase!=2)
			throw new RuntimeException("Fase must be 2 when executing method 'checkErrors()'");
		
		this.fase=3;
		String myErrors="";
		int i=0;
		
		
		
		return myErrors;
	}	
	
	private String DuplicatedComp() {
		Set<String>
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
