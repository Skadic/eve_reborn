package skadic.commands;


import skadic.commands.limiters.ILimiter;
import skadic.commands.limiters.PermissionLimiter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Instantiate this to create your Command and register them to a {@link CommandRegistry} object
 * Annotate every subclass with the {@link Help} annotation to give them a help text
 */
@Help
public abstract class Command{

    protected final Set<ILimiter> limiters;
    protected CommandRegistry registry;
    protected Set<SubCommandTuple> subCommands;

    public Command(CommandRegistry registry, ILimiter... limiters) {
        this.limiters = Arrays.stream(limiters).collect(Collectors.toSet());
        this.registry = registry;
        subCommands = new HashSet<>();
        if(!getLimiter(PermissionLimiter.class).isPresent())
            this.limiters.add(new PermissionLimiter(Permission.LOW));
    }

    public final void addSubCommand(SubCommand subCommand, String name, String... aliases){
        SubCommandTuple tuple = new SubCommandTuple(subCommand, name, aliases);
        subCommands.add(tuple);
        registry.register(subCommand, name, aliases);
    }

    public final Set<SubCommandTuple> getSubCommands() {
        return subCommands;
    }

    public final boolean isSubCommand(String name){
        if(!hasSubCommands()) return false;
        for (SubCommandTuple subCommand : subCommands) {
            if(subCommand.getName().equalsIgnoreCase(name)) return true;
            for (String[] aliases : subCommands.stream().map(SubCommandTuple::getAliases).collect(Collectors.toSet())) {
                for (String alias : aliases) {
                    if(name.equalsIgnoreCase(alias)) return true;
                }
            }
        }
        return false;
    }

    public final Optional<Command> getSubCommand(String name){
        for (SubCommandTuple subCommand : subCommands) {
            if(subCommand.getName().equalsIgnoreCase(name)) return Optional.of(subCommand.getCommand());
        }
        return Optional.empty();
    }

    public final boolean hasSubCommands(){
        return !subCommands.isEmpty();
    }

    public final Set<ILimiter> getLimiters() {
        return limiters;
    }

    public final void addLimiter(ILimiter limiter){
        limiters.add(limiter);
    }

    @SuppressWarnings("unchecked cast")
    public final  <T extends ILimiter> Optional<T> getLimiter(Class<? extends T> limiterType){
        for (ILimiter limiter : limiters) {
            if(limiterType.isInstance(limiter))
                return Optional.of((T) limiter);
        }
        return Optional.empty();
    }

    public CommandRegistry getRegistry(){
        return registry;
    }

    /**
     * command execution. Not recommended to be made public
     *
     * @param ctx the Command Context
     * @return returns true if the command has been executed successfully
     */
    protected abstract boolean execute(CommandContext ctx);

    protected static class SubCommandTuple {
        private SubCommand command;
        private String name;
        private String[] aliases;

        public SubCommandTuple(SubCommand command, String name, String[] aliases) {
            this.command = command;
            this.name = name;
            this.aliases = aliases;
        }

        public SubCommand getCommand() {
            return command;
        }

        public void setCommand(SubCommand command) {
            this.command = command;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String[] getAliases() {
            return aliases;
        }

        public void setAliases(String[] aliases) {
            this.aliases = aliases;
        }
    }
}
