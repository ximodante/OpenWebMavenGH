package openadmin.action;

import openadmin.model.Base;

public interface OtherActionFacade {
	 
	public <T extends Base> void execute (String pAction, T pBase, ContextActionEdu ctx);
	
}
