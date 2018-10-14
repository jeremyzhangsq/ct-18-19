package ast;

import java.util.List;

public class StructTypeDecl implements ASTNode {

    public final StructType stype;
    public final List<VarDecl> vars;

    public StructTypeDecl(StructType stype, List<VarDecl> vars){
        this.stype = stype;
        this.vars = vars;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructTypeDecl(this);
    }

}
