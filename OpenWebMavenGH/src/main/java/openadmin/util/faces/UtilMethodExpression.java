package openadmin.util.faces;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

public class UtilMethodExpression {

	
	public static MethodExpression createMethodExpression(String expression, Class<?> returnType, Class<?>... parameterTypes) {
	    FacesContext facesContext = FacesContext.getCurrentInstance();
	    
	    return facesContext.getApplication().getExpressionFactory().createMethodExpression(
	        facesContext.getELContext(), expression, returnType, parameterTypes);
	}
	
	public static ValueExpression createValueExpression(String expression, Class<?> typeClass) {
	    
		FacesContext facesContext = FacesContext.getCurrentInstance();
	    
	    return facesContext.getApplication().getExpressionFactory().createValueExpression(
	        facesContext.getELContext(), expression, typeClass);
	}
}
