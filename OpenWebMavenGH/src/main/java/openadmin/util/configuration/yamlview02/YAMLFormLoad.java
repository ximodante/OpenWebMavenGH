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

import com.sun.faces.renderkit.AttributeManager.Key;

import lombok.Getter;
import lombok.Setter;
import openadmin.dao.operation.DaoOperationFacadeEdu;
import openadmin.model.control.ClassName;
import openadmin.model.control.MenuItem;
import openadmin.model.yamlform02.ButtonType;
import openadmin.model.yamlform02.ElementType;
import openadmin.model.yamlform02.YAction;
import openadmin.model.yamlform02.YComponent;
import openadmin.model.yamlform02.YEvent;
import openadmin.model.yamlform02.YProperty;
import openadmin.util.configuration.yamlview.IYAMLElement;
import openadmin.util.edu.ReflectionUtilsEdu;

public class YAMLFormLoad {

	// YAML Form to read from a YAML File
	@Getter @Setter
	YAMLComponent form=null;
	
	@Setter
	private DaoOperationFacadeEdu connection = null; 	
	
	// The DB component
	@Getter
	//YComponent yForm=new YComponent();
	YComponent yForm=null;
	
	// Sequence of loading a YAML File into the DB
	@Getter @Setter
	int fase=0;
	
	// control.MenuItem that reference this form
	List<MenuItem> lstMenuItems=new ArrayList<>();
	
	
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
	private List<String> dupComponents= null;
	
	//Duplicated Events
	private List<String> dupEvents= null;
		
	//Duplicated Events
	private List<String> dupActions= null;
	
	//Duplicated Role Groups
	private List<String> dupRoleGroups= null;
	
	//Duplicated components in a lines
	private List<String> dupCompsInLines=new ArrayList<>();
	
	//Duplicated properties for a component
	private List<String> dupProperties=new ArrayList<>();
		
		//Components that are detailed in a line and don't exist
	private List<String> notExistingComponents=new ArrayList<>();
	
	//Classes that are referenced and don't exist
	private List<String> notExistingClasses=new ArrayList<>();
	
	//Attributes not existing in a class
	private List<String>notExistingAttributes=new ArrayList<>();	
	
	//Classes and/or methods in events that don't exist
	private List<String>notExistingEventClass=new ArrayList<>();
	
	//Classes and/or methods in actions that don't exist
	private List<String>notExistingActionClass=new ArrayList<>();
		
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
		
		this.getMenuItems();
		
		//1. Fill the helpers
		this.fillHelpers();
		
		//2. Fill the DB structure
		this.fillYForm();
	}
	
	/**
	 * Get the MenuItems that can call this form
	 */
	private void getMenuItems() {
		String myStr=
			" SELECT m " +  
		    " FROM MenuItem m " + 
			" WHERE m.className.description='" + this.getSimpleName(this.form.getKlass())+ "'" +
			 "  AND m.type=4";
		this.lstMenuItems= this.connection.findObjectPersonalized2(myStr);
	}
	
	private String getSimpleName(String klass) {
		return StringUtils.substringAfterLast("."+ klass, ".");
	}
	/**
	 * Fill the helpers
	 */
	private void fillHelpers() {
		//1. Get a Hash Map of Components and fetch duplicates
		this.dupComponents=
			this.form.getComponents().stream()
				.map(e-> this.hYAMLCom.put(e.getName(), e))
				.filter(e-> e!=null)
				.map(e->e.getName())
				.collect(Collectors.toList());
		
		//2. Get a Hash Map of Events and fetch duplicates
		this.dupEvents=
			this.form.getEvents().stream()
				.map( e-> this.hYAMLEve.put(e.getParent()+"-" + e.getType().toString(), e))
				.filter(e-> e!=null)
				.map(e->e.getParent()+"-" + e.getType().toString())
				.collect(Collectors.toList());
		
		//3. Get a Hash Map of Actions and fetch duplicates
		this.dupActions=
			this.form.getActions().stream()
				.map(e-> this.hYAMLAct.put(e.getParent()+"-"+ e.getName(), e))
				//if the component existed previously then return not null
				.filter(e-> e!=null)
				.map(e-> e.getParent()+"-"+e.getName())
				.collect(Collectors.toList());
				
		
		//4. Get a Hash Map of Role groups and fetch duplicates
		this.dupRoleGroups=
			this.form.getRoleGroups().stream()
				.map( e-> this.hYAMLRgr.put(e.getName(), e))
				//if the component existed previously then return not null
				.filter(e-> e!=null)
				.map(e-> e.getName())
				.collect(Collectors.toList());
				
		
		//5. Role Names ???? the program should be considered!!!!!
		this.sYAMLRnm=this.form.getRoleGroups().stream()
			.flatMap( roleGroup-> roleGroup.getRoles().stream())
			.collect(Collectors.toSet());
		
		//6. Duplicated and not existing components through lines
		this.getErrorsInLines(new HashSet<String>(), this.getForm());
		
		//7. Duplicated properties per component
		this.getAllDuplicatedProperties();
		
		//8. Not existing classes or methods in Events
		this.getNotExistingClassesInEvents();
		
		//9. Not existing classes or methods in Actions
		this.getNotExistingClassesInActions();
				
	}
	
	// Get duplicate components from all component lines 
	private void getErrorsInLines (Set<String> linCompSet, YAMLComponent comp) {
		//For each line of the component
		for (List<String> ls: comp.getLines()) {
			//For each component name of the line
			for (String name: ls) {
				
				//Find if the component exists in the list of components
				if (linCompSet.add(name)) {
					//1 if the component not exists yet
					YAMLComponent myChildComp=hYAMLCom.get(name);
					//If the component doesn't exist in the hashmap, maybe it is a field
					// or maybe not.
					//1.1 The component doesn't exist
					if (myChildComp==null) { ???????
						String myClassName=this.getClassNameYML(myChildComp);
						if (!this.isExistingClass(myClassName) && ! this.notExistingClasses.contains(myClassName))
							this.notExistingClasses.add(myClassName);
						//1.1 if the component is a container (not a field)	
						if (this.getCompType(name)!=ElementType.FIELD) 
							this.notExistingComponents.add(name);
						//If not existing component is a field, it must be an attribute of the class
						// if not, then an error should be reported
						//1.1.2 The component is a field (if exists in the class, it is OK) 
						//1.1.2.2 If the field doesn't exist in the class	
						//1.1.2.2 If the field doesn't exist in the class
						else if (!this.isAttributeFromClass(this.getClassNameYML(myChildComp), name)) 
							this.notExistingAttributes.add(this.getClassNameYML(myChildComp)+"-"+name);
					
					//1.2 The component exists, recursively inspect its lines
					} else this.getErrorsInLines(linCompSet, myChildComp);
				
				//2. if the component existed yet, it is a duplicate 
				}else this.dupCompsInLines.add(name);
	}	}	}
	
	/**
	 * Tests if there are classes or methods in events that do not exist
	 */
	private void getNotExistingClassesInEvents() {
		for (YAMLEvent yEvent:this.form.getEvents()) {
			if (isExistingClass(yEvent.getKlass())) {
				if (! this.isMethodFromClass(yEvent.getKlass(), yEvent.getMethod())) {
					this.notExistingEventClass.add(yEvent.getKlass() +"." + yEvent.getMethod());
				}
					
			} else {
				this.notExistingEventClass.add(yEvent.getKlass());
			}
		}
	}
	
	/**
	 * Tests if there are classes or methods in actions that do not exist
	 */
	private void getNotExistingClassesInActions() {
		for (YAMLAction yAction:this.form.getActions()) {
			if (isExistingClass(yAction.getKlass())) {
				if (! this.isMethodFromClass(yAction.getKlass(), yAction.getMethod())) {
					this.notExistingActionClass.add(yAction.getKlass() +"." + yAction.getMethod());
				}
					
			} else {
				this.notExistingActionClass.add(yAction.getKlass());
			}
		}
	}
	
	/**
	 * If a field is an attribute from the class
	 * @param myClassName
	 * @param myFieldName
	 * @return
	 */
	private boolean isAttributeFromClass(String myClassName, String myFieldName) {
		String fullClassName=myClassName;
		ClassName cName=this.getClassName(myClassName);
		if (cName !=null) fullClassName=cName.getFullName();
		return ReflectionUtilsEdu.doesClassContainField(fullClassName, myFieldName);
	}
	
	private boolean isMethodFromClass(String myClassName, String myMethodName) {
		String fullClassName=myClassName;
		ClassName cName=this.getClassName(myClassName);
		if (cName !=null) fullClassName=cName.getFullName();
		return ReflectionUtilsEdu.doesClassContainMethod(fullClassName, myMethodName);
	}
	
	private boolean isExistingClass(String myClassName) {
		String fullClassName=myClassName;
		ClassName cName=this.getClassName(myClassName);
		if (cName !=null) fullClassName=cName.getFullName();
		return ReflectionUtilsEdu.doesClassExists(fullClassName);
	}
	
	/**
	 * Get duplicated properties of all components
	 */
	private void getAllDuplicatedProperties() {
		getDuplicatedProperties(this.form);
		this.hYAMLCom
			.forEach((key,value)->getDuplicatedProperties(value));
	}	
		
	/**
	 * Get duplicated properties of a component
	 * @param myComp
	 */
	private void getDuplicatedProperties(YAMLComponent myComp) {
		Set<String>myProps=new HashSet<>();
		if (myComp.getLines()!=null) {
			for (List<String> lNames: myComp.getLines()) {
				for( String name: lNames) {
					if (!myProps.add(name)) this.dupProperties.add(myComp.getName()+"-"+name);
				}
			}
		}
	}
	
	/**
	 * Gets the "klass" property if not null
	 * If it is null then return its parents "klass" property
	 * @param myComp
	 * @return
	 */
	private String getClassNameYML(YAMLComponent myComp) {
		String klass=myComp.getKlass();
		if(klass==null) klass=this.getClassNameYML(myComp.getParent());
		return klass;
	}
	
	/*******************************************************************
	 * 3. TRANSLATE INFORMATION From YAMLComponent to Component
	 * 3.1 
	 *******************************************************************/
	
	/**
	 * Convert YMLComponent general form to YComponent 
	 */
	private void fillYForm() {
		
		this.yForm=getComponent((byte)-1, (byte)-1,"form", null);
		
	}
	
	/**
	 * Convert YAML Componet to YComponent
	 * @param row
	 * @param col
	 * @param compName
	 * @param myParent
	 * @return
	 */
	private YComponent getComponent (byte row, byte col, String compName, YComponent myParent) {
		
		
		YComponent myComp =new YComponent();
		YAMLComponent ymlComp=null;
		
		myComp.setName(compName);
		myComp.setRow(row);
		myComp.setCol(col);
		byte level=(Byte) null;
		// Main Form
		if (myParent==null) {
			this.yForm=myComp;
			ymlComp=this.form;
			myComp.setDescription(this.form.getDescription());
			myComp.setType(ElementType.FORM);
			myComp.setAttribute(null);
			level=(byte) -1;
		// Child component
		} else {
			ymlComp=this.hYAMLCom.get(compName);
			myComp.setType(this.getCompType(compName));
			myComp.setAttribute(this.getAttribute(ymlComp));
			level=myParent.getLevel();
		}
				
		myComp.setClassName(this.getClassName(ymlComp.getKlass(), myParent));
		myComp.setLevel(++level); 
		myComp.setParent(myParent);
		
		myComp.setLstActions(this.getActions(myComp));
		myComp.setLstEvents(this.getEvents(myComp));
		myComp.setLstComponents(this.getComponents(ymlComp.getLines(),myComp));
		myComp.setLstProperties(this.getProperties(ymlComp.getProperties(), myComp));
		
		// Description updated by @PrePersist and @Preupdate method
		//myComp.setDescription(this.form.getDescription());
		
		return myComp;
	}
	
	/**
	 * Convert List<YAMLAction> to List<YAction>
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
	 * Convert List<YAMLEvent> to List<YEvent>
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
	 * Convert List<YMLComponent> to List<YComponent>
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
	private List<YProperty> getProperties(List<YAMLProperty> ymlProperties, YComponent myParent) {
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
		
		String myStr=StringUtils.substringAfterLast("."+name, ".");
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
			if (this.getCompType(name)==ElementType.FIELD)
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
		
		myErrors= myErrors + 
				this.withErrors(this.dupActions,            "Action"   ,         "Duplicated" , i++,verbose, myErrors) + 
				this.withErrors(this.dupComponents,         "Component",         "Duplicated" , i++,verbose, myErrors) +
				this.withErrors(this.dupCompsInLines,       "In Line Component", "Duplicated" , i++,verbose, myErrors) +
				this.withErrors(this.dupEvents,             "Event",             "Duplicated" , i++,verbose, myErrors) +
				this.withErrors(this.dupRoleGroups,         "Role Groups",       "Duplicated" , i++,verbose, myErrors) +
				
				this.withErrors(this.notExistingAttributes, "Class Attribute",   "Inexistent" , i++,verbose, myErrors) +
				this.withErrors(this.notExistingClasses,    "Class",             "Inexistent" , i++,verbose, myErrors) +
				this.withErrors(this.notExistingComponents, "Component",         "Inexistent" , i++,verbose, myErrors) +
				
				this.withErrors(this.notExistingEventClass, "Class in Event",    "Inexistent" , i++,verbose, myErrors) +
				this.withErrors(this.notExistingActionClass,"Class in Action",   "Inexistent" , i++,verbose, myErrors) +
				
				
				this.ClassNameError(i++, verbose, myErrors) +
				this.MenuItemError (i++, verbose, myErrors) + 
				this.ActionError   (i++, verbose, myErrors) + 
				this.ProgramError  (i++, verbose, myErrors);
			
		
		
		
		return myErrors;
	}	
	
	public String withErrors(List<String> myLst, String elemName, String errorType,
			int counter, boolean verbose, String myErrors ) {
		if (verbose) myErrors = myErrors +
				"\n\n" +
				"======================================================\n" +
				counter + ". " + errorType  + "s " + elemName + "s :\n" + 
				"======================================================\n";
		if (myLst!=null) 
			for(String s: myLst) 
				myErrors=myErrors + "\n" + "-> " + errorType + ": " + s;
			
		return myErrors;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
