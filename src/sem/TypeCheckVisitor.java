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
	    Type t = soe.type;
	    if (t == BaseType.INT)
	        return t;
	    else error("Invalid Type Should be Int:"+t.getClass());
		return null;
	}

	@Override
	public Type visitFunCallExpr(FunCallExpr fce) {
	    int i = 0;
	    if (fce.params.size() != fce.fd.params.size())
            error("Param Type Mismatch:"+fce.params.size());
	    for (Expr expr : fce.params){
            expr.accept(this);
            Type nt = fce.fd.params.get(i).type;
            if (expr.type.getClass() == nt.getClass()){
				if (expr.type instanceof BaseType && nt instanceof BaseType){
					if ( expr.type !=  nt)
						error("Param Type Mismatch:"+expr.type .getClass());
				}
				else if (expr.type instanceof StructType && nt instanceof StructType){
					if ( expr.type !=  nt)
						error("Param Type Mismatch:"+expr.type .getClass());
				}
				else if (expr.type instanceof ArrayType && nt instanceof ArrayType){
					if (((ArrayType) expr.type).type !=  ((ArrayType) nt).type)
						error("Param Type Mismatch:"+((ArrayType) expr.type).type.getClass());
				}
				else if (expr.type instanceof PointerType && nt instanceof PointerType){
					if (((PointerType) expr.type).type != ((PointerType) nt).type)
						error("Param Type Mismatch:"+((PointerType) expr.type).type.getClass());
				}
			}
			else {
				error("Param Type Mismatch:"+expr.type .getClass());
			}



            i++;
        }

	    fce.type = fce.fd.type;
		return fce.fd.type;
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
	    Type t = aae.arr.accept(this);
        Type idT = aae.idx.accept(this);
	    if (t instanceof ArrayType || t instanceof PointerType){
	        if (idT == BaseType.INT){
                if (t instanceof ArrayType)
                    return ((ArrayType) t).type;
                else
                    return ((PointerType) t).type;
            }
            else error("Not A Int Index:"+idT.getClass());
        }
        else error("Not a Array Variable:"+t.getClass());

		return null;
	}

	@Override
	public Type visitFieldAccessExpr(FieldAccessExpr fae) {
		return null;
	}

	@Override
	public Type visitValueAtExpr(ValueAtExpr vae) {
	    Type t = vae.val.accept(this);
	    if (t instanceof PointerType)
	        return ((PointerType) t).type;
	    else
	        error("Not PointerType:"+t.getClass());
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
