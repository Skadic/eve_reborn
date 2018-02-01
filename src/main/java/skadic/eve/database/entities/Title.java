package skadic.eve.database.entities;

import javax.persistence.*;

@Entity
public class Title {

    private int assignID;

    @Column(name = "owner")
    private User owner;

    private long giver;

    private String title;


    public long getGiver() {
        return giver;
    }

    public void setGiver(long giver) {
        this.giver = giver;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToOne
    @JoinColumn(name = "owner")
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getAssignID() {
        return assignID;
    }

    public void setAssignID(int assignID) {
        this.assignID = assignID;
    }
}
