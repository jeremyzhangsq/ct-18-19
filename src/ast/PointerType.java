package ast;

import gen.Register;

public class PointerType implements Type {
    public final Type type;
    public Register register;
    public PointerType(Type type){
        this.type = type;
    }
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitPointerType(this);
    }
}
