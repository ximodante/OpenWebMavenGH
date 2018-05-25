package openadmin.action;

import java.beans.IntrospectionException;

/**
*	Classe ContextAction
*   <desc> class that represents the context of application</desc>
*	@version  0.1
*	Creada  21-10-2009
*   Revisió 01-12-2009
*   @author Alfred Oliver 
*/

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
//import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import java.util.stream.Collectors;

import openadmin.dao.operation.DaoJpaEdu;
import openadmin.dao.operation.DaoOperationFacadeEdu;
import openadmin.model.Base;
import openadmin.model.control.Access;

import openadmin.model.control.EntityAdm;
import openadmin.model.control.Role;
import openadmin.model.control.User;
//import openadmin.util.configuration.FirstControlLoadEdu;
import openadmin.util.configuration.FirstControlLoadYAML;
//import openadmin.util.configuration.FirstControlLoad;
import openadmin.util.configuration.TypeEnvironment;
import openadmin.util.configuration.TypeLanguages;
import openadmin.util.edu.CollectionUtilsEdu;
import openadmin.util.lang.LangTypeEdu;
import openadmin.util.lang.WebMessages;


import openadmin.web.view.ViewFacadeEdu;

//import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Named(value = "ctx")
@SessionScoped
@ToString
public class ContextActionEdu implements Serializable {

	private static final long serialVersionUID = 21100901L;

	/** Field that contain the connection*/
	@Getter @Setter
	private DaoOperationFacadeEdu connControl = null;
	
	/** Field that contain the connection with log*/
	//private DaoOperationFacadeEdu connLog = null;
	
	/** Field that contain the connection with log*/
	@Getter @Setter
	private DaoOperationFacadeEdu connDefault = null;
	
	/** Field that contain the menuItems*/
	//private Map<String, List<Base>> menuItems = new HashMap<String, List<Base>>();
	
	/** Field that contain the actions of the view*/
	//private Map<String, List<Base>> actionsViews = new HashMap<String, List<Base>>();
	
	@Getter @Setter
	private Map<EntityAdm, List<Access>> mapEntityAccess = new HashMap<EntityAdm, List<Access>>();
	
	//private Set<EntityAdm> listEntity = new TreeSet<EntityAdm>();
	
	//private  List<EntityRole> lstEntitiRol = new ArrayList<EntityRole>();
	
	//private EntityAdm entityDefault = null;
	
	//private Role rolDefault = null;
	
	/** Field that contain the list of actions*/
	private Map<Integer, ViewFacadeEdu> lstView = new HashMap<Integer, ViewFacadeEdu>();
	
	/** Field that contain the user*/
	@Getter @Setter
	private User user;

	/** If is connect with database*/
	private boolean connected = false;
	
	/** Field that contain the Log 
	@Getter
	LogDaoEdu log = null;
	*/
	@Inject
	@Getter
	private LangTypeEdu langType;
	
	@Getter @Setter
	private Role activeRol;
	
	/*
	@PostConstruct
	public void initEdu () {
	    this.connected=false;
	    connControl=null;
	    System.out.println("-99.5 connected=" + connected);
	}
    */
	
	private void connect() {
		//comprova si hi ha conexi� a la base de dades
		if (!connected){
			
			//hi ha que modificar l'idioma del log per el de la configuracio manual, la connexió també  
			langType.changeMessageLog(TypeLanguages.es);
			
			/*
			//Log connection
			connLog = new DaoJpaEdu(user, "log_post", null, langType);									
			log = new LogDaoEdu(connLog, "clientweb", langType);
			*/
			//connection
			connControl = new DaoJpaEdu(user, "control_post", (short)0, langType);
			connControl.setEnvironment(TypeEnvironment.WEB);
			connected = true;
		
		}
		
	}
	
	/**<desc> Performs the application login</desc>
 	 * @return true if login user is correct
	 */
	public boolean login (User pUser)  {
		
		//Usuari que es connecta
		user = pUser;
		user.setActive(true);
		user.setFirma(false);
		
		// Open JPA db connections if closed
		this.connect();
		
		user=CollectionUtilsEdu.get(connControl.findObjects(user),0);
		
		try {
			
			if (null != user) {
				loadPrograms();
				return true;
			}
			
			//if (connControl.isEmpty(user)) FirstControlLoadEdu.PersistConfiguration();
			//else if (connControl.isEmpty(User.class)) FirstControlLoadEdu.dataLoad();
			else if (connControl.isEmpty(User.class)) FirstControlLoadYAML.dataLoad();
			
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IntrospectionException | RuntimeException e) {
			
			e.printStackTrace();
		
		}
		
		WebMessages.messageError("error_validation_login");
		
		
		return false;
	
	}
	
	public void connEntityDefault(String pconn, Long entityId){
		
		//connection
		if (connDefault != null){
			
			connDefault.finalize();
		}
		
		connDefault = new DaoJpaEdu(user, pconn,entityId.shortValue(), langType);
		connDefault.setEnvironment(TypeEnvironment.WEB);
	}
	
	/**
	public boolean loginFirma(String pIdentificador) {
		
		boolean exist = false;
		
		//Database connection 
		if (!connected){
				
			//hi ha que modificar l'idioma del log per el de la configuracio manual, la connexió també  
			MessagesTypes.changeMessageLog(TypeLanguages.es);
			MessagesTypes.changeMessage("es");
			
			//Log connection
			connLog = new DaoJpaHibernate(user, "log_post", null);									
			log = new LogDao(connLog, "clientweb");
			
			//connection
			connControl = new DaoJpaHibernate(user, "control_post", log);
			connControl.setEnvironment(TypeEnvironment.WEB);
			connected = true;
		
		}
		
		//if user is activate
		user.setActive(true);
								
		//Identificador
		user.setIdentifier(pIdentificador);
		
		//Find user
		List <Base> listUsers = connControl.findObjects(user);
		
		//If user exist
		if (connControl.isResultOperation()){
			
			user = (User)listUsers.get(0);
			
			if (user.getActive()){
				
				//Locale default user
				connLog.setUser(user);
				connControl.setUser(user);
				connEntityDefault(user.getEntityDefault());
				loadPrograms();
				exist = true;
				
				//load Messages languages 
				MessagesTypes.changeMessage(connControl.getUser().getLanguage());
				return exist;
			}	
		}
		
		return exist;
		
	}*/
	
	
	/**<desc> Performs the logout</desc>
 	 */
	public String logout () {
				
		user = new User();
		connControl.finalize();
		//connDefault.finalize();
		/*
		log.finalizeLog(); 
		connControl = null;
		//connDefault = null;
		connLog = null;
		*/
		connected = false;
		System.out.println("-Connected=false");
		
		System.out.println("  Eixir 100 ");
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "index";
		
	}


		
	
	
	private void loadPrograms() {
		
		/*****************************************************************
		 * Find all the entities that has the user, in the table access
		 *****************************************************************/
		
		Access access = new Access();
		access.setUser(user);
				
		mapEntityAccess = 
				connControl.findObjects(access).stream()
				.collect(Collectors.groupingBy(Access::getEntityAdm));
		
	}
	
	
	//Work view
	public ViewFacadeEdu getView(Integer key) {
		System.out.println("VISTA: " + key);
		return lstView.get(key);
	
	}

	public void setView(Integer key, ViewFacadeEdu pVista) {
		
		lstView.put(key, pVista);
		
	}
	
	public void deleteView() {
		
		lstView.remove(lstView.size());
		
	}
	
	public void deleteAllView() {
		
		lstView.clear();
		
	}
	
	public Integer numberView() {
		
		return lstView.size();
		
	}
	
}
