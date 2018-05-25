package openadmin.action.control;

import openadmin.action.ContextActionEdu;
import openadmin.action.OtherActionFacade;
import openadmin.model.Base;

public class RoleAction  implements OtherActionFacade  {
	
	public <T extends Base> void execute(String pAction, T pBase, ContextActionEdu pCtx){
		
		System.out.println("Acció a calcular: " + pAction);
		
	}
}
