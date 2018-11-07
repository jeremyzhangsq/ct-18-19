package gen;

import ast.*;
import sem.TypeCheckVisitor;

import java.io.PrintWriter;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

public class ValueVisitor extends BaseGenVisitor<Register>{
	private TypeCheckVisitor typeCheckVisitor;
	public ValueVisitor(PrintWriter writer, Program program) {
		super(writer);
		typeCheckVisitor = new TypeCheckVisitor();
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
	public Register visitFunCallExpr(FunCallExpr fce) {
		//TODO: arugument
		switch (fce.funcName) {
			case "read_c": {
				emit("li", Register.v0.toString(), "12", null);
				Register register = freeRegs.getRegister();
				writer.println("syscall");
				emit("addi", register.toString(), Register.v0.toString(), "0");
				return register;
			}
			case "read_i": {
				emit("li", Register.v0.toString(), "5", null);
				Register register = freeRegs.getRegister();
				writer.println("syscall");
				emit("addi", register.toString(), Register.v0.toString(), "0");
				return register;
			}
			case "print_c": {
				emit("li", Register.v0.toString(), "11", null);
				Register param = Register.paramRegs[0];
				Expr e = fce.params.get(0);
				if (e instanceof ChrLiteral)
					emit("lb", param.toString(), freeRegs.Chrs.get(((ChrLiteral) e).val), null);
				else if (e instanceof VarExpr) {
					Register r = e.accept(this);
					emit("move", param.toString(), r.toString(), null);
					freeRegs.freeRegister(r);
				}

				writer.println("syscall");
				return null;
			}
			case "print_i": {
				emit("li", Register.v0.toString(), "1", null);
				Register param = Register.paramRegs[0];
				Expr e = fce.params.get(0);
				if (e instanceof IntLiteral)
					emit("li", param.toString(), Integer.toString(((IntLiteral) fce.params.get(0)).val), null);
				else if (e instanceof VarExpr) {
					Register r = e.accept(this);
					emit("move", param.toString(), r.toString(), null);
					freeRegs.freeRegister(r);
				}
				writer.println("syscall");
				return null;
			}
			case "print_s": {
				emit("li", Register.v0.toString(), "4", null);
				Register param = Register.paramRegs[0];
				Type t = fce.params.get(0).accept(typeCheckVisitor);
				if (t instanceof PointerType && ((PointerType) t).register != null)
					emit("la", param.toString(), "0(" + ((PointerType) t).register.toString() + ")", null);
				else
					emit("la", param.toString(), freeRegs.Strs.get(((StrLiteral) (((TypecastExpr) (fce.params.get(0))).expr)).val), null);
				writer.println("syscall");
				return null;
			}
			case "mcmalloc":
				return null;
			default:
				List<Register> occupy = storeRegister();
				for (Expr expr : fce.params)
					expr.accept(this);
				emit("jal",fce.funcName,null,null);
				restoreRegister(occupy);
				if (fce.fd.type != BaseType.VOID){
					Register r = freeRegs.getRegister();
					emit("move", r.toString(), Register.v0.toString(),null);
					return r;
				}
				return null;
		}
	}
	private void restoreRegister(List<Register> occupy) {
		emit("move", Register.sp.toString(),Register.fp.toString(),null);
		for (int i = 0;i < occupy.size(); i++) {
			emit("lw",occupy.get(i).toString(),"0("+Register.sp.toString()+")",null);
			emit("addi",Register.sp.toString(),Register.sp.toString(),"4");
		}
	}
	private List<Register> storeRegister() {
		List<Register> occupied = freeRegs.getOccupyRegs();
		occupied.add(Register.ra);
		for (int i = occupied.size()-1;i >= 0; i--) {
			emit("addi",Register.sp.toString(),Register.sp.toString(),"-4");
			emit("sw",occupied.get(i).toString(),"0("+Register.sp.toString()+")",null);
		}
		emit("move",Register.fp.toString(), Register.sp.toString(),null);
		return occupied;
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
        if (v.type instanceof ArrayType || v.type instanceof StructType)
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
        if (aae.arr.type == BaseType.CHAR)
            emit("li",offset.toString(),"1",null);
        else
            emit("li",offset.toString(),"4",null);
		emit("mult",offset.toString(),idxRegister.toString(),null);
		emit("mflo",offset.toString(),null,null);
		emit("add",addrRegister.toString(),addrRegister.toString(),offset.toString());
		if (aae.arr.type == BaseType.CHAR)
			emit("lb",result.toString(),"0("+addrRegister+")",null);
		else
			emit("lw",result.toString(),"0("+addrRegister+")",null);
		freeRegs.freeRegister(idxRegister);
		freeRegs.freeRegister(addrRegister);
		freeRegs.freeRegister(offset);
		return result;
	}
    @Override
    public Register visitFieldAccessExpr(FieldAccessExpr fae) {
        Register result = freeRegs.getRegister();
        Register addrRegister= fae.structure.accept(this);
        if (fae.structure instanceof VarExpr){
            int offset = 0;
            int size = 0;
            Type t = null;
//            for (VarDecl vd: ((VarExpr) fae.structure).std.vars){
//                size += vd.offset;
//            }
            for (VarDecl vd: ((VarExpr) fae.structure).std.vars){
                if (!vd.varName.equals(fae.fieldName))
                    offset += vd.offset;
                else {
                    t = vd.type;
                    break;
                }
            }
//            offset = size - offset;
            emit("add",addrRegister.toString(),addrRegister.toString(),Integer.toString(offset));
            if (t == BaseType.CHAR)
                emit("lb",result.toString(),"0("+addrRegister+")",null);
            else
                emit("lw",result.toString(),"0("+addrRegister+")",null);
        }
        else {
            writer.println("error");
        }
        freeRegs.freeRegister(addrRegister);
        return result;
    }
//    @Override
//    public Register visitFunCallExpr(FunCallExpr fce) {
//        System.out.println("FunCallExpr:"+fce.funcName);
//        for (Expr expr: fce.params)
//            expr.accept(this);
//        return null;
//    }
}
