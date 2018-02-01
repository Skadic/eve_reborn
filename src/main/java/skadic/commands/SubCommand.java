package skadic.commands;

import skadic.commands.limiters.ILimiter;

import java.util.Set;

public abstract class SubCommand extends Command {

    private final Command parentCommand;

    public SubCommand(CommandRegistry registry, Command parentCommand, Set<ILimiter> limiters) {
        super(registry, limiters);
        this.parentCommand = parentCommand;
    }

    public SubCommand(CommandRegistry registry, Command parentCommand, ILimiter... limiters) {
        super(registry, limiters);
        this.parentCommand = parentCommand;
    }

    public Command getParentCommand() {
        return parentCommand;
    }
}
