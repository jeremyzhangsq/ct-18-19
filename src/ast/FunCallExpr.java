package ast;

import java.util.List;

public class FunCallExpr extends Expr {
    public final String funcName;
    public final List<Expr> params;

    public FunCallExpr(String funcName, List<Expr> params){
        this.funcName = funcName;
        this.params = params;
    }
    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFunCallExpr(this);
    }
}
