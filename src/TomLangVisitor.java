// Generated from TomLang.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TomLangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TomLangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TomLangParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(TomLangParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl(TomLangParser.DeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(TomLangParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(TomLangParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#assign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign(TomLangParser.AssignContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#ifStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(TomLangParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#whileStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(TomLangParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#doWhileStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoWhileStmt(TomLangParser.DoWhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#switchStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchStmt(TomLangParser.SwitchStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#switchSection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchSection(TomLangParser.SwitchSectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#defaultSection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefaultSection(TomLangParser.DefaultSectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#switchLabel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchLabel(TomLangParser.SwitchLabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#breakStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(TomLangParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#forStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStmt(TomLangParser.ForStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#forInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInit(TomLangParser.ForInitContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#forCond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForCond(TomLangParser.ForCondContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#forUpdate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForUpdate(TomLangParser.ForUpdateContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(TomLangParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#printStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintStmt(TomLangParser.PrintStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#readStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReadStmt(TomLangParser.ReadStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(TomLangParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#orExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrExpr(TomLangParser.OrExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#andExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndExpr(TomLangParser.AndExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#eqExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqExpr(TomLangParser.EqExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#relExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelExpr(TomLangParser.RelExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#addExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExpr(TomLangParser.AddExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#mulExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulExpr(TomLangParser.MulExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#unary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary(TomLangParser.UnaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link TomLangParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(TomLangParser.PrimaryContext ctx);
}