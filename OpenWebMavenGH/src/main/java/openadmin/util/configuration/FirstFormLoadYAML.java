package openadmin.util.configuration;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
//import java.time.LocalDateTime;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import openadmin.dao.operation.DaoJpaEdu;
import openadmin.dao.operation.DaoOperationFacadeEdu;
import openadmin.model.control.User;
import openadmin.util.configuration.yamlview.YAMLComponent;
import openadmin.util.configuration.yamlview.YAMLDefaultValues;
import openadmin.util.configuration.yamlview.YAMLFormLoad;
import openadmin.util.edu.FileUtilsEdu;
import openadmin.util.edu.PropertyUtilsEdu;
import openadmin.util.edu.YAMLUtilsEdu;
import openadmin.util.lang.LangTypeEdu;

/**
 * ???????? We should load all the forms indicated in the properties file
 * @author eduard
 *
 */
public class FirstFormLoadYAML {

	private static User firstLoadUser = new User("FirstLoader","123456","First Load User");
	
	private static DaoOperationFacadeEdu connection = null; 	
	
	// Route to src/main/resources
	private static final String Path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	
	private static final String PropertyPath = Path + "properties/yamlview.properties";
	
	// Property file
	private static final Properties Props = PropertyUtilsEdu.getPropertiesNoException(PropertyPath);
	
	// Folder from src/main/resources where data in csv format is stored
	private static final String DataFolder = Props.getProperty("data_folder");
	
	// Files of YAML View files
	private static final String YAMLFiles = Props.getProperty("yaml.files");
		
	
	//NO Maven
	//private static final String fileName = Path + DataFolder + "/" + Props.getProperty("yaml.file");
		
	// Maven
	//private static final String fileName = DataFolder + "/" + Props.getProperty("yaml.file");
	
	//https://stackoverflow.com/a/42473349/7704658
	/**
	 * Get all files a folder that matches a suffix
	 * @see https://stackoverflow.com/a/5751357/7704658
	 * @param folder
	 * @return
	 */
	//@SuppressWarnings("unused")
	private static File[] getAllYamlViewFiles(String folder, String suffix) {
		
		//No Maven
		//String folderPath=FileUtilsEdu.getPathFromWebContentFolder(folder);
		
		//Maven
		String folderPath=FileUtilsEdu.getPathFromResourcesFolder(folder);
		File dir = new File(folderPath);
		File[] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(suffix);
		    }
		});
		return files;
		
	}
	
    private static File[] getYamlViewFiles(String folder, String commaFileList) {
		
		//No Maven
		//String folderPath=FileUtilsEdu.getPathFromWebContentFolder(folder);
		
		//Maven
		String folderPath=FileUtilsEdu.getPathFromResourcesFolder(folder);
		String[] fileNames=StringUtils.split(commaFileList, ',');
		File[] files=new File[fileNames.length];
		for (int i=0; i<fileNames.length; i++) 
			files[i]=new File(folderPath +"/"+fileNames[i].trim());
		
		return files;
		
	}
    /**
     * Get YAMLDefaultValues from file _defaultValues.yaml
     * @return
     */
    public static YAMLDefaultValues getDefaultValues() {
    	YAMLDefaultValues yDV=null;
    	String folderPath=FileUtilsEdu.getPathFromResourcesFolder(DataFolder);
		File f=new File(folderPath +"/_defaultValues.yaml");
		try {
			InputStream in = new FileInputStream(f);
			yDV = YAMLUtilsEdu.YAMLFileToObject(in, YAMLDefaultValues.class);
			System.out.println(yDV.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	return yDV;
    }
	
	public static void dataLoad()
			throws ClassNotFoundException, IOException, IntrospectionException, InstantiationException, 
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, RuntimeException {	
		
		//LocalDateTime myDate = LocalDateTime.now();
		YAMLDefaultValues yDV=getDefaultValues();
		//for (File f:getAllYamlViewFiles(DataFolder,".yaml")) {
		for (File f:getYamlViewFiles(DataFolder,YAMLFiles)) {	
			
			YAMLComponent yc=null;
			YAMLFormLoad yFL=new YAMLFormLoad();
		    
			//1. Read YAML File
			InputStream in = new FileInputStream(f);
			
			try {
				//1. Load values from YAML File
				yc = YAMLUtilsEdu.YAMLFileToObject(in, YAMLComponent.class);
				yFL.setForm(yc);
				
				//2. Assign Default values
				yFL.setDefaultValues(yDV);
				
				
				System.out.println(yc.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			LangTypeEdu langType = new LangTypeEdu();
			langType.changeMessageLog(TypeLanguages.es);
			//1.0- Open connections
			connection = new DaoJpaEdu(firstLoadUser, "control_post", (short) 0,langType);
	
			//3. Assign current connection to YAMLControlLoad
			yFL.setConnection(connection);
	
			connection.begin();
			//Fill helper and control structure
			yFL.init();
			
			if (yFL.checkErrors(false).trim().length()>10)
				System.out.println(yFL.checkErrors(true));
			else {
		
				// Fill DB structure
				yFL.fillYForm();
				
				// Persist
				yFL.persist();		
				//5. Delete old configuration
				/*
				connection.deleteOlderThan(YVwAction.class     , myDate);
				connection.deleteOlderThan(YVwEvent.class      , myDate);
				connection.deleteOlderThan(YVwField.class      , myDate);
				connection.deleteOlderThan(YVwTabElement.class , myDate);
				connection.deleteOlderThan(YVwTabGroup.class   , myDate);
				connection.deleteOlderThan(YVwListPanel.class  , myDate);
				connection.deleteOlderThan(YVwListPanel.class  , myDate);
				connection.deleteOlderThan(YVwPanel.class      , myDate);
				*/
				//Cascade deletion !!!!
				//connection.deleteOlderThan(YVwForm.class       , myDate);
				
		
			}	
			//5. Close DB Connection	
			connection.commit();	
			connection.finalize();

	}	}	
	
	
	
	public static void main(String[] args) {
		try {
			dataLoad();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException | IOException | IntrospectionException | RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
