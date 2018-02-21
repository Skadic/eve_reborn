package skadic.commands;

import skadic.commands.limiters.PermissionLimiter;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static skadic.commands.util.Utils.sendMessage;

@Help(description = "Lets me help you as far as my ability goes", syntax = "<command>")
public class CommandHelp extends Command {
    public CommandHelp(CommandRegistry registry) {
        super(registry);
    }

    @Override
    protected boolean execute(CommandContext ctx) {
        Map<String, Command> commands = registry.getFilteredCommands();
        IChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.withAuthorName("Help");

            for(Permission permission : Arrays.stream(Permission.values())
                    .sorted(Comparator.comparingInt(p -> -p.ordinal()))
                    .collect(Collectors.toList())){
                List<String> list = commands.keySet().stream()
                        .filter(s -> commands.get(s).getLimiter(PermissionLimiter.class).get().getPermission() == permission)
                        .collect(Collectors.toList());

                if(!list.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    list.forEach(s -> sb.append(s).append("\n"));

                    eb.appendField(permission.toString(), sb.toString(), false);
                }
            }

            channel.sendMessage(eb.build());
            return true;
        } else {
            sendHelp(ctx);
            return true;
        }
    }

    @SuppressWarnings("all")
    private void sendHelp(CommandContext ctx){
        EmbedBuilder builder = new EmbedBuilder();
        List<String> args = ctx.getArgs();
        String commandName = args.get(0);
        IChannel channel = ctx.getChannel();

        if(!registry.getCommand(commandName).isPresent()){
            sendMessage(channel, "Unknown command: " + commandName);
        } else {
            Command command = registry.getCommand(commandName).get();

            if(args.size() > 1){
                if(command.hasSubCommands() && command.isSubCommand(args.get(1))){
                    ctx.getArgs().remove(0);
                    sendHelp(ctx);
                    return;
                } else {
                    sendMessage(channel, "Unknown subcommand: " + args.get(1));
                    return;
                }
            }
            List<String> subComms = new ArrayList<>();

            String mainName = registry.getMainName(commandName).get();
            Help help = command.getClass().getAnnotation(Help.class);

            Permission permission = command.getLimiter(PermissionLimiter.class).get().getPermission();
            String prefix = registry.getPrefix();

            builder.withAuthorName(mainName);

            if(help.syntax().equals("") && help.description().equals(""))
                builder.appendDescription(mainName + " has no specified Help annotation");
            else {
                builder.appendField("Syntax", prefix + getFullCommandName(command) + " " + help.syntax(), false);

                if (!help.description().equals(""))
                    builder.appendField("Description", help.description(), false);
            }

            if(registry.hasAlias(commandName)) {
                StringBuilder sb = new StringBuilder();
                for (String alias : registry.getAliases(commandName).get())
                    sb.append(alias).append('\n');
                builder.appendField("Aliases", sb.toString(), false);
            }

            if(command.hasSubCommands()){
                StringBuilder sb = new StringBuilder();
                for (SubCommandTuple subCommand : command.getSubCommands()) {
                    sb.append(subCommand.getName()).append('\n');
                }
                builder.appendField("Sub Commands", sb.toString(), false);
            }

            builder.appendField("Permission Required", permission.toString(), false);

            sendMessage(channel, builder.build());
        }
    }

    private String getFullCommandName(Command command){
        if(command instanceof SubCommand)
            return getFullCommandName(((SubCommand) command).getParentCommand()) + " " + registry.getCommands().inverse().get(command);
        else
            return registry.getCommands().inverse().get(command);
    }
}
