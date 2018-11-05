package gen;

import ast.*;

import java.io.PrintWriter;

public class AddrVisitor extends BaseGenVisitor<Register>{


	public AddrVisitor(PrintWriter writer) {
		super(writer);
	}


	@Override
	public Register visitVarExpr(VarExpr v) {
		Register addrRegister = freeRegs.getRegister();
		if (v.vd.isGlobal)
			emit("la",addrRegister.toString(),v.name,null);
		else
			emit("la",addrRegister.toString(),v.vd.offset+"("+Register.sp.toString()+")",null);
		freeRegs.Occupied.put(v,addrRegister);
		return addrRegister;
	}


}
