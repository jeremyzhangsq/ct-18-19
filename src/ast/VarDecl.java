package ast;

import gen.Register;

import java.util.Stack;

public class VarDecl implements ASTNode {
    public final Type type;
    public final String varName;
    public Stack<Integer> offset;
    public boolean isGlobal;
    public int paramIdx;
    public StructTypeDecl std;
    public Register paramRegister = null;
    public String FuncName;
    public VarDecl(Type type, String varName) {
	    this.type = type;
	    this.varName = varName;
	    this.paramIdx = -1;
	    offset = new Stack<>();
    }

     public <T> T accept(ASTVisitor<T> v) {
        return v.visitVarDecl(this);
    }
}
