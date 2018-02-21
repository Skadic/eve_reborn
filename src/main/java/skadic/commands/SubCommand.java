package skadic.commands;

import skadic.commands.limiters.ILimiter;

public abstract class SubCommand extends Command {

    private final Command parentCommand;

    public SubCommand(CommandRegistry registry, Command parentCommand, ILimiter... limiters) {
        super(registry, limiters);
        this.parentCommand = parentCommand;
    }

    public Command getParentCommand() {
        return parentCommand;
    }
}
