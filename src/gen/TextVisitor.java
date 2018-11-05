package gen;

import ast.*;
import sem.TypeCheckVisitor;

import java.io.PrintWriter;
import java.util.List;

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
			Register param = Register.paramRegs[0];
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
	public int getOffset(List<VarDecl> v){
		int offset = 0;
		int size = 0;
		for (VarDecl vd : v){
			if (vd.type instanceof ArrayType && ((ArrayType) vd.type).type == BaseType.INT){
				size = 4*((ArrayType) vd.type).arrSize;
				offset += size;
				vd.offset = size;
			}
			else if (vd.type instanceof ArrayType && ((ArrayType) vd.type).type == BaseType.CHAR){
				int s = ((ArrayType) vd.type).arrSize;
				if (s % 4 == 0){
					offset += s;
					vd.offset = s;
				}
				else{
					size = 4*(s/4 + 1);
					offset += size;
					vd.offset = size;
				}

			}
			else if (vd.type instanceof ArrayType && ((ArrayType) vd.type).type instanceof StructType){
				size = getOffset(vd.std.vars);
				size *= ((ArrayType) vd.type).arrSize;
				offset += size;
				vd.offset = size;
			}
			else if (vd.type instanceof StructType){
				size = getOffset(vd.std.vars);
				offset += size;
				vd.offset = size;
			}
			else{
				offset += 4;
				vd.offset = 4;
			}
		}
		return offset;
	}
	@Override
	public Register visitBlock(Block b) {
		if (b.vars != null){
			int offset = getOffset(b.vars);
			emit("addi",Register.sp.toString(),Register.sp.toString(),Integer.toString(-offset));
			int minus = 0;
			for (VarDecl vd : b.vars){
				minus = vd.offset;
				vd.offset = offset;
				vd.isGlobal = false;
				offset -= minus;
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
