package openadmin.web;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import openadmin.util.configuration.FirstFormLoadYAML;

@SuppressWarnings("serial")
@Named
@RequestScoped
public class EduAction implements Serializable{

	
	public String loadYamlForm() {
		try {
			FirstFormLoadYAML.dataLoad();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException | IOException | IntrospectionException | RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
