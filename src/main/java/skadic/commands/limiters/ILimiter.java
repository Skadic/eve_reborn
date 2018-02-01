package skadic.commands.limiters;


import skadic.commands.CommandContext;

@FunctionalInterface
public interface ILimiter{

    boolean check(CommandContext ctx);
}
