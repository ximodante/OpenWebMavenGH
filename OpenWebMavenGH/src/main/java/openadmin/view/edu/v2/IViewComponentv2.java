package openadmin.view.edu.v2;

import java.util.List;

public interface IViewComponentv2 {
	public String getId();
	public void setId(String id);
	
	public List<CmpAttribute> getAttributes();
	public void setAttributes(List<CmpAttribute>lsatAtt);
	
}
