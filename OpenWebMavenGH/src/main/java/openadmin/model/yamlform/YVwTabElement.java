package openadmin.model.yamlform;


import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * TAb element from a group.
 * It may have a Panel element incorporated
 * @author eduard
 *
 */
@Entity
@Table(name = "ymltabelm", schema = "control"//, 
//uniqueConstraints = @UniqueConstraint(columnNames =  { "parent", "row","column" })//,
//indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
@SuppressWarnings("serial")
@NoArgsConstructor
//@ToString(callSuper=true, includeFieldNames=true)
@ToString
public class YVwTabElement extends YVwContainer{
	
}