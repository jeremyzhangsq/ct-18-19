package ast;

public class StructType implements Type {

    public final String structName;
    public StructType(String name){
        this.structName = name;
    }
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructType(this);
    }
}
