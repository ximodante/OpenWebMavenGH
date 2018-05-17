package proves.edu;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import lombok.Getter;
import lombok.Setter;
 
@Named(value = "dfView")
@SessionScoped
public class DFView implements Serializable{
     
    @Getter @Setter
	private String tatia="Hola Tia!";
    
	public void viewCars() {
		tatia="mechachis!";
		
        Map<String,Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        PrimeFacesEdu.current().dialog().openDynamic("viewCars", options, null);
        
		//return "viewCars";
    }
     
    public void viewCarsCustomized() {
    	tatia="jolin!";
        
    	Map<String,Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("width", 640);
        options.put("height", 340);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        options.put("headerElement", "customheader");
         
        PrimeFacesEdu.current().dialog().openDynamic("viewCars", options, null);
        
        //RequestContext.getCurrentInstance().openDialog("employee", options, null);
    }
    
    public void showMessage() {
    	tatia="showMessage!";
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "What we do in life", "Echoes in eternity.");
         
        PrimeFacesEdu.current().dialog().showMessageDynamic(message);
    }
    
    public void showMessage1() {
    	tatia="intenta mostrar una pantalla que no existeix!";
                 
        PrimeFacesEdu.current().dialog().openDynamic("viewCarswwwwwwww");
    }
    
    
    
    
}
