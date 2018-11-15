package gen;

import ast.*;
import sem.TypeCheckVisitor;

import java.io.PrintWriter;
import java.util.ArrayList;
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
		p.accept(new FuncSeqVistior(this.writer));
		for (int i = p.funDecls.size()-1; i>=0;i--) {
			if (p.funDecls.get(i).name.equals("main")){
				p.funDecls.get(i).accept(this);
				break;
			}
		}
		for (FunDecl fd : freeRegs.functions){
			fd.accept(this);
		}
		return null;
	}

    private void reloadRegister(List<Register> occupied) {
//        emit("move", Register.sp.toString(),Register.fp.toString(),null);
        for (int i = occupied.size()-1;i >= 0; i--) {
            emit("lw",occupied.get(i).toString(),"0("+Register.sp.toString()+")",null);
            emit("addi",Register.sp.toString(),Register.sp.toString(),"4");
        }
        freeRegs.restoreRegister(occupied);
    }
    private List<Register> storeRegister(FunDecl fd) {
//        for (Register o : fd.Occupied)
//            freeRegs.freeRegister(o);
        fd.Occupied.add(Register.ra);
        for (int i = 0;i < fd.Occupied.size(); i++) {
            emit("addi",Register.sp.toString(),Register.sp.toString(),"-4");
            emit("sw",fd.Occupied.get(i).toString(),"0("+Register.sp.toString()+")",null);
        };
        for (int i = 0; i<fd.params.size();i++) {
            Register r = freeRegs.getRegister();
            fd.Occupied.add(r);
			emit("addi",Register.sp.toString(),Register.sp.toString(),"-4");
            if (i<4){
                emit("move",r.toString(),Register.paramRegs[i].toString(),null);
                r.forParam = true;
            }
            else {
				Register tmp = fd.params.get(i).paramRegister;
				emit("move",r.toString(),tmp.toString(),null);
				freeRegs.freeRegister(tmp);
				r.forParam = true;
            }
			emit("sw",r.toString(),"0("+Register.sp.toString()+")",null);
            fd.params.get(i).paramRegister = r;
        }
//        emit("move",Register.fp.toString(), Register.sp.toString(),null);
        return fd.Occupied;
    }


	@Override
	public Register visitFunDecl(FunDecl p) {
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
				p.block.accept(this);
				if (p.type == BaseType.VOID)
					emit("li", Register.v0.toString(), "10", null);
				else {
					emit("move", Register.paramRegs[0].toString(), Register.v0.toString() ,null);
					emit("li", Register.v0.toString(), "17", null);
				}
				writer.println("syscall");
				return null;
			default:
				writer.println(p.name + ":");
				int cnt = 0;
				List<Register> occupied = storeRegister(p);
				freeRegs.earlyReturn = occupied;
                for (VarDecl vd : p.params){
                    vd.paramIdx = cnt;
                    cnt ++;
                }
				p.block.FuncName = p;
				p.block.accept(this);
				reloadRegister(occupied);
				emit("jr", Register.ra.toString(), null, null);
				freeRegs.freeAll();
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
		//TODO:check
		if (lhs instanceof PointerType)
			((PointerType) lhs).register = rhsRegister;
		if (rhsRegister != null){
			Type rhs = a.rhs.accept(typeCheckVisitor);
			// special case for char* a = (char*)"string";
			if (rhs instanceof PointerType && ((PointerType) rhs).type==BaseType.CHAR)
				rhsRegister.forParam = true;
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
				vd.offset.push(size);
			}
			else if (vd.type instanceof ArrayType && ((ArrayType) vd.type).type == BaseType.CHAR){
				int s = ((ArrayType) vd.type).arrSize;
				if (s % 4 == 0){
					offset += s;
					vd.offset.push(s);
				}
				else{
					size = 4*(s/4 + 1);
					offset += size;
					vd.offset.push(size);
				}
			}
			else if (vd.type instanceof ArrayType && ((ArrayType) vd.type).type instanceof StructType){
				size = getOffset(vd.std.vars);
				size *= ((ArrayType) vd.type).arrSize;
				offset += size;
				vd.offset.push(size);
			}
			else if (vd.type instanceof StructType){
				size = getOffset(vd.std.vars);
				offset += size;
				vd.offset.push(size);
			}
			else{
				offset += 4;
				vd.offset.push(4);
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
		freeRegs.freeRegister(conRegister);
		w.stmt.FuncName = w.FuncName;
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
		freeRegs.freeRegister(conRegister);
		i.stmt.FuncName = i.FuncName;
		i.stmt.accept(this);
		emit("j","endif"+idx,null,null);
		writer.println("else"+idx+":");
		if (i.elseStmt!=null){
			i.elseStmt.FuncName = i.FuncName;
			i.elseStmt.accept(this);
		}
		writer.println("endif"+idx+":");
		return null;
	}

	@Override
	public Register visitBlock(Block b) {
		if (!b.vars.isEmpty()){
			int offset = getOffset(b.vars);
//
			for (VarDecl vd: freeRegs.vars){
				int a = vd.offset.pop();
				a += offset;
				vd.offset.push(a);
			}

			emit("addi",Register.sp.toString(),Register.sp.toString(),Integer.toString(-offset));
			offset = 0;
			int add = 0;
			// to backtrace the address of the varaiable, push offset into stack for shadowing
			for (VarDecl vd : b.vars){
				vd.FuncName = b.FuncName.name;
				add = vd.offset.peek();
				vd.offset.push(offset);
				freeRegs.vars.add(vd); // for shadowing recore existing vars
				offset += add;
				vd.isGlobal = false;
			}
		}
		Register register = null;
		if (!b.stmts.isEmpty()){
			for (Stmt s: b.stmts){
				s.accept(this);
			}
		}
		// for shadowing: pop out the offset for the local shadowing
		if (!b.vars.isEmpty()){
			int cnt = 0;
			for (VarDecl vd : b.vars){
				vd.offset.pop();
				cnt += vd.offset.pop();
				freeRegs.vars.remove(vd); //for shadowing update vars, remove local variable
			}
			emit("addi",Register.sp.toString(),Register.sp.toString(),Integer.toString(cnt)); // row back the sp cuz the local variable is used up
			// row back the offset for remaining variable
			for (VarDecl vd : freeRegs.vars){
				int a = vd.offset.pop();
				a -= cnt;
				vd.offset.push(a);
			}
		}
		return register;
	}

	@Override
	public Register visitReturn(Return r) {
		if (r.optionReturn != null){
			Register register = r.optionReturn.accept(valueVisitor);
			emit("addi", Register.v0.toString(), register.toString(), "0");
			freeRegs.freeRegister(register);
//			return register;
//			emit("jr",Register.ra.toString(),null,null);
		}
		else {
            reloadRegister(freeRegs.earlyReturn);
            emit("jr", Register.ra.toString(), null, null);
        }
        // early return for main
        if (r.FuncName.name.equals("main")){
			if (r.FuncName.type == BaseType.VOID)
				emit("li", Register.v0.toString(), "10", null);
			else {
				emit("move", Register.paramRegs[0].toString(), Register.v0.toString() ,null);
				emit("li", Register.v0.toString(), "17", null);
			}
			writer.println("syscall");
		}
		return null;
	}
}
