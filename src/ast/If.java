package ast;

public class If extends Stmt {

    public final Expr condition;
    public final Stmt stmt;
    public final Stmt elseStmt;
    public If(Expr condition, Stmt stmt, Stmt elseStmt){
        this.condition = condition;
        this.stmt = stmt;
        this.elseStmt = elseStmt;
    }
    public If(Expr condition, Stmt stmt){
        this(condition,stmt,null);
    }
    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitIf(this);
    }
}
