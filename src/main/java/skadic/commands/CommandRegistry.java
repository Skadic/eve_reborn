package skadic.commands;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import skadic.commands.limiters.ILimiter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static skadic.commands.util.Utils.sendMessage;

public class CommandRegistry{

    /**
     * Command name to Command
     */
    private final BiMap<String, Command> commands;
    /**
     * Alias to Command name
     */
    private final Map<String, String> aliasToName;
    private final Map<String, String[]> nameToAliases;

    private final String prefix;

    public CommandRegistry(String prefix) {
        commands = HashBiMap.create();
        this.prefix = prefix;
        aliasToName = new HashMap<>();
        nameToAliases = new HashMap<>();
    }

    public void call(String name, CommandContext ctx){
        getCommand(name).ifPresent((command) -> call(command, ctx));
    }

    private void call(Command command, CommandContext ctx){
        List<String> args = ctx.getArgs();
        if(!args.isEmpty() && command.isSubCommand(args.get(0))){
            String subCommand = getMainName(args.get(0)).get();
            ctx.args = args.subList(1, args.size());
            call(command.getSubCommand(subCommand).get(), ctx);
            return;
        }
            for (ILimiter limiter : command.getLimiters()) {
                if (!limiter.check(ctx)) {
                    sendMessage(ctx.getChannel(), "Unable to execute command. Limiter denying access: " + limiter.getClass().getSimpleName().replace("Limiter", ""));
                    return;
                }
            }
            command.execute(ctx);
    }

    public void register(Command command, String name, String... aliases) throws IllegalArgumentException{
        register(command, name);
        nameToAliases.put(name, aliases);
        for (String alias : aliases) {
            if(aliasToName.containsKey(alias)) throw new IllegalArgumentException("alias can not be registered twice: " + alias);
            aliasToName.put(alias, name);
        }
    }

    private void register(Command command, String name) throws IllegalArgumentException {
        if(commands.containsKey(name)) throw new IllegalArgumentException("command can not be registered twice: " + name);
        commands.put(name, command);
    }

    public Optional<Command> getCommand(String name){

        if(commands.containsKey(name)){
            return Optional.of(commands.get(name));
        }else if (aliasToName.containsKey(name)){
            return getCommand(aliasToName.get(name));
        } else
            return Optional.empty();
    }

    public String getPrefix() {
        return prefix;
    }


    public BiMap<String, Command> getFilteredCommands() {
        return ImmutableBiMap.copyOf(commands.entrySet().stream()
                .filter((entry) -> !(entry.getValue() instanceof SubCommand))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public BiMap<String, Command> getCommands() {
        return ImmutableBiMap.copyOf(commands);
    }

    public Optional<String[]> getAliases(String name) {
        if(nameToAliases.containsKey(name))
            return Optional.of(nameToAliases.get(name));
        else if(aliasToName.containsKey(name))
            return Optional.of(nameToAliases.get(aliasToName.get(name)));
        else
            return Optional.empty();
    }

    public boolean hasAlias(String name){
        Optional<String[]> aliases = getAliases(name);
        return aliases.isPresent() && aliases.get().length != 0;
    }

    public Optional<String> getMainName(String name){
        if(commands.containsKey(name))
            return Optional.of(name);
        else
            return Optional.ofNullable(aliasToName.get(name));
    }
}
