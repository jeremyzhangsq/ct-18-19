package ast;

public abstract class Expr implements ASTNode {
    public Type type; // to be filled in by the type analyser
    public int paramIndex = -1;
    public abstract <T> T accept(ASTVisitor<T> v);
}
