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
		writer.println("j main");
		for (FunDecl fd : p.funDecls) {
			fd.accept(this);
		}
		return null;
	}

	@Override
	public Register visitFunDecl(FunDecl p) {
		//TODO: arugument
		switch (p.name) {
			case "read_c":
				return null;
			case "print_c":
				return null;
			case "print_i":
				return null;
			case "print_s":
				return null;
			case "mcmalloc":
				return null;
			case "read_i":
				return null;
			case "main":
				writer.println(p.name + ":");
				Register register = p.block.accept(this);
				if (p.type == BaseType.VOID)
					emit("li", Register.v0.toString(), "10", null);
				else {
					emit("addi", Register.paramRegs[0].toString(), register.toString(), "0");
					emit("li", Register.v0.toString(), "17", null);
				}
				writer.println("syscall");
				return null;
			default:
				writer.println(p.name + ":");
				Register reg = p.block.accept(this);
				if (reg != null)
					emit("addi", Register.v0.toString(), reg.toString(), "0");
				emit("jr", Register.ra.toString(), null, null);
				return null;
		}
	}

	@Override
	public Register visitFunCallExpr(FunCallExpr fce) {
		return fce.accept(valueVisitor);
	}

	@Override
	public Register visitAssign(Assign a) {
		Register lhsRegister = a.lhs.accept(addrVisitor);
		Register rhsRegister = a.rhs.accept(valueVisitor);
		Type lhs = a.lhs.accept(typeCheckVisitor);
		if (lhs instanceof PointerType)
			((PointerType) lhs).register = rhsRegister;
		if (rhsRegister != null){
			Type rhs = a.rhs.accept(typeCheckVisitor);
			if (rhs == BaseType.CHAR)
				emit("sb",rhsRegister.toString(),"0("+lhsRegister.toString()+")",null);
			else {
				emit("sw",rhsRegister.toString(),"0("+lhsRegister.toString()+")",null);
			}
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
