package openadmin.web.components;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import openadmin.model.Base;

public class BaseDataModel <T extends Base>  extends ListDataModel<T> implements SelectableDataModel<T>, Serializable {


		private static final long serialVersionUID = 14051803L;
		
	    public BaseDataModel(List<T> pData) {
	    	
	    	super(pData);
	   
	    }
	
	    @Override
		public T getRowData(String pDescription) {
			
			System.out.println("Base model: " + pDescription);
			
			@SuppressWarnings("unchecked")
			List<T> lstBase = (List<T>) getWrappedData();  
	        
			int comptador = 0;
			
			for(T pBase : lstBase) { 
	        	
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
