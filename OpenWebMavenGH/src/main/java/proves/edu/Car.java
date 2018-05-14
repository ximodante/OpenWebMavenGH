package proves.edu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Car {
	@Getter @Setter private int id;
	@Getter @Setter private int year;
	@Getter @Setter private String brand;
	@Getter @Setter private String color;
	
	
}
