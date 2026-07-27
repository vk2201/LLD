package advanced.lld.models;

import lombok.Getter;

@Getter
public class User {
    private final String userId;
    private String name;

    public User(String id, String name) {
        this.userId = id;
        this.name = name;
    }

}

