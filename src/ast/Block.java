package ast;

import java.util.List;

public class Block extends Stmt {
    public final List<VarDecl> vars;
    public final List<Stmt> stmts;
    public Block(List<VarDecl> vars, List<Stmt> stmts){
        this.vars = vars;
        this.stmts = stmts;
    }
    // to complete ...

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitBlock(this);
    }
}
