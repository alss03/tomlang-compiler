import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CompilationErrorTest {

    private List<String> collectErrors(String relPath) throws IOException {
        String source = Files.readString(Path.of(relPath));
        CharStream input = CharStreams.fromString(source);

        TomLangLexer lexer = new TomLangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TomLangParser parser = new TomLangParser(tokens);

        TomLangErrorListener err = new TomLangErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(err);

        ParseTree tree = parser.prog();

        List<String> errors = new ArrayList<>();
        if (err.hasErrors()) errors.addAll(err.getErrors());

        SemanticVisitor sema = new SemanticVisitor();
        sema.visit(tree);
        if (sema.hasErrors()) errors.addAll(sema.getErrors());

        return errors;
    }

    @Test
    public void testErrorProgramReportsCompilationErrors() throws Exception {
        List<String> errors = collectErrors("tests/error.mlg");
        assertFalse(errors.isEmpty(), "Expected compilation errors in tests/error.mlg");

        boolean hasRedeclaration = errors.stream().anyMatch(s -> s.contains("já declarada"));
        boolean hasUndeclared = errors.stream().anyMatch(s -> s.contains("não declarada"));
        boolean hasIncompatible = errors.stream().anyMatch(s -> s.contains("Atribuição incompatível") || s.contains("incompatível"));
        boolean hasBreakOutside = errors.stream().anyMatch(s -> s.contains("Uso de 'break' fora de loop/switch"));

        assertTrue(hasRedeclaration, "Expected redeclaration error");
        assertTrue(hasUndeclared, "Expected undeclared variable error");
        assertTrue(hasIncompatible, "Expected incompatible assignment or case type error");
        assertTrue(hasBreakOutside, "Expected break-outside-loop error");
    }

    @Test
    public void testOkProgramHasNoCompilationErrors() throws Exception {
        List<String> errors = collectErrors("tests/ok.mlg");
        assertTrue(errors.isEmpty(), () -> "Expected no errors for ok.mlg but got: " + errors);
    }
}
