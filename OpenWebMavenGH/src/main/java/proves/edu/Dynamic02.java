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
public class Dynamic02 implements Serializable{
	
	@Getter @Setter
	private OutputPanel outPanel;
	
	private String[] tBarButts= {"New","Edit","Delete"};
	private String[] panels= {"Football","Swimming", "Basketball"};
	
	//(0) Let's fill an output panel with a toolbar(1) and a panel grid(2)
	public Dynamic02() {
		System.out.println("Dynamic02 constructor call");
		outPanel=new OutputPanel();
		outPanel.getChildren().add(myPanelGrid());
		outPanel.getChildren().add(myPanelGrid2());
		
		
		
	}
	
		
	
	
	//(2) Let's create a panel grid with 3 panels
	private OutputPanel myPanelGrid() {
		//return JSFEdu.newPanelGrid("id_PanelGrid",false, this.myPanels());
		String id="id_pgrid";
		return JSFEdu.newComplexContainer(
			id, 
			JSFEdu.newToolbar(id+ "_tb", "Prova Panel Grid", myCButts("1")),
			JSFEdu.newPanelGrid(id, false, myPanels("a")));// First Line
		
	}
	
	//Let's create 3 trivial buttons tBarButts to fill the toolbar
		private List<UIComponent> myCButts(String idPrefix) {
			List<UIComponent>lCB=new ArrayList<UIComponent>();
			for (String s: this.tBarButts) 
				lCB.add(JSFEdu.newCommandButton("id_"+idPrefix+"_" + s.substring(0, Integer.min(s.length(), 5)), s, "#{dynamic01.butAction()}", "msgs", "ui-icon-disk"));
			return lCB;
		}
		
	
	//Let's create 3 panels to fill the panelgrid(2)
	private List<UIComponent> myPanels(String idPrefix) {
		List<UIComponent>lP=new ArrayList<UIComponent>();
		int i=0;
		for (String s: this.panels) {
			lP.add(myPanel(idPrefix+i,s));
			i++;
		}	
		return lP;
	}
	
	private Panel myPanel(String idPrefix, String header) {
		List<UIComponent>lOT=new ArrayList<UIComponent>();
		HtmlOutputText t = new HtmlOutputText();
		t.setValue(header + ": is my Value");
		lOT.add(t);
		String id="id-"+ idPrefix +header.substring(0,Integer.min(header.length(),6));
		return JSFEdu.newPanel(id, header, lOT);
	}
	
	//(2) Let's create a panel grid with 3 panels
	private OutputPanel myPanelGrid2() {
		String id="id_no_grid";
		return JSFEdu.newComplexContainer(
			id, 
			JSFEdu.newToolbar(id+ "_tb22", "Prova Panel Grid 2", myCButts("2")),
			myPanels("22"));
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



