package skadic.eve.database.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Permission {

    @Id
    private long roleID;

    private int priority;

    public long getRoleID() {
        return roleID;
    }

    public void setRoleID(long roleID) {
        this.roleID = roleID;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
