package openadmin.view.edu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@NoArgsConstructor
public class FieldEdu extends ComponentEdu {
	
	@Getter @Setter 
	private int length=0;
	
	@Getter @Setter 
	private int nChar=0;
	
	@Getter @Setter
	private FontEdu font;
	
	@Getter @Setter
	private EditorTypeEdu editor;
		
	@Getter @Setter
	private String mask;
	
	@Getter @Setter
	private boolean required=false;
	
	@Getter @Setter
	private boolean enabled=true;
		
	@Getter @Setter
	private boolean visible=true;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
