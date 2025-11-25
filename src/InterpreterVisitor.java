import java.util.*;
import java.util.regex.Pattern;

public class InterpreterVisitor extends TomLangBaseVisitor<Object> {

    private final Deque<Map<String, Object>> scopes = new ArrayDeque<>();
    private final Deque<Map<String, SymbolTable.Type>> types = new ArrayDeque<>();
    private enum Ctx { LOOP, SWITCH }
    private final Deque<Ctx> breakCtx = new ArrayDeque<>();
    private Ctx breakSignal = null;
    private final Scanner in;

    public InterpreterVisitor(Scanner in) {
        this.in = in;
        pushScope();
    }

    private void pushScope() { scopes.push(new HashMap<>()); types.push(new HashMap<>()); }
    private void popScope()  { scopes.pop(); types.pop(); }

    private void declare(String name, SymbolTable.Type t, Object value) {
        types.peek().put(name, t);
        scopes.peek().put(name, value);
    }
    private SymbolTable.Type typeOf(String name) {
        for (Map<String, SymbolTable.Type> s : types) if (s.containsKey(name)) return s.get(name);
        return null;
    }
    private Object get(String name) {
        for (Map<String, Object> s : scopes) if (s.containsKey(name)) return s.get(name);
        return null;
    }
    private void set(String name, Object v) {
        for (Map<String, Object> s : scopes) if (s.containsKey(name)) { s.put(name, v); return; }
        scopes.peek().put(name, v);
    }

    @Override
    public Object visitProg(TomLangParser.ProgContext ctx) {
        for (var c : ctx.children) visit(c);
        return null;
    }

    @Override
    public Object visitDecl(TomLangParser.DeclContext ctx) {
        String name = ctx.ID().getText();
        SymbolTable.Type t = toType(ctx.type().getText());
        Object init = defaultFor(t);
        if (ctx.expr() != null) init = castTo(t, eval(ctx.expr()));
        declare(name, t, init);
        return null;
    }

    @Override
    public Object visitBlock(TomLangParser.BlockContext ctx) {
        pushScope();
        for (var s : ctx.stmt()) {
            visit(s);
            if (breakSignal != null) break;
        }
        popScope();
        return null;
    }

    @Override
    public Object visitAssign(TomLangParser.AssignContext ctx) {
        String name = ctx.ID().getText();
        SymbolTable.Type t = typeOf(name);
        Object v = castTo(t, eval(ctx.expr()));
        set(name, v);
        return null;
    }

    @Override
    public Object visitIfStmt(TomLangParser.IfStmtContext ctx) {
        if (truthy(eval(ctx.expr()))) {
            visit(ctx.block(0));
        } else if (ctx.block().size() > 1) {
            visit(ctx.block(1));
        }
        return null;
    }

    @Override
    public Object visitWhileStmt(TomLangParser.WhileStmtContext ctx) {
        breakCtx.push(Ctx.LOOP);
        while (truthy(eval(ctx.expr()))) {
            visit(ctx.block());
            if (breakSignal == Ctx.LOOP) { breakSignal = null; break; }
        }
        breakCtx.pop();
        return null;
    }

    @Override
    public Object visitDoWhileStmt(TomLangParser.DoWhileStmtContext ctx) {
        breakCtx.push(Ctx.LOOP);
        do {
            visit(ctx.block());
            if (breakSignal == Ctx.LOOP) { breakSignal = null; break; }
        } while (truthy(eval(ctx.expr())));
        breakCtx.pop();
        return null;
    }

    @Override
    public Object visitForStmt(TomLangParser.ForStmtContext ctx) {
        pushScope();
        breakCtx.push(Ctx.LOOP);
        if (ctx.forInit().getText().length() > 0) visit(ctx.forInit());
        while (ctx.forCond() == null || truthy(eval(ctx.forCond().expr()))) {
            visit(ctx.block());
            if (breakSignal == Ctx.LOOP) { breakSignal = null; break; }
            if (ctx.forUpdate() != null) visit(ctx.forUpdate());
        }
        breakCtx.pop();
        popScope();
        return null;
    }

    @Override
    public Object visitBreakStmt(TomLangParser.BreakStmtContext ctx) {
        if (!breakCtx.isEmpty()) breakSignal = breakCtx.peek();
        return null;
    }

    @Override
    public Object visitSwitchStmt(TomLangParser.SwitchStmtContext ctx) {
        Object key = eval(ctx.expr());
        breakCtx.push(Ctx.SWITCH);
        boolean matched = false;

        for (var sect : ctx.switchSection()) {
            Object label = evalSwitchLabel(sect.switchLabel());
            boolean eq = equalsValue(key, label);
            if (eq || matched) {
                matched = true;
                for (var s : sect.stmt()) {
                    visit(s);
                    if (breakSignal == Ctx.SWITCH) {
                        break;
                    }
                }
                if (breakSignal == Ctx.SWITCH) {
                    breakSignal = null;
                    break;
                }
            }
        }

        if (!matched && ctx.defaultSection() != null) {
            for (var s : ctx.defaultSection().stmt()) {
                visit(s);
                if (breakSignal == Ctx.SWITCH) { breakSignal = null; break; }
            }
        }

        breakCtx.pop();
        return null;
    }

    private Object evalSwitchLabel(TomLangParser.SwitchLabelContext ctx) {
        if (ctx.INT_LIT() != null)   return Integer.parseInt(ctx.INT_LIT().getText());
        if (ctx.STRING() != null)    return unquote(ctx.STRING().getText());
        if (ctx.ID() != null)        return get(ctx.ID().getText());
        return null;
    }

    @Override
    public Object visitPrintStmt(TomLangParser.PrintStmtContext ctx) {
        if (ctx.STRING() != null) {
            System.out.println(unquote(ctx.STRING().getText()));
        } else if (ctx.expr() != null) {
            Object v = eval(ctx.expr());
            System.out.println(stringify(v));
        }
        return null;
    }

    @Override
    public Object visitReadStmt(TomLangParser.ReadStmtContext ctx) {
        String name = ctx.ID().getText();
        SymbolTable.Type t = typeOf(name);
        String line = in.nextLine();
        Object v = parseAs(t, line);
        set(name, v);
        return null;
    }

    private Object eval(TomLangParser.ExprContext ctx)      { return visit(ctx); }
    private Object eval(TomLangParser.OrExprContext ctx)    { return visit(ctx); }
    private Object eval(TomLangParser.AndExprContext ctx)   { return visit(ctx); }
    private Object eval(TomLangParser.EqExprContext ctx)    { return visit(ctx); }
    private Object eval(TomLangParser.RelExprContext ctx)   { return visit(ctx); }
    private Object eval(TomLangParser.AddExprContext ctx)   { return visit(ctx); }
    private Object eval(TomLangParser.MulExprContext ctx)   { return visit(ctx); }
    private Object eval(TomLangParser.UnaryContext ctx)     { return visit(ctx); }
    private Object eval(TomLangParser.PrimaryContext ctx)   { return visit(ctx); }

    @Override
    public Object visitOrExpr(TomLangParser.OrExprContext ctx) {
        Object left = eval(ctx.andExpr(0));
        for (int i = 1; i < ctx.andExpr().size(); i++) {
            Object right = eval(ctx.andExpr(i));
            left = (truthy(left) || truthy(right));
        }
        return left;
    }

    @Override
    public Object visitAndExpr(TomLangParser.AndExprContext ctx) {
        Object left = eval(ctx.eqExpr(0));
        for (int i = 1; i < ctx.eqExpr().size(); i++) {
            Object right = eval(ctx.eqExpr(i));
            left = (truthy(left) && truthy(right));
        }
        return left;
    }

    @Override
    public Object visitEqExpr(TomLangParser.EqExprContext ctx) {
        Object left = eval(ctx.relExpr(0));
        for (int i = 1; i < ctx.relExpr().size(); i++) {
            String op = ctx.getChild(2*i - 1).getText();
            Object right = eval(ctx.relExpr(i));
            boolean res = op.equals("==") ? equalsValue(left, right) : !equalsValue(left, right);
            left = res;
        }
        return left;
    }

    @Override
    public Object visitRelExpr(TomLangParser.RelExprContext ctx) {
        Object left = eval(ctx.addExpr(0));
        for (int i = 1; i < ctx.addExpr().size(); i++) {
            String op = ctx.getChild(2*i - 1).getText();
            Object right = eval(ctx.addExpr(i));
            double a = toDouble(left), b = toDouble(right);
            boolean res = switch (op) {
                case "<"  -> a <  b;
                case ">"  -> a >  b;
                case "<=" -> a <= b;
                case ">=" -> a >= b;
                default   -> false;
            };
            left = res;
        }
        return left;
    }

    @Override
    public Object visitAddExpr(TomLangParser.AddExprContext ctx) {
        Object val = eval(ctx.mulExpr(0));
        for (int i = 1; i < ctx.mulExpr().size(); i++) {
            String op = ctx.getChild(2*i - 1).getText();
            Object rhs = eval(ctx.mulExpr(i));
            if (op.equals("+")) {
                if (val instanceof String || rhs instanceof String) {
                    val = stringify(val) + stringify(rhs);
                } else if (val instanceof Double || rhs instanceof Double) {
                    val = toDouble(val) + toDouble(rhs);
                } else {
                    val = ((Number)val).intValue() + ((Number)rhs).intValue();
                }
            } else {
                if (val instanceof String || rhs instanceof String) {
                    throw new RuntimeException("Operador '-' inválido para string");
                } else if (val instanceof Double || rhs instanceof Double) {
                    val = toDouble(val) - toDouble(rhs);
                } else {
                    val = ((Number)val).intValue() - ((Number)rhs).intValue();
                }
            }
        }
        return val;
    }

    @Override
    public Object visitMulExpr(TomLangParser.MulExprContext ctx) {
        Object val = eval(ctx.unary(0));
        for (int i = 1; i < ctx.unary().size(); i++) {
            String op = ctx.getChild(2*i - 1).getText();
            Object rhs = eval(ctx.unary(i));
            if (val instanceof String || rhs instanceof String) {
                throw new RuntimeException("Operadores '*'/'/' inválidos para string");
            }
            if (op.equals("*")) {
                if (val instanceof Double || rhs instanceof Double) {
                    val = toDouble(val) * toDouble(rhs);
                } else {
                    val = ((Number)val).intValue() * ((Number)rhs).intValue();
                }
            } else {
                val = toDouble(val) / toDouble(rhs);
            }
        }
        return val;
    }

    @Override
    public Object visitUnary(TomLangParser.UnaryContext ctx) {
        if (ctx.getChildCount() == 2) {
            String op = ctx.getChild(0).getText();
            Object v = eval(ctx.unary());
            return switch (op) {
                case "!" -> !truthy(v);
                case "+" -> (v instanceof Double) ? v : ((Number)v).intValue();
                case "-" -> (v instanceof Double) ? -((Double)v) : -((Number)v).intValue();
                default  -> v;
            };
        }
        return eval(ctx.primary());
    }

    @Override
    public Object visitPrimary(TomLangParser.PrimaryContext ctx) {
        if (ctx.INT_LIT()   != null) return Integer.parseInt(ctx.INT_LIT().getText());
        if (ctx.FLOAT_LIT() != null) return Double.parseDouble(ctx.FLOAT_LIT().getText());
        if (ctx.STRING()    != null) return unquote(ctx.STRING().getText());
        if (ctx.ID()        != null) return get(ctx.ID().getText());
        return eval(ctx.expr());
    }

    private SymbolTable.Type toType(String t) {
        return switch (t) {
            case "int"    -> SymbolTable.Type.INT;
            case "float"  -> SymbolTable.Type.FLOAT;
            case "string" -> SymbolTable.Type.STRING;
            default       -> SymbolTable.Type.UNKNOWN;
        };
    }
    private Object defaultFor(SymbolTable.Type t) {
        return switch (t) {
            case INT   -> 0;
            case FLOAT -> 0.0;
            case STRING-> "";
            case BOOL  -> false;
            default    -> null;
        };
    }
    private Object castTo(SymbolTable.Type t, Object v) {
        if (v == null) return null;
        return switch (t) {
            case INT   -> (v instanceof Double) ? ((Double)v).intValue() : ((Number)v).intValue();
            case FLOAT -> (v instanceof Double) ? v : ((Number)v).doubleValue();
            case STRING-> stringify(v);
            case BOOL  -> truthy(v);
            default    -> v;
        };
    }
    private boolean truthy(Object v) {
        if (v instanceof Boolean b) return b;
        if (v instanceof Number n)  return n.doubleValue() != 0.0;
        if (v instanceof String s)  return !s.isEmpty();
        return v != null;
    }
    private double toDouble(Object v) {
        return (v instanceof Double d) ? d : ((Number)v).doubleValue();
    }
    private boolean equalsValue(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) return toDouble(a) == toDouble(b);
        return Objects.equals(stringify(a), stringify(b));
    }
    private String stringify(Object v) { return String.valueOf(v); }
    private String unquote(String s)   { return s.substring(1, s.length()-1); }

    private Object parseAs(SymbolTable.Type t, String s) {
        return switch (t) {
            case INT    -> Integer.parseInt(s.trim());
            case FLOAT  -> Double.parseDouble(s.trim());
            case STRING -> s;
            case BOOL   -> Pattern.compile("^(1|true|TRUE|True)$").matcher(s.trim()).find();
            default     -> s;
        };
    }

    @Override
    public Object visitForInit(TomLangParser.ForInitContext ctx) {
        if (ctx.decl() != null) return visit(ctx.decl());
        if (ctx.assign() != null) return visit(ctx.assign());
        return null;
    }
    @Override
    public Object visitForUpdate(TomLangParser.ForUpdateContext ctx) {
        if (ctx.assign() != null) return visit(ctx.assign());
        return null;
    }
}
