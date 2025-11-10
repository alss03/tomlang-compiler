import java.util.*;

public class SymbolTable {
    public enum Type { INT, FLOAT, STRING, BOOL, UNKNOWN }

    private final Deque<Map<String, Type>> scopes = new ArrayDeque<>();

    public SymbolTable() {
        pushScope();
    }

    public void pushScope() { scopes.push(new HashMap<>()); }
    public void popScope()  { scopes.pop(); }

    public boolean declare(String name, Type type) {
        Map<String, Type> top = scopes.peek();
        if (top.containsKey(name)) return false; // redeclaração no mesmo escopo
        top.put(name, type);
        return true;
    }

    public Type lookup(String name) {
        for (Map<String, Type> s : scopes) {
            if (s.containsKey(name)) return s.get(name);
        }
        return null; // não declarada
    }

    // Regras de promoção numérica simples
    public static Type promote(Type a, Type b, String op) {
        if (a == null || b == null) return Type.UNKNOWN;
        if (op.matches("==|!=|<=|>=|<|>|&&|\\|\\|")) return Type.BOOL;

        if ((a == Type.FLOAT && (b == Type.FLOAT || b == Type.INT)) ||
                (b == Type.FLOAT && (a == Type.FLOAT || a == Type.INT))) return Type.FLOAT;

        if (a == Type.INT && b == Type.INT) return Type.INT;

        if (op.equals("+") && (a == Type.STRING || b == Type.STRING)) return Type.STRING;

        return Type.UNKNOWN;
    }
}