package skadic.commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandContext {

    private final String content;
    private final String prefixUsed;
    private final IUser author;
    private final IDiscordClient client;
    private final IChannel channel;
    private final IGuild guild;
    private final IMessage message;
    private final String name;
    List<String> args;

    public CommandContext(MessageReceivedEvent e, String name, String args, String prefixUsed) {
        content = e.getMessage().getContent();
        author = e.getAuthor();
        client = e.getClient();
        channel = e.getChannel();
        guild = e.getGuild();
        message = e.getMessage();
        this.name = name;
        this.args = args.equals("") ? new ArrayList<>() : new ArrayList<>(Arrays.asList(args.split("\\s+")));
        this.prefixUsed = prefixUsed;
    }

    public String getName() {
        return name;
    }

    public String getPrefixUsed() {
        return prefixUsed;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getContent() {
        return content;
    }

    public IUser getAuthor() {
        return author;
    }

    public IDiscordClient getClient() {
        return client;
    }

    public IChannel getChannel() {
        return channel;
    }

    public IGuild getGuild() {
        return guild;
    }

    public IMessage getMessage() {
        return message;
    }
}
