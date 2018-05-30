package proves.edu;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.BehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;


import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Named
@ViewScoped
public class DashboardBean02 implements Serializable{
	@Getter @Setter
	private OutputPanel outPanel;
	private String[] titles= {"Finance","Sport","News","Cooking","Technology"};
	
	
	
	public DashboardBean02() {
		System.out.println("DashboardBean02 constructor call");
		outPanel=new OutputPanel();
		outPanel.getChildren().add(this.getDashbard());
	}
	
	/*
	@PostConstruct
	public void init() {
	}
	*/
	private Dashboard getDashbard() {
		System.out.println("getDashbard call ");
		Dashboard dBoard= new Dashboard();
		dBoard.setId("board");
		dBoard.addClientBehavior("reorder", getAjaxBehavour("#{dashboardBean02.handleReorder}","msgs"));
		for (String s: titles) {
			System.out.println("Adding panel " + s );
			Panel panel=new Panel();
			panel.setId(s);
			panel.setHeader(s + " Header");
			panel.setToggleable(true);
			HtmlOutputText t = new HtmlOutputText();
			t.setValue(s + ": is muy Value");
			panel.getChildren().add(t);
			dBoard.getChildren().add(panel);
		}
		
		dBoard.setModel(this.getDBModel());
		return dBoard;
	}
	
	/**
	 * Helper to get an AjaxBehaviour
	 * @param elExpression
	 * @return
	 */
	private AjaxBehavior getAjaxBehavour(String elExpression, String ComponentsToUpdate) {
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
	/**
	 * Helper to get a DashboardModel
	 * @return
	 */
	private DashboardModel getDBModel() {
		System.out.println("getDBModel call ");
	    
		DashboardModel dBModel= new DefaultDashboardModel();
		
	    // Initialize the dashboard column #1
		DashboardColumn column1 = new DefaultDashboardColumn();
		// Initialize the dashboard column #2
		DashboardColumn column2 = new DefaultDashboardColumn();
		// Initialize the dashboard column #3
		DashboardColumn column3 = new DefaultDashboardColumn();

		// Add widgets into column1
		column1.addWidget("Sports");
		column1.addWidget("Technology");
		
		// Add widgets into column2
		column2.addWidget("Finance");
		column2.addWidget("Cooking");	
		
		// Add widget into column3
		column3.addWidget("News");

		// Add columns into your model
		dBModel.addColumn(column1);
		dBModel.addColumn(column2);
		dBModel.addColumn(column3);
		
		return dBModel;

	}
	
	public void handleReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex()+ ", Column index: " + event.getColumnIndex() 
				+ ", Sender index: " + event.getSenderColumnIndex());

		addMessage(message);
	}
	
	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}
