package skadic.eve.listeners;

import org.springframework.jdbc.core.JdbcTemplate;
import skadic.commands.Permission;
import skadic.eve.Eve;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class ReadyListener implements IListener<ReadyEvent> {

    private static JdbcTemplate template = Eve.getTemplate();

    @Override
    public void handle(ReadyEvent event) {
        Eve.getClient().changePlayingText("with big red buttons!");
        List<Long> users = getUserIDs();
        for (IGuild guild : event.getClient().getGuilds()) {
            for (IUser user : guild.getUsers()) {
                if(!users.contains(user.getLongID())) addUser(user.getLongID());
            }
        }

        List<Long> roles = getRoleIDs();
        for (IGuild guild : event.getClient().getGuilds()) {
            for (IRole role : guild.getRoles()) {
                if(!roles.contains(role.getLongID())) addRole(role.getLongID());
            }
        }
    }

    private static List<Long> getUserIDs(){
        return template.query("SELECT userid FROM user",
                (rs, row) -> rs.getLong("userid"));
    }

    private static void addUser(long userID){
        if(!getUserIDs().contains(userID)){
            template.update("INSERT INTO user (userid, zircons) VALUES (?, 0)", userID);
        }
    }

    private static List<Long> getRoleIDs(){
        return template.query("SELECT roleid FROM permission",
                (rs, row) -> rs.getLong("roleid"));
    }

    private static void addRole(long roleID){
        if(!getRoleIDs().contains(roleID)){
            template.update("INSERT INTO permission (roleid, priority) VALUES (?, ?)", roleID, Permission.LOW.getPriority());
        }
    }




}
