package skadic.commands;

import org.springframework.jdbc.core.JdbcTemplate;
import skadic.eve.Eve;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum Permission {
    LOWEST(-1), LOW(0), MEDIUM(1), HIGH(2), ADMIN(3);

    private final int priority;
    private static JdbcTemplate template = Eve.getTemplate();

    Permission(int priority){

        this.priority = priority;
    }


    public static Permission getPermissionLevel(long userID, IGuild guild){
        if(guild.getOwnerLongID() == userID || 197336103722811392L == userID) return ADMIN;

        IUser user = guild.getUserByID(userID);
        List<IRole> roles = guild.getRolesForUser(user);

        if(containsAny(roles, getRoles(ADMIN)))
            return ADMIN;
        if(containsAny(roles, getRoles(LOWEST)))
            return LOWEST;
        if(containsAny(roles, getRoles(HIGH)))
            return HIGH;
        if(containsAny(roles, getRoles(MEDIUM)))
            return MEDIUM;

        return LOW;
    }

    public static Permission getPermissionLevel(IUser user, IGuild guild){
        return getPermissionLevel(user.getLongID(), guild);
    }

    public int compare(Permission permission){
        return this.priority - permission.priority;
    }

    public int getPriority() {
        return priority;
    }

    public static Set<IRole> getRoles() {
        return new HashSet<>(template.query("SELECT roleid FROM permission",
                (rs, row) -> Eve.getClient().getRoleByID(rs.getLong("roleid"))));
    }

    public static Set<IRole> getRoles(Permission permission) {
        return new HashSet<>(template.query("SELECT roleid FROM permission WHERE priority = ?", new Object[]{permission.getPriority()},
                (rs, row) -> Eve.getClient().getRoleByID(rs.getLong("roleid"))));
    }


    public static Permission getByString(String name){
        for (Permission p : values())
            if(p.toString().equalsIgnoreCase(name)) return p;
        return null;
    }

    public static Permission byPriority(int priority){
        for (Permission permission : values()) {
            if(permission.priority == priority) return permission;
        }
        return null;
    }

    private static <T> boolean containsAny(Collection<? extends T> c1, Collection<? extends T> c2){
        for(T t : c1){
            if(c2.contains(t)) return true;
        }
        return false;
    }

    public static Permission getPermissionLevel(IRole role) {
        if(getRoles(ADMIN).contains(role))
            return ADMIN;
        if(getRoles(LOWEST).contains(role))
            return LOWEST;
        if(getRoles(HIGH).contains(role))
            return HIGH;
        if(getRoles(MEDIUM).contains(role))
            return MEDIUM;
        return LOW;
    }
}
