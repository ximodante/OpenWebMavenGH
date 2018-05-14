package openadmin.web.components;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.dialog.Dialog;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.context.RequestContext;

import openadmin.model.Base;
import openadmin.util.faces.UtilFaces;
import openadmin.util.lang.LangTypeEdu;

public class PFDialog {

	private OutputPanel panelDialogo;	
	
private LangTypeEdu langType;
	
	public PFDialog(LangTypeEdu pLangType) {
		
		langType = pLangType;
		
	}
	
	public String  panel01(List<Base> lstbase) {
		
		FacesContext _context = FacesContext.getCurrentInstance();
		//PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(severity, summary, detail));
		Map<String,Object> properties  = new HashMap<String,Object>();
		properties.put("modal", true);
		properties.put("resizable", true);
		properties.put("draggable", true);
		properties.put("width", 600);
		properties.put("height", 600);
		
		System.out.println("Obrint dialog dinamic /pages/dialogs/dialogo01.xhtml" );
		//PrimeFaces.current().dialog().openDynamic("/pages/dialogs/dialogo01.xhtml", properties, null);
		PrimeFaces.current().dialog().openDynamic("/pages/dialogs/viewCars", properties, null);
		//DefaultRequestContext.getCurrentInstance().openDialog("/pages/dialogs/dialogo02.xhtml", properties, null);
		System.out.println("Obert dialog dinamic /pages/dialogs/dialogo01.xhtml" );
		UIComponent component = _context.getViewRoot().findComponent("form1");
		
		//UtilFaces.findComponentOfId(component, "idContingutDialog");
		
		return "";
		
	}
	
	public void panel02(List<Base> lstbase) {
		
		FacesContext _context = FacesContext.getCurrentInstance();	
		Dialog dialog = (Dialog) _context.getViewRoot().findComponent("form1:dialogo01");
		
		UIComponent form = _context.getViewRoot().findComponent("form1");
		
		if (null != dialog) {
			 
			dialog.getChildren().clear();
			UtilFaces.removeComponentOfId(form, "dialogo01");
			
		} else {
			
			dialog =  (Dialog) _context.getApplication().createComponent(Dialog.COMPONENT_TYPE);
			dialog.setId("dialogo01");
		}
		
		System.out.println("Dialogo:  " + dialog);
		
		dialog.setWidgetVar("widget");
		dialog.setHeader("Carles");
		dialog.setVisible(true);
		dialog.setMinimizable(true);
		dialog.setDynamic(true);
		dialog.setFooter("Alex");
		dialog.setDraggable(true);
		dialog.setMinWidth(300);
		dialog.setClosable(true);
		dialog.setModal(true);
		
		
		dialog.getChildren().add(createDataTable(lstbase));
		
		form.getChildren().add(dialog);
		
		//RequestContext request = RequestContext.getCurrentInstance();
		//request.execute("PF('widget').show()");
		//request.update("form1");
		PrimeFaces.current().executeScript("PF('widget').show()");
		PrimeFaces.current().ajax().update("form1");
		
	}
	
	private DataTable createDataTable(List<Base> lstbase) {
		
		FacesContext _context = FacesContext.getCurrentInstance();
		
		DataTable table = (DataTable) _context.getApplication().createComponent(DataTable.COMPONENT_TYPE);
		table.setId("idlist");
		table.setRows(10);
		table.setValue(lstbase);
		table.setVar("pbase");
		table.setSelectionMode("single");
		table.setSelection("#{ctx.getView(ctx.numberView()).selectRow()}");
		table.setRowKey("#{pbase.id}");
		//table.setRowSelectMode(_rowSelectMode);
		
		Base base = lstbase.get(0);
		
		UIOutput tableTitle = (UIOutput)_context.getApplication().createComponent(UIOutput.COMPONENT_TYPE);
		tableTitle.setValue(langType.msgDao(base.getClass().getSimpleName()));
		table.getFacets().put("header", tableTitle);
		
		
		Column column = (Column) _context.getApplication().createComponent(Column.COMPONENT_TYPE);
		
		for (Field f: base.getClass().getDeclaredFields()){				
			
			//Exclusions
			//System.out.println("Atributs:  " + f.getName());	
			if (	f.getName().equals("debuglog") ||
					f.getName().equals("historiclog") ||
					f.getName().equals ("serialVersionUID"))
					continue;
			
			//View of fields 
			f.setAccessible(true);
			
				column  = (Column) _context.getApplication().createComponent(Column.COMPONENT_TYPE);
				
				//New column name
				HtmlOutputText head = new HtmlOutputText();
				
				//Value column head
				head.setValue(langType.msgLabels(base.getClass().getSimpleName(), f.getName()));
				column.setHeader(head);
												
				//add component to column
				//If is String	
				if (f.getType().getSimpleName().endsWith("String") || 
					f.getType().getSimpleName().endsWith("Integer") ||			
					f.getType().getSimpleName().endsWith("Date")    ||
					f.getType().getSimpleName().endsWith("Short")) {	
				
				 HtmlOutputText outText = new HtmlOutputText();
				 
				 outText.setValueExpression("value", _context.getApplication().getExpressionFactory().createValueExpression(
						 _context.getELContext(), 
						 "#{pbase." +  f.getName() + "}",
						  f.getType()));
			
				 column.getChildren().add(outText);
				
				 //Object
				} else if (f.getType().getName().startsWith("openadmin.model")) {
					
					HtmlOutputText outText = new HtmlOutputText();
				 
					outText.setValueExpression("value", _context.getApplication().getExpressionFactory().createValueExpression(
							_context.getELContext(), 
						 "#{pbase." +  f.getName() + ".description}",
						  String.class ));
				
					column.getChildren().add(outText);
				
				}
				
				/**
				MethodExpression me = _context.getApplication().getExpressionFactory().createMethodExpression(_context.getELContext(),
					     "#{ctx.getView(ctx.numberView()).selectRow()}", String.class, new Class[0]);
				
				MethodExpression meArg = _context.getApplication().getExpressionFactory().createMethodExpression(_context.getELContext(),
					     "#{ctx.getView(ctx.numberView()).selectRow()}", String.class, new Class[]{SelectEvent.class});
				
				AjaxBehavior ajaxBehavior = new AjaxBehavior();
				ajaxBehavior.setUpdate("form1");
				ajaxBehavior.setProcess("@this");
				ajaxBehavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(me, meArg));
				table.addClientBehavior("rowSelect", ajaxBehavior);
				*/
				//add column to data table
				table.getChildren().add(column);	
		}
		
		return table;
	}

}
