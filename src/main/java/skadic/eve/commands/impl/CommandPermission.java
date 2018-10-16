package skadic.eve.commands.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import skadic.commands.*;
import skadic.commands.limiters.PermissionLimiter;
import skadic.commands.util.Utils;
import skadic.eve.Eve;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

@Help(syntax = "[role]", description = "Shows, which permission level a role has")
public class CommandPermission extends Command {

    private static JdbcTemplate template = Eve.getTemplate();

    public CommandPermission(CommandRegistry registry) {
        super(registry);
        addSubCommand(new CommandSetPermission(registry, this), "set");
        addSubCommand(new CommandGetPermission(registry, this), "get");
    }

    @Override
    protected boolean execute(CommandContext ctx) {
        if(ctx.getArgs().size() != 1 || ctx.getMessage().getRoleMentions().size() != 1) return false;

        IRole role = ctx.getMessage().getRoleMentions().get(0);
        Long roleID = role.getLongID();
        Permission perm = getPermission(roleID);

        Utils.sendMessage(ctx.getChannel(), "**" + role.getName() + "**'s permission level is: " + perm.toString());

        return true;
    }

    private static Permission getPermission(long roleID){
        return Permission.byPriority(template.query("SELECT priority FROM permission WHERE roleid = ?", new Object[]{roleID},
                (rs, row) -> rs.getInt("priority")).get(0));
    }

    private static void setPermission(long roleID, Permission perm){
        int priority = perm.getPriority();
        template.update("UPDATE permission SET priority = ? WHERE roleid = ?", priority, roleID);
    }

    @Help(syntax = "[Permission] [Role]", description = "Sets the permission of a role")
    private class CommandSetPermission extends SubCommand {

        public CommandSetPermission(CommandRegistry registry, Command parentCommand) {
            super(registry, parentCommand, new PermissionLimiter(Permission.ADMIN));
        }

        @Override
        protected boolean execute(CommandContext ctx) {
            List<IRole> mentions = ctx.getMessage().getRoleMentions();
            List<String> args = ctx.getArgs();

            if(args.size() == 2 && mentions.size() == 1){
                Permission perm = Permission.getByString(args.get(0));
                if(perm == null) return false;

                setPermission(mentions.get(0).getLongID(), perm);
                Utils.sendMessage(ctx.getChannel(), "Set **" + mentions.get(0).getName() + "**'s permission level to: " + perm.toString());
                return true;
            }
            return false;
        }
    }

    @Help(syntax = "<User/Role>", description = "Gets the permission level of a user, a role or yourself")
    private class CommandGetPermission extends SubCommand{

        public CommandGetPermission(CommandRegistry registry, Command parentCommand) {
            super(registry, parentCommand);
        }

        @Override
        protected boolean execute(CommandContext ctx) {
            List<String> args = ctx.getArgs();
            IUser author = ctx.getAuthor();
            IChannel channel = ctx.getChannel();
            List<IUser> mentions = ctx.getMessage().getMentions();
            List<IRole> roleMentions = ctx.getMessage().getRoleMentions();

            if (args.isEmpty()){
                Permission perm = Permission.getPermissionLevel(author, ctx.getGuild());
                Utils.sendMessage(channel, "Your permission level is: " + perm.toString());
                return true;
            }

            if(args.size() == 1){
                if(mentions.size() == 1 && roleMentions.size() == 0){
                    IUser mention = mentions.get(0);
                    Permission perm = Permission.getPermissionLevel(mention, ctx.getGuild());
                    Utils.sendMessage(channel, mention.getName() + "'s permission level is: " + perm.toString());
                    return true;
                }

                if(mentions.size() == 0 && roleMentions.size() == 1){
                    IRole mention = roleMentions.get(0);
                    Permission perm = Permission.getPermissionLevel(mention);
                    Utils.sendMessage(channel, mention.getName() + "'s permission level is: " + perm.toString());
                    return true;
                }
            }
            return false;
        }
    }

}
