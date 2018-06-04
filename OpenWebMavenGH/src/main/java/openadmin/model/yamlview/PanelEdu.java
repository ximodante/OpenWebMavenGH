package openadmin.model.yamlview;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * Simple container of fields and other containers
 * @author eduard
 *
 */
@Entity
@Table(name = "yamlpanel", schema = "control"//, 
       //uniqueConstraints = @UniqueConstraint(columnNames =  { "parent", "row","column" })//,
       //indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class PanelEdu extends YamlComponent{

	@Getter @Setter
	@OneToMany(
		mappedBy = "parent", 
	    cascade = CascadeType.ALL, 
	    orphanRemoval = true
	)
	private List<YamlComponent> children= null; // Distribution of panels and tabs (and maybe fields)
	
}
