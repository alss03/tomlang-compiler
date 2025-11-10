import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.nio.file.*;
import java.util.Scanner;

public class TomLangMain {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Uso: java TomLangMain <arquivo.tl>");
            System.exit(1);
        }

        String source = Files.readString(Path.of(args[0]));
        CharStream input = CharStreams.fromString(source);

        TomLangLexer lexer = new TomLangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TomLangParser parser = new TomLangParser(tokens);

        TomLangErrorListener err = new TomLangErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(err);

        ParseTree tree = parser.prog();

        if (err.hasErrors()) {
            System.err.println("Falha de compilação (erros sintáticos):");
            err.getErrors().forEach(System.err::println);
            System.exit(1);
        }

        // Semântica
        SemanticVisitor sema = new SemanticVisitor();
        sema.visit(tree);
        if (sema.hasErrors()) {
            System.err.println("Falha de compilação (erros semânticos):");
            for (String e : sema.getErrors()) System.err.println(e);
            System.exit(1);
        }

        // Execução (interpretação)
        Scanner inputScanner = new Scanner(System.in);
        InterpreterVisitor interpreter = new InterpreterVisitor(inputScanner);
        interpreter.visit(tree);                 // ← executa e imprime via printf
    }
}
