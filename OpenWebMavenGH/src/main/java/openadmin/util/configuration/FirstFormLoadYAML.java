package openadmin.util.configuration;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import openadmin.dao.operation.DaoJpaEdu;
import openadmin.dao.operation.DaoOperationFacadeEdu;
import openadmin.model.control.User;
import openadmin.model.yamlform.YVwAction;
import openadmin.model.yamlform.YVwEvent;
import openadmin.model.yamlform.YVwField;
import openadmin.model.yamlform.YVwForm;
import openadmin.model.yamlform.YVwListPanel;
import openadmin.model.yamlform.YVwPanel;
import openadmin.model.yamlform.YVwTabElement;
import openadmin.model.yamlform.YVwTabGroup;
import openadmin.util.configuration.yamlview.YAMLVwFormLoad;
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
		int i=0;
		for (String name:fileNames) {
			files[i]=new File(folderPath +"/"+fileNames[i++].trim());
		}
		return files;
		
	}
	
	public static void dataLoad()
			throws ClassNotFoundException, IOException, IntrospectionException, InstantiationException, 
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, RuntimeException {	
		
		LocalDateTime myDate = LocalDateTime.now();
		//for (File f:getAllYamlViewFiles(DataFolder,".yaml")) {
		for (File f:getAllYamlViewFiles(DataFolder,YAMLFiles)) {	
			
			YAMLVwFormLoad yv=null;
		
			//1. Read YAML File
			InputStream in = new FileInputStream(f);
			try {
				//yc = YAMLUtilsEdu.YAMLStringToObject(myStr, YAMLControlLoad.class);
				yv = YAMLUtilsEdu.YAMLFileToObject(in, YAMLVwFormLoad.class);
				System.out.println(yv.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			if (yv.checkErrors(false).trim().length()>10)
				System.out.println(yv.checkErrors(true));
			else {
		
				//2. Open BD Connection	
				LangTypeEdu langType = new LangTypeEdu();
				langType.changeMessageLog(TypeLanguages.es);
				//1.0- Open connections
				connection = new DaoJpaEdu(firstLoadUser, "control_post", (short) 0,langType);
		
				//3. Assign current connection to YAMLControlLoad
				yv.setConnection(connection);
		
				connection.begin();
		
				//4. Persist configuration in DB	
				yv.Init();
				
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
				connection.deleteOlderThan(YVwForm.class       , myDate);
				
		
		
				//5. Close DB Connection	
				connection.commit();	
				connection.finalize();

	}	}	}
	
	
	
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
