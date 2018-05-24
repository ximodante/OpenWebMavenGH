package openadmin.web;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


import javax.enterprise.context.SessionScoped;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import lombok.Getter;
import lombok.Setter;
import openadmin.action.ContextActionEdu;

import openadmin.model.Base;
import openadmin.model.control.Access;
import openadmin.model.control.Action;
import openadmin.model.control.ActionViewRole;
import openadmin.model.control.EntityAdm;
import openadmin.model.control.MenuItem;
import openadmin.model.control.Program;
import openadmin.model.control.Role;

import openadmin.util.edu.ReflectionUtilsEdu;
import openadmin.util.lang.LangTypeEdu;
import openadmin.util.reflection.ReflectionField;
import openadmin.web.components.PFMenuBarEdu;

import openadmin.web.components.PFPanelMenuEdu;
import openadmin.web.view.DefaultViewEdu;

import openadmin.web.view.ViewFacadeEdu;



@Named (value = "main")
@SessionScoped
public class MainScreenEdu implements Serializable {
	
	// Atributs
	private static final long serialVersionUID = 6081501L;
	
	private HtmlOutputText programaActual;
	
	private EntityAdm activeEntity;
	
	//private Role activeRol;
	
	List<Access> lstAccess;
	
	Set<EntityAdm> entities; //EDU
	PFMenuBarEdu pfMenuBar; //edu

	/** Field that contain the connection*/
	@Inject
	private ContextActionEdu ctx;
	
	@Inject
	private LangTypeEdu lang;

	@Setter
	private MenuModel menuBar=null;
	
	@Getter @Setter
	private MenuModel  menuLateral;
	
	private boolean visualiza = false;
	
	//Fi atributs

	/**
	@PostConstruct
    public void init() {
		System.out.println("Inicializando.....");
		getMenuBar();
		System.out.println("Se acabo la inicializacion..........");
	}
	*/
	///////////////////////////////////////////////////////////////////
	//                    Per generar el menu horizontal
	///////////////////////////////////////////////////////////////////
	/**<desc> Genera el menu amb les entitats, aplicacions i altres accions</desc>
 	 * @return Menubar
 	 */		
	public MenuModel  getMenuBar() {
		System.out.println("CTX1=" + ctx.toString());
		//if (menuBar==null ) {
			menuBar = new DefaultMenuModel();
		
			//PFMenuBarEdu pfMenuBar = new PFMenuBarEdu(lang);
			pfMenuBar = new PFMenuBarEdu(lang);

			//List <String[]> lst = new ArrayList<String[]>();
		
			// ****************** Genera els items **********************
			// El String te tres entrades valor, acci� i icona
			// Si el valor d'acci� �s 0 �s un grup
			//Per finalizar el grup l'acci� val 1
				
			//Es crea el logout
			//String[] file = new String[] {MessagesTypes.msgActions("exit"), "#{ctx.logout}", "ui-icon-extlink"};
			//lst.add(file);
		
			//Add item 
			//menuBar.addElement(PFMenuBar.subMenuSimple(MessagesTypes.msgActions("Archivo"), lst));

			// ***************   Genera el submenu de les aplicacions ********************

			//Set<EntityAdm> entities = ctx.getMapEntityAccess().keySet();		
			entities = ctx.getMapEntityAccess().keySet(); //EDU
		
			//if there are two o more entities
			if (entities.size() > 1) {
			 
				menuBar.addElement(pfMenuBar.menuEntities("entities", entities));
			 
							 
			}
		
			//if there is an entity, no entity is displayed and go straight to the program menu
			if (entities.size() == 1) {
						 
				if (null == activeEntity)  activeEntity = entities.stream().findFirst().get();
				
				loadMenuItems(activeEntity.getId()); //EDU
				/*
				lstAccess = ctx.getMapEntityAccess().get(activeEntity);
			
				//If there is a program go to the first program
				if (lstAccess.size() == 1) {
				
					Access vaccess = lstAccess.stream().findFirst().get();
				
					loadMenuItems(vaccess.getRole().getId(), vaccess.getProgram().getId());
				
			
				} else menuBar.addElement(pfMenuBar.menuPrograms("programs", lstAccess));
			 
			    */
			
			}
		
			menuBar.generateUniqueIds();
		
		//}	
		return menuBar;
	
	}
	
	//Edu Begin
	public void loadMenuItems(long pEntity) {
		System.out.println("LOADMENUITEMS-"+pEntity);
		activeEntity = entities.stream().filter(e -> e.getId()==pEntity).findFirst().get();
		
		lstAccess = ctx.getMapEntityAccess().get(activeEntity);
	
		//If there is a program go to the first program
		if (lstAccess.size() == 1) {
		
			Access vaccess = lstAccess.stream().findFirst().get();
			System.out.println("ENTITYYYY="+vaccess.getEntityAdm().getId());
		
			loadMenuItems(vaccess.getRole().getId(), vaccess.getProgram().getId());
		
	
		} else menuBar.addElement(pfMenuBar.menuPrograms("programs", lstAccess));
	 
	
	}
	//Edu Fin
    
	public void loadMenuItems(long pRol, long pProgram) {
		
		menuLateral = new DefaultMenuModel();
		
		PFPanelMenuEdu pPFPanelMenu = new PFPanelMenuEdu(lang);
		
		Role rol = 	new Role();
		rol.setId(pRol);
		
		//activeRol = ctx.getConnControl().findObjectPK(rol);
		
		
		//Current Rol
		ctx.setActiveRol(ctx.getConnControl().findObjectPK(rol));
		
		Program program = new Program();
		program.setId(pProgram);
		
		program = ctx.getConnControl().findObjectPK(program);
		 
		ActionViewRole actionViewRole = new ActionViewRole();		 
		actionViewRole.setRole(ctx.getActiveRol());
		
		Set<MenuItem> lstMenuItems = 
				ctx.getConnControl().findObjects(actionViewRole).stream()
				.map(ActionViewRole::getMenuItem).collect(Collectors.toCollection(TreeSet::new));
		
		
		//Registra en el log el nom del programa seleccionat
	    if (lstMenuItems.size() > 0){
				
	    	//ctx.getLog().changeProgram(program.getDescription());
	    	
			
	    }
				
		//Seleccioa si es pare  o fill
	    for (MenuItem vr: lstMenuItems){
					
				
	    	if (vr.getTypeNode().equals("c") && null == vr.getParent() ){
	    		
	    		//Calls the method loadChild
	    		menuLateral.addElement(pPFPanelMenu.itemFill(vr));
	    		 
	    	}
						
	    	else if (vr.getTypeNode().equals("p") && null == vr.getParent()) {
			
			  //Calls the method loadParent
	    		menuLateral.addElement(pPFPanelMenu.itemPare(vr, lstMenuItems));
	    		
	    	}
	    }
	    
	    menuLateral.generateUniqueIds();
		
	}
	/*****************************************************************************************************************************
	 *
	 *              Load screen
	 ********************************************************************************************************************************/
public void loadScreen(long pMenuItem) 
		throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
	
	System.out.println("Model directe");
	
	if (ctx.numberView() > 0) ctx.deleteAllView();
	
	MenuItem menuItem = new MenuItem();
	menuItem.setId(pMenuItem);
	menuItem = ctx.getConnControl().findObjectPK(menuItem);
	
	screen(menuItem, null);

}

public <T extends Base> void  loadScreenRecursive(String pMenuItem) 
		throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
	

	MenuItem menuItem = new MenuItem();
	menuItem.setDescription(pMenuItem);
	menuItem = ctx.getConnControl().findObjectDescription(menuItem);
	
	//Objecte actual
	T _obj = ctx.getView(ctx.numberView()).getBase();
	
	//Objecte a crear
	T obj = (T) ReflectionUtilsEdu.createObject(menuItem.getClassName().getDescription());
	
	//Find object if is instance
	if (null != _obj){
					
		obj = ReflectionField.copyObject2(_obj, (T)obj);
		
	}
	
	
	screen(menuItem, obj);

}

public void exitScreenRecursive() 
		throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
			

	Base _obj = ctx.getView(ctx.numberView()).getBase();
			
	ReflectionField refl = new ReflectionField();
	
	Base pObejectCopy = refl.copyObject(_obj, ctx.getView(ctx.numberView() - 1).getBase(), ctx.getView(ctx.numberView()).getMetodo());
	
	ctx.deleteView();
	
	MenuItem menuItem = ctx.getView(ctx.numberView()).getMenuItem();
	
	ctx.deleteView();
	
	screen(menuItem, pObejectCopy);
	
}

public void screen(MenuItem pMenuItem, Object obj) 
		throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException{
	
	//Delete screen
	FacesContext _context = FacesContext.getCurrentInstance();	
	OutputPanel outView = (OutputPanel)_context.getViewRoot().findComponent("form1:idContingut");
	
	//System.out.println("Llista main: " + outView.getChildren());
	
	//System.out.println("Nom vista: " + viewDefault);
	
	if (outView.getChildCount() > 0) {
		
		System.out.println("Esborra component del contingut");
		
		outView.getChildren().clear();
	}
	
	//Action view
	ActionViewRole actionViewRole = new ActionViewRole();
	actionViewRole.setMenuItem(pMenuItem);
	actionViewRole.setRole(ctx.getActiveRol());
	
	List<Action> lstActionView = 
	ctx.getConnControl().findObjects(actionViewRole).stream()
	.map(ActionViewRole::getAction)
	.collect(Collectors.toList());
	
	//Create object
	if (null == obj) {
		
		obj = ReflectionUtilsEdu.createObject(pMenuItem.getClassName().getDescription());
		
	}
	
	//Default View
	if (pMenuItem.getViewType().equals("default")) {
				
		Integer numberView = ctx.numberView()+1;
		ViewFacadeEdu view = new DefaultViewEdu();
		view.setCtx(ctx);
		view.setBase((Base) obj);
		view.execute(lang, numberView, lstActionView);
		outView.getChildren().add(view.getOutPanel());
		
		System.out.println("Afegeix outputpanel + vista: " + numberView);
		ctx.setView(numberView, view);
				
	}	
	
	
}

/**
public void closedScreen() {
	
	Base _obj = ctx.getView(ctx.numberView()).getBase();
	
	if (null != _obj && ctx.numberView() > 1){
		
		//FacesContext _context = FacesContext.getCurrentInstance();
		
		//OutputPanel outView = (OutputPanel)_context.getViewRoot().findComponent("form1:idContingut");
		
		if (outView.getChildCount() > 0) {
			
			
			System.out.println("Esborra 2 component del contingu eixir");
			
			outView.getChildren().clear();
			
		}
		
		ctx.deleteView();
		outView.getChildren().add(ctx.getView(ctx.numberView()).getOutPanel());
	}
}*/


/**
public void carregaMenuLateral (String pId, String pValor) {
	 

    //Actualiza panel informaci� del programa
	programaActual.setValue(entitatActual + " - " + pValor);
    
	programa = entitatActual + " - " + pValor;
	
	menuLateral = new DefaultMenuModel();
	
	
    //Load of menu items
    Set<MenuItem> listMenuItem = new TreeSet<MenuItem>();
    ActionViewRole actionViewRole = new ActionViewRole();
  		
    for (Base b : ctx.getLoadMenuItems(pId)) {
  			
    	actionViewRole = (ActionViewRole)b;
    	listMenuItem.add(actionViewRole.getMenuItem());

    }
    
    //Registra en el log el nom del programa seleccionat
    if (listMenuItem.size() > 0){
			
    	ctx.setRolDefault(actionViewRole.getRole());
    	ctx.getLog().changeProgram(actionViewRole.getRole().getProgram().getDescription());
		
    }
  
    //Seleccioa si es pare  o fill
    for (MenuItem vr: listMenuItem){
			
			
    	System.out.println("MENU ITEM NODE: " + vr.getTypenode());
			
    	if (vr.getTypenode().equals("c") && vr.getParent() == null ){
					
    		//Calls the method loadChild
    		menuLateral.addElement(PFPanelMenu.itemFill(vr, actionViewRole.getRole().getId()));
    		
    	}
					
    	else if (vr.getTypenode().equals("p") && vr.getParent() == null ) {
								
		  //Calls the method loadParent
    		menuLateral.addElement(PFPanelMenu.itemPare(vr, listMenuItem, actionViewRole.getRole().getId()));
    		
    	}			
  }

    menuLateral.generateUniqueIds();
    System.out.println("Elements MENU ITEM : " + menuLateral.getElements().size() );	    
		
}


public void carregaVista (String pId, String pValor) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
	
	if (pId == null) return;
	
	programaActual.setValue(programa + " - " + pValor);
	
	//Find all Action view role
	List<Base> loadActionView = ctx.getLoadActionsView(pId);
	
	InterceptorVista interceptor = new InterceptorVista();
	ctx.borraTotesVistes();
	interceptor.carregaVista(loadActionView, ctx, 1);
	
}


public ContextAction getCtx() {
	return ctx;
}

public void setCtx(ContextAction ctx) {
	this.ctx = ctx;
}

public HtmlOutputText getProgramaActual() {
	
	//La primera vegada carrega la entitat 
	entitatActual = MessagesTypes.msgGenerals("aplicacioActual") +  MessagesTypes.msgGenerals(ctx.getUser().getEntityDefault());
				
	return JSFComponents.HtmlOutputText01(MessagesTypes.msgGenerals("aplicacioActual") +  MessagesTypes.msgGenerals(ctx.getUser().getEntityDefault())); 

}

public void setProgramaActual(HtmlOutputText programaActual) {
	
	this.programaActual = programaActual;
}
*/
}
