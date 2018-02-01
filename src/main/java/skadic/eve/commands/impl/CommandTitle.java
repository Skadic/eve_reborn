package skadic.eve.commands.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import skadic.commands.*;
import skadic.commands.limiters.PermissionLimiter;
import skadic.commands.util.Utils;
import skadic.eve.Eve;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Help(syntax = "[list/set] [user] [title]")
public class CommandTitle extends Command {

    private static JdbcTemplate template = Eve.getTemplate();

    public CommandTitle(CommandRegistry registry) {
        super(registry, new PermissionLimiter(Permission.LOW, registry));
    }

    @Override
    protected boolean executeCommand(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        IUser author = ctx.getAuthor();
        IMessage message = ctx.getMessage();
        IChannel channel = ctx.getChannel();

        if(args.size() == 0) return false;

        if(args.size() == 1){
            if(args.get(0).equals("list") || args.get(0).equals("l")){
                StringBuilder sb = new StringBuilder();
                sb.append("```\nYour titles:\n");
                for (Map.Entry<IUser, String> entry : getTitles(author.getLongID()).entrySet()) {
                    sb.append(String.format("%s:\t\"%s\"\n", entry.getKey().getName(), entry.getValue()));
                }
                sb.append("```");
                Utils.sendMessage(channel, sb.toString());
                return true;
            }
        }

        if(args.size() == 2){
            if(message.getMentions().size() == 1) {
                if (args.get(0).equals("list") || args.get(0).equals("l")) {
                    IUser mention = message.getMentions().get(0);
                    StringBuilder sb = new StringBuilder();
                    sb.append("```\n" + mention.getName() + "'s titles:\n");
                    for (Map.Entry<IUser, String> entry : getTitles(mention.getLongID()).entrySet()) {
                        sb.append(String.format("%s:\t\"%s\"\n", entry.getKey().getName(), entry.getValue()));
                    }
                    sb.append("```");
                    Utils.sendMessage(channel, sb.toString());
                    return true;
                } else if(args.get(0).equals("remove") || args.get(0).equals("r")){
                    IUser mention = message.getMentions().get(0);
                    removeTitle(mention.getLongID(), author.getLongID());
                }

            }
        }

        if(args.size() >= 3){
            if(message.getMentions().size() == 1){
                IUser mention = message.getMentions().get(0);
                String title = String.join(" ", args.subList(2, args.size()));
                if(args.get(0).equals("set")){
                    addTitle(mention.getLongID(), author.getLongID(), title);
                    Utils.sendMessage(channel, "Your title for " + mention.getName() + " has been set to: " + title);
                    return true;
                }
            }
        }

        return false;
    }


    private static Map<IUser, String> getTitles(long userID){
        return template.query("SELECT giver, title FROM title WHERE owner = ?", new Object[]{userID},
                (rs, row) -> new Pair<>(Eve.getClient().getUserByID(rs.getLong("giver")), rs.getString("title"))).stream()
                .collect(Collectors.toMap(Pair::getFirstValue, Pair::getSecondValue));
    }

    private static void addTitle(long ownerID, long giverID, String title){
        List<Integer> list = template.query("SELECT giver, title FROM title WHERE owner = ? AND giver = ?",
                new Object[]{ownerID, giverID},
                (rs, row) -> row);

        if(list.isEmpty()) {
            template.update("INSERT INTO title (owner, giver, title) VALUES (?, ?, ?)", ownerID, giverID, title);
            return;
        } else {
            template.update("UPDATE title SET title = ? WHERE owner = ? AND giver = ?", title, ownerID, giverID);
        }
    }

    private static void removeTitle(long ownerID, long giverID){
        template.update("DELETE FROM title WHERE owner = ? AND giver = ?", ownerID, giverID);
    }

    private static class Pair<T, U>{


        private final T t;
        private final U u;

        public Pair(T t, U u) {
            this.t = t;
            this.u = u;
        }

        public T getFirstValue(){
            return t;
        }

        public U getSecondValue(){
            return u;
        }
    }
}
