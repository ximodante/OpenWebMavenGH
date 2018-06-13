package openadmin.model.yamlform;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import lombok.AllArgsConstructor;
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
//@DiscriminatorColumn(name = "Component_Type")
@Table(name = "ymlcomponent", schema = "control" //, 
       //uniqueConstraints = { 
       //		   @UniqueConstraint(columnNames =  { "pare", "fila","columna" }),
    	//	   @UniqueConstraint(columnNames =  { "pare", "nom" })
       //}
       //indexes = {@Index (name = "idx_usuari_entityadm", columnList = "usuari, entityAdm")})
)
@Audited
@SuppressWarnings("serial")
@NoArgsConstructor @AllArgsConstructor
//@ToString(callSuper=true, includeFieldNames=true)
@ToString
public class YVwComponent extends Audit implements Base, Serializable{
	/** attribute that contain the identifier*/
	@Getter @Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Default(visible=true)
	private Long id;
	
	//the caption of a container or component)
	@Getter @Setter
	@Size(max = 25)
	@Column(name = "nom")
	private String name=null; //
	
	@Getter @Setter
	@Size(max = 80)
	@NotNull
	@Column(name= "descripcio", unique = true)
	private String description=null; //header or tooltiptext
	
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name = "nomClasse", nullable= false)
	private ClassName className;
	
	@Getter @Setter
	@Column(name= "fila") 
	private byte row=0; // Or line
	
	@Getter @Setter
	@Column(name= "columna")
	private byte col=0; // Column or possition in a line
	
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name="pare")
	//@NoSql
	private YVwComponent parent=null; // Parent component container
	
	@Getter @Setter
	@OneToMany(
		mappedBy = "parent", 
		//mappedBy = "pare",
	    cascade = CascadeType.ALL, 
	    orphanRemoval = true
	)
	private List<YVwAction> lstActions=new ArrayList<YVwAction>(); // detail of the Actions included in the tab
	
	@Getter @Setter
	@OneToMany(
		mappedBy = "parent", 
		//mappedBy = "pare",	
	    cascade = CascadeType.ALL, 
	    orphanRemoval = true
	)
	private List<YVwEvent> lstEvents=new ArrayList<YVwEvent>();

	
}
