package openadmin.model.yamlform;



import javax.persistence.Entity;

import javax.persistence.Table;

import org.hibernate.envers.Audited;


import lombok.NoArgsConstructor;
import lombok.ToString;
/**
 * Simple container of fields and other containers
 * @author eduard
 *
 */
@Entity
@Table(name = "ymlpanel", schema = "control"//, 
       //uniqueConstraints = @UniqueConstraint(columnNames =  { "parent", "row","column" })//,
       //indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
@SuppressWarnings("serial")
//@ToString(callSuper=true, includeFieldNames=true)
@ToString
@NoArgsConstructor
public class YVwPanel extends YVwContainer{
}
