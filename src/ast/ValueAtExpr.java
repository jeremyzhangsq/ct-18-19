package ast;

public class ValueAtExpr extends Expr {
    public final Expr val;
    public ValueAtExpr(Expr val){
        this.val = val;
    }
    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitValueAtExpr(this);
    }
}
