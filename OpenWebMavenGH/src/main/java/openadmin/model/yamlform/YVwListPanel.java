package openadmin.model.yamlform;



import javax.persistence.Entity;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.envers.Audited;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * For displaying collections in a grid. 
 * In a future, components will be added to the grid
 * @author eduard
 *
 */
@Entity
//@ToString(callSuper=true, includeFieldNames=true)
@ToString
@NoArgsConstructor
@Table(name = "ymllstpanel", schema = "control"//, 
       //uniqueConstraints = @UniqueConstraint(columnNames =  { "pare", "row","column" })//,
       //indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
@SuppressWarnings("serial")
public class YVwListPanel extends YVwContainer {

}
