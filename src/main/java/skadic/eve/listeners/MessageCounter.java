package skadic.eve.listeners;

import org.springframework.jdbc.core.JdbcTemplate;
import skadic.eve.Eve;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MessageCounter, register 1 for each server
 */
public class MessageCounter implements IListener<MessageReceivedEvent> {

    private static JdbcTemplate template = Eve.getTemplate();

    @Override
    public void handle(MessageReceivedEvent e) {
        IGuild guild = e.getGuild();
        IUser author = e.getAuthor();
        if(!author.isBot()) incrementMsgCount(guild.getLongID(), author.getLongID());
    }

    public static int getMessageCountForUser(long serverID, long userID){
        List<Integer> list = template.query("SELECT userid, count FROM msg_count WHERE serverid = ? AND userid = ?", new Object[] {serverID, userID},
                (rs, row) -> rs.getInt("count"));
        if(list.size() == 0){
            template.update("INSERT INTO msg_count (userid, count, serverid) VALUES (?, 0, ?)", userID, serverID);
            return 0;
        }

        return list.get(0);
    }

    public static Map<Long, Integer> getMessageCountsForServer(long serverID){
        return template.query("SELECT userid, count FROM msg_count WHERE serverid = ?", new Object[] {serverID},
                (rs, row) -> new Pair<>(rs.getLong("userid"), rs.getInt("count"))).stream()
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private static void incrementMsgCount(long serverID, long userID){
        List<Integer> list = template.query("SELECT * FROM msg_count WHERE serverid = ? AND userid = ?",
                new Object[] {serverID, userID},
                (rs, rowNum) -> rs.getInt("count"));
        if(list.size() == 0){
            template.update("INSERT INTO msg_count (userid, count, serverid) VALUES (?, 1, ?)", userID, serverID);
        } else {
            template.update("UPDATE msg_count SET count = ? WHERE serverid = ? AND userid = ?", list.get(0) + 1, serverID, userID);
        }
    }

    private static class Pair<T, U>{


        private final T t;
        private final U u;

        public Pair(T t, U u) {
            this.t = t;
            this.u = u;
        }

        public T getFirst(){
            return t;
        }

        public U getSecond(){
            return u;
        }
    }

}
