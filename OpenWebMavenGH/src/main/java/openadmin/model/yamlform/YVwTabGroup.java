package openadmin.model.yamlform;

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
@Table(name = "ymltabgp", schema = "control" //, 
       //uniqueConstraints = @UniqueConstraint(columnNames =  { "parent", "row","column" })//,
       //indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
//@ToString(callSuper=true, includeFieldNames=true)
@ToString
@NoArgsConstructor @SuppressWarnings("serial")
public class YVwTabGroup extends YVwContainer {
		
}
