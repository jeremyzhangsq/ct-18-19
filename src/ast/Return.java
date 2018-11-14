package ast;

public class Return extends Stmt {
    public final Expr optionReturn;
    public FunDecl FuncName;
    public Return(Expr optionReturn){
        this.optionReturn = optionReturn;
        this.FuncName = null;
    }
    public Return(){
        this(null);
    }
    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitReturn(this);
    }
}
