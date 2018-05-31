package proves.edu;

import java.util.List;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.BehaviorEvent;

import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.toolbar.Toolbar;
import org.primefaces.component.toolbar.ToolbarGroup;

public class JSFEdu {
	/*******************************************************************
	 * 01 HELPERS
	 *******************************************************************/
	/**
	/**
	 * 
	 * @param expression
	 * @param returnType
	 * @param parameterTypes
	 * @return
	 * 
	 * Example:
	 *   newMethodExpression(String.format("#{bean.submit('%s')}", id), null, String.class));
	 */
	public static MethodExpression newMethodExpression(String expression, Class<?> returnType, Class<?>... parameterTypes) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext.getApplication().getExpressionFactory().createMethodExpression(
            facesContext.getELContext(), expression, returnType, parameterTypes);
    }
	/**
	 * Helper to get an ajax behavior
	 * @param elExpression Bean.method to handle the event
	 * @param ComponentsToUpdate
	 * @return
	 * 
	 * Example to use it:
	 * 
	 * PFComponent.addClientBehavior("reorder", getAjaxBehavour("#{dashboardBean02.handleReorder}","msgs"));
	 * 
	 */
	public static AjaxBehavior newAjaxBehavour(String elExpression, String ComponentsToUpdate) {
		System.out.println("getAjaxBehavour call ");
		FacesContext fc = FacesContext.getCurrentInstance();
		ExpressionFactory ef = fc.getApplication().getExpressionFactory();

		MethodExpression me = ef.createMethodExpression(fc.getELContext(), elExpression, null, new Class<?>[]{BehaviorEvent.class});
		AjaxBehavior ajaxBehavior = (AjaxBehavior) fc.getApplication().createBehavior(AjaxBehavior.BEHAVIOR_ID);
		ajaxBehavior.setProcess("@this");
		ajaxBehavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(me, me));
		ajaxBehavior.setUpdate(ComponentsToUpdate);
		return ajaxBehavior;
	}
	/*******************************************************************
	 * 02 COMPONENT CREATION
	 *******************************************************************/
	/**
	 * Create a responsive PanelGrid of several columns and fills components
	 * Can have several rows
	 * @param _columns number of columns
	 * @return
	 */
	public static PanelGrid newPanelGrid (String id, boolean isGrid, int nCols, List<UIComponent>lstComponents) {
		
		PanelGrid pGrid=new PanelGrid();
		pGrid.setId(id);
		
		if (nCols>0) pGrid.setColumns(nCols);
		else pGrid.setColumns(lstComponents.size());
		
		if (isGrid) pGrid.setLayout("grid"); // responsive
		
		for (UIComponent uiComp: lstComponents) pGrid.getChildren().add(uiComp);
		
		return pGrid;
	}
	
	/**
	 * Create a responsive PanelGrid adding a component in a column
	 * It has aonly one row
	 * @param _columns number of columns
	 * @return
	 */
	public static PanelGrid newPanelGrid (String id, boolean isGrid, List<UIComponent>lstComponents) {
		return newPanelGrid(id, isGrid, 0, lstComponents);
	}
	
	/**
	 * Creates a toolbar with some components 
	 * @param id
	 * @param lstComponents
	 * @return
	 */
	public static Toolbar newToolbar(String id, String header, List<UIComponent>lstComponents) {
		Toolbar tBar=new Toolbar();
		tBar.setId(id);
		tBar.getChildren().add(newToolbarGroup(id+"_1",header));
		tBar.getChildren().add(newToolbarGroup(id+"_2",lstComponents));
		return tBar;
	}
	
	/**
	 * ToolbarGroup (with butttons to the right) to add to a Toolbar 
	 * @param id
	 * @param lstComponents
	 * @return
	 */
	public static ToolbarGroup newToolbarGroup(String id, List<UIComponent>lstComponents) {
		ToolbarGroup tBGroup =new ToolbarGroup();
		tBGroup.setAlign("right");
		//tBGroup.setAlign("center");
		//tBGroup.setAlign("justify");
		for (UIComponent uiComp: lstComponents) 
			tBGroup.getChildren().add(uiComp);
		return tBGroup;
	}

	/**
	 * Toolbargroup (with a header to the left) to add to a toolbar
	 * @param id
	 * @param header
	 * @return
	 */
	public static ToolbarGroup newToolbarGroup(String id, String header) {
		ToolbarGroup tBGroup =new ToolbarGroup();
		tBGroup.setAlign("left");
		tBGroup.getChildren().add(newHtmlOutputText(header));
		return tBGroup;
	}

	/**
	 * Create a simpel text
	 * @param text
	 * @return
	 */
	public static HtmlOutputText newHtmlOutputText(String text) {
		HtmlOutputText t =new HtmlOutputText();
		t.setValue(text);
		return t;
	}	
	
	
	
	/**
	 * Creates a button whose expresion accepts one string and returns no value
	 * @param id
	 * @param actionExpression
	 * @return
	 * 
	 * Example:
	 * newCommandButton("bt01",String.format("#{bean.submit('%s')}", id) ) {
	 */
	public static CommandButton newCommandButton(String id, String value, String actionExpression, String updateComponents, String icon) {
		
		CommandButton cBt=new CommandButton();
		
		cBt.setId(id);
		
		if (value != null & value.length()>0) 
			cBt.setValue(value);
		
		cBt.setActionExpression(
			// Method that accepts a string and return nothing (void)
			newMethodExpression(actionExpression, null, String.class));
		
		if (updateComponents != null & updateComponents.length()>0)
			cBt.setUpdate(updateComponents);
		
		if (icon != null & icon.length()>0)
			cBt.setIcon(icon);
		return cBt;
	}
	
	/**
	 * Creates a panel with components
	 * @param id
	 * @param lstComponents
	 * @return
	 */
	public static Panel newPanel (String id, String header, List<UIComponent>lstComponents) {
		Panel panel=new Panel();
		panel.setId(id);
		panel.setHeader(header);
		for (UIComponent uiComp: lstComponents) 
			panel.getChildren().add(uiComp);
		return panel;
	}
	
	/*******************************************************************
	 * 03 COMPLEX COMPONERT CREATION
	 *******************************************************************/
	/**
	 * Create an outputPanel with: 
	 *   1. A toolbar on top that 
	 *   2. another container (for instance panel, panelgrid etc)
	 *   
	 * @param id
	 * @param tb
	 * @param container
	 * @return
	 */
	public static OutputPanel newComplexContainer(String id, Toolbar tb, UIComponent container) {
		OutputPanel op=new OutputPanel();
		op.getChildren().add(tb);
		op.getChildren().add(container);
		return op;
	}
	
	/**
	  * Create an outputPanel with: 
	 *   1. A toolbar on top that 
	 *   2. A list of containerso components to display each in a different line
	 *   
	 * @param id
	 * @param tb
	 * @param containers
	 * @return
	 */
	public static OutputPanel newComplexContainer(String id, Toolbar tb, List<UIComponent> containers) {
		OutputPanel op=new OutputPanel();
		op.getChildren().add(tb);
		for (UIComponent ui: containers) op.getChildren().add(ui);
		return op;
	}
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

