package openadmin.action.control;

import openadmin.action.ContextActionEdu;
import openadmin.action.OtherActionFacade;
import openadmin.model.Base;

public class RoleAction <T extends Base> implements OtherActionFacade <T> {
	
	public void execute(String pAction, T pBase, ContextActionEdu pCtx){
		
		System.out.println("Acció a calcular: " + pAction);
		
	}
}
