package gen;

import ast.*;
import sem.TypeCheckVisitor;

import java.io.PrintWriter;
import java.util.List;

public class TextVisitor extends BaseGenVisitor<Register> {

	private AddrVisitor addrVisitor;
	private ValueVisitor valueVisitor;
	private TypeCheckVisitor typeCheckVisitor;
	public TextVisitor(PrintWriter writer, Program program) {
		super(writer);
		addrVisitor = new AddrVisitor(writer,program);
		valueVisitor = new ValueVisitor(writer,program);
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
			Register register = p.block.accept(this);
			if (p.name.equals("main")){
				if (p.type == BaseType.VOID)
					emit("li",Register.v0.toString(),"10",null);
				else{
					emit("addi",Register.paramRegs[0].toString(),register.toString(),"0");
					emit("li",Register.v0.toString(),"17",null);
				}
				writer.println("syscall");
			}
			else {
				if (register !=null)
					emit("addi",Register.v0.toString(),register.toString(),"0");
				emit("jr",Register.ra.toString(),null,null);
			}
			return null;
		}

	}

	@Override
	public Register visitFunCallExpr(FunCallExpr fce) {
		//TODO: arugument
		if (fce.funcName.equals("read_c")){
			emit("li",	Register.v0.toString(), "12",null);
			Register register = freeRegs.getRegister();
			writer.println("syscall");
			emit("addi",register.toString(),Register.v0.toString(),"0");
			return register;
		}
		else if (fce.funcName.equals("read_i")){
			emit("li",	Register.v0.toString(), "5",null);
			Register register = freeRegs.getRegister();
			writer.println("syscall");
			emit("addi",register.toString(),Register.v0.toString(),"0");
			return register;
		}
		else if (fce.funcName.equals("print_c")){
			emit("li",Register.v0.toString(),"11",null);
			Register param = Register.paramRegs[0];
			Expr e = fce.params.get(0);
			if ( e instanceof ChrLiteral)
				emit("lb",param.toString(),freeRegs.Chrs.get(((ChrLiteral) e).val),null);
			else if (e instanceof VarExpr){
				Register r = e.accept(valueVisitor);
				emit("move",param.toString(),r.toString(),null);
				freeRegs.freeRegister(r);
			}

			writer.println("syscall");
			return null;
		}
		else if	(fce.funcName.equals("print_i")){
			emit("li",Register.v0.toString(),"1",null);
			Register param = Register.paramRegs[0];
			Expr e = fce.params.get(0);
			if ( e instanceof IntLiteral)
				emit("li",param.toString(),Integer.toString(((IntLiteral) fce.params.get(0)).val),null);
			else if (e instanceof VarExpr){
				Register r = e.accept(valueVisitor);
				emit("move",param.toString(),r.toString(),null);
				freeRegs.freeRegister(r);
			}
			writer.println("syscall");
			return null;
		}
		else if (fce.funcName.equals("print_s")){
			emit("li",Register.v0.toString(),"4",null);
			Register param = Register.paramRegs[0];
			Type t = fce.params.get(0).accept(typeCheckVisitor);
			if (t instanceof PointerType && ((PointerType) t).register != null)
				emit("la",param.toString(),"0("+((PointerType) t).register.toString()+")",null);
			else
				emit("la",param.toString(),freeRegs.Strs.get(((StrLiteral)(((TypecastExpr)(fce.params.get(0))).expr)).val),null);
			writer.println("syscall");
			return null;
		}
		else if (fce.funcName.equals("mcmalloc"))
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
		if (a.rhs instanceof FunCallExpr &&
				(((FunCallExpr) a.rhs).funcName.equals("read_i")||((FunCallExpr) a.rhs).funcName.equals("read_c")))
			rhsRegister = a.rhs.accept(this);
		if (rhs == BaseType.CHAR)
			emit("sb",rhsRegister.toString(),"0("+lhsRegister.toString()+")",null);
		else{
			emit("sw",rhsRegister.toString(),"0("+lhsRegister.toString()+")",null);
		}
		freeRegs.freeRegister(lhsRegister);
		freeRegs.freeRegister(rhsRegister);
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
	public Register visitWhile(While w) {
		String idx;
		String ai = Integer.toString(freeRegs.getControlIdx());
		writer.println("while"+ai+":");
		Register conRegister = w.expr.accept(valueVisitor);
		if (conRegister.controlIndex == null){
			idx = Integer.toString(freeRegs.getControlIdx());
			emit("beq",conRegister.toString(),"0","else"+idx);
		}
		else
			idx = conRegister.controlIndex;
		w.stmt.accept(this);
		emit("j","while"+ai,null,null);
		writer.println("else"+idx+":");
		return null;
	}
	@Override
	public Register visitIf(If i) {
		Register conRegister = i.condition.accept(valueVisitor);
		String idx;
		if (conRegister.controlIndex == null){
			idx = Integer.toString(freeRegs.getControlIdx());
			emit("beq",conRegister.toString(),"0","else"+idx);
		}
		else
			idx = conRegister.controlIndex;
		i.stmt.accept(this);
		emit("j","endif"+idx,null,null);
		writer.println("else"+idx+":");
		if (i.elseStmt!=null){
			i.elseStmt.accept(this);
		}
		writer.println("endif"+idx+":");
		return null;
	}

	@Override
	public Register visitBlock(Block b) {
		if (b.vars != null){
			int offset = getOffset(b.vars);
			emit("addi",Register.sp.toString(),Register.sp.toString(),Integer.toString(-offset));
			offset = 0;
			int add = 0;
			for (VarDecl vd : b.vars){
				add = vd.offset;
				vd.offset = offset;
				offset += add;
				vd.isGlobal = false;
			}
		}
		Register register = null;
		if (b.stmts != null){
			for (Stmt s: b.stmts){
				if (s instanceof Return)
					register = s.accept(this);
				else
					s.accept(this);
			}

		}
		return register;
	}

	@Override
	public Register visitReturn(Return r) {
		System.out.println("Return");
		if (r.optionReturn != null){
			Register register = r.optionReturn.accept(valueVisitor);
			freeRegs.freeRegister(register);
			return register;
//			emit("jr",Register.ra.toString(),null,null);
		}
		return null;
	}
}
