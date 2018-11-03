package gen;


import ast.*;

public class BaseGenVisitor<T> implements GenVisitor<T> {


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
		return null;
	}

	@Override
	public T visitFieldAccessExpr(FieldAccessExpr fae) {
		System.out.println("FieldAccessExpr");
		return null;
	}

	@Override
	public T visitValueAtExpr(ValueAtExpr vae) {
		System.out.println("ValueAtExpr");
		vae.val.accept(this);
		return null;
	}

	@Override
	public T visitTypecastExpr(TypecastExpr te) {
		System.out.println("TypecastExpr");
		te.expr.accept(this);
		return null;
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
		est.expr.accept(this);
		return null;
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
		return null;
	}

	@Override
	public T visitBlock(Block b) {
		System.out.println("Block");
		if (b.stmts != null){
			for (Stmt s: b.stmts)
				s.accept(this);
		}
		return null;
	}

	@Override
	public T visitFunDecl(FunDecl p) {
		System.out.println("FunDecl");
		p.block.accept(this);
		return null;
	}

	@Override
	public T visitProgram(Program p) {
		System.out.println("Program");
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
}
