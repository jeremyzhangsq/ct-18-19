package sem;

import ast.*;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

	@Override
	public Type visitProgram(Program p) {
        for (StructTypeDecl std : p.structTypeDecls) {
            std.accept(this);
        }
        for (VarDecl vd : p.varDecls) {
            vd.accept(this);
        }
        for (FunDecl fd : p.funDecls) {
            fd.accept(this);
        }
		return null;
	}
    @Override
    public Type visitOp(Op op) { return null; }
	@Override
	public Type visitBaseType(BaseType bt) { return null; }

    @Override
    public Type visitPointerType(PointerType pt) {
        return null;
    }

    @Override
	public Type visitStructType(StructType st) { return null; }

	@Override
	public Type visitArrayType(ArrayType at) { return null; }

	@Override
	public Type visitIntLiteral(IntLiteral il) {
		return BaseType.INT;
	}

	@Override
	public Type visitStrLiteral(StrLiteral sl) {
	    int len = sl.val.length()+1;
		return new ArrayType(BaseType.CHAR, len);
	}

	@Override
	public Type visitChrLiteral(ChrLiteral cl) {
		return BaseType.CHAR;
	}
    @Override
    public Type visitFunDecl(FunDecl p) {
	    for (VarDecl vd : p.params)
	        vd.accept(this);
	    p.block.accept(this);
        return null;
    }

    @Override
    public Type visitVarDecl(VarDecl vd) {
        if (vd.type == BaseType.VOID)
            error("Invalid Type VarDecl:" + BaseType.VOID);
        return null;
    }

    @Override
    public Type visitBlock(Block b) {
        for (VarDecl vd : b.vars)
            vd.accept(this);
        for (Stmt st : b.stmts)
            st.accept(this);
        return null;
    }

	@Override
	public Type visitSizeOfExpr(SizeOfExpr soe) {
		return null;
	}

	@Override
	public Type visitFunCallExpr(FunCallExpr fce) {
		return null;
	}

    @Override
    public Type visitVarExpr(VarExpr v) {
        v.type = v.vd.type;
        return v.vd.type;
    }

    @Override
	public Type visitBinOp(BinOp bop) {
	    Type lhsT = bop.lhs.accept(this);
	    Type rhsT = bop.rhs.accept(this);
	    if ((bop.op == Op.NE) || (bop.op == Op.EQ)){
            if ((lhsT instanceof StructType) || (lhsT instanceof ArrayType) || (lhsT == BaseType.VOID))
                error("Illegal Operand Type for BinOp:"+lhsT.getClass());
            else if ((rhsT instanceof StructType) || (rhsT instanceof ArrayType) || (rhsT == BaseType.VOID))
                error("Illegal Operand Type for BinOp:"+rhsT.getClass());
            else {
                bop.type = BaseType.INT;
                return BaseType.INT;
            }
        }else {
	        if (lhsT == BaseType.INT && rhsT == BaseType.INT){
	            bop.type = BaseType.INT;
	            return BaseType.INT;
            }
            else error("Illegal Operand Type for BinOp:"+lhsT.getClass()+"\t"+rhsT.getClass());
        }
		return null;
	}

	@Override
	public Type visitArrayAccessExpr(ArrayAccessExpr aae) {
		return null;
	}

	@Override
	public Type visitFieldAccessExpr(FieldAccessExpr fae) {
		return null;
	}

	@Override
	public Type visitValueAtExpr(ValueAtExpr vae) {
		return null;
	}

	@Override
	public Type visitTypecastExpr(TypecastExpr te) {
		return null;
	}



	@Override
	public Type visitWhile(While w) {
		return null;
	}

	@Override
	public Type visitAssign(Assign a) {
		Type lhsT = a.lhs.accept(this);
		Type rhsT = a.rhs.accept(this);
		if ((lhsT == BaseType.VOID ) || (lhsT instanceof ArrayType)){
			error("Invalid Type for Assign Target:"+lhsT.getClass());
		}
		else {
			if (lhsT == rhsT)
				return lhsT;
			else
				error("Illegal Operand Type for BinOp:"+lhsT.getClass()+"\t"+rhsT.getClass());
		}
		return null;
	}

	@Override
	public Type visitExprStmt(ExprStmt est) {
		return null;
	}

	@Override
	public Type visitIf(If i) {
		return null;
	}

	@Override
	public Type visitReturn(Return r) {
		return null;
	}



	@Override
	public Type visitStructTypeDecl(StructTypeDecl st) {
		// To be completed...
		return null;
	}







}
