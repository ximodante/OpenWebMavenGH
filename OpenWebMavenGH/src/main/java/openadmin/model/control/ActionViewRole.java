package openadmin.model.control;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import openadmin.annotations.Default;
import openadmin.annotations.Search;
import openadmin.model.Audit;
import openadmin.model.Base;

@Entity
@ToString @NoArgsConstructor
@Table(name = "accioVistaRol", schema = "control", 
       uniqueConstraints = @UniqueConstraint(columnNames =  { "gruprols", "menuitem","accio" }),
       indexes = {@Index (name = "idx_role_menuitem", columnList = "gruprols, menuitem")})
@Audited
public class ActionViewRole extends Audit implements Base, java.io.Serializable {
 
	private static final long serialVersionUID = 01011001L;
	
	/** attribute that contain the identifier*/
	@Getter @Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Default(visible=true)
	private Long id;
	
	/** attribute that contains the description (Unique value)*/
	@Getter
	@Size(max = 30)
	@Column(name="nom", unique=true)
	private String description;
	
	/** attribute that contain the relationship with role*/
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name = "gruprols", nullable= false)
	private RoleGroup roleGroup;
	
	/** attribute that contain the relationship with view*/
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name = "menuitem", nullable= false)
	@Search(nameObjects= "Action:ActionClass")
	private MenuItem menuItem;
	
	/** attribute that contain the relationship with view*/
	@Getter @Setter
	@ManyToOne
	//@JoinColumn(name = "accio", nullable= false)
	@JoinColumn(name = "accio")
	@Search(nameObjects= "MenuItem:ActionClass")
	private Action action;
	
	/** Getters and setters*/	
	public void setDescription(String pDescription) {
		/*		
		if( null != getMenuItem() ||null !=  getAction() || null != getRole())
			this.description = ""+ getRole().getId() + "_" + getMenuItem().getId() + "_" + getAction().getId() ;
		*/
		this.description = ""+ getRoleGroup().getId() + "_" + getMenuItem().getId() + "_" ;
		
		if (this.getAction()==null) this.description = this.description + "null"; 
		else this.description = this.description +  getAction().getId();		
		 
	}
	
}