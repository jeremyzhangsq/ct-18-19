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

	private boolean isEqual(Type a, Type b){
	    if ((a instanceof BaseType || a instanceof StructType)
                && (b instanceof BaseType || b instanceof StructType))
	        return a==b;
	    else if (a instanceof BaseType){
	        if (b instanceof ArrayType)
	            return isEqual(a, ((ArrayType) b).type);
	        else
	            return isEqual(a, ((PointerType) b).type);
        }
        else {
            if (a instanceof ArrayType)
                return isEqual(((ArrayType) a).type, b);
            else
                return isEqual(((PointerType) a).type, b);
        }

    }
    @Override
    public Type visitFunDecl(FunDecl p) {
	    for (VarDecl vd : p.params)
	        vd.accept(this);
	    Block b = p.block;
        for (VarDecl vd : b.vars)
            vd.accept(this);
        for (Stmt st : b.stmts){
            Type t = st.accept(this);
            if (st instanceof Return){
                if (t == null && p.type == BaseType.VOID)
                    continue;
                else if (isEqual(t,p.type)){
                    if (t instanceof ArrayType && p.type instanceof ArrayType){
                        if (((ArrayType) t).arrSize != ((ArrayType) p.type).arrSize)
                            error("Unmatch Array Size:"+((ArrayType) t).arrSize);
                    }
                }

                else {
                    error("Illegal Return:"+t);
                    return null;
                }
            }
        }

        return null;
    }

    @Override
    public Type visitVarDecl(VarDecl vd) {
        if (vd.type == BaseType.VOID)
            error("Invalid Type VarDecl:" + BaseType.VOID);
        return null;
    }

	@Override
	public Type visitStructTypeDecl(StructTypeDecl st) {
        for (VarDecl vd : st.vars)
            vd.accept(this);
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
	    else
	    	error("Invalid Type Should be Int:"+t);
		return null;
	}

	@Override
	public Type visitFunCallExpr(FunCallExpr fce) {
	    int i = 0;
	    if (fce.fd == null)
	    	return null;
	    if (fce.params.size() != fce.fd.params.size()){
			error("Param Type Mismatch:"+fce.params.size());
			return null;
		}

	    for (Expr expr : fce.params){
            Type t = expr.accept(this);
            if (t == null)
            	return null;
            Type nt = fce.fd.params.get(i).type;
            if (nt == null)
            	return null;
            if (isEqual(t,nt)){
				if (t instanceof BaseType && nt instanceof BaseType){
					if ( t !=  nt)
						error("Param Type Mismatch:"+ t);
				}
				else if (t instanceof StructType && nt instanceof StructType){
					if ( t !=  nt)
						error("Param Type Mismatch:"+ t);
				}
				else if (t instanceof ArrayType && nt instanceof ArrayType){
					if (((ArrayType) t).type !=  ((ArrayType) nt).type)
						error("Param Type Mismatch:"+((ArrayType) t).type);
				}
				else if (t instanceof PointerType && nt instanceof PointerType){
					if (((PointerType) t).type != ((PointerType) nt).type)
						error("Param Type Mismatch:"+((PointerType) t).type);
				}
			}
			else {
				error("Param Type Mismatch:"+ t);
			}
            i++;
        }
	    fce.type = fce.fd.type;
		return fce.fd.type;
	}

    @Override
    public Type visitVarExpr(VarExpr v) {
		if (v.vd==null)
			return null;
        v.type = v.vd.type;
        return v.vd.type;
    }

    @Override
	public Type visitBinOp(BinOp bop) {
	    Type lhsT = bop.lhs.accept(this);
	    if (lhsT == null)
	    	return null;
	    Type rhsT = bop.rhs.accept(this);
	    if (rhsT == null)
	    	return null;
	    if ((bop.op == Op.NE) || (bop.op == Op.EQ)){
            if ((lhsT instanceof StructType) || (lhsT instanceof ArrayType) || (lhsT == BaseType.VOID))
                error("Illegal Operand Type for BinOp:"+lhsT);
            else if ((rhsT instanceof StructType) || (rhsT instanceof ArrayType) || (rhsT == BaseType.VOID))
                error("Illegal Operand Type for BinOp:"+rhsT);
            else {
                bop.type = BaseType.INT;
                return BaseType.INT;
            }
        }else {
	        if (lhsT == BaseType.INT && rhsT == BaseType.INT){
	            bop.type = BaseType.INT;
	            return BaseType.INT;
            }
            else error("Illegal Operand Type for BinOp:"+lhsT+rhsT);
        }
		return null;
	}


	@Override
	public Type visitArrayAccessExpr(ArrayAccessExpr aae) {
	    Type t = aae.arr.accept(this);
	    if (t == null)
	    	return null;
        Type idT = aae.idx.accept(this);
        if (idT == null)
        	return null;
	    if (t instanceof ArrayType || t instanceof PointerType){
	        if (idT == BaseType.INT){
                if (t instanceof ArrayType)
                    return ((ArrayType) t).type;
                else
                    return ((PointerType) t).type;
            }
            else error("Not A Int Index:"+idT);
        }
        else error("Not a Array Variable:"+t);

		return null;
	}

	@Override
	public Type visitFieldAccessExpr(FieldAccessExpr fae) {
		Type t = fae.structure.accept(this);
		if (t instanceof StructType && fae.structure instanceof VarExpr){
		    for (VarDecl vd : ((VarExpr) fae.structure).std.vars){
		        if (vd.varName.equals(fae.fieldName))
		            return t;
            }
            error("Non Exist Struct Field:"+fae.fieldName);
        }
		return null;
	}

	@Override
	public Type visitValueAtExpr(ValueAtExpr vae) {
	    Type t = vae.val.accept(this);
	    if (t instanceof PointerType){
	    	vae.type = ((PointerType) t).type;
			return ((PointerType) t).type;
		}

	    else
	        error("Not PointerType:"+t);
		return null;
	}

	@Override
	public Type visitTypecastExpr(TypecastExpr te) {
	    Type src = te.expr.accept(this);
	    Type dst = te.type;
	    if (src == null)
	        return null;
	    if (dst == null)
	        return null;
	    if (src == BaseType.CHAR && dst == BaseType.INT)
	        return dst;
	    else if (src instanceof ArrayType && dst instanceof PointerType){
	        if (((PointerType) dst).type == ((ArrayType) src).type)
                return ((PointerType) dst).type;
        }
        else if (src instanceof PointerType && dst instanceof PointerType){
            return ((PointerType) dst).type;
        }
        error("Illegal Type Casting:"+src+dst);

		return null;
	}



	@Override
	public Type visitWhile(While w) {
	    Type condition = w.expr.accept(this);
	    w.stmt.accept(this);
	    if (condition != BaseType.INT)
	        error("Contition Should Return Int:"+condition);
		return null;
	}

	@Override
	public Type visitAssign(Assign a) {
		Type lhsT = a.lhs.accept(this);
		Type rhsT = a.rhs.accept(this);
		if ((lhsT == BaseType.VOID ) || (lhsT instanceof ArrayType)){
			error("Invalid Type for Assign Target:"+lhsT);
		}
		return null;
	}

	@Override
	public Type visitExprStmt(ExprStmt est) {
		return null;
	}

	@Override
	public Type visitIf(If i) {
	    Type condition = i.condition.accept(this);
	    Type st = i.stmt.accept(this);
	    if (i.elseStmt != null){
            Type opst = i.elseStmt.accept(this);
        }
        if (condition != BaseType.INT)
            error("Contition Should Return Int:"+condition);
		return null;
	}

	@Override
	public Type visitReturn(Return r) {
	    if (r.optionReturn != null){
            return r.optionReturn.accept(this);
        }
	    return null;
	}











}
