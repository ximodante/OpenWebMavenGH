package openadmin.model.yamlform;

import java.util.List;

import javax.persistence.CascadeType;

import javax.persistence.Entity;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.envers.Audited;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "yamlcontainer", schema = "control" //, 
       //uniqueConstraints = @UniqueConstraint(columnNames =  { "parent", "row","column" },
       //indexes = {@Index (name = "idx_pare_row_column", columnList = "parent, row, column")
)
@Audited
@ToString @NoArgsConstructor @AllArgsConstructor
@SuppressWarnings("serial")
public class YVwContainer extends YVwComponent {
	
	@Getter @Setter
	@OneToMany(
		mappedBy = "parent", 
	    cascade = CascadeType.ALL, 
	    orphanRemoval = true
	)
	private List<YVwComponent> children= null; // Distribution of panels and tabs (and maybe fields)
	
}
