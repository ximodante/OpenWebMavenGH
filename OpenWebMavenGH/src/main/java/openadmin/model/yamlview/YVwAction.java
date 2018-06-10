package openadmin.model.yamlview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
import openadmin.model.Audit;
import openadmin.model.Base;
import openadmin.model.control.ClassName;

@Entity
@Table(name = "yamlaction", schema = "control", 
       uniqueConstraints = @UniqueConstraint(columnNames =  { "pare", "descripcio" })//,
       //indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
@ToString @NoArgsConstructor @SuppressWarnings("serial")
public class YVwAction extends Audit implements Base, Serializable{
	
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
	private YVwComponent parent; // Component that has produces the action
		
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name = "clase", nullable= false)
	private ClassName className=null; //class that has the method to execute
	
	@Getter @Setter
	@NotNull
	@Column(name= "metode")
	private String method=null; //method from the class to execute
		
	@Getter @Setter
	@ElementCollection
	private List<String> lstAffectedIds=new ArrayList<String>(); // Ids of the the affected components
	
	@Getter @Setter
	private String icon=null; //Button icon
	
	@Getter @Setter
	@Column(name= "tipus", unique = true)
	private ButtonType type=ButtonType.Button; 
	
	@Getter @Setter
	@Size(max = 30)
	@NotNull
	@Column(name= "nom", unique = true)
	private String name=null; //descripcio
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
