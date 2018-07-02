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
@Table(name = "ymlprop", schema = "control_yaml_form" //, 
       //uniqueConstraints = @UniqueConstraint(columnNames =  { "pare", "descripcio" })//,
       //indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
@ToString @NoArgsConstructor @SuppressWarnings("serial")
public class YProperty extends Audit implements Base, Serializable{
	
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
	@Size(max = 60)
	@NotNull
	@Column(name= "nom")
	private String name=null; //property name
	
	@Getter @Setter
	@Size(max = 100)
	@NotNull
	@Column(name= "valor")
	private String value=null; //values
	
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name = "pare", nullable= false)
	private YComponent parent; // Component that has produces the action
	
	@PrePersist @PreUpdate
	public void prePersist() {
		this.setDescription(""+parent.getId()+"-" + this.getName());
	}
}	
