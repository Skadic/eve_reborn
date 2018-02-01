package skadic.commands.listeners;

import skadic.commands.CommandContext;
import skadic.commands.ServerCommandManager;
import skadic.commands.util.Utils;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.Collection;
import java.util.stream.Collectors;


public class CommandListener implements IListener<MessageReceivedEvent>{


    private final ServerCommandManager manager;

    public CommandListener(ServerCommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        String content = event.getMessage().getContent();
        Collection<String> prefixes = manager.getRegistryForServer(event.getGuild().getLongID()).getPrefixMap().values();

        if(!Utils.stringStartsWithAny(content, (String[]) prefixes.toArray(new String[prefixes.size()]))) return;

        //Determine which prefix was being used
        String prefix = "";
        for(String s : prefixes.stream().sorted((s1, s2) -> s2.length() - s1.length()).collect(Collectors.toList())){
            if(content.startsWith(s)){
                prefix = s;
                break;
            }
        }

        String prefixRemoved = content.substring(prefix.length()).trim(); // the content without the prefix
        if (prefixRemoved.isEmpty()) return; // message was just the prefix.
        int spaceIndex = prefixRemoved.indexOf(" "); // the index of the first space in the content
        int subIndex = spaceIndex == -1 ? prefixRemoved.length() : spaceIndex; // safety check for when there is no space

        String name = prefixRemoved.substring(0, subIndex); //command name
        String args = name.length() == prefixRemoved.length() ? "" : prefixRemoved.substring(name.length() + 1); //arguments

        manager.getRegistryForServer(event.getGuild().getLongID()).call(name, new CommandContext(event, name, args, prefix));
    }
}
