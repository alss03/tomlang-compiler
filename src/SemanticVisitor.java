import java.util.*;

public class SemanticVisitor extends TomLangBaseVisitor<SymbolTable.Type> {
    private final SymbolTable sym = new SymbolTable();
    private final List<String> errors = new ArrayList<>();

    public boolean hasErrors() { return !errors.isEmpty(); }
    public List<String> getErrors() { return errors; }

    @Override
    public SymbolTable.Type visitDecl(TomLangParser.DeclContext ctx) {
        String name = ctx.ID().getText();
        SymbolTable.Type t = toType(ctx.type().getText());
        if (!sym.declare(name, t)) {
            errors.add(err(ctx.start.getLine(), "Variável '" + name + "' já declarada neste escopo"));
        }
        if (ctx.expr() != null) {
            SymbolTable.Type v = visit(ctx.expr());
            if (!assignable(t, v)) {
                errors.add(err(ctx.start.getLine(), "Atribuição incompatível: " + t + " = " + v));
            }
        }
        return SymbolTable.Type.UNKNOWN;
    }

    @Override
    public SymbolTable.Type visitBlock(TomLangParser.BlockContext ctx) {
        sym.pushScope();
        for (var s : ctx.stmt()) visit(s);
        sym.popScope();
        return SymbolTable.Type.UNKNOWN;
    }

    @Override
    public SymbolTable.Type visitAssign(TomLangParser.AssignContext ctx) {
        String name = ctx.ID().getText();
        SymbolTable.Type declared = sym.lookup(name);
        if (declared == null) {
            errors.add(err(ctx.start.getLine(), "Variável '" + name + "' não declarada"));
            declared = SymbolTable.Type.UNKNOWN;
        }
        SymbolTable.Type value = visit(ctx.expr());
        if (!assignable(declared, value)) {
            errors.add(err(ctx.start.getLine(), "Atribuição incompatível: " + declared + " = " + value));
        }
        return declared;
    }

    @Override
    public SymbolTable.Type visitIfStmt(TomLangParser.IfStmtContext ctx) {
        var cond = visit(ctx.expr());
        if (cond == SymbolTable.Type.STRING) {
            errors.add(err(ctx.start.getLine(), "Condição do if não pode ser string"));
        }
        visit(ctx.block(0));
        if (ctx.block().size() > 1) visit(ctx.block(1));
        return SymbolTable.Type.UNKNOWN;
    }

    @Override
    public SymbolTable.Type visitWhileStmt(TomLangParser.WhileStmtContext ctx) {
        var cond = visit(ctx.expr());
        if (cond == SymbolTable.Type.STRING) {
            errors.add(err(ctx.start.getLine(), "Condição do while não pode ser string"));
        }
        breakable.push(true);
        visit(ctx.block());
        breakable.pop();
        return SymbolTable.Type.UNKNOWN;
    }

    @Override
    public SymbolTable.Type visitDoWhileStmt(TomLangParser.DoWhileStmtContext ctx) {
        var cond = visit(ctx.expr());
        if (cond == SymbolTable.Type.STRING) {
            errors.add(err(ctx.start.getLine(), "Condição do do-while não pode ser string"));
        }
        breakable.push(true);
        visit(ctx.block());
        breakable.pop();
        return SymbolTable.Type.UNKNOWN;
    }

    @Override
    public SymbolTable.Type visitForStmt(TomLangParser.ForStmtContext ctx) {
        sym.pushScope();
        if (ctx.forInit().getText().length() > 0) visit(ctx.forInit());
        if (ctx.forCond() != null) {
            var t = visit(ctx.forCond());
            if (t == SymbolTable.Type.STRING) {
                errors.add(err(ctx.start.getLine(), "Condição do for não pode ser string"));
            }
        }
        if (ctx.forUpdate() != null) visit(ctx.forUpdate());
        breakable.push(true);
        visit(ctx.block());
        breakable.pop();
        sym.popScope();
        return SymbolTable.Type.UNKNOWN;
    }

    @Override
    public SymbolTable.Type visitPrintStmt(TomLangParser.PrintStmtContext ctx) {
        if (ctx.expr() != null) visit(ctx.expr());
        return SymbolTable.Type.UNKNOWN;
    }

    @Override
    public SymbolTable.Type visitReadStmt(TomLangParser.ReadStmtContext ctx) {
        String name = ctx.ID().getText();
        if (sym.lookup(name) == null) {
            errors.add(err(ctx.start.getLine(), "Variável '" + name + "' não declarada (scanf)"));
        }
        return SymbolTable.Type.UNKNOWN;
    }

    @Override
    public SymbolTable.Type visitSwitchStmt(TomLangParser.SwitchStmtContext ctx) {
        var switchType = visit(ctx.expr());

        breakable.push(true);
        sym.pushScope();

        for (var sect : ctx.switchSection()) {
            SymbolTable.Type labelType = typeOfSwitchLabel(sect.switchLabel());
            if (!caseCompatible(switchType, labelType)) {
                errors.add(err(sect.start.getLine(), "Tipo do 'case' (" + labelType + ") incompatível com 'switch' (" + switchType + ")"));
            }
            for (var s : sect.stmt()) visit(s);
        }

        if (ctx.defaultSection() != null) {
            for (var s : ctx.defaultSection().stmt()) visit(s);
        }

        sym.popScope();
        breakable.pop();
        return SymbolTable.Type.UNKNOWN;
    }

    private SymbolTable.Type typeOfSwitchLabel(TomLangParser.SwitchLabelContext ctx) {
        if (ctx.INT_LIT() != null)   return SymbolTable.Type.INT;
        if (ctx.STRING() != null)    return SymbolTable.Type.STRING;
        if (ctx.ID() != null) {
            String name = ctx.ID().getText();
            var t = sym.lookup(name);
            if (t == null) return SymbolTable.Type.UNKNOWN;
            return t;
        }
        return SymbolTable.Type.UNKNOWN;
    }

    private boolean caseCompatible(SymbolTable.Type switchType, SymbolTable.Type labelType) {
        if (switchType == SymbolTable.Type.UNKNOWN || labelType == SymbolTable.Type.UNKNOWN) return true;
        if (switchType == labelType) return true;
        if (switchType == SymbolTable.Type.FLOAT && labelType == SymbolTable.Type.INT) return true;
        return false;
    }

    @Override
    public SymbolTable.Type visitBreakStmt(TomLangParser.BreakStmtContext ctx) {
        if (breakable.isEmpty() || !breakable.peek()) {
            errors.add(err(ctx.start.getLine(), "Uso de 'break' fora de loop/switch"));
        }
        return SymbolTable.Type.UNKNOWN;
    }

    @Override
    public SymbolTable.Type visitPrimary(TomLangParser.PrimaryContext ctx) {
        if (ctx.INT_LIT() != null)   return SymbolTable.Type.INT;
        if (ctx.FLOAT_LIT() != null) return SymbolTable.Type.FLOAT;
        if (ctx.STRING() != null)    return SymbolTable.Type.STRING;
        if (ctx.ID() != null) {
            String name = ctx.ID().getText();
            var t = sym.lookup(name);
            if (t == null) {
                errors.add(err(ctx.start.getLine(), "Variável '" + name + "' não declarada"));
                return SymbolTable.Type.UNKNOWN;
            }
            return t;
        }
        return visit(ctx.expr());
    }

    @Override
    public SymbolTable.Type visitUnary(TomLangParser.UnaryContext ctx) {
        if (ctx.getChildCount() == 2) {
            var t = visit(ctx.unary());
            String op = ctx.getChild(0).getText();
            if (op.equals("!")) return SymbolTable.Type.BOOL;
            if (t == SymbolTable.Type.STRING) {
                errors.add(err(ctx.start.getLine(), "Operador '" + op + "' inválido para string"));
                return SymbolTable.Type.UNKNOWN;
            }
            return t;
        }
        return visit(ctx.primary());
    }

    @Override
    public SymbolTable.Type visitMulExpr(TomLangParser.MulExprContext ctx) {
        var t = visit(ctx.unary(0));
        for (int i = 1; i < ctx.unary().size(); i++) {
            String op = ctx.getChild(2*i - 1).getText();
            var rhs = visit(ctx.unary(i));
            t = SymbolTable.promote(t, rhs, op);
            if (t == SymbolTable.Type.UNKNOWN) {
                errors.add(err(ctx.start.getLine(), "Operação inválida com '" + op + "'"));
                break;
            }
        }
        return t;
    }

    @Override
    public SymbolTable.Type visitAddExpr(TomLangParser.AddExprContext ctx) {
        var t = visit(ctx.mulExpr(0));
        for (int i = 1; i < ctx.mulExpr().size(); i++) {
            String op = ctx.getChild(2*i - 1).getText();
            var rhs = visit(ctx.mulExpr(i));
            t = SymbolTable.promote(t, rhs, op);
            if (t == SymbolTable.Type.UNKNOWN) {
                errors.add(err(ctx.start.getLine(), "Operação inválida com '" + op + "'"));
                break;
            }
        }
        return t;
    }

    @Override
    public SymbolTable.Type visitOrExpr(TomLangParser.OrExprContext ctx) {
        if (ctx.getChildCount() == 1) {
            return visit(ctx.andExpr(0));
        }
        for (var a : ctx.andExpr()) visit(a);
        return SymbolTable.Type.BOOL;
    }

    @Override
    public SymbolTable.Type visitAndExpr(TomLangParser.AndExprContext ctx) {
        if (ctx.getChildCount() == 1) {
            return visit(ctx.eqExpr(0));
        }
        for (var e : ctx.eqExpr()) visit(e);
        return SymbolTable.Type.BOOL;
    }

    @Override
    public SymbolTable.Type visitEqExpr(TomLangParser.EqExprContext ctx) {
        if (ctx.getChildCount() == 1) {
            return visit(ctx.relExpr(0));
        }
        for (var r : ctx.relExpr()) visit(r);
        return SymbolTable.Type.BOOL;
    }

    @Override
    public SymbolTable.Type visitRelExpr(TomLangParser.RelExprContext ctx) {
        if (ctx.getChildCount() == 1) {
            return visit(ctx.addExpr(0));
        }
        for (var a : ctx.addExpr()) visit(a);
        return SymbolTable.Type.BOOL;
    }

    private SymbolTable.Type toType(String t) {
        return switch (t) {
            case "int" -> SymbolTable.Type.INT;
            case "float" -> SymbolTable.Type.FLOAT;
            case "string" -> SymbolTable.Type.STRING;
            default -> SymbolTable.Type.UNKNOWN;
        };
    }

    private boolean assignable(SymbolTable.Type target, SymbolTable.Type value) {
        if (target == null || value == null) return false;
        if (target == value) return true;
        if (target == SymbolTable.Type.FLOAT && value == SymbolTable.Type.INT) return true;
        return false;
    }

    private String err(int line, String msg) {
        return "Linha " + line + " - " + msg;
    }

    private final Deque<Boolean> breakable = new ArrayDeque<>();
}
