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
        if (b.vars.size()==1){
            b.vars.get(0).accept(this);
        }
        else if (b.vars.size() > 1){
            for (int i = 0;i<b.vars.size()-1;i++){
                VarDecl vd = b.vars.get(i);
                vd.accept(this);
                writer.print(",");
            }
            b.vars.get(b.vars.size()-1).accept(this);
        }
        if (b.vars.size()>0 && b.stmts.size() >0)
            writer.print(",");
        if (b.stmts.size()==1){
            b.stmts.get(0).accept(this);
        }
        else if (b.stmts.size() > 1){
            for (int i = 0;i<b.stmts.size()-1;i++){
                Stmt stmt = b.stmts.get(i);
                stmt.accept(this);
                writer.print(",");
            }
            b.stmts.get(b.stmts.size()-1).accept(this);
        }
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
    public Void visitStructTypeDecl(StructTypeDecl st) {
        writer.print("StructTypeDecl(");
        st.stype.accept(this);
        for(VarDecl vd: st.vars){
            writer.print(",");
            vd.accept(this);
        }
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
        if (bt == BaseType.CHAR)
            writer.print("CHAR");
        else if (bt == BaseType.INT)
            writer.print("INT");
        else if (bt == BaseType.VOID)
            writer.print("VOID");
        else
            writer.print("INVALID");
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
        writer.print("IntLiteral(");
        writer.print(String.valueOf(il.val));
        writer.print(")");
        return null;
    }

    @Override
    public Void visitStrLiteral(StrLiteral sl) {
        writer.print("StrLiteral(");
        writer.print(sl.val);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitChrLiteral(ChrLiteral cl) {
        writer.print("ChrLiteral(");
        writer.print(String.valueOf(cl.val));
        writer.print(")");
        return null;
    }

    @Override
    public Void visitSizeOfExpr(SizeOfExpr soe) {
        writer.print("SizeOfExpr(");
        soe.type.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr fce) {
        writer.print("FunCallExpr(");
        writer.print(fce.funcName);
        for (Expr e: fce.params){
            writer.print(",");
            e.accept(this);
        }
        writer.print(")");
        return null;
    }

    @Override
    public Void visitBinOp(BinOp bop) {
        writer.print("BinOp(");
        bop.lhs.accept(this);
        writer.print(",");
        bop.op.accept(this);
        writer.print(",");
        bop.rhs.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
        writer.print("ArrayAccessExpr(");
        aae.arr.accept(this);
        writer.print(",");
        aae.idx.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr fae) {
        writer.print("FieldAccessExpr(");
        fae.structure.accept(this);
        writer.print(",");
        writer.print(fae.fieldName);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitValueAtExpr(ValueAtExpr vae) {
        writer.print("ValueAtExpr(");
        vae.val.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitTypecastExpr(TypecastExpr te) {
        writer.print("TypecastExpr(");
        te.type.accept(this);
        writer.print(",");
        te.expr.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitOp(Op op) {
        if (op == Op.ADD) writer.print("ADD");
        else if(op == Op.SUB) writer.print("SUB");
        else if(op == Op.MUL) writer.print("MUL");
        else if(op == Op.DIV) writer.print("DIV");
        else if(op == Op.GT)writer.print("GT");
        else if(op == Op.GE) writer.print("GE");
        else if(op == Op.LE) writer.print("LE");
        else if(op == Op.LT) writer.print("LT");
        else if(op == Op.NE) writer.print("NE");
        else if(op == Op.EQ) writer.print("EQ");
        else if(op == Op.OR) writer.print("OR");
        else if(op == Op.AND) writer.print("AND");
        else if(op == Op.MOD) writer.print("MOD");
        else writer.print("INVALID");
        return null;
    }

    @Override
    public Void visitWhile(While w) {
        writer.print("While(");
        w.expr.accept(this);
        writer.print(",");
        w.stmt.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitAssign(Assign a) {
        writer.print("Assign(");
        a.lhs.accept(this);
        writer.print(",");
        a.rhs.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt est) {
        writer.print("ExprStmt(");
        est.expr.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitIf(If i) {
        writer.print("If(");
        i.condition.accept(this);
        writer.print(",");
        i.stmt.accept(this);
        writer.print(",");
        i.elseStmt.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitReturn(Return r) {
        writer.print("Return(");
        r.optionReturn.accept(this);
        writer.print(")");
        return null;
    }




    // to complete ...
    
}
