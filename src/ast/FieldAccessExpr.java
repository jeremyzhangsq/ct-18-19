package ast;

public class FieldAccessExpr extends Expr {
    public final Expr structure;
    public final String fieldName;
    public FieldAccessExpr(Expr structure, String fieldName){
        this.fieldName = fieldName;
        this.structure = structure;
    }
    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFieldAccessExpr(this);
    }
}
