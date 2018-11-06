package gen;

import ast.*;

import java.io.PrintWriter;
import java.util.EmptyStackException;
import java.util.Stack;

public class ValueVisitor extends BaseGenVisitor<Register>{
	private DataVisitor dataVisitor;
	public ValueVisitor(PrintWriter writer, Program program) {
		super(writer);
		dataVisitor = new DataVisitor(writer, program);
	}
	@Override
	public Register visitBinOp(BinOp bop) {
		Register lhsRegister = bop.lhs.accept(this);
		Register rhsRegister = bop.rhs.accept(this);
		Register result = freeRegs.getRegister();
		switch (bop.op){
			case ADD:
				emit("add",result.toString(),lhsRegister.toString(),rhsRegister.toString());
				break;
			case SUB:
				emit("sub",result.toString(),lhsRegister.toString(),rhsRegister.toString());
				break;
			case MUL:
				emit("mult",lhsRegister.toString(),rhsRegister.toString(),null);
				emit("mflo",result.toString(),null,null);
				break;
			case DIV:
				emit("div",result.toString(),lhsRegister.toString(),rhsRegister.toString());
				emit("mflo",result.toString(),null,null);
				break;
			case MOD:
				emit("div",result.toString(),lhsRegister.toString(),rhsRegister.toString());
				emit("mfhi",result.toString(),null,null);
				break;
			case GT:
				emit("sgt",result.toString(),lhsRegister.toString(),rhsRegister.toString());
				break;
			case LT:
				emit("slt",result.toString(),lhsRegister.toString(),rhsRegister.toString());
				break;
			case GE:
				emit("sge",result.toString(),lhsRegister.toString(),rhsRegister.toString());
				break;
			case LE:
				emit("sle",result.toString(),lhsRegister.toString(),rhsRegister.toString());
				break;
			case NE:
				emit("sne",result.toString(),lhsRegister.toString(),rhsRegister.toString());
				break;
			case EQ:
				emit("seq",result.toString(),lhsRegister.toString(),rhsRegister.toString());
				break;
			case OR:
				freeRegs.freeRegister(result);
				result = lhsRegister;
				if (lhsRegister.controlIndex == null){
					lhsRegister.controlIndex = Integer.toString(freeRegs.getControlIdx());
				}
				emit("beq",lhsRegister.toString(),"1","if"+lhsRegister.controlIndex);
				emit("beq",rhsRegister.toString(),"0","else"+lhsRegister.controlIndex);
				writer.println("if"+lhsRegister.controlIndex+":");
				break;
			case AND:
				freeRegs.freeRegister(result);
				result = lhsRegister;
				if (lhsRegister.controlIndex == null){
					lhsRegister.controlIndex = Integer.toString(freeRegs.getControlIdx());
				}
				emit("beq",lhsRegister.toString(),"0","else"+lhsRegister.controlIndex);
				emit("beq",rhsRegister.toString(),"0","else"+lhsRegister.controlIndex);
				break;
		}
		freeRegs.freeRegister(lhsRegister);
		freeRegs.freeRegister(rhsRegister);
		return result;
	}

	@Override
	public Register visitIntLiteral(IntLiteral il) {
		Register next = freeRegs.getRegister();
		emit("li",next.toString(),Integer.toString(il.val), null);
		return next;
	}

	@Override
	public Register visitChrLiteral(ChrLiteral cl) {
		Register next = freeRegs.getRegister();
		if (freeRegs.Chrs.get(cl.val) == null)
			return null;
		emit("lb",next.toString(),freeRegs.Chrs.get(cl.val), null);
		return next;
	}

	@Override
	public Register visitStrLiteral(StrLiteral sl) {
		Register next = freeRegs.getRegister();
		if (freeRegs.Strs.get(sl.val) == null)
			return null;
		emit("la",next.toString(),freeRegs.Strs.get(sl.val), null);
		return next;
	}

	@Override
	public Register visitVarExpr(VarExpr v) {
		Register addrRegister = freeRegs.getRegister();
		Register result = freeRegs.getRegister();
		String cmd = "lw";
		if (v.type == BaseType.CHAR)
		    cmd = "lb";
		if (v.vd.isGlobal)
            emit("la",addrRegister.toString(),v.name,null);
		else
            emit("la",addrRegister.toString(),v.vd.offset+"("+Register.sp.toString()+")",null);
        if ((v.type instanceof ArrayType))
            return addrRegister;
        freeRegs.freeRegister(addrRegister);
        emit(cmd,result.toString(),"0("+addrRegister.toString()+")",null);
		return result;
	}

	@Override
	public Register visitArrayAccessExpr(ArrayAccessExpr aae) {
		Register idxRegister = aae.idx.accept(this);
		Register addrRegister = aae.arr.accept(this);
		Register result = freeRegs.getRegister();
		Register offset = freeRegs.getRegister();
		int size = aae.arr.type.accept(dataVisitor);
        if (aae.arr.type == BaseType.CHAR)
            emit("li",offset.toString(),"1",null);
        else
            emit("li",offset.toString(),"4",null);
		emit("mult",offset.toString(),idxRegister.toString(),null);
		emit("mflo",offset.toString(),null,null);
        emit("li",idxRegister.toString(),Integer.toString(size),null);
        emit("sub",offset.toString(),idxRegister.toString(),offset.toString());
		emit("sub",addrRegister.toString(),addrRegister.toString(),offset.toString());
		if (aae.arr.type == BaseType.CHAR)
			emit("lb",result.toString(),"0("+addrRegister+")",null);
		else
			emit("lw",result.toString(),"0("+addrRegister+")",null);
		freeRegs.freeRegister(idxRegister);
		freeRegs.freeRegister(addrRegister);
		freeRegs.freeRegister(offset);
		return result;
	}
}
