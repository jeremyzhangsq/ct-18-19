package gen;

import ast.*;

import java.io.PrintWriter;
import java.util.EmptyStackException;
import java.util.Stack;

public class ValueVisitor extends BaseGenVisitor<Register>{

	public ValueVisitor(PrintWriter writer) {
		super(writer);
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
				emit("mult",result.toString(),lhsRegister.toString(),rhsRegister.toString());
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
				break;
			case LT:
				break;
			case GE:
				break;
			case LE:
				break;
			case NE:
				break;
			case EQ:
				break;
			case OR:
				break;
			case AND:
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
	public Register visitVarExpr(VarExpr v) {
		Register addrRegister = freeRegs.getRegister();
		Register result = freeRegs.getRegister();
		emit("la",addrRegister.toString(),v.name,null);
		emit("lw",result.toString(),"0("+addrRegister.toString()+")",null);
		freeRegs.freeRegister(addrRegister);
		return result;
	}


}
