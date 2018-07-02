package openadmin.util.configuration.yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
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
import openadmin.model.control.RoleGroup;
import openadmin.model.control.RolePerGroup;
import openadmin.model.control.User;
import openadmin.util.configuration.yamlview.YAMLRoleGroup;
import openadmin.util.edu.YAMLUtilsEdu;


/**
 * Transforms a YAML file
 * @author eduard
 *
 */
@SuppressWarnings("serial")
@ToString
public class YAMLControlLoad implements Serializable{

	
	
	/************************************************************
	 * 1. YAML File structure
	 ************************************************************/
	
	@Getter @Setter
	private List<YAMLUser> users= null;        // Users
	
	@Getter @Setter
	private List<YAMLRole> roles= null;        // Roles
		
	@Getter @Setter
	private List<YAMLEntityAdm> entities= null;       // EntityAdm 
	
	@Getter @Setter
	private List<YAMLAction> defaultActions= null;         // Actions

	@Getter @Setter
	private List<YAMLMenuItem> menuItems= null;         // Programs

	//2018/06/06 Added roleGroup
	@Getter @Setter
	private List<YAMLRoleGroup> roleGroups= null;         // Programs

	
	/************************************************************
	 * 2. Helper structure
	 ************************************************************/
	
	
	@Getter
	private HashMap<String,User>cUsers=null;
	
	@Getter
	private HashMap<String,Role>cRoles=null;
	
	@Getter
	private HashMap<String,EntityAdm>cEntityAdms=null;
	
	@Getter
	private HashMap<String,Program>cPrograms=null;
	
	@Getter
	private HashMap<String,RoleGroup>cRoleGroups=null;
	
	@Getter
	private Set<Access>cAccesses=null;
	
	@Getter
	private HashMap<String,MenuItem> cMenuItems=null;
	
	@Getter
	private HashMap<String,ClassName> cClassNames=null;
	 
	@Getter
	private HashMap<String,Action> cActions=null;
	
	@Getter
	private HashMap<String,ActionViewRole> cActionViewRoles=null;
	
	@Getter
	private HashMap<String,List<String>> cRoleGroups2=null;
	
	private String defaultProgram="control";
	
	private Integer orden=1;
	
	@Setter
	private DaoOperationFacadeEdu connection = null; 	
	
	/**
	private User firstLoadUser = new User("FirstLoader","123456","First Load User");
	*/
	
	// ========================================
	// 1. Helpers
	// ========================================
	/**
	 * Get all Role Names
	 * @return
	 */
	
	private Set<String> getRoleNames() {
		Set<String> roleNames= new HashSet<String>();
		for (YAMLRole ymlRol: this.roles) {
			for (String sRol: ymlRol.getNames()) {
				//roleNames.add(sRol.trim().toLowerCase());
				for (String sProg: ymlRol.getPrograms()) {
					roleNames.add(this.getRoleDescription(sRol, sProg));
					//System.out.println("ROLES:"+ this.getRoleDescription(sRol, sProg));
				}
		}	}
		return roleNames;
	}
	
	/**
	 * Get all simple role name ("ADMIN" and NOT "ADMIN.control") 
	 * @return
	 */
	private Set<String> getSimpleRoleNames() {
		Set<String> roleNames= new HashSet<String>();
		for (YAMLRole ymlRol: this.roles) {
			for (String sRol: ymlRol.getNames()) {
				roleNames.add(sRol.trim().toUpperCase());
		}	}
		return roleNames;
	}
	
		
	
	/**
	 * Get all Program Names
	 * @return
	 */
	private Set<String> getProgramNames() {
		Set<String> programNames= new HashSet<String>();
		for (YAMLRole ymlRol: this.roles) {
			for (String sProgram: ymlRol.getPrograms()) {
				programNames.add(sProgram.trim().toLowerCase());
		}	}
		return programNames;
	}
	
	// Get Class Names
	private Set<String> getClassNamesPriv(Set<String> classNames, List<YAMLMenuItem> lMenuItems) {
		if (lMenuItems !=null) {
			for (YAMLMenuItem ymlMenu: lMenuItems) {
				if (ymlMenu.getClassName() !=null)
					//classNames.add(ymlMenu.getClassName().trim());
					classNames.add(ymlMenu.getClassSimpleName());
				classNames=getClassNamesPriv(classNames , ymlMenu.getMenuItems()); 
		}	}
		return classNames;
	}
	
	private Set<String> getClassNames() {
		Set<String> classNames= new HashSet<String>();
		classNames=getClassNamesPriv(classNames, this.menuItems);
		return classNames;
	}
	
	
    private String favIcon(String favIconName) {
    	if (favIconName !=null && favIconName.length()>1) return "fa " + favIconName;
    	else return favIconName;
    }
	
    /** Get which roleGroup has more roles
     * 
     * @param roleGroupName
     * @return
     */
    private String widerRG(String RG1, String RG2) {
    	if (this.cRoleGroups2!=null) {
    		List<String> s1=cRoleGroups2.get(RG1);
    		List<String> s2=cRoleGroups2.get(RG2);
    		
    		if (s1==null) return RG2;
    		else if (s2==null) return RG1;
    		else if (s1.size() > s2.size()) return RG1;
    		else return RG2;
    	} else return RG1;	
    }	

    // ======================================================
	// 2. Control classes extraction and populate to DB
	//    Also delete old configuration records from DB
	// ======================================================
	public void Init() {
		LocalDateTime myDate = LocalDateTime.now();
		
		/**
		//0. open connection
		LangTypeEdu langType = new LangTypeEdu();
		langType.changeMessageLog(TypeLanguages.es);
		connection = new DaoJpaEdu(firstLoadUser, "control_post", (short) 0,langType);
		
		connection.begin();
		*/
		//1. Users
		this.cUsers=this.getControlUsers();
			
		//2. Roles
		this.cRoles=this.getControlRoles();
		
		//3. Role Groups
		this.cRoleGroups2=this.getRolesFromRoleGroup();
		this.cRoleGroups =this.getControlRoleGroups();
		
		//4. Roles per groups
		this.obtainRolesPerRoleGroups();
		
		//5. Entity & Program & Access
		this.EntityAdmProgramAccess();
		
		
		//6. MenuItems & ClassName & Action
		this.MenuItemsClassNameActions();
		
		
				
		//6. Delete old configuration
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

	/**
	// 2.-1 Set estimated Roles to an Action (Knowing the program name)
	private void setEstimatedRoles(YAMLAction ymlAct, String programName) {
		
		if(ymlAct.getEstimatedRoles()==null || ymlAct.getEstimatedRoles().size()<1) {
			List<String>lRol=new ArrayList<>();
			//System.out.println("Action:"+ymlAct.getName());
			//System.out.println("      Action Role Group:"+ymlAct.getRoleGroup());
			if (ymlAct.getRoleGroup()!=null && ymlAct.getRoleGroup().trim().length()>1) {
				for(String rName:this.cRoleGroups.get(ymlAct.getRoleGroup())) 
					lRol.add(this.getRoleDescription(rName, programName));
				ymlAct.setEstimatedRoles(lRol);
	}	}	}
	*/
	
	//2.0 Get list of role names of a RoleGroup
	private HashMap<String,List<String>> getRolesFromRoleGroup() {
		HashMap<String,List<String>>lRoles=new HashMap<String,List<String>>();
		if(this.roleGroups !=null) 
			for (YAMLRoleGroup ymlRoleG : this.roleGroups) 
				lRoles.put(ymlRoleG.getName(),ymlRoleG.getRoles());
		return lRoles;
	}
	
	//2.1 Control Users -_> ja està
	private HashMap<String,User> getControlUsers() {
		HashMap<String,User>lUsers=new HashMap<String,User>();
		if (this.users != null) {
			for (YAMLUser ymlUser: this.users) {
				User myUser=new User (ymlUser.getDescription(), ymlUser.getPassword(), ymlUser.getFullName());
				myUser.setIdentifier(ymlUser.getIdentifier());
				myUser.setLanguage(ymlUser.getLanguage());
				
				// DateBegin
				if (ymlUser.getDateBegin() != null) { 
					myUser.setDateBegin(LocalDate.parse(ymlUser.getDateBegin()));
				} else {
					myUser.setDateBegin(LocalDate.now());
				}
				// DateEnd
				if (ymlUser.getDateEnd() != null) { 
					myUser.setDateEnd(LocalDate.parse(ymlUser.getDateEnd()));
				} else {
					// By default a user is active for 5 years
					myUser.setDateEnd(myUser.getDateBegin().plusYears(5));
				}
				lUsers.put(myUser.getDescription(), this.connection.persist(myUser));
		}	}	
		return lUsers;
	}
	
	
	//2.2 ContolRoles
	private HashMap<String,Role> getControlRoles() {
		HashMap<String,Role>lRoles=new HashMap<String,Role>();
		if(this.roles !=null) {
			for (YAMLRole ymlRole : this.roles) {
				
				for (String sRol: ymlRole.getNames()) { 
					for (String sProg: ymlRole.getPrograms()) {
						//Role myRole=new Role(sRol.trim().toUpperCase() + "." + sProg.trim().toLowerCase());
						Role myRole=new Role(this.getRoleDescription(sRol, sProg));
						if (lRoles.get(myRole.getDescription()) == null)
								lRoles.put(myRole.getDescription(), this.connection.persist(myRole));
		}	}	}	}
				/*
				for (String sRol: ymlRole.getNames()) { 
					Role myRole=new Role(sRol.trim().toUpperCase());
						if (lRoles.get(myRole.getDescription()) == null)
								lRoles.put(myRole.getDescription(), this.connection.persist(myRole));
		}	}	}*/	
		return lRoles;
	}
	
	
	//2.3 ControlRolesGroups
	private HashMap<String,RoleGroup> getControlRoleGroups() {
		HashMap<String,RoleGroup>lRG=new HashMap<String,RoleGroup>();
		if(this.roleGroups !=null) {
			for (YAMLRoleGroup ymlRG : this.roleGroups) {
				for (String prg:this.getProgramNames()) {
					RoleGroup myRG=new RoleGroup(ymlRG.getName()+ "." + prg);
					if (lRG.get(myRG.getDescription())==null)
						lRG.put(myRG.getDescription(), this.connection.persist(myRG));
		}	}	}	
		return lRG;   
	}
	
	// 2.4 RolePerGroup
	// The description of a role per group is "role.rolgroup.program"
	private void obtainRolesPerRoleGroups() {
		if (this.cRoles!=null) {
			for (Role r:cRoles.values()) {
				//s[0]=simple role name;  s[1]=program name
				String[] s=StringUtils.split(r.getDescription(),".");
				if(this.roleGroups !=null) {
					for (YAMLRoleGroup ymlRG : this.roleGroups) {
						if (ymlRG.getRoles().contains(s[0])) {
							RoleGroup rg=this.cRoleGroups.get(ymlRG.getName()+"."+s[1]);
							RolePerGroup rpg=new RolePerGroup();
							rpg.setRole(r);
							rpg.setRoleGroup(rg);
							rpg.setDescription(s[0]+"."+ymlRG.getName()+"."+s[1]);
							this.connection.persist(rpg);
	}	}	}	}	}	}
	
	// 2.5 EntityAdm & Program & Accesses
	public void EntityAdmProgramAccess() {
		this.cEntityAdms = new HashMap<String,EntityAdm>();
		this.cPrograms   = new HashMap<String,Program>() ;
		this.cAccesses   = new HashSet<Access>() ;
		
		if(this.entities !=null) {
			for (YAMLEntityAdm ymlEnt : this.entities) {
				
				// Add EntityAdm
				EntityAdm myEnt=new EntityAdm(ymlEnt.getName().trim().toLowerCase());
				myEnt.setConn(ymlEnt.getConn());
				myEnt.setIcon(this.favIcon(ymlEnt.getIcon()));
				myEnt.setTheme(ymlEnt.getTheme());
				this.cEntityAdms.put(myEnt.getDescription(), this.connection.persist(myEnt));
				
				if(ymlEnt.getPrograms() !=null) {
					for (YAMLProgram ymlProg: ymlEnt.getPrograms()) {
						
						// Add program
						Program myProg=new Program (ymlProg.getName().trim().toLowerCase());
						myProg.setIcon(this.favIcon(ymlProg.getIcon()));
						this.cPrograms.put(myProg.getDescription(), this.connection.persist(myProg));
						
						if(ymlProg.getAlloweds() !=null) {
							for (YAMLAllowed ymlAllow: ymlProg.getAlloweds()) {
								for (String sUser: ymlAllow.getUsers()) {
									Access myAcc=new Access ();
									myAcc.setEntityAdm(myEnt);
									myAcc.setProgram(myProg);
									myAcc.setUser(this.cUsers.get(sUser));
									//myAcc.setRole(this.cRoles.get(myProg.getDescription().trim().toLowerCase()+"."+ymlAllow.getRole().trim().toUpperCase()));
									//myAcc.setRole(this.cRoles.get(ymlAllow.getRole().trim().toUpperCase() + "." + ymlProg.getName().trim().toLowerCase()));
									
									//if the role exists for this program, this Access will be persisted
									Role role=this.cRoles.get(this.getRoleDescription(ymlAllow.getRole(), ymlProg.getName()));
									if (role!=null) {
										myAcc.setRole(role);
										myAcc.setDescription("");
										//System.out.println("------ACCESS: EntityAdm:" + myEnt.getDescription() + " - User:" + sUser + " - Program:"+ myProg.getDescription() + " - Role:"+ ymlAllow.getRole());
										this.cAccesses.add(this.connection.persist(myAcc));
									}
									else  System.out.println("------NOT PERSISTING ACCESS: EntityAdm:" + myEnt.getDescription() + " - User:" + sUser + " - Program:"+ myProg.getDescription() + " - Role:"+ ymlAllow.getRole() + " This role does not exist!!!.");
		
		
				
	}	}	}	}	}	}	}	}	
	
	
	// 2.4 MenuItems & ClassNames & Actions
	public void MenuItemsClassNameActions() {
		this.defaultProgram="control";
		this.cMenuItems =new HashMap<String,MenuItem>();
		this.cActions   =new HashMap<String,Action>();
		this.cClassNames=new HashMap<String,ClassName>();
		this.cActionViewRoles=new HashMap<String,ActionViewRole>();
		
		//Role Group name
		String myRG="";
		
		if(this.menuItems !=null) {
			for (YAMLMenuItem ymlMenu : this.menuItems) {
				//Submenu
				if (ymlMenu.getNodeType()==0) {
					myRG =this.SubMenuItemsClassNameActionsPriv(ymlMenu, null, myRG);
				// Not submenus (action or views)	
				} else {
					myRG = MenuItemsClassNameActionsPriv(ymlMenu, null);
	}	}	}	} 
	
	//2.4.1 SubMenu MenuItems
	private String SubMenuItemsClassNameActionsPriv(YAMLMenuItem ymlMenu, MenuItem parent, String myParentRG ) {
		MenuItem myMenu=new MenuItem();
		ClassName myClass=null;
		
		
		if (ymlMenu.getProgram().trim().length()>0) 
			this.defaultProgram=ymlMenu.getProgram().trim().toLowerCase();
		/* In a Submenu there are no classNames 
		// Add ClassName
		*/
		
		// Add MenuItems
		myMenu.setClassName(myClass);
		myMenu.setIcon(this.favIcon(ymlMenu.getIcon()));
		myMenu.setParent(parent);
		myMenu.setOrden(this.orden++);
		myMenu.setType(ymlMenu.getNodeType());
		myMenu.setDescription(ymlMenu.getDescription());
		
		//System.out.println("Submenu:"+ myMenu.getDescription());
		this.cMenuItems.put(myMenu.getDescription(), this.connection.persist(myMenu));
		
		
		String myRG="";
		String myRG1="";
		if(ymlMenu.getMenuItems() !=null) {
			for (YAMLMenuItem ymlMenu1 : ymlMenu.getMenuItems()) {
				//0=Submenu
				if (ymlMenu1.getNodeType()==0) 
					myRG1=SubMenuItemsClassNameActionsPriv(ymlMenu1, myMenu, myRG);
				//1=Command or action, 2=Default View, 3=Custom View 4= YAML View
				else 
					myRG1=MenuItemsClassNameActionsPriv(ymlMenu1, myMenu);
				
				myRG=this.widerRG(myRG, myRG1);
		}	}	
		
		//Submenu Gets roles from children. All the roles of the children can access a parent submenu
						
		//Add Submenu action
		YAMLAction ymlAct=new YAMLAction();
		ymlAct.setName("submenu");
		ymlAct.setGroup(0); // No matter the group value as there is only 1 action
		//ymlAct.setRoles(lRol);
		//ymlAct.setEstimatedRoleGroups(lRolGrp);
		ymlAct.setRoleGroup(myRG);
		myRG1= this.setMyActions(ymlAct, myClass, myMenu, false);
		
		return myRG;
	}	
		
	
	//2.4.2 Simple MenuItems (return roleGroupss)
	private String MenuItemsClassNameActionsPriv(YAMLMenuItem ymlMenu, MenuItem parent ) {
		
		MenuItem myMenu=new MenuItem();
		ClassName myClass=null;
		String myRG="";
		String myRG1="";
		
		if (ymlMenu.getProgram().trim().length()>0) 
			this.defaultProgram=ymlMenu.getProgram().trim().toLowerCase();
		
		// Add ClassName
		if (ymlMenu.getClassName().trim().length()>0) {
			myClass=new ClassName();
			myClass.setDescription(ymlMenu.getClassSimpleName());
			myClass.setPack(ymlMenu.getClassPackage());
			this.cClassNames.put(myClass.getDescription(), this.connection.persist(myClass));
		}
		
		
		// Add MenuItems
		myMenu.setClassName(myClass);
		myMenu.setIcon(this.favIcon(ymlMenu.getIcon()));
		myMenu.setParent(parent);
		myMenu.setOrden(this.orden++);
		myMenu.setType(ymlMenu.getNodeType());
		myMenu.setDescription(ymlMenu.getDescription().trim());
		
		//if (myMenu.getDescription().length()==0) myMenu.setDescription(myClass.getDescription());
		if (myMenu.getDescription().length()==0)
			myMenu.setDescription(myClass.getDescription() + "_" + this.defaultProgram );
		
		//System.out.println("   menu:"+ myMenu.getDescription());
		//20180618: Now a MenuItem can be in several programs
		this.cMenuItems.put(myMenu.getDescription(), this.connection.persist(myMenu));
		
		// Add default actions only if viewType is not action nor submenu
		//if(ymlMenu.isDefaultActions() && myMenu.getType()!=0 && myMenu.getType()!=1 )
		
		// Add default actions only if viewType is 2(Default) or 3(Custom)
		if(ymlMenu.isDefaultActions() && myMenu.getType()==2 || myMenu.getType()==3 )
			myRG=this.setMyDefaultActions(ymlMenu, myClass, myMenu);
		
		// Add YAML Action for viewType =4 (YAML View)
		if(myMenu.getType()==4)
			myRG=this.setYMLAction(ymlMenu, myClass, myMenu);
				
		//Add other actions
		if (ymlMenu.getActions() != null) {
			for (YAMLAction ymlAct: ymlMenu.getActions()) {
				//this.setEstimatedRoles(ymlAct,this.defaultProgram);
				myRG1=this.setMyActions(ymlAct, myClass, myMenu, false);
				myRG=this.widerRG(myRG, myRG1);
		}	}
		return myRG;
	}	
		
	// 2.4.3 Retrieve Default Actions
	private String setMyDefaultActions(YAMLMenuItem ymlMenu, ClassName myClass, MenuItem myMenu) {
		String myRG="";
		String myRG1="";
		if (ymlMenu.isDefaultActions()) {
			if (this.getDefaultActions() != null) {
				for (YAMLAction ymlAct: this.getDefaultActions() ) {
					//this.setEstimatedRoles(ymlAct,this.defaultProgram);
					myRG1=this.setMyActions(ymlAct, myClass, myMenu, true);
					myRG=this.widerRG(myRG, myRG1);
		}	}	}	
		return myRG;
	}
		
	private String setYMLAction(YAMLMenuItem ymlMenu, ClassName myClass, MenuItem myMenu) {
		String myRG="";
		Action myAct=null;
		// Add action
		myAct=new Action ();
		myAct.setDescription(myClass.getDescription().trim() + "_yaml" );
		myAct.setClassName(myClass);
		myAct.setGrup(0);
		//myAct.setIcon(this.favIcon(ymlAct.getIcon()));
					
		myAct.setType((byte)1);                 // Not default Action 
					
		this.cActions.put(myAct.getDescription(), this.connection.getOrPersist(myAct));
			
		myRG=ymlMenu.getRoleGroup();
			
			
		RoleGroup myRGrp=this.cRoleGroups.get(myRG+"."+this.defaultProgram);
		// If the role exists for this program
		if (myRGrp!=null) {
			ActionViewRole myAVR = new ActionViewRole();
			myAVR.setAction(myAct);
			myAVR.setMenuItem(myMenu);
			myAVR.setRoleGroup(myRGrp);
			myAVR.setDescription("");
			this.cActionViewRoles.put(myAVR.getDescription(), this.connection.persist(myAVR));
		}
		return myRG;
	}
	
	//2.4.4 Create Actions and ActionviewRole
	private String setMyActions(YAMLAction ymlAct, ClassName myClass, MenuItem myMenu, boolean isDefaultAction) {
		Action myAct=null;
		
		//(Not for submenus that have no actions) 1=Command, 2=Default View, 3=Custom View, 4=YAML View
		if (myMenu.getType()!=0) {
			// Add action
			myAct=new Action ();
			myAct.setDescription((myClass.getDescription().trim() + "_" + ymlAct.getName()).trim().toLowerCase());
			myAct.setClassName(myClass);
			myAct.setGrup(ymlAct.getGroup());
			myAct.setIcon(this.favIcon(ymlAct.getIcon()));
			
			if (isDefaultAction) myAct.setType((byte)0); // Default action
			else myAct.setType((byte)1);                 // Not default Action 
			
			this.cActions.put(myAct.getDescription(), this.connection.persist(myAct));
		}
		
		// Add ActionViewRole
		String myRG=ymlAct.getRoleGroup();
		RoleGroup myRGrp=this.cRoleGroups.get(myRG+"."+this.defaultProgram);
		// If the role exists for this program
		if (myRGrp!=null) {
			ActionViewRole myAVR = new ActionViewRole();
			myAVR.setAction(myAct);
			myAVR.setMenuItem(myMenu);
			myAVR.setRoleGroup(myRGrp);
			myAVR.setDescription("");
			this.cActionViewRoles.put(myAVR.getDescription(), this.connection.persist(myAVR));
					
		}	
		return myRG;
	}
	
	
	// ========================================
	// 3. Error detection
	// ========================================
	
		
	// Error detection A: Duplicates
	/**
	 * Detects duplicated users
	 * @return
	 */
	public String DuplicatedUsers(String myErrors) {
		Set<String> mySet= new HashSet<String>();
		for( YAMLUser ymlUser: this.users) {
			String myDesc=ymlUser.getDescription().trim().toLowerCase();
			if (mySet.add(myDesc)==false) 
				myErrors=myErrors + "\n" + "->Duplicated User:" + myDesc;
		}	
		return myErrors;
	}
	
	/**
	 * Detects duplicated combination of Role + Program
	 * @param myErrors
	 * @return
	 */
	public String DuplicatedRoles(String myErrors) {
		Set<String[]> mySet= new HashSet<String[]>();
		for (YAMLRole ymlRol: this.roles) {
			for (String sRol: ymlRol.getNames()) {
				for (String sProgram: ymlRol.getPrograms()) {
					String[]str=new String[2];
					str[0]=sRol;
					str[1]=sProgram;
					if (mySet.add(str)==false)
						myErrors=myErrors + "\n" + "->Duplicated Rol:" + sRol + "-" + sProgram; 
		}	}	}
		return myErrors;
	}
	
	/**
	 * Detects duplicated EntityAdm
	 * @param myErrors
	 * @return
	 */
	public String DuplicatedEntities(String myErrors) {
		Set<String> mySet= new HashSet<String>();
		for (YAMLEntityAdm ymlEnt: this.entities) {
			if (mySet.add(ymlEnt.getName())==false)
						myErrors=myErrors + "\n" + "->Duplicated EntityAdm:" + ymlEnt.getName(); 
		}
		return myErrors;
	}
	
	/**
	 * Detects duplicated Program per EntityAdm
	 * @param myErrors
	 * @return
	 */
	public String DuplicatedEntityProgram(String myErrors) {
		for (YAMLEntityAdm ymlEnt: this.entities) {
			Set<String> mySet= new HashSet<String>();
			for (YAMLProgram ymlProg: ymlEnt.getPrograms()) {
				if (mySet.add(ymlProg.getName())==false)
						myErrors=myErrors + "\n" + "->Duplicated Program in EntityAdm:" + ymlEnt.getName() + "-" + ymlProg.getName();
			}
		}
		return myErrors;
	}
	
	/**
	 * Detects duplicated User per Program per EntityAdm
	 * @param myErrors
	 * @return
	 */
	public String DuplicatedEntityProgramUser(String myErrors) {
		for (YAMLEntityAdm ymlEnt: this.entities) {
			for (YAMLProgram ymlProg: ymlEnt.getPrograms()) {
				Set<String> mySet= new HashSet<String>();
				for (YAMLAllowed ymlAllow: ymlProg.getAlloweds()) {
					for (String user: ymlAllow.getUsers()) {
						if (mySet.add(user)==false)
							myErrors=myErrors + "\n" + "->Duplicated User in Program in EntityAdm:" + ymlEnt.getName() + "-" + ymlProg.getName() + "-" + user;
					}
				}	
			}
		}
		return myErrors;
	}
	
	/**
	 * Detects duplicated Default Actions
	 * @return
	 */
	public String DuplicatedDefaultAction(String myErrors) {
		Set<String> mySet= new HashSet<String>();
		for( YAMLAction action: this.defaultActions) {
			//this.setEstimatedRoles(action);
			String name=action.getName().trim().toLowerCase();
			if (mySet.add(name)==false) 
				myErrors=myErrors + "\n" + "->Duplicated DefaultAction:" + name;
		}	
		return myErrors;
	}
	
	/**
	 * Detects duplicated Roles in Default Actions
	 * @return
	 */
	/**
	public String DuplicatedDefaultActionRole(String myErrors) {
		if (this.defaultActions!=null) {
			for( YAMLAction action: this.defaultActions) {
				Set<String> mySet= new HashSet<String>();
				//this.setEstimatedRoles(action,this.defaultProgram);
				//for (String sRol: action.getRoles()) {
				//for (String sRol: action.getEstimatedRoles()) {
				//	String name= sRol.trim().toLowerCase();
				//	if (mySet.add(name)==false) 
				//		myErrors=myErrors + "\n" + "->Duplicated DefaultAction Role:" + action.getName() + "-" + sRol;
				//}	
			}
		}	
		return myErrors;
	}
	**/
	/**
	 * Detects duplicated menu Items description
	 * @param myErrors
	 * @return
	 */
	public String DuplicatedMenuItemDescriptions(String myErrors) {
		Set<String> mySet= new HashSet<String>();
		return getDupMenuItem(myErrors, this.menuItems, mySet,0);
	}
	
	private String getDupMenuItem(String myErrors, List<YAMLMenuItem> myMenus, Set<String> mySet, int level) {
		if (myMenus!=null) {	
			
			for( YAMLMenuItem myMenu: myMenus) {
				String myDesc=myMenu.getDescription().trim().toLowerCase();
				if (myDesc.length()==0) myDesc=myMenu.getClassSimpleName().toLowerCase();	
				//System.out.println(""+level+" " + myDesc);
				if (mySet.add(myDesc)==false) 
					myErrors=myErrors + "\n" + "->Duplicated MenuItem Description:" +level + "--->"+ myDesc + "->" + myMenu.getClassName() + ":"+ myMenu.getDescription();
				if (myMenu.getMenuItems()!=null)
				myErrors=myErrors + getDupMenuItem(myErrors, myMenu.getMenuItems(), mySet, ++level);	
		}	}	
		return myErrors;
	}
	
	// Error detection B: Nonexistent elements
	
	/**
	 * Detects nonexistent roles in Entity-Program-Alloweds
	 * @param myErrors
	 * @return
	 */
	public String NoEntityProgramRole(String myErrors) {
		Set<String> mySet= this.getRoleNames();
		for (YAMLEntityAdm ymlEnt: this.entities) {
			for (YAMLProgram ymlProg: ymlEnt.getPrograms()) {
				for (YAMLAllowed ymlAllow: ymlProg.getAlloweds()) {
					//if (! mySet.contains(ymlAllow.getRole().trim().toLowerCase()))
					String roleName=this.getRoleDescription(ymlAllow.getRole(), ymlProg.getName());
					boolean isRolePresent = mySet.contains(roleName);
					//System.out.println("Verifying Role: " + roleName + " is present:" + isRolePresent);
					if (! isRolePresent) myErrors=myErrors + "\n" + "->Rol in Program in EntityAdm NOT defined in Roles:" + ymlEnt.getName() + "-" + ymlProg.getName() + "-" + ymlAllow.getRole();
					
				}	
			}
		}
		return myErrors;
	}
	
	
	/**
	 * Detects nonexistent roles in Entity-Program-Alloweds
	 * @param myErrors
	 * @return
	 */
	/**
	public String NoDefaultActionRole(String myErrors) {
		//Set<String> mySet= this.getRoleNames();
		Set<String> mySet= this.getSimpleRoleNames();
		if (this.defaultActions !=null) { 
			for (YAMLAction ymlAct: this.defaultActions) {
				this.setEstimatedRoles(ymlAct,this.defaultProgram);
				//for (String sRol: ymlAct.getRoles()) {
				for (String sRol: ymlAct.getEstimatedRoles()) {
					// Verify only in simple names of roles
					if (! mySet.contains(sRol.trim().toLowerCase()))
						myErrors=myErrors + "\n" + "->Role in DefaultActions NOT defined in Roles:" + 
								ymlAct.getName() +  "-" + sRol;
				}
			}
		}	
		return myErrors;
	}
	**/
	
	/**
	 * Detects nonexistent programs in MenuItem
	 * @param myErrors
	 * @return
	 */
	private String NoMenuItemProgramPriv(String myErrors, List<YAMLMenuItem> lMenuItems, Set<String> programSet ) {
		if (lMenuItems !=null) {
			for (YAMLMenuItem ymlMenu: lMenuItems) {
				if (ymlMenu.getProgram() != null && ymlMenu.getProgram().trim().length()>0) {
					if (! programSet.contains(ymlMenu.getProgram().trim().toLowerCase()))
						myErrors=myErrors + "\n" + "->Program in MenuItems NOT defined in Roles:" + 
								ymlMenu.getDescription() +"-" + ymlMenu.getClassName() +  "-" + 
								ymlMenu.getProgram();
				}
				myErrors= NoMenuItemProgramPriv(myErrors, ymlMenu.getMenuItems(), programSet);
			}	
		}
		return myErrors;
	}
	
	/**
	 * Detects nonexistent programs in MenuItem
	 * @param myErrors
	 * @return
	 */
	public String NoMenuItemProgram(String myErrors) {
		myErrors=NoMenuItemProgramPriv(myErrors, this.menuItems,this.getProgramNames());
		return myErrors;
	}	
	
	
	/**
	 * Detects nonexistent roles in menitems
	 * @param myErrors
	 * @return
	 */
	
	/**
	private String NoMenuItemActionRolePriv(String myErrors, List<YAMLMenuItem> lMenuItems, Set<String> roleSet ) {
		if (lMenuItems !=null) {
			for (YAMLMenuItem ymlMenu: lMenuItems) {
				if (ymlMenu.getActions() !=null) {
					for (YAMLAction ymlAction: ymlMenu.getActions()) {
						this.setEstimatedRoles(ymlAction,this.defaultProgram);
						//for (String sRol:ymlAction.getRoles()) {
						for (String sRol:ymlAction.getEstimatedRoles()) {	
							if (! roleSet.contains(sRol.trim().toLowerCase())) 
								myErrors=myErrors + "\n" + "->Roles in Actions in MenuItems NOT defined in Roles:" + 
										ymlMenu.getDescription() +"-" + ymlMenu.getClassName() +  "-" + 
										ymlAction.getName() + "-" + ymlMenu.getProgram();
						}
					}
					myErrors= NoMenuItemProgramPriv(myErrors, ymlMenu.getMenuItems(), roleSet);
				}	
			}
		}	
		return myErrors;
	}
	**/
	
	/**
	 * Detects nonexistent roles in MenuItem - Action 
	 * @param myErrors
	 * @return
	 */
	
	/**
	public String NoMenuItemActionRole(String myErrors) {
		myErrors=NoMenuItemActionRolePriv(myErrors, this.menuItems,this.getRoleNames());
		return myErrors;
	}
	
	**/
	
	/**
	 * Gets the role description as ROLE.program
	 * @param simpleRoleName
	 * @param programName
	 * @return
	 */
	private String getRoleDescription(String simpleRoleName, String programName) {
		//return simpleRoleName.trim().toUpperCase()+ "." + programName.trim().toLowerCase();
		return simpleRoleName.trim().toLowerCase()+ "." + programName.trim().toLowerCase();
	}
	
	public String checkErrors(boolean verbose) {
		if (this.cRoleGroups2==null) this.cRoleGroups2 =this.getRolesFromRoleGroup();
		String s="";
		if (verbose) s=s+
			"======================================================\n" +
			"1. Duplicated Users:\n" + 
			"======================================================\n";
		s=this.DuplicatedUsers(s);
		
		if (verbose) s=s + "\n\n" + 
			"======================================================\n" +
			"2. Duplicated Roles:\n" + 
			"======================================================\n";
		s=this.DuplicatedRoles(s);
		
		if (verbose) s= s + "\n\n" + 
			"======================================================\n" +
			"3. Duplicated EntityAdm:\n" + 
			"======================================================\n";
		s=this.DuplicatedEntities(s);
			
		if (verbose) s= s + "\n\n" + 
			"======================================================\n" +
			"4. Duplicated EntityProgram:\n" + 
			"======================================================\n";
		s=this.DuplicatedEntityProgram(s);
			
		
		if (verbose) s= s + "\n\n" + 
			"======================================================\n" +
			"5. Duplicated EntityProgramUser:\n" + 
			"======================================================\n";
		s=this.DuplicatedEntityProgramUser(s);
			
		if (verbose) s= s + "\n\n" + 
			"======================================================\n" +
			"6. Duplicated DefaultAction:\n" + 
			"======================================================\n";
		s=this.DuplicatedDefaultAction(s);
			
		/**
		if (verbose) s= s + "\n\n" + 
			"======================================================\n" +
			"7. Duplicated DefaultActionRole:\n" + 
			"======================================================\n";
		s=this.DuplicatedDefaultActionRole(s);
		**/	
			
		if (verbose) s= s + "\n\n" + 
			"======================================================\n" +
			"8. NoEntityProgramRole:\n" + 
			"======================================================\n";
		s=this.NoEntityProgramRole(s);
		
		//if (verbose) s= s + "\n\n" + 
		//	"======================================================\n" +
		//	"9. NoDefaultActionRole:\n" + 
		//	"======================================================\n";
		//s=this.NoDefaultActionRole(s);
			
		if (verbose) s= s + "\n\n" + 
			"======================================================\n" +
			"10. NoMenuItemProgram:\n" + 
			"======================================================\n";
		s=this.NoMenuItemProgram(s);
			
		
		/**
		if (verbose) s= s + "\n\n" + 
			"======================================================\n" +
			"11. NoMenuItemActionRole:\n" + 
			"======================================================\n";
		s=this.NoMenuItemActionRole(s);
		**/
		
		if (verbose) s= s + "\n\n" + 
			"======================================================\n" +
			"12. Duplicated Menu Items:\n" + 
			"======================================================\n";
		s=this.DuplicatedMenuItemDescriptions(s);
			
		return s;
	}	
	
	public String checkWarnings(boolean verbose) {
		if (this.cRoleGroups2==null) this.cRoleGroups2 =this.getRolesFromRoleGroup();
		String s="";
		
		/**
		if (verbose) s= s + "\n\n" + 
			"======================================================\n" +
			"9. NoDefaultActionRole:\n" + 
			"======================================================\n";
		s=this.NoDefaultActionRole(s);
		**/	
			
		return s;
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
