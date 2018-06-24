package openadmin.web.components;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultSubMenu;

import openadmin.model.control.MenuItem;
import openadmin.util.lang.LangTypeEdu;

public class PFPanelMenuEdu implements Serializable {

	private static final long serialVersionUID = 11081501L;
	
	private LangTypeEdu langType;
	
	public PFPanelMenuEdu(LangTypeEdu pLangType) {
		
		langType = pLangType;
		
	}
	
	//public DefaultSubMenu itemPare(MenuItem pMenuItem, Set<MenuItem> lstMenuItem) {
	public DefaultSubMenu itemPare(MenuItem pMenuItem, List<MenuItem> lstMenuItem) {
		 
		 DefaultSubMenu submenu = new DefaultSubMenu(langType.msgGenerals(pMenuItem.getDescription()));
		 
		 for (MenuItem vr: lstMenuItem ){
			 
			//if (pMenuItem.equals(vr.getParent()) && vr.getTypeNode().equals("c")){
			 if (pMenuItem.equals(vr.getParent()) && vr.getType()!=0){	 
				 
				//Calls the method loadChild
				 submenu.addElement(itemFill(vr));
			 }
			 
			 //else if (pMenuItem.equals(vr.getParent()) && vr.getTypeNode().equals("p")){
			 else if (pMenuItem.equals(vr.getParent()) && vr.getType()==0){	 
				//Calls again the method loadParent
				 submenu.addElement(itemPare(vr, lstMenuItem));
			 }
		 }
		 
		 return submenu;
	 }
	
		
	 /**
	  * <desc>class that load the panel menu item</desc>
	  * @author 			Alfred Oliver
	  * @param pMenuItem    Class MenuItem
	  * @param pRol  		id rol
	  */
	 public DefaultMenuItem itemFill(MenuItem pMenuItem) {
		 
		 
		 DefaultMenuItem item = new DefaultMenuItem(langType.msgGenerals(pMenuItem.getDescription())); 
		 		 
		 //item.setIcon("ui-icon-newwin " + pMenuItem.getIcon());
		 //item.setIcon( pMenuItem.getIcon()+ " fa fa-user");
		 
		 //item.setIcon(" <span class=\"fa-stack fa-2x\"> " +
		 //                  " <i class=\"fas fa-camera fa-stack-1x\"></i> " + 
		 //                  " <i class=\"fas fa-ban fa-stack-2x\" style=\"color:Tomato\"></i> " +  
		 //              " </span>");
		 //item.setIcon(" <i class=\"fas fa-camera fa-stack-1x\"></i> " + 
		 //	  	      " <i class=\"fas fa-ban fa-stack-2x\" style=\"color:Tomato\"></i> ");
		 
		 //item.setIcon(" fas fa-camera fa-stack-1x " + 
		 //	          " fas fa-ban fa-stack-2x ");
	 
		 item.setIcon( pMenuItem.getIcon());
		 item.setId("ida" + pMenuItem.getDescription());
		 item.setCommand("#{main.loadScreen(" + pMenuItem.getId() + ")}");
		 item.setUpdate("form1:idContingut");
		 //item.setUpdate("frmplan1:rutaPrograma frmplan1:idContingut");

		 return item;
		 
	 } 
	 
}
