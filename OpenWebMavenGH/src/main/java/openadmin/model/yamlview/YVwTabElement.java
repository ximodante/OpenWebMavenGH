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
 * TAb element from a group.
 * It may have a Panel element incorporated
 * @author eduard
 *
 */
@Entity
@Table(name = "yamltabelement", schema = "control"//, 
//uniqueConstraints = @UniqueConstraint(columnNames =  { "parent", "row","column" })//,
//indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
@SuppressWarnings("serial")
@NoArgsConstructor
@ToString
public class YVwTabElement extends YVwComponent{

	@Getter @Setter
	@OneToMany(
		mappedBy = "parent", 
		cascade = CascadeType.ALL, 
		orphanRemoval = true
	)
	private List<YVwComponent> children= null; // Distribution of panels and tabs (and maybe fields)

}
