package openadmin.model.yamlview;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.annotations.Default;
import openadmin.annotations.NoSql;
import openadmin.model.Audit;
import openadmin.model.Base;
import openadmin.model.control.ClassName;
import openadmin.model.control.MenuItem;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "Component_Type")
@Table(name = "componentedu", schema = "yaml" //, 
       //uniqueConstraints = @UniqueConstraint(columnNames =  { "programa", "usuari", "entityadm", }),
       //indexes = {@Index (name = "idx_usuari_entityadm", columnList = "usuari, entityAdm")})
      )
@Audited
@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class ComponentEdu extends Audit implements Base, Serializable{
	/** attribute that contain the identifier*/
	@Getter @Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Default(visible=true)
	private Long id;
	
	@Getter @Setter
	@JoinColumn(name = "nom", nullable= false)
	private String name=null; //
	
	@Getter @Setter
	@Size(max = 50)
	@NotNull
	@Column(name= "descripcio", unique = true)
	private String description=null; //header or tooltiptext
	
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name = "nomClasse", nullable= false)
	private ClassName className;
	
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name="padre")
	@NoSql
	private ComponentEdu parent=null; // Parent component container
	
	@Getter @Setter
	private List<ActionEdu> lstActions=new ArrayList<ActionEdu>(); // detail of the Actions included in the tab
	
	@Getter @Setter
	private List<EventEdu> lstEvents=new ArrayList<EventEdu>();

}
