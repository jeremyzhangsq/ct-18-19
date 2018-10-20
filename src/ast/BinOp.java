package ast;

public class BinOp extends Expr {
    public final Expr lhs;
    public final Op op;
    public final Expr rhs;
    public int precedence;
    public BinOp(Expr lhs, Op op, Expr rhs){
        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
        this.precedence = 0;
    }
    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitBinOp(this);
    }
}
