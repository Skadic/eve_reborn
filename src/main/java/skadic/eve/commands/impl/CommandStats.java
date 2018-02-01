package skadic.eve.commands.impl;

import skadic.commands.*;
import skadic.commands.util.Utils;
import skadic.eve.Eve;
import skadic.eve.listeners.MessageCounter;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Help(syntax = "<mention>", description = "Shows a user's points (achieved through writing messages) or their level")
public class CommandStats extends Command{


    private static final float levelFactor = 1.5F;
    private static final int baseValue = 15;

    public CommandStats(CommandRegistry registry) {
        super(registry);
        addSubCommand(new CommandLeaderboard(registry, this), "leaderboards", "lb");
        addSubCommand(new CommandLevel(registry, this), "level");
    }

    @Override
    protected boolean executeCommand(CommandContext ctx) {
        IMessage message = ctx.getMessage();
        List<String> args = ctx.getArgs();
        IUser user = null;
        List<IUser> mentions = message.getMentions();

        //Points
        if(args.isEmpty()){
            Utils.sendMessage(ctx.getChannel(), String.format("%s has %d points!", ctx.getAuthor().mention(true), MessageCounter.getMessageCountForUser(ctx.getGuild().getLongID(), ctx.getAuthor().getLongID())));
            return true;
        } else if (mentions.size() == 1 && args.size() == 1){
            Utils.sendMessage(ctx.getChannel(), String.format("%s has %d points!", mentions.get(0).mention(true), MessageCounter.getMessageCountForUser(ctx.getGuild().getLongID(), mentions.get(0).getLongID())));
            return true;
        }
        if(user != null){

        }

        return false;
    }

    private int calculateLevel(long serverID, long userID){
        int restXP = MessageCounter.getMessageCountForUser(serverID, userID);
        int level = 0;
        while (restXP > level * levelFactor * baseValue){
            restXP -= level * levelFactor * baseValue;
            level++;
        }
        return level;
    }

    @Help(syntax = "<number>", description = "Shows the leaderboards for this server.")
    private class CommandLeaderboard extends SubCommand{

        public CommandLeaderboard(CommandRegistry registry, Command parentCommand) {
            super(registry, parentCommand);
        }

        @Override
        protected boolean executeCommand(CommandContext ctx) {
            List<String> args = ctx.getArgs();

            if(args.size() <= 1){
                int count;
                try {
                    count = args.size() == 0 ? 15 : (Integer.parseInt(args.get(1)) >= 1 ? Integer.parseInt(args.get(1)) : 15);
                } catch (NumberFormatException e){
                    return false;
                }

                Map<Long, Integer> map = MessageCounter.getMessageCountsForServer(ctx.getGuild().getLongID());
                final StringBuilder sb = new StringBuilder();
                sb.append(ctx.getAuthor().mention());
                sb.append("```\n");
                sb.append("Leaderboards:\n");
                int i = 1;
                for(Map.Entry<Long, Integer> entry : map.entrySet().stream().sorted(Comparator.comparingInt(v -> -v.getValue())).collect(Collectors.toList())){
                    if(i > count) break;
                    sb.append(String.format("%d:\t%s\t%d\n", i++, Eve.getClient().getUserByID(entry.getKey()).getName(), entry.getValue()));
                }
                sb.append("```");
                Utils.sendMessage(ctx.getChannel(), sb.toString());
                return true;
            } else
                return false;
        }
    }

    @Help(syntax = "<mention>", description = "Shows your (or someone else's) level")
    private class CommandLevel extends SubCommand{

        public CommandLevel(CommandRegistry registry, Command parentCommand) {
            super(registry, parentCommand);
        }


        @Override
        protected boolean executeCommand(CommandContext ctx) {
            IMessage message = ctx.getMessage();
            List<String> args = ctx.getArgs();
            List<IUser> mentions = message.getMentions();

            if(args.size() == 0){
                Utils.sendMessage(ctx.getChannel(), String.format("%s is level %d!", ctx.getAuthor().mention(true), calculateLevel(ctx.getGuild().getLongID(), ctx.getAuthor().getLongID())));
                return true;
            } else if(args.size() == 1 && mentions.size() == 1){
                Utils.sendMessage(ctx.getChannel(), String.format("%s is level %d!", mentions.get(0).mention(true), calculateLevel(ctx.getGuild().getLongID(), mentions.get(0).getLongID())));
                return true;
            }

            return false;
        }
    }
}
