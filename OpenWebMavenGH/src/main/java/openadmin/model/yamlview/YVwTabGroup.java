package openadmin.model.yamlview;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.NoArgsConstructor;

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
public class YVwTabGroup extends YVwContainer {
		
}
