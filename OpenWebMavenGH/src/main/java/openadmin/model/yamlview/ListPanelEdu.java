package openadmin.model.yamlview;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * For displaying collections in a grid. 
 * In a future, components will be added to the grid
 * @author eduard
 *
 */
@Entity
@ToString @NoArgsConstructor
@Table(name = "yamllistpanel", schema = "control", 
       uniqueConstraints = @UniqueConstraint(columnNames =  { "parent", "row","column" })//,
       //indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
@SuppressWarnings("serial")
public class ListPanelEdu extends YamlComponent {
	
	@Getter @Setter
	@OneToMany(
		mappedBy = "parent", 
	    cascade = CascadeType.ALL, 
	    orphanRemoval = true
	)
	private List<YamlComponent> children= null; // Distribution of panels and tabs (and maybe fields)
	
}
