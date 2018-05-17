package openadmin.util.faces;

import java.util.List;

import javax.faces.component.UIComponent;

public class UtilFaces {

	public static UIComponent findComponentOfRoot(UIComponent pRoot){
		
		if (null == pRoot) return null;
		
		 int numberChildren = pRoot.getChildCount();
		 
		    if (numberChildren > 0) {

		    		System.out.println("ID Component Pare: " + pRoot.getId());
		    	
		    		UIComponent component = null;
		    		
		    		List lstChildren = pRoot.getChildren();
		    		
		    		while (--numberChildren >= 0) {
		    		
		    			component = (UIComponent) lstChildren.get(numberChildren);
		    			
		    			System.out.println("-- ID Component:  " + component.getId());
		    			System.out.println("---- Component2: " + component);
		        		System.out.println("---- Component3: " + component.getNamingContainer());
		        		System.out.println("---- Component4: " + component.getChildCount());
		        		System.out.println("---- Component5: " + component.getClientId());
		        		System.out.println("---- Component6: " + component.getFamily());
	
		    		}
		   }  		
		
		    return null;
	}
	
	public static UIComponent findComponentOfId2(UIComponent pRoot, String id){
		
		UIComponent root = pRoot;
		
		System.out.println("Count root: " + root.getChildCount());
		System.out.println("Component id root: " + root.getId());
		if(root.getId().equals(id)){
	    	
	        return root;
	    }
	    if(root.getChildCount() > 0){
	        for(UIComponent subUiComponent : root.getChildren()){
	        	
	        		System.out.println("-- Component1: " + subUiComponent.getId());
	        		System.out.println("-- Component2: " + subUiComponent);
	        		System.out.println("-- Component3: " + subUiComponent.getNamingContainer());
	        		System.out.println("-- Component4: " + subUiComponent.getChildCount());
	        		System.out.println("-- Component5: " + subUiComponent.getClientId());
	        		System.out.println("-- Component6: " + subUiComponent.getFamily());
	        		
	                UIComponent returnComponent = findComponentOfRoot(subUiComponent);	              
	                
	                if(returnComponent != null){
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
	    			
	    			//System.out.println("ID Component " + component.getId());
	    			
	    			//System.out.println("pID Component " + pid);
	    			
	    		    if (component.getId().equals(pid)) {
	    		    	
	    		    	System.out.println("Tancat ID Component " + pid);
	    		    	
	    		    	lstChildren.remove(numberChildren);	
	    		    	break;
	    		    }
	    		}
	   }  		
	}
}
