package skadic.eve.database.entities;

import javax.persistence.*;

@Entity
public class MsgCount{

    private int assignID;

    @Column(name = "userid")
    private User userid;

    private long serverID;

    private long count;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getAssignID() {
        return assignID;
    }

    @ManyToOne
    @JoinColumn(name = "userid")
    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

    public void setAssignID(int assignID) {
        this.assignID = assignID;
    }

    public long getServerID() {
        return serverID;
    }

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
