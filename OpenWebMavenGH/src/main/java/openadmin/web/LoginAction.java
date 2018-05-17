package openadmin.web;

//import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Properties;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import openadmin.action.ContextActionEdu;
import openadmin.model.control.User;
import openadmin.util.edu.FileUtilsEdu;
import openadmin.util.edu.PropertyUtilsEdu;
import openadmin.util.lang.LangTypeEdu;
import openadmin.util.lang.WebMessages;

@Named
@RequestScoped
public class LoginAction implements Serializable{
			
	private static final long serialVersionUID = 23031001L;
	
	/** Field that contain the user*/
	@Inject @Getter @Setter
	private User usuari;
	
	@Inject @Getter @Setter
	private ContextActionEdu ctx;
	
	/*
	@Inject @Getter @Setter
	private LangTypeEdu lang;
	*/
	@Getter @Setter
	private String langLogin;
	
	private String result;
		
	public String execute() {
		
		result = "index";
		
		//comprobar usuari i clau
		if (usuari.getDescription().length() < 4 || usuari.getDescription().length() > 15) {
			
			WebMessages.messageError("error_validation_login");
			
		}
		
		else if (usuari.getPassword().length() < 5 || usuari.getPassword().length() > 50) {
			
			WebMessages.messageError("error_validation_login");
			
		}
		
		else if (ctx.login(usuari)){
			
			result = "main";
		
		}
					
		return result;
	}
	
	public void changeLang(ValueChangeEvent e) {
				
		//lang.changeLocale( e.getNewValue().toString());
		FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(e.getNewValue().toString()));
		
		setLangLogin(e.getNewValue().toString());
	}
	
}
