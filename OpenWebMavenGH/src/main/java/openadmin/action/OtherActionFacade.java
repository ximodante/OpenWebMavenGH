package openadmin.action;

import openadmin.model.Base;

public interface OtherActionFacade <T extends Base>{
	 
	public void execute (String pAction, T pBase, ContextActionEdu ctx);
	
}
