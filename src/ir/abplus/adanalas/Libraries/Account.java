package ir.abplus.adanalas.Libraries;

import java.util.ArrayList;

/**
 * Created by Keyvan Sasani on 12/24/2014.
 */
public class Account {
    private String created;
    private String email;
    private String modified;
    private String filterConfig;
    private String budget;
    private String firstName;
    private String lastName;
    private String id;
    private ArrayList<String> tags;
    private ArrayList<Deposit> deposits;

    public String getCreated() {
        return created;
    }

    public String getEmail() {
        return email;
    }

    public String getModified() {
        return modified;
    }

    public String getFilterConfig() {
        return filterConfig;
    }

    public String getBudget() {
        return budget;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<Deposit> getDeposits() {
        return deposits;
    }

    public Account(String created, String email, String modified, String filterConfig, String budget, String firstName, String lastName, String id, ArrayList<String> tags, ArrayList<Deposit> deposits) {
        this.created = created;
        this.email = email;
        this.modified = modified;
        this.filterConfig = filterConfig;
        this.budget = budget;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.tags = tags;
        this.deposits = deposits;
    }
}
