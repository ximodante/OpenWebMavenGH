package openadmin.web.components;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;


import javax.el.MethodExpression;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;

import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;

import openadmin.model.Base;
import openadmin.util.lang.LangTypeEdu;

public class PFTable implements Serializable {
	
	private static final long serialVersionUID = 14051802L;

	private LangTypeEdu langType;
	
	private BaseDataModel baseDataModel; 
	
	public PFTable (LangTypeEdu pLangType) {
		
		langType = pLangType;
	}
	
	public <T extends Base> DataTable dataTable01(List<T> lstbase) {
		
		baseDataModel = new BaseDataModel(lstbase);
		
		FacesContext _context = FacesContext.getCurrentInstance();
		
		DataTable table = (DataTable) _context.getApplication().createComponent(DataTable.COMPONENT_TYPE);
		table.setId("idlist");
		table.setRows(10);
		table.setValue(baseDataModel);
		table.setVar("pbase");
		table.setSelectionMode("single");
		table.setResizableColumns(true);
		if (lstbase.size() > 10) table.setPaginator(true);
		table.setDraggableColumns(true);
		//table.setRowKey("#{pbase.description}");
		//table.setSelection("#{ctx.getView(ctx.sizeView()).setBase(pbase)}");
		
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
				if (f.getType().getSimpleName().toLowerCase().endsWith("string")  || 
					f.getType().getSimpleName().toLowerCase().endsWith("integer") ||			
					f.getType().getSimpleName().toLowerCase().endsWith("date")    ||
					f.getType().getSimpleName().toLowerCase().endsWith("long")    || 
					f.getType().getSimpleName().toLowerCase().endsWith("int")     || 
					f.getType().getSimpleName().toLowerCase().endsWith("short")) {	
				
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
								
				
				//add column to data table
				table.getChildren().add(column);
				
		}
		
		MethodExpression meArg = _context.getApplication().getExpressionFactory().createMethodExpression(_context.getELContext(),
				"#{ctx.getView(ctx.numberView()).setBase(pbase)}", null ,new Class<?>[]{Base.class});
		
		MethodExpression me = _context.getApplication().getExpressionFactory().createMethodExpression(_context.getELContext(), 
				"#{ctx.getView(ctx.numberView()).selectRow()}", void.class, new Class<?>[]{SelectEvent.class});
		
		MethodExpression me2 = _context.getApplication().getExpressionFactory().createMethodExpression(_context.getELContext(),
			     "#{ctx.getView(ctx.numberView()).selectRow}", String.class, new Class[]{});
		
		AjaxBehavior ajaxBehavior = new AjaxBehavior();
		ajaxBehavior.setUpdate("form1:idContingut");
		ajaxBehavior.setProcess("@this");
		ajaxBehavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(me2,null));
		table.addClientBehavior("rowSelect", ajaxBehavior);
		
		return table;
		
	}
	
	
}
