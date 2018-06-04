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
 * group of Tab Elements
 * @author eduard
 *
 */
@Entity
@Table(name = "yamltabgroup", schema = "control" //, 
       //uniqueConstraints = @UniqueConstraint(columnNames =  { "parent", "row","column" })//,
       //indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
@ToString @NoArgsConstructor @SuppressWarnings("serial")
public class TabGroupEdu extends YamlComponent {
	
	@Getter @Setter
	@OneToMany(
		mappedBy = "parent", 
	    cascade = CascadeType.ALL, 
	    orphanRemoval = true
	)
	private List<TabElementEdu> tabs= null; // Collection of tabs
	
	
}
