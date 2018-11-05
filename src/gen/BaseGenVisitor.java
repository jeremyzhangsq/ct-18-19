package gen;


import ast.*;

import java.io.PrintWriter;

public class BaseGenVisitor<T> implements GenVisitor<T> {

	protected PrintWriter writer;
	// contains all the free temporary registers
	protected FreeRegs freeRegs;

	public BaseGenVisitor(PrintWriter writer){
		this.writer = writer;
		freeRegs = FreeRegs.getInstance();
	}

	@Override
	public T visitBaseType(BaseType bt) {
		return null;
	}

	@Override
	public T visitStructType(StructType st) {
		System.out.println("StructType:"+st.structName);
		return null;
	}

	@Override
	public T visitArrayType(ArrayType at) {
		System.out.println("ArrayType:"+at.type+Integer.toString(at.arrSize));
		return null;
	}

	@Override
	public T visitIntLiteral(IntLiteral il) {
		System.out.println("IntLiteral:"+il.val);
		return null;
	}

	@Override
	public T visitStrLiteral(StrLiteral sl) {
		System.out.println("StrLiteral:"+sl.val);
		return null;
	}

	@Override
	public T visitChrLiteral(ChrLiteral cl) {
		System.out.println("ChrLiteral:"+cl.val);
		return null;
	}

	@Override
	public T visitSizeOfExpr(SizeOfExpr soe) {
		System.out.println("SizeOfExpr:"+soe.type);
		return null;
	}

	@Override
	public T visitFunCallExpr(FunCallExpr fce) {
		System.out.println("FunCallExpr:"+fce.funcName);
		for (Expr expr: fce.params)
			expr.accept(this);
		return null;
	}

	@Override
	public T visitBinOp(BinOp bop) {
		System.out.println("BinOp");
		bop.lhs.accept(this);
		bop.rhs.accept(this);
		return null;
	}

	@Override
	public T visitArrayAccessExpr(ArrayAccessExpr aae) {
		System.out.println("ArrayAccessExpr");
		aae.arr.accept(this);
		aae.idx.accept(this);
		return null;
	}

	@Override
	public T visitFieldAccessExpr(FieldAccessExpr fae) {
		System.out.println("FieldAccessExpr");
		fae.structure.accept(this);
		return null;
	}

	@Override
	public T visitValueAtExpr(ValueAtExpr vae) {
		System.out.println("ValueAtExpr");
		return vae.val.accept(this);
	}

	@Override
	public T visitTypecastExpr(TypecastExpr te) {
		System.out.println("TypecastExpr");
		return te.expr.accept(this);
	}

	@Override
	public T visitOp(Op op) {
		System.out.println("Op");
		return null;
	}

	@Override
	public T visitWhile(While w) {
		System.out.println("While");
		w.expr.accept(this);
		w.stmt.accept(this);
		return null;
	}

	@Override
	public T visitAssign(Assign a) {
		System.out.println("Assign");
		a.lhs.accept(this);
		a.rhs.accept(this);
		return null;
	}

	@Override
	public T visitExprStmt(ExprStmt est) {
		System.out.println("ExprStmt");
		return est.expr.accept(this);
	}

	@Override
	public T visitIf(If i) {
		System.out.println("If");
		i.condition.accept(this);
		i.stmt.accept(this);
		if (i.elseStmt!=null)
			i.elseStmt.accept(this);
		return null;
	}

	@Override
	public T visitReturn(Return r) {
		System.out.println("Return");
		if (r.optionReturn != null)
			r.optionReturn.accept(this);
		return null;
	}

	@Override
	public T visitPointerType(PointerType pt) {
		System.out.println("PointerType");
		return null;
	}

	@Override
	public T visitStructTypeDecl(StructTypeDecl st) {
		System.out.println("StructTypeDecl");
		if (st.vars != null){
			for (VarDecl vd : st.vars)
				vd.accept(this);
		}
		return null;
	}

	@Override
	public T visitBlock(Block b) {
		System.out.println("Block");
		if (b.vars != null){
			for (VarDecl vd : b.vars)
				vd.accept(this);
		}
		if (b.stmts != null){
			for (Stmt s: b.stmts)
				s.accept(this);
		}
		return null;
	}

	@Override
	public T visitFunDecl(FunDecl p) {
		System.out.println("FunDecl");
		if (p.name.equals("read_c") || p.name.equals("print_c") || p.name.equals("print_i")
				|| p.name.equals("print_s") || p.name.equals("mcmalloc") || p.name.equals("read_i"))
			return null;
		if (p.params != null){
			for (VarDecl vd : p.params){
				vd.accept(this);
			}
		}
		p.block.accept(this);
		return null;
	}

	@Override
	public T visitProgram(Program p) {
		System.out.println("Program");
		if (p.structTypeDecls != null){
			for (StructTypeDecl std : p.structTypeDecls)
				std.accept(this);
		}
		if (p.varDecls != null){
			for (VarDecl vd : p.varDecls)
				vd.accept(this);
		}
		if (p.funDecls != null){
			for (FunDecl fd : p.funDecls)
				fd.accept(this);
		}
		return null;
	}

	@Override
	public T visitVarDecl(VarDecl vd) {
		System.out.println("VarDecl");
		return null;
	}

	@Override
	public T visitVarExpr(VarExpr v) {
		System.out.println("VarExpr");
		return null;
	}
	public void emit(String command, String result, String lhs, String rhs){
		if (lhs == null && rhs == null)
			writer.println(command+"\t"+result);
		else if (rhs == null)
			writer.println(command+"\t"+result+",\t"+lhs);
		else
			writer.println(command+"\t"+result+",\t"+lhs+",\t"+rhs);
	}
}
