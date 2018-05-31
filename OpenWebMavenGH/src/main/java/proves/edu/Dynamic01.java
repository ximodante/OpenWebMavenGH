package proves.edu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;


import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.toolbar.Toolbar;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Named
@ViewScoped
public class Dynamic01 implements Serializable{
	
	@Getter @Setter
	private OutputPanel outPanel;
	
	private String[] tBarButts= {"New","Edit","Delete"};
	private String[] panels= {"Football","Swimming", "Basketball"};
	
	//(0) Let's fill an output panel with a toolbar(1) and a panel grid(2)
	public Dynamic01() {
		System.out.println("Dynamic01 constructor call");
		outPanel=new OutputPanel();
		
		outPanel.getChildren().add(myToolbar());
		//outPanel.getChildren().add(myPanelin());
		outPanel.getChildren().add(myPanelGrid());
		//outPanel.getChildren().add(myPanelin());
		
		
		
	}
	
	private Panel myPanelin() {
		Panel panel=JSFEdu.newPanel("id_panelin","", myCButts("1"));
		return panel;
	}
	
	//(1) Let's create a toolbar with 3 buttons tBarButts
	private Toolbar myToolbar() {
		Toolbar tBar=JSFEdu.newToolbar("id_toolbar", "Prova toolbar header", myCButts("2"));
		return tBar;
	}
	
	//Let's create 3 buttons tBarButts to fill the toolbar
	private List<UIComponent> myCButts(String idPrefix) {
		List<UIComponent>lCB=new ArrayList<UIComponent>();
		for (String s: this.tBarButts) 
			lCB.add(JSFEdu.newCommandButton("id_"+idPrefix+"_" + s.substring(0, Integer.min(s.length(), 5)), s, "#{dynamic01.butAction()}", "msgs", "ui-icon-disk"));
		return lCB;
	}
	
	//(2) Let's create a panel grid with 3 panels
	private PanelGrid myPanelGrid() {
		return JSFEdu.newPanelGrid("id_PanelGrid",false, this.myPanels());
	}
	
	//Let's create 3 panels to fill the panelgrid(2)
	private List<UIComponent> myPanels() {
		List<UIComponent>lP=new ArrayList<UIComponent>();
		for (String s: this.panels) {
			List<UIComponent>lOT=new ArrayList<UIComponent>();
			HtmlOutputText t = new HtmlOutputText();
			t.setValue(s + ": is muy Value");
			lOT.add(t);
			lP.add(JSFEdu.newPanel("id-"+s.substring(0,Integer.min(s.length(),6)), s, lOT));
		}	
		return lP;
	}
	
	
	public void butAction(ActionEvent actionEvent) {
        addMessage("Welcome to Primefaces!!");
    }
	
	public void butAction() {
        addMessage("Welcome to Primefaces now!!");
    }
     
    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}


