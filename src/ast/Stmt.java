package ast;

public abstract class Stmt implements ASTNode {
    public FunDecl FuncName;
    public abstract <T> T accept(ASTVisitor<T> v);
}
