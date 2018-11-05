package gen;

import ast.*;
import sem.TypeCheckVisitor;

import java.io.PrintWriter;

public class TextVisitor extends BaseGenVisitor<Register> {

	private AddrVisitor addrVisitor;
	private ValueVisitor valueVisitor;
	private TypeCheckVisitor typeCheckVisitor;
	public TextVisitor(PrintWriter writer) {
		super(writer);
		addrVisitor = new AddrVisitor(writer);
		valueVisitor = new ValueVisitor(writer);
		typeCheckVisitor = new TypeCheckVisitor();
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
			Register param = freeRegs.getParamRegister();
			Type t = fce.params.get(0).accept(typeCheckVisitor);
			if (t instanceof PointerType)
				emit("la",param.toString(),"0("+((PointerType) t).register.toString()+")",null);
			else
				emit("la",param.toString(),freeRegs.Strs.get(((StrLiteral) fce.params.get(0)).val),null);
			writer.println("syscall");
			freeRegs.freeParamRegister(param);
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
		Type lhs = a.lhs.accept(typeCheckVisitor);
		if (lhs instanceof PointerType)
			((PointerType) lhs).register = rhsRegister;
		Type rhs = a.rhs.accept(typeCheckVisitor);
		if (rhs == BaseType.CHAR)
			emit("sb",rhsRegister.toString(),"0("+lhsRegister.toString()+")",null);
		else if (rhs instanceof PointerType)
			emit("sw",rhsRegister.toString(),"0("+lhsRegister.toString()+")",null);
		else
			emit("sw",rhsRegister.toString(),"0("+lhsRegister.toString()+")",null);
		return null;
	}

	@Override
	public Register visitBlock(Block b) {
		if (b.vars != null){
			int offset = b.vars.size()*4;
			emit("addi",Register.sp.toString(),Register.sp.toString(),Integer.toString(-offset));
			for (VarDecl vd : b.vars){
				vd.offset = offset;
				vd.isGlobal = false;
				offset -= 4;
			}
		}
		if (b.stmts != null){
			for (Stmt s: b.stmts)
				s.accept(this);
		}
		return null;
	}

	@Override
	public Register visitReturn(Return r) {
		System.out.println("Return");
		if (r.optionReturn != null)
			return r.optionReturn.accept(valueVisitor);
		return null;
	}
}
