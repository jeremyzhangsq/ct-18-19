package gen;

import ast.*;

import java.io.PrintWriter;

public class AddrVisitor extends BaseGenVisitor<Register>{


	private DataVisitor dataVisitor;
	private ValueVisitor valueVisitor;
	public AddrVisitor(PrintWriter writer, Program program) {
		super(writer);
		valueVisitor = new ValueVisitor(writer,program);
		dataVisitor = new DataVisitor(writer, program);
	}

	@Override
	public Register visitVarExpr(VarExpr v) {
		Register addrRegister = freeRegs.getRegister();
		if (v.vd.isGlobal){
			emit("la",addrRegister.toString(),v.name,null);
		}
		else{
			if (freeRegs.DynamicAddr.keySet().contains(v.name))
				emit("la",addrRegister.toString(),v.vd.offset.peek()+"("+freeRegs.DynamicAddr.get(v.name).toString()+")",null);
			else
				emit("la",addrRegister.toString(),v.vd.offset.peek()+"("+Register.sp.toString()+")",null);
		}

		return addrRegister;
	}

	@Override
	public Register visitArrayAccessExpr(ArrayAccessExpr aae) {
		Register idxRegister = aae.idx.accept(valueVisitor);
		Register addrRegister = aae.arr.accept(this);
		Register offset = freeRegs.getRegister();
		if (aae.arr.type == BaseType.CHAR)
			emit("li",offset.toString(),"1",null);
		else
			emit("li",offset.toString(),"4",null);
		emit("mult",offset.toString(),idxRegister.toString(),null);
		emit("mflo",offset.toString(),null,null);
		emit("add",addrRegister.toString(),addrRegister.toString(),offset.toString());
		freeRegs.freeRegister(idxRegister);
		freeRegs.freeRegister(offset);
		return addrRegister;
	}

	@Override
	public Register visitFieldAccessExpr(FieldAccessExpr fae) {
		Register addrRegister= fae.structure.accept(valueVisitor);
		if (fae.structure instanceof VarExpr){
			int offset = 0;
//			int size = 0;
//			for (VarDecl vd: ((VarExpr) fae.structure).std.vars){
//				size += vd.offset;
//			}
			for (VarDecl vd: ((VarExpr) fae.structure).std.vars){
				if (!vd.varName.equals(fae.fieldName))
					offset += vd.offset.peek();
				else break;
			}
//			offset = size - offset;
			emit("add",addrRegister.toString(),addrRegister.toString(),Integer.toString(offset));
		}
		else {
			writer.println("error");
		}
		return addrRegister;
	}
}
