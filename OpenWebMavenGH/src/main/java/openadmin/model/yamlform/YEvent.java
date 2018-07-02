package openadmin.model.yamlform;

import java.io.Serializable;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.annotations.Default;
import openadmin.model.Audit;
import openadmin.model.Base;

@Entity
@Table(name = "ymlevent", schema = "control_yaml_form" //, 
       //uniqueConstraints = @UniqueConstraint(columnNames =  { "pare", "tipus" })//,
       //indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
@ToString @NoArgsConstructor @SuppressWarnings("serial")
public class YEvent extends Audit implements Base, Serializable{
	
	/** attribute that contain the identifier*/
	@Getter @Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Default(visible=true)
	private Long id;
	
	@Getter @Setter
	@Size(max = 30)
	@NotNull
	@Column(name= "descripcio", unique = true)
	private String description=null; //descripcio
	
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name = "pare", nullable= false)
	private YComponent parent; // Component that creates the event
		
	@Getter @Setter
	@NotNull
	@Column(name= "clase")
	//@ManyToOne
	//@JoinColumn(name = "clase", nullable= false)
	private String className=null; //class that has the method to execute
	
	@Getter @Setter
	@NotNull
	@Column(name= "metode")
	private String method=null; //method from the class to execute
		
	@Getter @Setter
	//@ElementCollection
	//private List<String> refresh=new ArrayList<String>(); // Ids of the the affected components
	private String refresh=null; // Ids of the the affected components separated by comma
	
	@Getter @Setter
	@Column(name= "tipus", unique = true)
	private EventType type=EventType.onclick; 
	
	@PrePersist @PreUpdate
	public void prePersist() {
		this.setDescription(""+parent.getId()+"-" + this.getType());
	}
	
}
