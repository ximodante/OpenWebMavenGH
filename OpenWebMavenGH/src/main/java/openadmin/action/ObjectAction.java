package openadmin.action;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.event.SelectEvent;

import lombok.Getter;
import lombok.Setter;
import openadmin.model.Base;
import openadmin.model.control.MenuItem;
import openadmin.util.edu.ReflectionUtilsEdu;
import openadmin.util.lang.WebMessages;
import openadmin.util.reflection.SerialClone;
import openadmin.web.components.PFDialog;


public class ObjectAction implements Serializable, ObjectActionFacade{
	
	private static final long serialVersionUID = 19091001L;
	
	@Getter
	private Base base;
	
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
		
 		System.out.println("MODIFICA");
	
	}
	
	public void _delete() {
		
 		System.out.println("ELIMINA");
	
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
 		
 		dialeg.panel01(lstbase);;
 		
	}
	

	public void selectRow(SelectEvent event) {
		
		System.out.println("Selecci� fila: " + ((Base) event.getObject()).getId());
		
	}
	
	public void selectRow(Base pBase) {
		
		System.out.println("Selecci� fila2: " + pBase.getId());
		
	}
	
	public void setBase(Base pBase) {
		
		objOriginal = SerialClone.clone(pBase);

		this.base =  pBase;
	
	} 
	
	//Copy object
	public void _exit() {
		
			
		Base _obj = ctx.getView(ctx.numberView()).getBase();
			
		if (null != _obj && ctx.numberView() > 1){
						
			//ReflectionField refl = new ReflectionField();
				
			//refl.copyObject(_obj, ctx.getView(ctx.numberView() - 1).getBase(), ctx.getView(ctx.numberView() - 1).getMetodo());
			
			FacesContext _context = FacesContext.getCurrentInstance();
				
			OutputPanel outView = (OutputPanel)_context.getViewRoot().findComponent("form1:idContingut");
			
			System.out.println("Llista exit: " + outView.getChildren().get(0).getId());
		
			if (outView.getChildCount() > 0) {
				
				
				System.out.println("Esborra component del contingu eixir");
				System.out.println(outView.getChildren().size());
				outView.getChildren().clear();
				System.out.println(outView.getChildren().size());
			}
			System.out.println(outView.getChildren().size());
			ctx.deleteView();
			System.out.println(outView.getChildren().size());
			System.out.println("Vista: " + ctx.getView(ctx.numberView()));
			outView.getChildren().add(ctx.getView(ctx.numberView()).getOutPanel());
				
		}
			
	}

}
