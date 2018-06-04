package openadmin.model.yamlview;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "yamField", schema = "control" //, 
       //uniqueConstraints = @UniqueConstraint(columnNames =  { "parent", "attribute" })//,
       //indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
@ToString @NoArgsConstructor @SuppressWarnings("serial")

public class FieldEdu extends YamlComponent{
	
	/**
	 *  Size attributtes, are defined by annotations in the model
	 *  Font are defined by default by css
	 */
	//Attribute of the class
	@Getter @Setter
	@Size(max = 20)
	@NotNull
	@Column(name= "atribut")
	private String attribute=null; 
	
	@Getter @Setter
	private EditorTypeEdu editor=EditorTypeEdu.InputText;
		
	@Getter @Setter
	@Column(name="mascara")
	private String mask;
	
	@Getter @Setter
	@Column(name="requerit")
	private boolean required=false;
	
	@Getter @Setter
	private boolean enabled=true;
		
	@Getter @Setter
	private boolean visible=true;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
