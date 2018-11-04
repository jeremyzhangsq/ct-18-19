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
		emit("la",addrRegister.toString(),v.name,null);
		return addrRegister;
	}


}
