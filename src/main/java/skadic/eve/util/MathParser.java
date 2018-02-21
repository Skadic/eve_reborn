package skadic.eve.util;

import java.util.Stack;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class MathParser {

    private Stack<Double> stack;
    private Stack<Function<Integer, Double>> expressions;
    private static final boolean DEBUG = true;
    private static final int ACCURACY = 6;

    //<editor-fold desc="Trinary Operators">
    private static final TrinaryOperator<Double>
            HYPER = MathParser::hyper;
    //</editor-fold>

    //<editor-fold desc="Binary Operators">
    private static final BinaryOperator<Double>
            ADD = (a, b) -> a + b,
            SUBTRACT = (a, b) -> a - b,
            MULTIPLY = (a, b) -> round(a * b),
            DIVIDE = (a, b) -> round(a / b),
            POWER = Math::pow,
            MOD = (a, b) -> a % b;
    //</editor-fold>

    //<editor-fold desc="Unary Operators">
    private static final UnaryOperator<Double>
            SIN = Math::sin,
            COS = Math::cos,
            TAN = Math::tan,
            ARSIN = Math::asin,
            ARCOS = Math::acos,
            ARTAN = Math::atan,
            ABS = Math::abs,
            LN = Math::log,
            SQRT = Math::sqrt,
            SIGN = x -> x > 0 ? 1D : (x < 0 ? -1D : 0D),
            SIGMOID = x -> 1 / (1 + Math.pow(Math.E, -x)),
            FAC = x -> {
                int acc = 1;
                for (int i = 2; i <= x; i++) {
                    acc *= i;
                }
                return round((double) acc);
            };
    //</editor-fold>

    public MathParser() {
        stack = new Stack<>();
        expressions = new Stack<>();
    }

    //186 Zeilen my mans
    public double parse(String op) {
        if (op.equalsIgnoreCase("")) return 0;

        op = op.trim();

        char[] arr = op.toCharArray();

        for (int i = 0; i < arr.length; i++) {
            BinaryOperator<Double> SUM = (from, to) -> {
                Function<Integer, Double> operator = expressions.pop();     //Nimm die oberste expression vom stack
                double acc = 0;
                debug("[");
                for (int j = (int) ((double) from); j <= to; j++) {         //Aufsummieren der Elemente
                    double res = operator.apply(j);
                    debug(round(acc) + " + "  + round(res) + " = " + round(acc + res));
                    acc += res;                                 //
                }
                debug("]");
                return round(acc, ACCURACY);
            };
            BinaryOperator<Double> PRODUCT = (from, to) -> {
                Function<Integer, Double> operator = expressions.pop();
                double acc = 1;
                debug("[");
                for (int j = (int) ((double) from); j <= to; j++) {
                    double res = operator.apply(j);
                    debug(round(acc) + " * "  + round(res) + " = " + round(acc * res));
                    acc *= res;                                 //
                }
                debug("]");
                return round(acc, ACCURACY);
            };

            char c = arr[i];

            if (Character.isDigit(c)) {
                int j;
                for (j = i; j < arr.length && Character.isDigit(arr[j]); j++);

                //the integer part of the number. if this number is an integer just push it to the stack
                //otherwise, add the remaining to it
                double intPart = Double.parseDouble(String.valueOf(op.substring(i, j)));
                if(j >= arr.length || !(arr[j] == ',' || arr[j] == '.')) {
                    stack.push(intPart);
                    debugPush();
                    i = j - 1;
                } else {
                    j++;
                    int h;
                    for (h = j; h < arr.length && Character.isDigit(arr[h]); h++);
                    double decimalPart = Double.parseDouble(String.valueOf(op.substring(j, h)));

                    //this is used in order to correct the magnitude of the decimal part (as it is an integer now)
                    int magnitude = (int) (Math.log10(decimalPart) + 1);
                    stack.push(intPart + decimalPart / Math.pow(10, magnitude));
                    i = h - 1;
                }
                continue;
            }

            if (isOperator(c))
                switch (c) {
                    case '+':
                        debugBinOp(c);
                        performOperation(ADD);
                        break;
                    case '-':
                        debugBinOp(c);
                        performOperation(SUBTRACT);
                        break;
                    case '*':
                        debugBinOp(c);
                        performOperation(MULTIPLY);
                        break;
                    case '/':
                        debugBinOp(c);
                        performOperation(DIVIDE);
                        break;
                    case '^':
                        debugBinOp(c);
                        performOperation(POWER);
                        break;
                    case '!':
                        debug(String.valueOf(stack.peek() + c));
                        performOperation(FAC);
                        break;
                    case '%':
                        debugBinOp('%');
                        performOperation(MOD);
                }
            else if (Character.isLetter(c))
                switch (c) {
                    case 's':
                        if (regionMatches(op, i, "sin")) {
                            debug("sin(" + stack.peek() + ")");
                            performOperation(SIN);
                            i += 2;
                        } else if (regionMatches(op, i, "sqrt")) {
                            debug("sqrt(" + stack.peek() + ")");
                            performOperation(SQRT);
                            i += 3;
                        } else if (regionMatches(op, i, "sum")) {
                            debug("sum");
                            performOperation(SUM);
                            i += 2;
                        } else if (regionMatches(op, i, "sgn")) {
                            debug("sgn(" + stack.peek() + ")");
                            performOperation(SIGN);
                            i += 3;
                        } else if (regionMatches(op, i, "sig")) {
                            debug("sig(" + stack.peek() + ")");
                            performOperation(SIGMOID);
                            i += 2;
                        }

                        break;
                    case 'c':
                        debug("cos(" + stack.peek() + ")");
                        if (regionMatches(op, i, "cos")) performOperation(COS);
                        i += 2;
                        break;
                    case 't':
                        debug("tan(" + stack.peek() + ")");
                        if (regionMatches(op, i, "tan")) performOperation(TAN);
                        i += 2;
                        break;
                    case 'l':
                        debug("ln(" + stack.peek() + ")");
                        if (regionMatches(op, i, "log") || regionMatches(op, i, "ln")) performOperation(LN);
                        i += 1;
                        break;
                    case 'a': {
                        i++;
                        c = arr[i];
                        switch (c) {
                            case 's':
                                debug("asin(" + stack.peek() + ")");
                                if (regionMatches(op, i, "sin")) performOperation(ARSIN);
                                i += 2;
                                break;
                            case 'c':
                                debug("acos(" + stack.peek() + ")");
                                if (regionMatches(op, i, "cos")) performOperation(ARCOS);
                                i += 2;
                                break;
                            case 't':
                                debug("atan(" + stack.peek() + ")");
                                if (regionMatches(op, i, "tan")) performOperation(ARTAN);
                                i += 2;
                                break;
                            case 'b':
                                debug("abs(" + stack.peek() + ")");
                                if (arr[i + 1] == 's') performOperation(ABS);
                        }
                    }
                    case 'p':
                        if (arr[i + 1] == 'i') {
                            stack.push(Math.PI);
                            debug("push pi");
                            i++;
                        } else if (regionMatches(op, i, "prod")) {
                            debug("product");
                            performOperation(PRODUCT);
                            i += 3;
                        }
                        break;
                    case 'e':
                        stack.push(Math.E);
                        debug("push e");
                        break;
                    case 'h':
                        double p3 = stack.pop(), p2 = stack.pop();
                        debug("hyper(" + stack.peek() + ", "+ p2 + ", " + p3 + ")");
                        stack.push(p2);
                        stack.push(p3);
                        if(regionMatches(op, i, "hyper")) performOperation(HYPER);
                        i += 4;
                        break;
                }
            else {
                switch (c) {
                    case '(': {
                        String remainder = op.substring(i, op.length());
                        int index = remainder.indexOf(')');
                        String exp = remainder.substring(1, index);
                        debug("push expression [" + exp + "]");
                        expressions.push((a) -> parse(exp.replace("x", String.valueOf(a))));
                        i += index;
                    }
                }
            }

        }
        if(stack.size() != 1)
            throw new IllegalStateException("More than 1 number on the stack: invalid input");

        if(stack.peek() == stack.peek().intValue())
            return stack.pop().intValue();
        else
            return stack.pop();
    }

    private static boolean isOperator(char c) {
        return "+-*/^!%".contains(String.valueOf(c));
    }

    private void performOperation(UnaryOperator<Double> operator) {
        stack.push(operator.apply(stack.pop()));
        if(DEBUG) {
            debugPush();
            System.out.println();
        }
    }

    private void performOperation(BinaryOperator<Double> operator) {
        double b = stack.pop(), a = stack.pop();
        stack.push(operator.apply(a, b));
        if(DEBUG) {
            debugPush();
            System.out.println();
        }
    }

    private void performOperation(TrinaryOperator<Double> operator) {
        double c = stack.pop(), b = stack.pop(), a = stack.pop();
        stack.push(operator.apply(a, b, c));
        if(DEBUG) {
            debugPush();
            System.out.println();
        }
    }

    private boolean regionMatches(String s, int offset, String check) {
        return s.regionMatches(true, offset, check, 0, check.length());
    }

    public static double hyper(double a, double b, double c) {
        if (b == 1)
            return POWER.apply(a, c);
        else if(c > 2){
            return hyper(a, b - 1, hyper(a, b, c - 1));
        } else if (c == 2) {
            return hyper(a, b-1, a);
        } else {
            return a;
        }
    }

    private static void debug(String s){
        if(DEBUG) System.out.println(s);
    }

    private void debugPush(){
        String peek = String.valueOf(stack.peek());
        debug("push " + (stack.peek() == Math.floor(stack.peek()) ? peek.substring(0, peek.length() - 2) : peek));
    }

    private void debugBinOp(char op){
        double b = stack.pop();
        debug(stack.peek() + " " + op + " " + b);
        stack.push(b);
    }

    private static double round(double d, int digits){
        double magnitude = Math.pow(10, digits);
        return (double)Math.round(d * magnitude) / magnitude;
    }

    private static double round(double d){
        return round(d, ACCURACY);
    }
}