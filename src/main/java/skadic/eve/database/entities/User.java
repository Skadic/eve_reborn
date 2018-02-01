package skadic.eve.database.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class User {

    private long userID;

    private long zircons;

    private Set<Title> titles;

    private Set<MsgCount> msgCount;

    @Id
    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getZircons() {
        return zircons;
    }

    public void setZircons(long zircons) {
        this.zircons = zircons;
    }

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    public Set<Title> getTitles() {
        return titles;
    }

    public void setTitles(Set<Title> titles) {
        this.titles = titles;
    }

    @OneToMany(mappedBy = "userid", cascade = CascadeType.ALL)
    public Set<MsgCount> getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(Set<MsgCount> msgCount) {
        this.msgCount = msgCount;
    }
}
