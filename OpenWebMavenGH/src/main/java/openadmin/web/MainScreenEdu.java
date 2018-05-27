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

			// ***************   Genera el submenu de les aplicacions ********************

			Set<EntityAdm> entities = ctx.getMapEntityAccess().keySet();		
					
			//if there are two o more entities
			if (entities.size() > 1) {
			 
				menuBar.addElement(pfMenuBar.menuEntities("entities", entities));
			 
							 
			}
		
			if (null == activeEntity)  activeEntity = entities.stream().findFirst().get();
			
			lstAccess = ctx.getMapEntityAccess().get(activeEntity);
				
			//If there is an program
			if (lstAccess.size() == 1) {
					
				Access vaccess = lstAccess.stream().findFirst().get();
					
				loadMenuItems(vaccess.getRole().getId(), vaccess.getProgram().getId());
				
			} else menuBar.addElement(pfMenuBar.menuPrograms("programs", lstAccess));
				 
			 
			menuBar.generateUniqueIds();
			
			return menuBar;
		
		}

     public void selectActiveEntity(Long pEntity){
		
		EntityAdm pEntityAdm = new EntityAdm();
		pEntityAdm.setId(pEntity);
		
		activeEntity = ctx.getConnControl().findObjectPK(pEntityAdm);
		getMenuBar();
		
		//Actualitzar el context
	}
	
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
					
				
	    	//if (vr.getTypeNode().equals("c") && null == vr.getParent() ){
	    	// If node is not a submenu and has no parent
	    	if (vr.getType()!=0  && null == vr.getParent() ){
	    		
	    		//Calls the method loadChild
	    		menuLateral.addElement(pPFPanelMenu.itemFill(vr));
	    		 
	    	}
						
	    	//else if (vr.getTypeNode().equals("p") && null == vr.getParent()) {
	    	//If node is a submenu and has no parent
	    	else if (vr.getType()==0 && null == vr.getParent()) {
			
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
		@SuppressWarnings("unchecked")
		T _obj = (T) ctx.getView(ctx.numberView()).getBase(); 
	
		//Objecte a crear
		@SuppressWarnings("unchecked")
		T obj = (T) ReflectionUtilsEdu.createObject(menuItem.getClassName().getDescription());
	
		//Find object if is instance
		if (null != _obj){
					
			obj = ReflectionField.copyObject2(_obj, obj);
		
		}
	
	
		screen(menuItem, obj);

	}

	public <T extends Base> void exitScreenRecursive() 
		throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
			
		@SuppressWarnings("unchecked")
		T _obj = (T) ctx.getView(ctx.numberView()).getBase();
			
		//ReflectionField refl = new ReflectionField();
		//T pObejectCopy = refl.copyObject(_obj, ctx.getView(ctx.numberView() - 1).getBase(), ctx.getView(ctx.numberView()).getMetodo());
		@SuppressWarnings("unchecked")
		T pObejectCopy = (T) ReflectionField.copyObject(_obj, ctx.getView(ctx.numberView() - 1).getBase(), ctx.getView(ctx.numberView()).getMetodo());
	
		ctx.deleteView();
	
		MenuItem menuItem = ctx.getView(ctx.numberView()).getMenuItem();
	
		ctx.deleteView();
	
		screen(menuItem, pObejectCopy);
	
	}

	@SuppressWarnings("unchecked")
	public <T extends Base> void screen(MenuItem pMenuItem, Object obj) 
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
		//if (pMenuItem.getViewType().equals("default")) {
		if (pMenuItem.getType()==1) {
				
			Integer numberView = ctx.numberView()+1;
			ViewFacadeEdu view = new DefaultViewEdu();
			view.setCtx(ctx);
			view.setBase((T) obj);
			view.execute(lang, numberView, lstActionView);
			outView.getChildren().add(view.getOutPanel());
		
			System.out.println("Afegeix outputpanel + vista: " + numberView);
			ctx.setView(numberView, view);
				
		}	
	
	
	}


}
