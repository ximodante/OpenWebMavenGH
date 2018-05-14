package proves.edu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.ManagedProperty;
//import javax.faces.bean.ViewScoped;
import org.primefaces.PrimeFaces;
/*
import org.primefaces.showcase.domain.Car;
import org.primefaces.showcase.service.CarService;
*/ 
@Named(value = "dfCarsView")
@SessionScoped
public class DFCarsView implements Serializable {
     
    private List<Car> cars=new ArrayList<Car>();
 
    /*Inject("#{carService}")
    private CarService service;
    */ 
    @PostConstruct
    public void init() {
        //cars = service.createCars(9);
    	cars.add(new Car(1,2000,"Citroen","Red"));
    	cars.add(new Car(2,2001,"Seat","White"));
    	cars.add(new Car(3,2003,"Renault","Blue"));
    	cars.add(new Car(4,2004,"Peugeot","Yelow"));
    }
     
    public List<Car> getCars() {
        return cars;
    }
 
    /*
    public void setService(CarService service) {
        this.service = service;
    }
    */
}
