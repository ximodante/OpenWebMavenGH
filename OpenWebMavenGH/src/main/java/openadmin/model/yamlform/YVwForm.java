package openadmin.model.yamlform;

import javax.persistence.Entity;

import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Main view for yaml definition
 * @author eduard
 *
 */
@Entity
@Table(name = "ymlform", schema = "control"//, 
       //uniqueConstraints = @UniqueConstraint(columnNames =  { "parent", "row","column" })//,
       //indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")}
)
@Audited
@SuppressWarnings("serial")
@NoArgsConstructor
//@ToString(callSuper=true, includeFieldNames=true)
@ToString
public class YVwForm extends YVwContainer {

	@Getter @Setter 
	private String rsbundle=null; // Resource Bundle Key
	
}