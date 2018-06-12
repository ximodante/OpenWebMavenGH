package openadmin.util.configuration.yamlview;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.dao.operation.DaoOperationFacadeEdu;
import openadmin.model.Base;
import openadmin.model.control.Action;
import openadmin.model.control.ActionViewRole;
import openadmin.model.control.ClassName;
import openadmin.model.control.MenuItem;
import openadmin.model.control.Program;
import openadmin.model.control.Role;
import openadmin.model.yamlform.ElementTypeEdu;
import openadmin.model.yamlform.YVwAction;
import openadmin.model.yamlform.YVwComponent;
import openadmin.model.yamlform.YVwContainer;
import openadmin.model.yamlform.YVwEvent;
import openadmin.model.yamlform.YVwField;
import openadmin.model.yamlform.YVwForm;
import openadmin.model.yamlform.YVwListPanel;
import openadmin.model.yamlform.YVwPanel;
import openadmin.model.yamlform.YVwTabElement;
import openadmin.model.yamlform.YVwTabGroup;
import openadmin.util.edu.CollectionUtilsEdu;
import openadmin.util.edu.YAMLUtilsEdu;

/**
 * A custom view
 * @author eduard
 *
 */
@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
//public class YAMLFormLoad implements IYAMLElement<String>, Serializable{
public class YAMLVwFormLoad extends YAMLVwComponent implements Serializable{
	
	/************************************************************
	 * 1. YAML File structure
	 ************************************************************/
	
	/*
	@Getter @Setter
	private String name=null; //view name
	
	@Getter @Setter
	private String description=null; //Header of the view
	
	@Getter @Setter
	private String klass=null; // Full name of Class to display
	*/
	
	@Getter @Setter
	private String program=null; // Description of the program
	
	
	@Getter @Setter
	private String rsbundle=null; // Name of the resource bundle for i18n
	
	@Getter @Setter
	private boolean defaultFormActions=true;
	
	@Getter @Setter
	private boolean defaultRoleGroups=true;
		
	//----- 1. Distribution of components
	@Getter @Setter
	private List<List<String>> lines= null; // Distribution of panels and tabs (and maybe fields)
	
	
	//----- 2. Components
	@Getter @Setter
	private List<YAMLVwPanel> panels= null; // panels in the view
	
	@Getter @Setter
	private List<YAMLVwListPanel> listPanels= null; // panels in the view
	
	@Getter @Setter
	private List<YAMLVwTab> tabs= null;     // tabs in the view
	
	@Getter @Setter
	private List<YAMLVwField> fields= null; // all the fields in the view
	
	//----- 3. Events
	@Getter @Setter
	private List<YAMLVwEvent> formEvents= null; 
	
	@Getter @Setter
	private List<YAMLVwEvent> panelEvents= null; 
	
	@Getter @Setter
	private List<YAMLVwEvent> listPanelEvents= null; 
	
	@Getter @Setter
	private List<YAMLVwEvent> tabEvents= null; 
	
	@Getter @Setter
	private List<YAMLVwEvent> fieldEvents= null; 
	
	//----- 4. Actions
	@Getter @Setter
	private List<YAMLVwAction> formActions= null; 
	
	@Getter @Setter
	private List<YAMLVwAction> panelActions= null; 
	
	@Getter @Setter
	private List<YAMLVwAction> listPanelActions= null; 
	
	@Getter @Setter
	private List<YAMLVwAction> tabActions= null; 
	
	@Getter @Setter
	private List<YAMLVwAction> fieldActions= null; 
	
	@Getter @Setter
	private List<YAMLVwRoleGroup> roleGroups= null; 
	
	
	
	/************************************************************
	 * 2. Helper structure
	 ************************************************************/
	/**
	 * Fictitious Action View Role:
	 * It only represents an instance containing:
	 *   1. ClassName that points the component
	 *   2. Action: A null fictitious action for yamlViews
	 *   3. Role: each of the roles allowed to access this yaml window
	 *   4. MenuItem (located by ClassName and type==4)
	 * 	 
	 **/
	
	private HashMap<String,ActionViewRole> cActionViewRoles=null;
	
	private Program myProgram=new Program();
	
	private MenuItem myMenuItem=new MenuItem();
	
	private ClassName myClassName= new ClassName();
	
	private Action myAction = new Action();
	
	//Roles the program myProgram can be accesses
	private Map<String,Role> myProgramRoles = new HashMap<>();
	
	//Roles that can access this YAM VIEW
	private Map<String, Role> myRoles = new HashMap<>();
		
	//RolesGroups that can access this YAM VIEW
	private Set<String> myRoleGroups = new HashSet<>();
	
	
	
	@Setter
	private DaoOperationFacadeEdu connection = null; 	
	
	private boolean updatedDefaultValues=false;
	
	@Setter
	private YAMLDefaultValues defaultValues=null;
	
	/************************************************************
	 * 3. Persistence Structure
	 ************************************************************/
	YVwForm yView=new YVwForm();
	
	/************************************************************
	 * 4. METHODS
	 ************************************************************/
	
	/************************************************************
	 * 4.1 INIT METHODS
	 * @throws IntrospectionException 
	 * @throws RuntimeException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 ************************************************************/
	// ======================================================
	// Control classes extraction and populate to DB
	// Also delete old configuration records from DB
	// ======================================================
	
	
	public void Init() throws IllegalAccessException, InvocationTargetException, RuntimeException, IntrospectionException {
		//LocalDateTime myDate = LocalDateTime.now();
			
		/**
		//0. open connection
		LangTypeEdu langType = new LangTypeEdu();
		langType.changeMessageLog(TypeLanguages.es);
		connection = new DaoJpaEdu(firstLoadUser, "control_post", (short) 0,langType);
		
		connection.begin();
		*/
		
		
				
		// 3. Set Other default values
		this.setDefaultInfo();
		
		// 4. Create Fictitious  Action and ActionviewRole to access the menuItem previously created
		this.persistControlInfo();
				
		// 5. Transform YAML clas structure to DB structure
		this.setViewInfo();
		
		// 6. Persist 
		this.yView= this.connection.persist(this.yView);
		
		
		
		/**
		//7. Commit connection
		connection.commit();
		connection.finalize();
		*/
	}
	// Procedures
	
	/******************************************************************
	 * 0. Set the correct values to collections
	 * @throws IntrospectionException 
	 * @throws RuntimeException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * 
	 ******************************************************************/
	private void setDefaultInfo() 
		throws IllegalAccessException, InvocationTargetException, RuntimeException, IntrospectionException {
		if (!this.updatedDefaultValues) {
			if (this.defaultValues!=null) {
				// 1. Load default Form actions
				if (this.defaultFormActions) {
					if (this.formActions ==null) this.formActions=this.defaultValues.getFormActions();
						else this.formActions.addAll(this.defaultValues.getFormActions());
				}
				
				// 2. Load default Role Groups
				if (this.defaultRoleGroups) {
					if (this.roleGroups ==null) this.roleGroups=this.defaultValues.getRoleGroups();
					else this.roleGroups.addAll(this.defaultValues.getRoleGroups());
				}
			}
			
			//1. form actions and events
			String[] attrNames = {"elementType"};
			Object[] attrValues1= {ElementTypeEdu.FORM };
			CollectionUtilsEdu.updateListFields(formActions, attrNames, attrValues1);
			CollectionUtilsEdu.updateListFields(formEvents,  attrNames, attrValues1);
		
			//2. Panels actions and events
			Object[] attrValues2= {ElementTypeEdu.PANEL };
			CollectionUtilsEdu.updateListFields(panelActions, attrNames, attrValues2);
			CollectionUtilsEdu.updateListFields(panelEvents,  attrNames, attrValues2);
		
			//3. ListPanels actions and events
			Object[] attrValues3= {ElementTypeEdu.LISTPANEL };
			CollectionUtilsEdu.updateListFields(listPanelActions, attrNames, attrValues3);
			CollectionUtilsEdu.updateListFields(listPanelEvents,  attrNames, attrValues3);
				
			//4. Tabs actions and events
			Object[] attrValues4= {ElementTypeEdu.TAB };
			CollectionUtilsEdu.updateListFields(tabActions, attrNames, attrValues4);
			CollectionUtilsEdu.updateListFields(tabEvents,  attrNames, attrValues4);
		
			//5. Fields actions and events
			Object[] attrValues5= {ElementTypeEdu.FIELD };
			CollectionUtilsEdu.updateListFields(fieldActions, attrNames, attrValues5);
			CollectionUtilsEdu.updateListFields(fieldEvents,  attrNames, attrValues5);
		 
			this.setDefaultControlInfo();
			
			this.updatedDefaultValues=true;
			
		}
	}
	
	private void setDefaultControlInfo() {
		//1. Define the Classname. It must exists as it was declared in a menuitem
		String myStr=StringUtils.substringAfterLast(this.getKlass(), ".").trim();
		myClassName.setDescription(myStr);
		myClassName=this.connection.findObjectDescription(myClassName);
				
		//myStr=StringUtils.substringBeforeLast(this.getKlass(), ".").trim();
		//myClassName.setPack(myStr);
				
		//2. Define MenuItem. It must exists
		System.out.println("--------------Buscant MenuItems-------");
		myMenuItem.setDescription(myClassName.getDescription());;
		myMenuItem=this.connection.findObjectDescription(myMenuItem);
		//if not a custon form we need to look for a menu item with the same classname and type=4
		if (myMenuItem.getType()!=4) {
			myStr="SELECT m FROM MenuItem m " + 
				  "WHERE m.className.description='" + myClassName.getDescription() + "'" +
				  "  AND m.type=4";
			myMenuItem= (MenuItem) this.connection.findObjectPersonalized2(myStr).get(0);
		}
				
		//3. Define a fictitious action
		myAction.setDescription(myClassName.getDescription().trim() + "_yamlview" );
		myAction.setClassName(myClassName);
		myAction.setGrup(0);  //No matter what group is!
		myAction.setIcon("");
		myAction.setType((byte)1); //Not a default action but doesn't matter
				
		//4. Define program
		myProgram.setDescription(this.program.trim().toLowerCase());
		myProgram=this.connection.findObjectDescription(myProgram);
				
		//5. Get roles that access this program
		myStr="SELECT e FROM Role e " + 
			  "WHERE e.description LIKE'%." + myProgram.getDescription() + "'";
				
		for(Base role: this.connection.findObjectPersonalized2(myStr))
			myProgramRoles.put(StringUtils.substringBefore(role.getDescription(), "."),(Role) role);
		
		//6. Get roles that access this view
		//6.1 Get Role Groups 
		if (this.formActions !=null)      for (YAMLVwAction yA: formActions)      this.myRoleGroups.add(yA.getRoleGroup().toLowerCase());
		if (this.panelActions !=null)     for (YAMLVwAction yA: panelActions)     this.myRoleGroups.add(yA.getRoleGroup().toLowerCase());
		if (this.listPanelActions !=null) for (YAMLVwAction yA: listPanelActions) this.myRoleGroups.add(yA.getRoleGroup().toLowerCase());
		if (this.tabActions !=null)       for (YAMLVwAction yA: tabActions)       this.myRoleGroups.add(yA.getRoleGroup().toLowerCase());
		if (this.fieldActions !=null)     for (YAMLVwAction yA: fieldActions)     this.myRoleGroups.add(yA.getRoleGroup().toLowerCase());
		
		//6.2 Only get roles in role group that are allowed in this program
		if (this.roleGroups != null) 
			for (YAMLVwRoleGroup rGroup: this.roleGroups) 
				if (this.myRoleGroups.contains(rGroup.getName().trim().toLowerCase())) 
					if (rGroup.getRoles() != null) 
						for(String sRole: rGroup.getRoles())  
							if (this.myProgramRoles.containsKey(sRole) && ! this.myRoles.containsKey(sRole))  
								this.myRoles.put(sRole, myProgramRoles.get(sRole));
			
		
	}	

	/**
	 * Retrieve and save information from model.control package
	 * @param persist If true creates a new instance of Action and several of ActionViewRole
	 */
	private void persistControlInfo() {
		myAction=this.connection.persist(myAction);
		
		// 7. Generates Action view Role
		for (Role role: this.myRoles.values()) {
			ActionViewRole aVR=new ActionViewRole();
			aVR.setAction(this.myAction);
			aVR.setMenuItem(this.myMenuItem);
			aVR.setRole(role);
			aVR.setDescription("");
			aVR=this.connection.persist(aVR);
	}	}		
	
	
	
	/******************************************************************
	 * 4.2. HELPERS For getting children components information: 
	 ******************************************************************/
	
	/******************************************************************
	 * 4.2.1 HELPERS For getting FIELD information:
	 ******************************************************************/
	
	/**
	 * Get additional info form a field
	 * @param fieldName
	 * @return
	 */
	private Optional<YAMLVwField> getField(String name) {
		if (fields==null) return null;
		else return fields.stream()
				.filter(e-> e.getName().equalsIgnoreCase(name))
				.findFirst();
	}
	
	/**
	 * Return events of a field 
	 * @param fieldName
	 * @return
	 */
	private List<YAMLVwEvent> getFieldEvents (String fieldName) {
		if (fieldEvents==null) return null;
		else return fieldEvents.stream()
				.filter(e-> e.getElement().equalsIgnoreCase(fieldName))
				.collect(Collectors.toList());
	}
	/**
	 * Return actions for the field
	 * @param fieldName
	 * @return
	 */
	private List<YAMLVwAction> getFieldActions (String fieldName) {
		if (fieldActions==null) return null;
		else return fieldActions.stream()
				.filter(e-> e.getElement().equalsIgnoreCase(fieldName))
				.collect(Collectors.toList());
	}
	
	/******************************************************************
	 * 4.2.2 HELPERS for getting PANEL Info
	 ******************************************************************/
	/**
	 * Get additional info from a panel
	 * @param paneldName
	 * @return
	 */
	private Optional<YAMLVwPanel> getPanel(String name) {
		if (panels==null) return null;
		else return panels.stream()
				.filter(e-> e.getName().equalsIgnoreCase(name))
				.findFirst();
	}
	
	/**
	 * Return panel events of a panel
	 * @param panelName
	 * @return
	 */
	private List<YAMLVwEvent> getPanelEvents (String panelName) {
		if (panelEvents==null) return null;
		else return panelEvents.stream()
				.filter(e-> e.getElement().equalsIgnoreCase(panelName))
				.collect(Collectors.toList());
	}
	/**
	 * Return actions for the panel
	 * @param panelName
	 * @return
	 */
	private List<YAMLVwAction> getPanelActions (String panelName) {
		if (panelActions==null) return null;
		else return panelActions.stream()
				.filter(e-> e.getElement().equalsIgnoreCase(panelName))
				.collect(Collectors.toList());
	}
	
	/******************************************************************
	 * 4.2.3 HELPERS for getting LIST PANEL Info
	 ******************************************************************/
	/**
	 * Get additional info from a listpanel
	 * @param paneldName
	 * @return
	 */
	private Optional<YAMLVwListPanel> getListPanel(String name) {
		if (listPanels==null) return null;
		else return listPanels.stream()
				.filter(e-> e.getName().equalsIgnoreCase(name))
				.findFirst();
	}
	/**
	 * Return events of a list panel
	 * @param lpanelName
	 * @return
	 */
	private List<YAMLVwEvent> getListPanelEvents (String lPanelName) {
		if (panelEvents==null) return null;
		else return panelEvents.stream()
				.filter(e-> e.getElement().equalsIgnoreCase(lPanelName))
				.collect(Collectors.toList());
	}
	/**
	 * Return actions for the list panel
	 * @param panelName
	 * @return
	 */
	private List<YAMLVwAction> getListPanelActions (String lPanelName) {
		if (panelActions==null) return null;
		else return panelActions.stream()
				.filter(e-> e.getElement().equalsIgnoreCase(lPanelName))
				.collect(Collectors.toList());
	}
	

	/******************************************************************
	 * 4.2.4 HELPERS for getting TAB INFO
	 ******************************************************************/
	/**
	 * Get additional info from a tab group
	 * @param paneldName
	 * @return
	 */
	private Optional<YAMLVwTab> getTab(String name) {
		if (tabs==null) return null;
		else return tabs.stream()
				.filter(e-> e.getName().equalsIgnoreCase(name))
				.findFirst();
	}
	
	/**
	 * Return tab events of a tab
	 * @param fieldName
	 * @return
	 */
	private List<YAMLVwEvent> getTabEvents (String tabName) {
		if (tabEvents==null) return null;
		else return tabEvents.stream()
				.filter(e-> e.getElement().equalsIgnoreCase(tabName))
				.collect(Collectors.toList());
	}
	/**
	 * Return actions for the panel
	 * @param fieldName
	 * @return
	 */
	private List<YAMLVwAction> getTabActions (String tabName) {
		if (tabActions==null) return null;
		else return tabActions.stream()
				.filter(e-> e.getElement().equalsIgnoreCase(tabName))
				.collect(Collectors.toList());
	}
	
	
	
	
	/*****************************************************************
	 * 4.2.5 HELPERS for transformations
	 *****************************************************************/
	/**
	 * Fill DB View attributes
	 */
	private void setViewInfo() {
		this.yView.setRow((byte)-1);
		this.yView.setCol((byte)-1);
		this.yView.setName(this.getName());
		this.yView.setDescription(this.getDescription());
		this.yView.setParent(null);
		this.yView.setRsbundle(this.rsbundle);
		this.yView.setClassName(this.getClassName(this.getKlass()));
		this.yView.setLstActions(this.getActions(""));
		this.yView.setLstEvents(this.getEvents(""));
		this.yView.setChildren(this.getListComponents(this.lines, this.yView));
	}
	
	/**
	 * Gets a panel at a grip x,y position for DB persistence
	 * @param comp
	 * @param x
	 * @param y
	 * @return
	 */
	private YVwPanel getVwPanel (String comp, byte row, byte col, YVwContainer myParent) {
		YVwPanel yP= new YVwPanel();
		YAMLVwPanel ymlP=this.getPanel(comp).get();
		yP.setRow(row);
		yP.setCol(col);
		yP.setName(ymlP.getName());
		yP.setDescription(ymlP.getDescription());
		
		//yTG.setParent(this.yView); // Not necessary. JPA manages this
		yP.setParent(myParent); // Not necessary. JPA manages this
								
		if (ymlP.getKlass()==null) yP.setClassName(myParent.getClassName());
		else yP.setClassName(this.getClassName(ymlP.getKlass()));
		
		yP.setLstActions(this.getActions(comp));
		yP.setLstEvents(this.getEvents(comp));
		yP.setChildren(this.getListComponents(ymlP.getLines(), yP));
		return yP;
	}
	
	/**
	 * Gets a panel at a grip x,y position for DB persistence
	 * @param comp
	 * @param x
	 * @param y
	 * @return
	 */
	private YVwListPanel getVwListPanel (String comp, byte row, byte col, YVwContainer myParent) {
		YVwListPanel yP= new YVwListPanel();
		YAMLVwListPanel ymlP=this.getListPanel(comp).get();
		yP.setRow(row);
		yP.setCol(col);
		yP.setName(ymlP.getName());
		yP.setDescription(ymlP.getDescription());
		
		//yTG.setParent(this.yView); // Not necessary. JPA manages this
		yP.setParent(myParent); // Not necessary. JPA manages this
						
		if (ymlP.getKlass()==null) yP.setClassName(myParent.getClassName());
		else yP.setClassName(this.getClassName(ymlP.getKlass()));
		
				
		yP.setLstActions(this.getActions(comp));
		yP.setLstEvents(this.getEvents(comp));
		yP.setChildren(this.getListPanelComponents(ymlP.getFields(), myParent));
		return yP;
	}
	
	
	/**
	 * Gets a panel at a grid x,y position for DB persistence
	 * @param comp
	 * @param x
	 * @param y
	 * @return
	 */
	private YVwTabGroup getVwTabGroup (String comp, byte row, byte col, YVwContainer myParent) {
		YVwTabGroup yTG= new YVwTabGroup();
		YAMLVwTab ymlT=this.getTab(comp).get();
		yTG.setRow(row);
		yTG.setCol(col);
		yTG.setName(ymlT.getName());
		
		yTG.setDescription(ymlT.getDescription());
		
		//yTG.setParent(this.yView); // Not necessary. JPA manages this
		yTG.setParent(myParent); // Not necessary. JPA manages this
				
		if (ymlT.getKlass()==null) yTG.setClassName(myParent.getClassName());
		else yTG.setClassName(this.getClassName(ymlT.getKlass()));
		
		yTG.setLstActions(this.getActions(comp));
		yTG.setLstEvents(this.getEvents(comp));
		yTG.setChildren(this.getTabElements(ymlT.getContainers(), myParent));
		return yTG;
	}
	
	/**
	 * Gets a Field at a grid x,y position for DB persistence
	 * @param comp
	 * @param row
	 * @param col
	 * @return
	 */
	private YVwField getVwField (String comp, byte row, byte col, YVwContainer myParent) {
		YVwField yF= new YVwField();
		YAMLVwField ymlF=this.getField(comp).get();
		yF.setRow(row);
		yF.setCol(col);
		yF.setName(ymlF.getName());
		
		//yF.setParent(this.yView); // Not necessary. JPA manages this
		yF.setParent(myParent); // Not necessary. JPA manages this
		
		if (ymlF !=null && ymlF.getKlass()!=null) yF.setClassName(this.getClassName(ymlF.getKlass())); 
		else  yF.setClassName(myParent.getClassName());
		
		// When
		if (ymlF !=null & ymlF.getAttribute()!=null) yF.setAttribute(ymlF.getAttribute());
		else if (Character.isUpperCase(ymlF.getName().charAt(2))) yF.setAttribute(ymlF.getName().substring(3));
		else yF.setAttribute(ymlF.getName().substring(2)); 
			
		yF.setLstActions(this.getActions(comp));
		yF.setLstEvents(this.getEvents(comp));
		if (ymlF !=null & ymlF.getEditor()!=null) yF.setEditor(ymlF.getEditor());
		yF.setDescription(yF.getClassName().getDescription()+"."+ yF.getAttribute() +"-"+ this.getName());
		this.setModelAttributes(yF);
		return yF;
	}
	
	
	/**
	 * Return a list of components defined in Lines definition
	 * @param myLines
	 * @return
	 */
	private List<YVwComponent> getListComponents(List<List<String>> myLines, YVwContainer myParent) {
		List<YVwComponent> myComps= new ArrayList<>();
		byte row=0; byte col=0;
		for (List<String> line: myLines) {
			for (String comp: line) {
				myComps.add(this.getComponent(comp, row, col, myParent));
				col++;
			}
			row++;
			col=0;
		}
		return myComps;
	}
	
	/**
	 * Return a list of components of a List panel (collection)
	 * @param myLines
	 * @return
	 */
	private List<YVwComponent> getListPanelComponents(List<String> myLine, YVwContainer myParent) {
		List<YVwComponent> myComps= new ArrayList<>();
		byte row=0; byte col=0;
		for (String comp: myLine) {
			myComps.add(this.getComponent(comp, row, col, myParent));
			col++;
		}
		return myComps;
	}
	
	/**
	 * Return the Tab elements of a tab group
	 * @param containers
	 * @return
	 */
	private List<YVwComponent> getTabElements(List<String> containers, YVwContainer myParent) {
		List<YVwComponent> myComps= new ArrayList<>();
		byte row=0; byte col=0;
		for (String comp: containers) {
			myComps.add(this.getTabElement(comp, row, col, myParent));
			col++;
		}
		return myComps;
	}
	/**
	 * Defines the components of a Tab element
	 * @param container
	 * @param row
	 * @param col
	 * @return
	 */
	private YVwTabElement getTabElement(String container, byte row, byte col, YVwContainer myParent) {
		YVwTabElement myTab=new YVwTabElement();
		myTab.setRow(row); //Not important
		myTab.setCol(col);
		myTab.setName("tbe_"+ container);
		myTab.setDescription(myTab.getName());
		
		//yF.setParent(this.yView); // Not necessary. JPA manages this
		myTab.setParent(myParent); // Not necessary. JPA manages this
		
		//A tab element has no class!!
		//if (ymlF.getKlass()==null) yF.setClassName(myParent.getClassName());
		//else yF.setClassName(this.getClassName(ymlF.getKlass()));
		myTab.setClassName(myParent.getClassName());		
		
		//A tab element has no actions or events 
		// The child of a tab element is a container that has the events and actions
		//myTab.setLstActions(this.getActions(comp)); // Not important
		//myTab.setLstEvents(this.getEvents(comp)); // Not important
		//There is only one container into a Tab Element
		List<YVwComponent>lC=new ArrayList<>();
		lC.add(this.getComponent(container, (byte) 0, (byte)0, myTab));
		myTab.setChildren(lC);
		
		return myTab;
	}
	
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
	
	private ElementTypeEdu getElType(String comp) {
		ElementTypeEdu myType=ElementTypeEdu.FORM;
		System.out.println("Component:" + comp);
		if (comp.length()>=2) {
			String compType=comp.trim().substring(0, 2).toLowerCase();
			switch (compType) {
				case "p_":	myType = ElementTypeEdu.PANEL;     break;
				case "l_":	myType = ElementTypeEdu.LISTPANEL; break;
				case "t_":	myType = ElementTypeEdu.TAB;       break;
				case "f_":	myType = ElementTypeEdu.FIELD;     break;
				default:    myType=  ElementTypeEdu.FORM;      break;
		}	}
		return myType;
	}
	
		
	/**
	 * Get a Component at a grid position x,y
	 * @param comp
	 * @param x
	 * @param y
	 */
	private YVwComponent getComponent(String comp, byte row, byte col, YVwContainer myParent) {
		YVwComponent myComp=null;
		ElementTypeEdu elType=getElType(comp);
		switch (elType) {
        	case PANEL:	    myComp = getVwPanel    (comp,row,col, myParent); break;
        	case LISTPANEL:	myComp = getVwListPanel(comp,row,col, myParent); break;
        	case TAB:	    myComp = getVwTabGroup (comp,row,col, myParent); break;
        	case FIELD:	    myComp = getVwField    (comp,row,col, myParent); break;
        	default:
                throw new IllegalArgumentException("Invalid component type: " + elType); 
        }
		return myComp;
	}
	
	
	private List<YVwEvent> getEvents (String compName) {
		List<YAMLVwEvent> lstYamE=null;
		ElementTypeEdu elType=getElType(compName);
		switch (elType) {
			case FORM:      lstYamE=this.getFormEvents     ();         break;
			case PANEL:     lstYamE=this.getPanelEvents    (compName); break;
			case LISTPANEL: lstYamE=this.getListPanelEvents(compName); break;
			case TAB:       lstYamE=this.getTabEvents      (compName); break;
			case FIELD:     lstYamE=this.getFieldEvents    (compName); break;
			default:        throw new IllegalArgumentException("Invalid component type: " + elType);
		}
		return getEvents (lstYamE);
	}
	
	/**
	 * Convert from YAMVwEvents to YVwEvents 
	 * @param ymlEvents
	 * @return
	 */
	private List<YVwEvent> getEvents (List<YAMLVwEvent> ymlEvents ) {
		List<YVwEvent> myEvents= new ArrayList<>();
		if (ymlEvents!=null) {
			for (YAMLVwEvent ymlE: ymlEvents) {
				YVwEvent myEvent=new YVwEvent();
				String[]s=StringUtils.split(ymlE.getAction(), ".");
				myEvent.setClassName(this.getClassName(s[0]));
				myEvent.setMethod(s[1]);
				//myEvent.setParent(parent); //NOt necessary, already defined by JPA
				myEvent.setDescription(ymlE.getElementType().toString()+"-"+ymlE.getElement());
				myEvent.setLstAffectedIds(ymlE.getRefresh());
				myEvents.add(myEvent);
		}	}
		return myEvents;
		
	}
	

	private List<YVwAction> getActions (String compName) {
		List<YAMLVwAction> lstYamA=null;
		ElementTypeEdu elType=getElType(compName);
		switch (elType) {
			case FORM:      lstYamA=this.getFormActions();              break;
			case PANEL:     lstYamA=this.getPanelActions(compName);     break;
			case LISTPANEL: lstYamA=this.getListPanelActions(compName); break;
			case TAB:       lstYamA=this.getTabActions(compName);       break;
			case FIELD:     lstYamA=this.getFieldActions(compName);     break;
			default:        throw new IllegalArgumentException("Invalid component type: " + elType);
		}
		return getActions (lstYamA);
	}
	
	/**
	 * Convert from YAMVwEvents to YVwEvents 
	 * @param ymlEvents
	 * @return
	 */
	private List<YVwAction> getActions (List<YAMLVwAction> ymlActions ) {
		List<YVwAction> myActions= new ArrayList<>();
		if (ymlActions!=null) {
			for (YAMLVwAction ymlE: ymlActions) {
				YVwAction myAction=new YVwAction();
				if (ymlE.getAction()!=null) {
					String[]s=StringUtils.split(ymlE.getAction(), ".");
					myAction.setClassName(this.getClassName(s[0]));
					myAction.setMethod(s[1]);
				}
				//myAction.setParent(parent); //NOt necessary, already defined by JPA
				myAction.setDescription(ymlE.getElementType().toString()+"-"+ymlE.getElement());
				myAction.setLstAffectedIds(ymlE.getRefresh());
				myAction.setIcon(ymlE.getIcon());
				myAction.setType(ymlE.getButton());
				myAction.setName(ymlE.getName());
				myActions.add(myAction);
		}	}
		return myActions;
		
	}
	/**
	 * Assign other properties to field, got by annotations in the model
	 * @param yF
	 */
	private void setModelAttributes(YVwField yF) {
		//Gets other properties obtained by annotations etc.
	}
	
	
	/******************************************************************
	 * 10. ERROR CHECKING 
	 ******************************************************************/
	// Error detection A: Duplicates
	
	@SuppressWarnings("rawtypes")
	public String Duplicated(List<? extends IYAMLElement> myLst, String elemName,
			int counter, boolean verbose, String myErrors ) {
		if (verbose) myErrors = myErrors +
				"\n\n" +
				"======================================================\n" +
				counter + ". Duplicated " + elemName + "s :\n" + 
				"======================================================\n";
		Set<String> mySet= new HashSet<String>();
		if (myLst!=null) {
			for( IYAMLElement ymlElem: myLst) {
				String myDesc=ymlElem.getName().toString().trim().toLowerCase();
				if (mySet.add(myDesc)==false) 
					myErrors=myErrors + "\n" + "->Duplicated " + elemName + ": " + myDesc;
		}	}
		return myErrors;
	}
	
	// ClassName not found
	public String ClassNameError(int counter, boolean verbose, String myErrors) {
		if (verbose) myErrors = myErrors +
				"\n\n" +
				"======================================================\n" +
				counter + ". ClassNameErrors :\n" + 
				"======================================================\n";
		String longName=myClassName.getPack() + "." + myClassName.getDescription();
		if (! longName.equals(this.getKlass()))
			myErrors=myErrors + "\n" + "->No class matches " + this.getKlass() + "<> " + longName;
		return myErrors;
	}
	
	// MenuItem not found
	public String MenuItemError(int counter, boolean verbose, String myErrors) {
		if (verbose) myErrors = myErrors +
				"\n\n" +
				"======================================================\n" +
				counter + ". MenuItemErrors :\n" + 
				"======================================================\n";
		String sError="";
		if (this.myMenuItem == null) sError= sError + "Not found menu item for description " + this.getKlass();
		else if (+ this.myMenuItem.getType()!=4) sError= sError + "MenuItem.type<>4 ---- Menuitem.type= "+ this.myMenuItem.getType() ;
		if (sError.length()>0)
			myErrors=myErrors + "\n" + "->" + sError;
		return myErrors;
	}
	
	// Action not found
	public String ActionError(int counter, boolean verbose, String myErrors) {
		if (verbose) myErrors = myErrors +
				"\n\n" +
				"======================================================\n" +
				counter + ". ActionErrors :\n" + 
				"======================================================\n";
		String sError="";
		if (this.myAction == null) sError= sError + "Action not persisted" + this.getKlass();
		if (sError.length()>0)
			myErrors=myErrors + "\n" + "->" + sError;
		return myErrors;
	}
	
	// Program not found
		public String ProgramError(int counter, boolean verbose, String myErrors) {
		if (verbose) myErrors = myErrors +
				"\n\n" +
				"======================================================\n" +
				counter + ". ProgramErrors :\n" + 
				"======================================================\n";
		String sError="";
		if (this.myProgram == null) sError= sError + "Not found program for description " + this.program;
		if (sError.length()>0)
			myErrors=myErrors + "\n" + "->" + sError;
		return myErrors;
	}
		

	public String checkErrors(boolean verbose) 
		throws IllegalAccessException, InvocationTargetException, RuntimeException, IntrospectionException {
		String myErrors="";
		int i=0;
		
		//1. Set default values of this class
		try {
			this.setDefaultInfo();
		} catch (IllegalAccessException | InvocationTargetException | RuntimeException | IntrospectionException e) {
			myErrors=myErrors + "Exception:" + e.getMessage();
			e.printStackTrace();
		}
		this.setDefaultInfo();
		
		myErrors= myErrors + 
			this.Duplicated(this.panels           ,"Panel"             ,i++,verbose, myErrors) + 
			this.Duplicated(this.listPanels       ,"ListPanel"         ,i++,verbose, myErrors) +
			this.Duplicated(this.tabs             ,"Tab"               ,i++,verbose, myErrors) +
			this.Duplicated(this.fields           ,"Field"             ,i++,verbose, myErrors) +
			
			this.Duplicated(this.formEvents       ,"Form Event"        ,i++,verbose, myErrors) +
			this.Duplicated(this.panelEvents      ,"Panel Event"       ,i++,verbose, myErrors) +
			this.Duplicated(this.listPanelEvents  ,"List Panel Event"  ,i++,verbose, myErrors) +
			this.Duplicated(this.tabEvents        ,"Tab Event"         ,i++,verbose, myErrors) +		
			this.Duplicated(this.fieldEvents      ,"Field Event"       ,i++,verbose, myErrors) +
		
			this.Duplicated(this.formActions      ,"Form Action"       ,i++,verbose, myErrors) +
			this.Duplicated(this.panelActions     ,"Panel Action"      ,i++,verbose, myErrors) +
			this.Duplicated(this.listPanelActions ,"List Panel Action" ,i++,verbose, myErrors) +
			this.Duplicated(this.tabActions       ,"Tab Action"        ,i++,verbose, myErrors) +		
			this.Duplicated(this.fieldActions     ,"Field Action"      ,i++,verbose, myErrors) + 
			
			this.ClassNameError(i++, verbose, myErrors) +
			this.MenuItemError (i++, verbose, myErrors) + 
			this.ActionError   (i++, verbose, myErrors) + 
			this.ProgramError  (i++, verbose, myErrors);
		
		/*	
		if (verbose) myErrors= myErrors + "\n\n" + 
			"======================================================\n" +
			"15. NoEntityProgramRole:\n" + 
			"======================================================\n";
		this.NoEntityProgramRole(myErrors);
		*/	
		return myErrors;
	}	
	
		
	public static void main(String[] args) {
		
		YAMLVwFormLoad yv=null;
		//1. Read YAML File
		InputStream in = YAMLUtilsEdu.class.getResourceAsStream("/view/prova.yaml");
		try {
			yv = YAMLUtilsEdu.YAMLFileToObject(in, YAMLVwFormLoad.class);
			System.out.println(yv.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*****
		//2. Display sets
		System.out.println("Displaying Roles...........");
		for (String s1: yc.getRoleNames()) System.out.println(s1);
		
		System.out.println("Displaying Programs...........");
		for (String s1: yc.getProgramNames()) System.out.println(s1);
		
		System.out.println("Displaying ClassNames...........");
		for (String s1: yc.getClassNames()) System.out.println(s1);
		
		//3. Test Errors
		System.out.println(yc.checkErrors(true));
				
		//4.- Load Control data
		yc.Init();
		System.out.println(yc.toString());
	    *****/
	}

}
