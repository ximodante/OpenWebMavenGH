package openadmin.util.configuration.yamlview;
/** 
 * For easily reduce Java code to detect duplications
 * @author eduard
 *
 */
public interface IYAMLElement<T> {
	public T getName();
	public void setName(T name);
}
