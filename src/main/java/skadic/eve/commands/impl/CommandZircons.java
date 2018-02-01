package skadic.eve.commands.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import skadic.commands.*;
import skadic.commands.limiters.PermissionLimiter;
import skadic.commands.util.Utils;
import skadic.eve.Eve;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

@Help(syntax = "<mention>", description = "Shows your, or someone else's, balance")
public class CommandZircons extends Command {

    private static final String ZIRCON = ":large_orange_diamond:";

    private static JdbcTemplate template = Eve.getTemplate();

    public CommandZircons(CommandRegistry registry) {
        super(registry);
        addSubCommand(new CommandGive(registry, this), "give", "gift");
        addSubCommand(new CommandGrant(registry, this), "grant");
    }

    @Override
    protected boolean executeCommand(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        IChannel channel = ctx.getChannel();
        IUser author = ctx.getAuthor();
        List<IUser> mentions = ctx.getMessage().getMentions();

        if(args.size() == 0){
            Utils.sendMessage(channel, "You have " + getZircons(author.getLongID()) + " :large_orange_diamond: !");
            return true;
        }

        if(args.size() == 1 && mentions.size() == 1){
            Utils.sendMessage(channel, mentions.get(0).getName() + " has " + getZircons(mentions.get(0).getLongID()) + " " + ZIRCON + "!");
            return true;
        }

        return false;
    }

    private static long getZircons(long userID){
        return template.query("SELECT zircons FROM user WHERE userid = ?", new Object[] {userID},
                (rs, rowNum) -> rs.getLong("zircons")).get(0);
    }

    private static void addZircons(long userID, int amount){
        long newBalance = Math.max(getZircons(userID) + amount, 0);

        template.update("UPDATE user SET zircons = ? WHERE userid = ?", newBalance, userID);
    }

    @Help(syntax = "[mention] [amount]", description = "Gives someone some of your zircons!")
    private class CommandGive extends SubCommand {

        public CommandGive(CommandRegistry registry, Command parentCommand) {
            super(registry, parentCommand);
        }

        @Override
        protected boolean executeCommand(CommandContext ctx) {
            List<String> args = ctx.getArgs();
            IChannel channel = ctx.getChannel();
            IUser author = ctx.getAuthor();
            List<IUser> mentions = ctx.getMessage().getMentions();


            if(args.size() == 2 && mentions.size() == 1){
                long balance = getZircons(author.getLongID());
                int amount;
                try {
                    amount = Integer.parseInt(args.get(1));
                } catch (NumberFormatException e){
                    return false;
                }
                if(amount > balance){
                    Utils.sendMessage(channel, "You do no have enough " + ZIRCON);
                } else {
                    addZircons(mentions.get(0).getLongID(), amount);
                    addZircons(author.getLongID(), -amount);
                    Utils.sendMessage(channel, "You gave " + amount + ZIRCON + " to " + mentions.get(0).getName());
                }
                return true;
            }

            return false;
        }
    }

    @Help(syntax = "[mention] [amount]", description = "Grants someone zircons!")
    private class CommandGrant extends SubCommand {

        public CommandGrant(CommandRegistry registry, Command parentCommand) {
            super(registry, parentCommand, new PermissionLimiter(Permission.ADMIN));
        }

        @Override
        protected boolean executeCommand(CommandContext ctx) {
            List<String> args = ctx.getArgs();
            IChannel channel = ctx.getChannel();
            List<IUser> mentions = ctx.getMessage().getMentions();

            if(args.size() == 2 && mentions.size() == 1){
                int amount;
                try {
                    amount = Integer.parseInt(args.get(1));
                } catch (NumberFormatException e){
                    return false;
                }
                addZircons(mentions.get(0).getLongID(), amount);
                Utils.sendMessage(channel, "You granted " + amount + ZIRCON + " to " + mentions.get(0).getName());

                return true;
            }
            return false;
        }
    }
}
