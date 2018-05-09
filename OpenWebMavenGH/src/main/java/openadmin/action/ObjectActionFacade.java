package openadmin.action;

import openadmin.model.Base;
import openadmin.model.control.MenuItem;

public interface ObjectActionFacade {
	
	public Base getBase();
	public void setBase(Base pBase);
	public ContextActionEdu getCtx();
	public void setCtx(ContextActionEdu ctx);
	public MenuItem getMenuItem();
	public void setMenuItem(MenuItem pMenuItem);
	public String getMetodo();
	
}
