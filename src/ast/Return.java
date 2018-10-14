package ast;

public class Return extends Stmt {
    public final Expr optionReturn;
    public Return(Expr optionReturn){
        this.optionReturn = optionReturn;
    }
    public Return(){
        this(null);
    }
    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitorReturn(this);
    }
}
