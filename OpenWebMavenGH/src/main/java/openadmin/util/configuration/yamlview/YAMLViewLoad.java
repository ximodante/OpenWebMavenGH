package openadmin.util.configuration.yamlview;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.dao.operation.DaoOperationFacadeEdu;
import openadmin.model.control.Access;
import openadmin.model.control.Action;
import openadmin.model.control.ActionViewRole;
import openadmin.model.control.ClassName;
import openadmin.model.control.EntityAdm;
import openadmin.model.control.MenuItem;
import openadmin.model.control.Program;
import openadmin.model.control.Role;
import openadmin.model.control.User;
import openadmin.model.yamlview.ElementTypeEdu;
import openadmin.model.yamlview.YVwAction;
import openadmin.model.yamlview.YVwComponent;
import openadmin.model.yamlview.YVwEvent;
import openadmin.model.yamlview.YVwField;
import openadmin.model.yamlview.YVwListPanel;
import openadmin.model.yamlview.YVwPanel;
import openadmin.model.yamlview.YVwTabElement;
import openadmin.model.yamlview.YVwTabGroup;
import openadmin.model.yamlview.YVwView;
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
public class YAMLViewLoad implements IYAMLElement<String>, Serializable{
	
	/************************************************************
	 * 1. YAML File structure
	 ************************************************************/
	
	@Getter @Setter
	private String name=null; //view name
	
	@Getter @Setter
	private String description=null; //Header of the view
	
	@Getter @Setter
	private String klass=null; // Full name ofClass to display
	
	@Getter @Setter
	private String rsbundle=null; // Name of the resource bundle for i18n
		
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
	@Setter
	private DaoOperationFacadeEdu connection = null; 	
	
	/************************************************************
	 * 3. Persistence Structure
	 ************************************************************/
	YVwView yView=new YVwView();
	
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
		LocalDateTime myDate = LocalDateTime.now();
			
		/**
		//0. open connection
		LangTypeEdu langType = new LangTypeEdu();
		langType.changeMessageLog(TypeLanguages.es);
		connection = new DaoJpaEdu(firstLoadUser, "control_post", (short) 0,langType);
		
		connection.begin();
		*/
		// 1. Set default values
		this.setDefaultInfo();
		
		// 2. Set view info
		this.setViewInfo();
		
		// 3. for each line let's get the components
		
				
		/*
		//2. Roles
			this.cRoles=this.getControlRoles();
			
			//3. Entity & Program & Access
			this.EntityAdmProgramAccess();
			
			
			//4. MenuItems & ClassName & Action
			this.MenuItemsClassNameActions();
			
		*/	
					
			//5. Delete old configuration
		    /*
			connection.deleteOlderThan(ActionViewRole.class, myDate);
			connection.deleteOlderThan(Access.class        , myDate);
			connection.deleteOlderThan(Program.class       , myDate);
			connection.deleteOlderThan(User.class          , myDate);
			connection.deleteOlderThan(EntityAdm.class     , myDate);
			connection.deleteOlderThan(Role.class          , myDate);
			connection.deleteOlderThan(Action.class        , myDate);
			connection.deleteOlderThan(MenuItem.class      , myDate);
			connection.deleteOlderThan(ClassName.class     , myDate);
			*/
			
			/**
			
			//5. Commit connection
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
	private void setDefaultInfo() throws IllegalAccessException, InvocationTargetException, RuntimeException, IntrospectionException {
		
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
		 
	}
	
	
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
		return fields.stream()
				.filter(e-> e.getName().equalsIgnoreCase(name))
				.findFirst();
	}
	
	/**
	 * Return events of a field 
	 * @param fieldName
	 * @return
	 */
	private List<YAMLVwEvent> getFieldEvents (String fieldName) {
		return fieldEvents.stream()
				.filter(e-> e.getElement().equalsIgnoreCase(fieldName))
				.collect(Collectors.toList());
	}
	/**
	 * Return actions for the field
	 * @param fieldName
	 * @return
	 */
	private List<YAMLVwAction> getFieldActions (String fieldName) {
		return fieldActions.stream()
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
		return panels.stream()
				.filter(e-> e.getName().equalsIgnoreCase(name))
				.findFirst();
	}
	
	/**
	 * Return panel events of a panel
	 * @param panelName
	 * @return
	 */
	private List<YAMLVwEvent> getPanelEvents (String panelName) {
		return panelEvents.stream()
				.filter(e-> e.getElement().equalsIgnoreCase(panelName))
				.collect(Collectors.toList());
	}
	/**
	 * Return actions for the panel
	 * @param panelName
	 * @return
	 */
	private List<YAMLVwAction> getPanelActions (String panelName) {
		return panelActions.stream()
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
		return listPanels.stream()
				.filter(e-> e.getName().equalsIgnoreCase(name))
				.findFirst();
	}
	/**
	 * Return events of a list panel
	 * @param lpanelName
	 * @return
	 */
	private List<YAMLVwEvent> getListPanelEvents (String lPanelName) {
		return panelEvents.stream()
				.filter(e-> e.getElement().equalsIgnoreCase(lPanelName))
				.collect(Collectors.toList());
	}
	/**
	 * Return actions for the list panel
	 * @param panelName
	 * @return
	 */
	private List<YAMLVwAction> getListPanelActions (String lPanelName) {
		return panelActions.stream()
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
		return tabs.stream()
				.filter(e-> e.getName().equalsIgnoreCase(name))
				.findFirst();
	}
	
	/**
	 * Return tab events of a tab
	 * @param fieldName
	 * @return
	 */
	private List<YAMLVwEvent> getTabEvents (String tabName) {
		return tabEvents.stream()
				.filter(e-> e.getElement().equalsIgnoreCase(tabName))
				.collect(Collectors.toList());
	}
	/**
	 * Return actions for the panel
	 * @param fieldName
	 * @return
	 */
	private List<YAMLVwAction> getTabActions (String tabName) {
		return tabActions.stream()
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
		this.yView.setName(this.name);
		this.yView.setDescription(this.description);
		this.yView.setParent(null);
		this.yView.setRsbundle(this.rsbundle);
		this.yView.setClassName(this.getClassName(this.klass));
		this.yView.setLstActions(this.getActions(""));
		this.yView.setLstEvents(this.getEvents(""));
		this.yView.setChildren(this.getListComponents(this.lines));
	}
	
	/**
	 * Gets a panel at a grip x,y position for DB persistence
	 * @param comp
	 * @param x
	 * @param y
	 * @return
	 */
	private YVwPanel getVwPanel (String comp, byte row, byte col) {
		YVwPanel yP= new YVwPanel();
		YAMLVwPanel ymlP=this.getPanel(comp).get();
		yP.setRow(row);
		yP.setCol(col);
		yP.setName(ymlP.getName());
		yP.setDescription(ymlP.getDescription());
		//yP.setParent(this.yView); // Not necessary. JPA manages this
		yP.setClassName(this.yView.getClassName());
		yP.setLstActions(this.getActions(comp));
		yP.setLstEvents(this.getEvents(comp));
		yP.setChildren(this.getListComponents(ymlP.getLines()));
		return yP;
	}
	
	/**
	 * Gets a panel at a grip x,y position for DB persistence
	 * @param comp
	 * @param x
	 * @param y
	 * @return
	 */
	private YVwListPanel getVwListPanel (String comp, byte row, byte col) {
		YVwListPanel yP= new YVwListPanel();
		YAMLVwListPanel ymlP=this.getListPanel(comp).get();
		yP.setRow(row);
		yP.setCol(col);
		yP.setName(ymlP.getName());
		yP.setDescription(ymlP.getDescription());
		//yP.setParent(this.yView); // Not necessary. JPA manages this
		yP.setClassName(this.getClassName(ymlP.getKlass()));
		yP.setLstActions(this.getActions(comp));
		yP.setLstEvents(this.getEvents(comp));
		yP.setChildren(this.getListPanelComponents(ymlP.getFields()));
		return yP;
	}
	
	
	/**
	 * Gets a panel at a grid x,y position for DB persistence
	 * @param comp
	 * @param x
	 * @param y
	 * @return
	 */
	private YVwTabGroup getVwTabGroup (String comp, byte row, byte col) {
		YVwTabGroup yTG= new YVwTabGroup();
		YAMLVwTab ymlT=this.getTab(comp).get();
		yTG.setRow(row);
		yTG.setCol(col);
		yTG.setName(ymlT.getName());
		yTG.setDescription(ymlT.getDescription());
		//yTG.setParent(this.yView); // Not necessary. JPA manages this
		yTG.setClassName(this.yView.getClassName());
		yTG.setLstActions(this.getActions(comp));
		yTG.setLstEvents(this.getEvents(comp));
		yTG.setChildren(this.getTabElements(ymlT.getContainers()));
		return yTG;
	}
	
	/**
	 * Gets a Field at a grid x,y position for DB persistence
	 * @param comp
	 * @param row
	 * @param col
	 * @return
	 */
	private YVwField getVwField (String comp, byte row, byte col) {
		YVwField yF= new YVwField();
		YAMLVwField ymlF=this.getField(comp).get();
		yF.setRow(row);
		yF.setCol(col);
		yF.setName(ymlF.getName());
		//yF.setParent(this.yView); // Not necessary. JPA manages this
		
		if (ymlF.getKlass()==null)
			yF.setClassName(this.yView.getClassName());
		else 
			yF.setClassName(this.getClassName(ymlF.getKlass()));
		
		if (ymlF.getAttribute()==null)
			yF.setAttribute(ymlF.getName().substring(2));
		else 
			yF.setAttribute(ymlF.getAttribute());
			
		yF.setLstActions(this.getActions(comp));
		yF.setLstEvents(this.getEvents(comp));
		yF.setEditor(ymlF.getEditor());
		yF.setDescription(yF.getClassName().getDescription()+"."+ yF.getAttribute() +"-"+ this.getName());
		this.setModelAttributes(yF);
		return yF;
	}
	
	
	/**
	 * Return a list of components defined in Lines definition
	 * @param myLines
	 * @return
	 */
	private List<YVwComponent> getListComponents(List<List<String>> myLines) {
		List<YVwComponent> myComps= new ArrayList<>();
		byte row=0; byte col=0;
		for (List<String> line: myLines) {
			for (String comp: line) {
				myComps.add(this.getComponent(comp, row, col));
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
	private List<YVwComponent> getListPanelComponents(List<String> myLine) {
		List<YVwComponent> myComps= new ArrayList<>();
		byte row=0; byte col=0;
		for (String comp: myLine) {
			myComps.add(this.getComponent(comp, row, col));
			col++;
		}
		return myComps;
	}
	
	/**
	 * Return the Tab elements of a tab group
	 * @param containers
	 * @return
	 */
	private List<YVwComponent> getTabElements(List<String> containers) {
		List<YVwComponent> myComps= new ArrayList<>();
		byte row=0; byte col=0;
		for (String comp: containers) {
			myComps.add(this.getTabElement(comp, row, col));
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
	private YVwTabElement getTabElement(String container, byte row, byte col) {
		YVwTabElement myTab=new YVwTabElement();
		myTab.setRow(row); //Not important
		myTab.setCol(col);
		myTab.setName("tbe_"+ container);
		myTab.setDescription(myTab.getName());
		//myTab.setParent(this.yView); // Not necessary. JPA manages this
		myTab.setClassName(this.yView.getClassName()); // Not important
		
		//A tab element has no actions or events 
		// The child of a tab element is a container that has the events and actions
		//myTab.setLstActions(this.getActions(comp)); // Not important
		//myTab.setLstEvents(this.getEvents(comp)); // Not important
		//There is only one container into a Tab Element
		List<YVwComponent>lC=new ArrayList<>();
		lC.add(this.getComponent(container, (byte) 0, (byte)0));
		myTab.setChildren(lC);
		
		return myTab;
	}
	
	/** 
	 * Gets a ClassName class from its description
	 * @param name
	 * @return
	 */
	private ClassName getClassName(String name) {
		return this.connection.findObjectPK(new ClassName(name));
	}
	
	private ElementTypeEdu getElType(String comp) {
		ElementTypeEdu myType=ElementTypeEdu.FORM;
		String compType=comp.trim().substring(0, 2).toLowerCase();
		switch (compType) {
			case "p_":	myType = ElementTypeEdu.PANEL;     break;
			case "l_":	myType = ElementTypeEdu.LISTPANEL; break;
    		case "t_":	myType = ElementTypeEdu.TAB;       break;
    	   	case "f_":	myType = ElementTypeEdu.FIELD;     break;
    	   	default:    myType=  ElementTypeEdu.FORM;      break;
		}
		return myType;
	}
	
		
	/**
	 * Get a Component at a grid position x,y
	 * @param comp
	 * @param x
	 * @param y
	 */
	private YVwComponent getComponent(String comp, byte row, byte col) {
		YVwComponent myComp=null;
		ElementTypeEdu elType=getElType(comp);
		switch (elType) {
        	case PANEL:	myComp = getVwPanel(comp,row,col);     break;
        	case LISTPANEL:	myComp = getVwListPanel(comp,row,col); break;
        	case TAB:	myComp = getVwTabGroup(comp,row,col);  break;
        	case FIELD:	myComp = getVwField(comp,row,col);     break;
        	default:
                throw new IllegalArgumentException("Invalid component type: " + elType); 
        }
		return myComp;
	}
	
	
	private List<YVwEvent> getEvents (String compName) {
		List<YAMLVwEvent> lstYamE=null;
		ElementTypeEdu elType=getElType(compName);
		switch (elType) {
			case FORM:      lstYamE=this.getFormEvents();              break;
			case PANEL:     lstYamE=this.getPanelEvents(compName);     break;
			case LISTPANEL: lstYamE=this.getListPanelEvents(compName); break;
			case TAB:       lstYamE=this.getTabEvents(compName);       break;
			case FIELD:     lstYamE=this.getFieldEvents(compName);     break;
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
		for (YAMLVwEvent ymlE: ymlEvents) {
			YVwEvent myEvent=new YVwEvent();
			String[]s=StringUtils.split(ymlE.getAction(), ".");
			myEvent.setClassName(this.getClassName(s[0]));
			myEvent.setMethod(s[1]);
			//myEvent.setParent(parent); //NOt necessary, already defined by JPA
			myEvent.setDescription(ymlE.getElementType().toString()+"-"+ymlE.getElement());
			myEvent.setLstAffectedIds(ymlE.getRefresh());
			myEvents.add(myEvent);
		}	
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
		for (YAMLVwAction ymlE: ymlActions) {
			YVwAction myAction=new YVwAction();
			String[]s=StringUtils.split(ymlE.getAction(), ".");
			myAction.setClassName(this.getClassName(s[0]));
			myAction.setMethod(s[1]);
			//myAction.setParent(parent); //NOt necessary, already defined by JPA
			myAction.setDescription(ymlE.getElementType().toString()+"-"+ymlE.getElement());
			myAction.setLstAffectedIds(ymlE.getRefresh());
			myAction.setIcon(ymlE.getIcon());
			myAction.setType(ymlE.getButton());
			myAction.setName(ymlE.getName());
			myActions.add(myAction);
		}	
		return myActions;
		
	}
	/**
	 * Assign other properties to field, got by annotations in the modeñ
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
		for( IYAMLElement ymlElem: myLst) {
			String myDesc=ymlElem.getName().toString().trim().toLowerCase();
			if (mySet.add(myDesc)==false) 
				myErrors=myErrors + "\n" + "->Duplicated " + elemName + ": " + myDesc;
		}
		return myErrors;
	}
	
	

	public String checkErrors(boolean verbose) {
		String myErrors="";
		int i=0;
		
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
			this.Duplicated(this.fieldActions     ,"Field Action"      ,i++,verbose, myErrors);
		
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
		
		YAMLViewLoad yv=null;
		//1. Read YAML File
		InputStream in = YAMLUtilsEdu.class.getResourceAsStream("/view/prova.yaml");
		try {
			yv = YAMLUtilsEdu.YAMLFileToObject(in, YAMLViewLoad.class);
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
