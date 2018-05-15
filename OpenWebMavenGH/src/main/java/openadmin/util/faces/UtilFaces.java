package openadmin.util.faces;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class UtilFaces {

	public static UIComponent findComponentOfId(UIComponent pRoot, String id, String arbre){
		
		UIComponent root = pRoot;
		
		System.out.println("Buscant " + id + "en el component " + root.getId());

		if(root.getId().equals(id)){
	        return root;
	    }
	    if(root.getChildCount() > 0){
	        for(UIComponent subUiComponent : root.getChildren()){
	        	String arbre1=arbre + "-->" + subUiComponent.getId();
	        	System.out.println(arbre1);
	        		/*
	        		System.out.println("Component1: " + subUiComponent.getId());
	        		System.out.println("Component2: " + subUiComponent);
	        		System.out.println("Component3: " + subUiComponent.getNamingContainer());
	        		System.out.println("Component4: " + subUiComponent.getChildCount());
	        		System.out.println("Component5: " + subUiComponent.getClientId());
	        		System.out.println("Component6: " + subUiComponent.getFamily());
	        		*/
	                UIComponent returnComponent = findComponentOfId(subUiComponent, id, arbre1);
	                
	                
	                
	                if(returnComponent != null){
	                	System.out.println("Component trobat: " + returnComponent);
		                System.out.println("Component trobat...: " + returnComponent.getClientId());
	                    return returnComponent;
	                }
	        }
	    }
	    return null;
	}
	
	public static void removeComponentOfId(UIComponent root, String pid){
		
	    int numberChildren = root.getChildCount();
	    		 
	    if (numberChildren > 0) {

	    		UIComponent component = null;
	    		
	    		List lstChildren = root.getChildren();
	    		
	    		while (--numberChildren >= 0) {
	    		
	    			component = (UIComponent) lstChildren.get(numberChildren);
	    			
	    			System.out.println("ID Component " + component.getId());
	    			
	    		    if (component.getId().equals(pid)) {
	    		    	
	    		    	System.out.println("Tancat ID Component " + pid);
	    		    	
	    		    	lstChildren.remove(numberChildren);	
	    		    	continue;
	    		    }
	    		}
	   }  		
	}
	
	public static UIComponent getComponentFromPage(String id) {
		UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
		return findComponentOfId(view, id, "[MAIN("+view.getId()+")");
	}
}
