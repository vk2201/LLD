package advance.lld.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Restaurant {
    Long id;
    String name;
    List<Branch> branchList;

    public Restaurant(Long id, String name) {
        this.id = id;
        this.name = name;
        branchList = new ArrayList<>();
    }

    public void addBranch(Branch branch) {
        branchList.add(branch);
    }

}
