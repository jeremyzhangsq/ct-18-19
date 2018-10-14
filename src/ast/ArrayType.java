package ast;

public class ArrayType {
    public final Type type;
    public final int arrSize;

    public ArrayType(Type type, int arrSize) {
        this.type = type;
        this.arrSize = arrSize;
    }
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayType(this);
    }
}
