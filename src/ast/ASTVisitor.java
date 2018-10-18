package ast;

public interface ASTVisitor<T> {
    public T visitBaseType(BaseType bt);
    public T visitStructType(StructType st);
    public T visitArrayType(ArrayType at);
    public T visitIntLiteral(IntLiteral il);
    public T visitStrLiteral(StrLiteral sl);
    public T visitChrLiteral(ChrLiteral cl);
    public T visitSizeOfExpr(SizeOfExpr soe);
    public T visitFunCallExpr(FunCallExpr fce);
    public T visitBinOp(BinOp bop);
    public T visitArrayAccessExpr(ArrayAccessExpr aae);
    public T visitFieldAccessExpr(FieldAccessExpr fae);
    public T visitValueAtExpr(ValueAtExpr vae);
    public T visitTypecastExpr(TypecastExpr te);
    public T visitOp(Op op);
    public T visitWhile(While w);
    public T visitAssign(Assign a);
    public T visitExprStmt(ExprStmt est);
    public T visitorIf(If i);
    public T visitReturn(Return r);
    public T visitPointerType(PointerType pt);
    public T visitStructTypeDecl(StructTypeDecl st);
    public T visitBlock(Block b);
    public T visitFunDecl(FunDecl p);
    public T visitProgram(Program p);
    public T visitVarDecl(VarDecl vd);
    public T visitVarExpr(VarExpr v);

    // to complete ... (should have one visit method for each concrete AST node class)
}
