package gen;

import ast.*;

import java.io.PrintWriter;

public class TextVisitor extends BaseGenVisitor<Register> {

	private AddrVisitor addrVisitor;
	private ValueVisitor valueVisitor;
	public TextVisitor(PrintWriter writer) {
		super(writer);
		addrVisitor = new AddrVisitor(writer);
		valueVisitor = new ValueVisitor(writer);
	}

	@Override
	public Register visitProgram(Program p) {
		writer.println(".text");
		for (FunDecl fd : p.funDecls) {
			fd.accept(this);
		}
		return null;
	}

	@Override
	public Register visitFunDecl(FunDecl p) {
		//TODO: arugument
		if (p.name.equals("read_c"))
			return null;
		else if (p.name.equals("print_c"))
			return null;
		else if	(p.name.equals("print_i"))
			return null;
		else if (p.name.equals("print_s")){
			return null;
		}
		else if (p.name.equals("mcmalloc"))
			return null;
		else if (p.name.equals("read_i"))
			return null;
		else {
			writer.println(p.name+":");
			p.block.accept(this);
			emit("li",Register.v0.toString(),"10",null);
			writer.println("syscall");
			return null;
		}

	}

	@Override
	public Register visitFunCallExpr(FunCallExpr fce) {
		//TODO: arugument
		if (fce.funcName.equals("read_c"))
			return null;
		else if (fce.funcName.equals("print_c"))
			return null;
		else if	(fce.funcName.equals("print_i"))
			return null;
		else if (fce.funcName.equals("print_s")){
			emit("li",Register.v0.toString(),"4",null);
			if (fce.params.get(0) instanceof StrLiteral)
				emit("la",Register.paramRegs[0].toString(),freeRegs.Strs.get(((StrLiteral) fce.params.get(0)).val),null);
			writer.println("syscall");
			return null;
		}
		else if (fce.funcName.equals("mcmalloc"))
			return null;
		else if (fce.funcName.equals("read_i"))
			return null;
		else {
			for (Expr expr: fce.params)
				expr.accept(this);
			return null;
		}
	}

	@Override
	public Register visitAssign(Assign a) {
		Register lhsRegister = a.lhs.accept(addrVisitor);
		Register rhsRegister = a.rhs.accept(valueVisitor);
		emit("sw",rhsRegister.toString(),"0("+lhsRegister.toString()+")",null);
		return null;
	}

}
