package openadmin.action.control;

import openadmin.action.ContextActionEdu;
import openadmin.action.OtherActionFacada;
import openadmin.model.Base;

public class RoleAction implements OtherActionFacada {
	
	public void execute(String pAction, Base pBase, ContextActionEdu pCtx){
		
		System.out.println("Acció a calcular: " + pAction);
		
	}
}
