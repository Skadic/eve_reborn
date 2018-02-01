package skadic.commands.listeners;

import skadic.commands.CommandContext;
import skadic.commands.ServerCommandManager;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;


public class CommandListener implements IListener<MessageReceivedEvent>{


    private final ServerCommandManager manager;

    public CommandListener(ServerCommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        String content = event.getMessage().getContent();
        String prefix = manager.getRegistryForServer(event.getGuild().getLongID()).getPrefix();

        if(!content.startsWith(prefix)) return;

        String prefixRemoved = content.substring(prefix.length()).trim(); // the content without the prefix
        if (prefixRemoved.isEmpty()) return; // message was just the prefix.
        int spaceIndex = prefixRemoved.indexOf(" "); // the index of the first space in the content
        int subIndex = spaceIndex == -1 ? prefixRemoved.length() : spaceIndex; // safety check for when there is no space

        String name = prefixRemoved.substring(0, subIndex); //command name
        String args = name.length() == prefixRemoved.length() ? "" : prefixRemoved.substring(name.length() + 1); //arguments

        manager.getRegistryForServer(event.getGuild().getLongID()).call(name, new CommandContext(event, name, args));
    }
}
