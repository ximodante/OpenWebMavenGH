package openadmin.web.components;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import openadmin.model.Base;

public class BaseDataModel  extends ListDataModel<Base> implements SelectableDataModel<Base>, Serializable {


		private static final long serialVersionUID = 14051803L;
		
	    public BaseDataModel(List<Base> pData) {
	    	
	    	super(pData);
	   
	    }
	
	    @Override
		public Base getRowData(String pDescription) {
			
			System.out.println("Base model: " + pDescription);
			
			@SuppressWarnings("unchecked")
			List<? extends Base> lstBase = (List<? extends Base>) getWrappedData();  
	        
			int comptador = 0;
			
			for(Base pBase : lstBase) { 
	        	
	        	System.out.println("LstBase: " + pBase.getDescription() + " - " + comptador++);
	        	
	            if(pBase.getDescription().equals(pDescription)) {
	            	
	            	System.out.println("Base model select: " + pBase.getDescription());
	            	
	            	Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
	            	
	            	sessionMap.put("idBase", pBase);
	            	
	                return pBase; 
	            	
	            }
	               	
	        } 
			
			return null;
		}

		@Override
		public Object getRowKey(Base pBase) {
					
			return pBase.getDescription();
		}
	
}
