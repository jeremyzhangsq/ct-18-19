package sem;

import ast.*;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {
	public Scope scope;
	public NameAnalysisVisitor(){
		this.scope = new Scope();
	}

    @Override
    public Void visitProgram(Program p) {
        // To be completed...
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
    public Void visitOp(Op op) {
        return null;
    }

    @Override
    public Void visitBinOp(BinOp bop) { return null; }

	@Override
	public Void visitBaseType(BaseType bt) { return null; }

    @Override
    public Void visitPointerType(PointerType pt) {
        return null;
    }

	@Override
	public Void visitStructType(StructType st) {
		return null;
	}

	@Override
	public Void visitArrayType(ArrayType at) {
		return null;
	}

	@Override
	public Void visitIntLiteral(IntLiteral il) {
		return null;
	}

	@Override
	public Void visitStrLiteral(StrLiteral sl) {
		return null;
	}

	@Override
	public Void visitChrLiteral(ChrLiteral cl) { return null; }

    @Override
    public Void visitStructTypeDecl(StructTypeDecl sts) {
        // To be completed...
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl p) {
        // To be completed...
        Symbol s = scope.lookupCurrent(p.name);
        if (s !=null)
            error("Existed FunDecl:"+p.name);
        else
            scope.put(new FuncSymbol(p));
        for (VarDecl vd : p.params){
            vd.accept(this);
        }
        p.block.accept(this);
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd) {
        Symbol s = scope.lookupCurrent(vd.varName);
        if (s !=null)
            error("Existed VarDecl:"+vd.varName);
        else
            scope.put(new VarDeclSymbol(vd));
        return null;
    }

    @Override
	public Void visitSizeOfExpr(SizeOfExpr soe) {
		return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr fce) {
        Symbol fd = scope.lookup(fce.funcName);
        if (fd == null)
            error("Not Declared Function:"+fce.funcName);
        else if (!(fd instanceof FuncSymbol))
            error("Wrong Symbol:"+fd.getClass());
        else
            fce.fd = ((FuncSymbol) fd).fd;
        return null;
	}

    @Override
    public Void visitVarExpr(VarExpr v) {
        Symbol vs = scope.lookup(v.name);
        if (vs == null)
            error("Not Declared Variable:"+v.name);
        else if (!(vs instanceof VarDeclSymbol))
            error("Wrong Symbol:"+vs.getClass());
        else
            v.vd = ((VarDeclSymbol) vs).vd;
        return null;
    }
	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr aae) { return null; }

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fae) {
		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr vae) {
		return null;
	}

	@Override
	public Void visitTypecastExpr(TypecastExpr te) {
		return null;
	}

	@Override
	public Void visitWhile(While w) {
		return null;
	}

	@Override
	public Void visitAssign(Assign a) {
	    Expr lhs = a.lhs;
	    Expr rhs = a.rhs;
	    lhs.accept(this);
	    rhs.accept(this);
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt est) {
		return null;
	}

	@Override
	public Void visitIf(If i) {
		return null;
	}

	@Override
	public Void visitReturn(Return r) {
		return null;
	}

	@Override
	public Void visitBlock(Block b) {
		Scope oldScope = scope;
		scope = new Scope(oldScope);
		for (VarDecl vd : b.vars)
		    vd.accept(this);
		for (Stmt st : b.stmts)
		    st.accept(this);
		scope = oldScope;
		return null;
	}
}
