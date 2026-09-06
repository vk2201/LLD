package advance.lld.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Branch {
    List<Table> tableList;
    Menu menu;
    List<Chef> chefList;
    List<Waiter> waiterList;

    public Branch(Menu menu, List<Table> tableList, List<Chef> chefList, List<Waiter> waiterList) {
        this.tableList = tableList;
        this.menu = menu;
        this.chefList = chefList;
        this.waiterList = waiterList;
    }

    public Branch(Menu menu, List<Table> tableList) {
        this.tableList = tableList;
        this.menu = menu;
    }

    public void addChef(Employee employee, String employeeType) {

    }

    public void addWaiter(Waiter waiter, String employeeType) {

    }

    public void removeEmployee(Long id, String employeeType) {

    }

}
