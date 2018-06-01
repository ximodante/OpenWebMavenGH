package openadmin.model.yamlview;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
import openadmin.model.control.Role;

/**
 * Defines line and col with the component
 * @author eduard
 *
 */
@Entity
@ToString @NoArgsConstructor
@Table(name = "liniacolumna", schema = "yaml", 
       uniqueConstraints = @UniqueConstraint(columnNames =  { "parent", "row","column" }),
       indexes = {@Index (name = "idx_role_menuitem", columnList = "rol, menuitem")})
@Audited
@SuppressWarnings("serial")
public class LineColEdu extends Audit implements Base, Serializable{
	/** attribute that contain the identifier*/
	@Getter @Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Default(visible=true)
	private Long id;
	
	/** Line or row number */
	@Getter @Setter
	@JoinColumn(name = "linia", nullable= false)
	private byte row=0; //
	
	/** column number or possition of the component in the line */
	@Getter @Setter
	@JoinColumn(name = "orden", nullable= false)
	private byte column=0; //
	
	@Getter @Setter
	@Size(max = 30)
	@NotNull
	@Column(name= "descripcio", unique = true)
	private String description=null; //descripcio
	
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name = "pare", nullable= false)
	private ComponentEdu parent;
	
	
	@Getter @Setter
	@ManyToOne
	@JoinColumn(name = "fill", nullable= false)
	private ComponentEdu child;
	
	
	
	
}
