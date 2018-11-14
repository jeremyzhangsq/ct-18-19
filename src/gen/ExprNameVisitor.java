package gen;


import ast.*;

import java.io.PrintWriter;

public class ExprNameVisitor extends BaseGenVisitor<String> {

	protected PrintWriter writer;
	// contains all the free temporary registers
	protected FreeRegs freeRegs;

	public ExprNameVisitor(PrintWriter writer){
		super(writer);
	}


	@Override
	public String visitArrayAccessExpr(ArrayAccessExpr aae) {
		String a = aae.arr.accept(this);
		aae.idx.accept(this);
		return a;
	}

	@Override
	public String visitFieldAccessExpr(FieldAccessExpr fae) {
		return fae.structure.accept(this);
	}

	@Override
	public String visitVarExpr(VarExpr v) {
		return (String) v.name;
	}
}
