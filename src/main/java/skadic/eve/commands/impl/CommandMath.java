package skadic.eve.commands.impl;

import skadic.commands.Command;
import skadic.commands.CommandContext;
import skadic.commands.CommandRegistry;
import skadic.commands.Help;
import skadic.commands.util.Utils;
import skadic.eve.util.MathParser;

@Help(syntax = "[expression]", description = "Evaluates a mathematical expression in postfix-notation (Reverse Polish Notation)")
public class CommandMath extends Command{

    private MathParser mathParser;

    public CommandMath(CommandRegistry registry) {
        super(registry);
        mathParser = new MathParser();
    }


    @Override
    protected boolean execute(CommandContext ctx) {
        if(ctx.getArgs().size() < 1) return false;
        StringBuilder expression = new StringBuilder();

        for (String s : ctx.getArgs()) {
            expression.append(s).append(" ");
        }

        try {
            Utils.sendMessage(ctx.getChannel(), String.valueOf(mathParser.parse(expression.toString())));
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
