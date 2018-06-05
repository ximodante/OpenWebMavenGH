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
import openadmin.model.yamlview.YVwComponent;
import openadmin.model.yamlview.YVwEvent;
import openadmin.model.yamlview.YVwPanel;
import openadmin.model.yamlview.YVwView;
import openadmin.util.configuration.yaml.YAMLUser;
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
			connection.deleteOlderThan(ActionViewRole.class, myDate);
			connection.deleteOlderThan(Access.class        , myDate);
			connection.deleteOlderThan(Program.class       , myDate);
			connection.deleteOlderThan(User.class          , myDate);
			connection.deleteOlderThan(EntityAdm.class     , myDate);
			connection.deleteOlderThan(Role.class          , myDate);
			connection.deleteOlderThan(Action.class        , myDate);
			connection.deleteOlderThan(MenuItem.class      , myDate);
			connection.deleteOlderThan(ClassName.class     , myDate);
			
			
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
	
	private void setViewInfo() {
		this.yView.setRow((byte)-1);
		this.yView.setCol((byte)-1);
		this.yView.setName(this.name);
		this.yView.setDescription(this.description);
		this.yView.setParent(null);
		this.yView.setRsbundle(this.rsbundle);
		this.yView.setClassName(this.getClassName(this.klass));
		this.yView.setLstActions(this.getFormActions(""));
		this.yView.setLstEvents(this.getFormEvents(""));
		this.yView.setChildren(this.getListComponents(this.lines));
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
	 * Gets a ClassName class from its description
	 * @param name
	 * @return
	 */
	private ClassName getClassName(String name) {
		return this.connection.findObjectPK(new ClassName(name));
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
	 * Get a Component at a grid position x,y
	 * @param comp
	 * @param x
	 * @param y
	 */
	private YVwComponent getComponent(String comp, byte row, byte col) {
		
		YVwComponent myComp=null;
		String compType=comp.trim().substring(0, 3).toLowerCase();
		switch (compType) {
        	
			case "pnl":	myComp = getVwPanel(comp,row,col);     break;
        	
        	case "lpn":	myComp = getVwListPanel(comp,row,col); break;
        	
        	case "tab":	myComp = getVwTabGroup(comp,row,col);  break;
        	
        	case "fld":	myComp = getVwField(comp,row,col);     break;
        		
        	default:
                throw new IllegalArgumentException("Invalid component type: " + compType);
        }
		return myComp;
	}
	/**
	 * Gets a panel at a grip x,y position
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
		yP.setLstActions(this.geActions(comp));
		yP.setLstEvents(this.getEvents(comp));
		yP.getChildren().
		return yP;
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
			myEvent.setKlass(this.getClassName(s[0]));
			myEvent.setMethod(s[1]);
			//myEvent.setParent(parent); //NOt necessary, already defined by JPA
			myEvent.setDescription(ymlE.getElementType().toString()+"-"+ymlE.getElement());
			myEvents.add(myEvent);
		}	
		return myEvents;
		
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
		
		YAMLControlLoad yc=null;
		//1. Read YAML File
		InputStream in = YAMLUtilsEdu.class.getResourceAsStream("/data/Application.yaml");
		try {
			yc = YAMLUtilsEdu.YAMLFileToObject(in, YAMLControlLoad.class);
			System.out.println(yc.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
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

	}

}
