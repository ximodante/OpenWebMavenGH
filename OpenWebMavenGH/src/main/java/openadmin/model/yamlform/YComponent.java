package openadmin.model.yamlform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//import javax.persistence.CascadeType;
//import javax.persistence.OneToMany;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.annotations.Default;
import openadmin.model.Audit;
import openadmin.model.Base;
import openadmin.model.control.ClassName;

/**
 * Component in a yaml file
 * It can be: mainform, panel, gridpanel, tabgroup, tab and field type
 * A component can have events and associated actions
 * If a component is a container, it can contain other components
 * 
 * JPA does not work well with Collections. Some attributtes like audits and last user 
 *   are not persisted. o collections have been defined as @Transient.
 * @author eduard
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
//@DiscriminatorColumn(name = "Component_Type")
@Table(name = "ymlcomp", schema = "control_yaml_form" //, 
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
public class YComponent extends Audit implements Base, Serializable{
	/** attribute that contain the identifier*/
	@Getter @Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Default(visible=true)
	private Long id;
	
	@Getter @Setter
	@Size(max = 80)
	@NotNull
	@Column(name= "descripcio", unique = true)
	private String description=null; //header or tooltiptext
	
	@Getter @Setter
	@Column(name="tipus")
	private ElementType type=ElementType.FIELD; //
	
	//the caption of a container or component)
	@Getter @Setter
	@Size(max = 25)
	@Column(name = "nom")
	private String name="form"; //
	
	// Class to edit
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name = "nomClasse", nullable= false)
	private ClassName className;
	
	//Attribute of the class only for fields
	@Getter @Setter
	@Size(max = 20)
	@Column(name= "atribut")
	private String attribute=null; 
	
	@Getter @Setter
	@Column(name= "fila") 
	private byte row=-1; // Or line
	
	@Getter @Setter
	@Column(name= "columna")
	private byte col=-1; // Column or possition in a line
	
	@Getter @Setter
	@Column(name="nivell")
	private byte level=0; // Nesting level in the component hierarchy 
	
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name="pare")
	//@NoSql
	private YComponent parent=null; // Parent component container
	
	
	@Getter @Setter
	@Transient
	/*
	@OneToMany(
	mappedBy = "parent", 
	//mappedBy = "pare",
	cascade = CascadeType.ALL, 
	orphanRemoval = true
	)*/
	
	private List<YAction> lstActions=new ArrayList<>(); // detail of the Actions included in the tab
	
	
	@Getter @Setter
	@Transient
	/*
	@OneToMany(
		mappedBy = "parent", 
		//mappedBy = "pare",	
	    cascade = CascadeType.ALL, 
	    orphanRemoval = true
	)
	*/
	private List<YEvent> lstEvents=new ArrayList<>();

	//1. Part for containers
	
	@Getter @Setter
	@Transient
	/*
	@OneToMany(
		mappedBy = "parent", 
		//mappedBy = "pare",	
	    cascade = CascadeType.ALL, 
	    orphanRemoval = true
	)
	*/
	private List<YComponent> lstComponents=new ArrayList<>();

	//Additional properties 
	@Getter @Setter
	@Transient
	/*
	@OneToMany(
		mappedBy = "parent", 
	    //mappedBy = "pare",	
	    cascade = CascadeType.ALL, 
	    orphanRemoval = true
	)
	*/
	private List<YProperty> lstProperties=new ArrayList<>();
    
	@PrePersist @PreUpdate
	public void prePersist() {
		if (this.getType()!=ElementType.FORM)
			this.setDescription(""+parent.getId()+"-" + this.getName());
	}
}
