package ast;

import java.io.PrintWriter;

public class ASTPrinter implements ASTVisitor<Void> {

    private PrintWriter writer;

    public ASTPrinter(PrintWriter writer) {
            this.writer = writer;
    }

    @Override
    public Void visitBlock(Block b) {
        writer.print("Block(");
        // to complete
        writer.print(")");
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl fd) {
        writer.print("FunDecl(");
        fd.type.accept(this);
        writer.print(","+fd.name+",");
        for (VarDecl vd : fd.params) {
            vd.accept(this);
            writer.print(",");
        }
        fd.block.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitProgram(Program p) {
        writer.print("Program(");
        String delimiter = "";
        for (StructTypeDecl std : p.structTypeDecls) {
            writer.print(delimiter);
            delimiter = ",";
            std.accept(this);
        }
        for (VarDecl vd : p.varDecls) {
            writer.print(delimiter);
            delimiter = ",";
            vd.accept(this);
        }
        for (FunDecl fd : p.funDecls) {
            writer.print(delimiter);
            delimiter = ",";
            fd.accept(this);
        }
        writer.print(")");
	    writer.flush();
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd){
        writer.print("VarDecl(");
        vd.type.accept(this);
        writer.print(","+vd.varName);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
        writer.print("VarExpr(");
        writer.print(v.name);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitBaseType(BaseType bt) {
        writer.print("BaseType(");
        switch (bt){
            case CHAR:
                writer.print("CHAR");
            case INT:
                writer.print("INT");
            case VOID:
                writer.print("void");
        }
        writer.print(")");
        return null;
    }

    @Override
    public Void visitStructType(StructType st) {
        writer.print("StructType(");
        writer.print(st.structName);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitPointerType(PointerType pt) {
        writer.print("PointerType(");
        Type t = pt.type;
        printType(t);
        writer.print(")");
        return null;
    }


    @Override
    public Void visitArrayType(ArrayType at) {
        writer.print("ArrayType(");
        Type t = at.type;
        printType(t);
        writer.print(",");
        writer.print(at.arrSize);
        writer.print(")");
        return null;
    }
    private void printType(Type t) {
        if (t instanceof BaseType)
            visitBaseType((BaseType) t);
        else if (t instanceof StructType)
            visitStructType((StructType) t);
        else if (t instanceof ArrayType)
            visitArrayType((ArrayType) t);
        else if (t instanceof PointerType)
            visitPointerType((PointerType) t);
        else
            writer.print("INVALID TYPE");
    }

    @Override
    public Void visitIntLiteral(IntLiteral il) {
        return null;
    }

    @Override
    public Void visitStrLiteral(StrLiteral sl) {
        return null;
    }

    @Override
    public Void visitChrLiteral(ChrLiteral cl) {
        return null;
    }

    @Override
    public Void visitSizeOfExpr(SizeOfExpr soe) {
        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr fce) {
        return null;
    }

    @Override
    public Void visitBinOp(BinOp bop) {
        return null;
    }

    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
        return null;
    }

    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr fae) {
        return null;
    }

    @Override
    public Void visitValueAtExpr(ValueAtExpr vae) {
        return null;
    }

    @Override
    public Void visitTypecastExpr(TypecastExpr te) {
        return null;
    }

    @Override
    public Void visitOp(Op op) {
        return null;
    }

    @Override
    public Void visitWhile(While w) {
        return null;
    }

    @Override
    public Void visitAssign(Assign a) {
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt est) {
        return null;
    }

    @Override
    public Void visitorIf(If i) {
        return null;
    }

    @Override
    public Void visitorReturn(Return r) {
        return null;
    }



    @Override
    public Void visitStructTypeDecl(StructTypeDecl st) {
        // to complete ...
        return null;
    }

    // to complete ...
    
}
