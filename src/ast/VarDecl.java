package ast;

public class VarDecl implements ASTNode {
    public final Type type;
    public final String varName;
    public int offset;
    public boolean isGlobal;
    public int paramIdx;
    public StructTypeDecl std;
    public VarDecl(Type type, String varName) {
	    this.type = type;
	    this.varName = varName;
	    this.paramIdx = -1;
    }

     public <T> T accept(ASTVisitor<T> v) {
        return v.visitVarDecl(this);
    }
}
