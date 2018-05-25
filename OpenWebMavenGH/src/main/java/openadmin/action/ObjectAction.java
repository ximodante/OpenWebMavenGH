package openadmin.action;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

//import org.primefaces.PrimeFaces;
//import org.primefaces.component.outputpanel.OutputPanel;
//import org.primefaces.event.SelectEvent;

import lombok.Getter;
import lombok.Setter;
import openadmin.model.Base;
import openadmin.model.control.MenuItem;
import openadmin.util.edu.ReflectionUtilsEdu;
import openadmin.util.faces.UtilFaces;
import openadmin.util.lang.WebMessages;
import openadmin.util.reflection.SerialClone;
import openadmin.web.components.PFDialog;


public class  ObjectAction implements Serializable, ObjectActionFacade { 
	
	private static final long serialVersionUID = 19091001L;
	
	@Getter
	private Base  base;
	
	@Getter @Setter
	private ContextActionEdu ctx;
	
	@Getter @Setter
	private MenuItem menuItem;
	
	//To edit
	private Base objOriginal;
	
	@Getter
	private String metodo;
	
	private List<Base> lstbase;
	
	
	
	public void _new() {
		
 		System.out.println("ALTA");
 		//if (WebValidator.execute(base)) return;
 		if (this.base.getId() != null ) {
			
			WebMessages.messageError("exist_find_description");
			
			return;
			
		}
 		
		ctx.getConnControl().findObjectDescription(base);
		
		if (ctx.getConnControl().isResultOperation()) {
				
			WebMessages.messageError("exist_find_description");

			return;
				
		}
		
		ctx.getConnControl().begin();
		ctx.getConnControl().persistObject(base);
		ctx.getConnControl().commit();
			
		try {
			//this.base = (Base) ReflectionUtilsEdu.createObject(this.base.getClass());
			this.base = (Base) ReflectionUtilsEdu.createObject(this.base.getClass());
		
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		
		if (ctx.getConnControl().isResultOperation())
		
			WebMessages.messageInfo("operation_new_correct");
		
 		
	}
	
	public void _edit() {
		
 		//if (WebValidator.execute(base)) return;
		
		if (this.base.getId() == null) {
			
			WebMessages.messageError("noexist_find_pk");
			
			return;
			
		}
			    					    				
		ctx.getConnControl().begin();
		ctx.getConnControl().updateObject(objOriginal, base);
		ctx.getConnControl().commit();
		
		if (ctx.getConnControl().isResultOperation()) WebMessages.messageInfo("operation_edit_correct");
			
			
	}
	
	public void _delete() {
		
 		System.out.println("ELIMINA");
	
 		if (this.base.getId() == null) {
						
			WebMessages.messageError("noexist_find_pk");
			
			return;
			
		}
		
		ctx.getConnControl().begin();
		ctx.getConnControl().removeObject(base);
		ctx.getConnControl().commit();
 		
		if (ctx.getConnControl().isResultOperation()) WebMessages.messageInfo("operation_delete_correct");
	}

	public void otherAction(String pAction) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
			
		String pack = base.getClass().getPackage().getName();
		int j=pack.lastIndexOf(".");
		pack=pack.substring(j+1, pack.length());
		Object objAction  = ReflectionUtilsEdu.createObject("openadmin.action." +
   				pack + "." +
   				base.getClass().getSimpleName() + "Action");
   		
		OtherActionFacade action = (OtherActionFacade) objAction;
       	
   		action.execute(pAction, base, ctx);
	}
	
	
	public void _search() {
		
 		System.out.println("BUSCA");
 		List<Base> lstbaseNew = new ArrayList<Base>();
 		
		lstbaseNew = ctx.getConnControl().findObjects(base);
		
		lstbase = SerialClone.clone(lstbaseNew);
 		
		if (lstbase.size() == 0 ) {
			
			WebMessages.messageError("noexist_find_pk");
			
			return;
			
		}
		
		
        PFDialog dialeg = new PFDialog(ctx.getLangType());
 		
        dialeg.dialog01(lstbase);
 		
	}
	

	//Closed dialog, param pDialog: ID component 
	public void closedDialog(String pDialog) {
		
		FacesContext _context = FacesContext.getCurrentInstance();
		UIComponent component = _context.getViewRoot().findComponent("form1");
		
		UIComponent dialog = _context.getViewRoot().findComponent("form1:"+pDialog);
		
		if (null != dialog) {
			
			//PrimeFaces.current().executeScript("PF('widget').hide()");
			//dialog.getChildren().clear();
			UtilFaces.removeComponentOfId(component, pDialog);
		}
		
		
	}

	/*
	public void selectRow(SelectEvent event) {
		
		System.out.println("Selecció fila: " + ((Base) event.getObject()).getId());
		
	}
	*/
	
	public void selectRow() {
		
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		
		Base pBaseMap = (Base) sessionMap.get("idBase");
		
		sessionMap.remove("idBase");
		
		objOriginal = SerialClone.clone(pBaseMap);
		
		ctx.getView(ctx.numberView()).setBase(objOriginal);

		//this.base =  pBaseMap;
		
	}
	
	public void setBase (Base pBase) {
		
		System.out.println("Base: "  + pBase);
		
		objOriginal = SerialClone.clone(pBase);

		metodo = pBase.getClass().getSimpleName().substring(0, 1).toUpperCase() +  pBase.getClass().getSimpleName().substring(1);
		
		this.base =  pBase;
	
	} 
	
	public void clean() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		
		this.base = (Base) ReflectionUtilsEdu.createObject(this.base.getClass().getCanonicalName());
	
	}
	
}
