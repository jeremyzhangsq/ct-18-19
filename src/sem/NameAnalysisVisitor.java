package sem;

import ast.*;

import java.util.ArrayList;
import java.util.List;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {
	public Scope scope;
	public NameAnalysisVisitor(){
		this.scope = new Scope();
	}

    @Override
    public Void visitProgram(Program p) {
        // To be completed...
        // insert default function

        List<VarDecl> avds = new ArrayList<>();
        FunDecl fd;
        avds.add(new VarDecl(new PointerType(BaseType.CHAR),"s"));
        Block block = new Block(null,null);
        fd = new FunDecl(BaseType.VOID, "print_s", avds,block);
        p.funDecls.add(0,fd);
        avds = new ArrayList<>();
        avds.add(new VarDecl(BaseType.INT,"i"));
        fd = new FunDecl(BaseType.INT, "print_i", avds,block);
        p.funDecls.add(0,fd);
        avds = new ArrayList<>();
        avds.add(new VarDecl(BaseType.CHAR,"c"));
        fd = new FunDecl(BaseType.CHAR, "print_c", avds,block);
        p.funDecls.add(0,fd);
        avds = new ArrayList<>();
        fd = new FunDecl(BaseType.CHAR, "read_c", avds,block);
        p.funDecls.add(0,fd);
        fd = new FunDecl(BaseType.INT, "read_i", avds,block);
        p.funDecls.add(0,fd);
        avds = new ArrayList<>();
        avds.add(new VarDecl(BaseType.INT,"size"));
        fd = new FunDecl(new PointerType(BaseType.VOID), "mcmalloc", avds,block);
        p.funDecls.add(0,fd);
        for (StructTypeDecl std : p.structTypeDecls) {
            std.accept(this);
        }
        for (VarDecl vd : p.varDecls) {
            vd.accept(this);
        }
        for (FunDecl fds : p.funDecls) {
            fds.accept(this);
        }
        return null;
    }

    @Override
    public Void visitOp(Op op) {
        return null;
    }

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
	public Void visitIntLiteral(IntLiteral il) { return null; }

	@Override
	public Void visitStrLiteral(StrLiteral sl) {
		return null;
	}

	@Override
	public Void visitChrLiteral(ChrLiteral cl) { return null; }

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

    @Override
    public Void visitStructTypeDecl(StructTypeDecl std) {
        Symbol s = scope.lookupCurrent(std.stype.structName);
        if (s !=null)
            error("Existed StructTypeDecl:"+std.stype.structName);
        else
            scope.put(new StructSymbol(std));

        Scope oleScope = scope;
        scope = new Scope(oleScope);
        for (VarDecl vd : std.vars){
            vd.accept(this);
        }
        scope = oleScope;
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl p) {
        Symbol s = scope.lookupCurrent(p.name);
        if (s !=null)
            error("Existed FunDecl:"+p.name);
        else
            scope.put(new FuncSymbol(p));
        Scope oleScope = scope;
        scope = new Scope(oleScope);
//        if (p.params.size()>0){
//
//        }
        if (p.params != null){
            for (VarDecl vd : p.params){
                vd.accept(this);
            }
        }
        Block b = p.block;
        if (b.vars != null){
            for (VarDecl vd : b.vars)
                vd.accept(this);
        }
        if (b.stmts != null){
            for (Stmt st : b.stmts)
                st.accept(this);
        }
        scope = oleScope;
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
    public Void visitBinOp(BinOp bop) {
	    bop.lhs.accept(this);
	    bop.rhs.accept(this);
	    return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr fce) {
        Symbol fd = scope.lookup(fce.funcName);
        if (fd == null)
            error("Not Declared Function:"+fce.funcName);
        else if (!(fd instanceof FuncSymbol))
            error("Wrong Symbol:"+fd);
        else
            fce.fd = ((FuncSymbol) fd).fd;
        if (fce.params!=null){
            for (Expr e:fce.params){
                e.accept(this);
            }
        }
        return null;
	}

    @Override
    public Void visitVarExpr(VarExpr v) {
        Symbol vs = scope.lookup(v.name);
        if (vs == null)
            error("Not Declared Variable:"+v.name);
        else if (!(vs instanceof VarDeclSymbol))
            error("Wrong Symbol:"+vs);
        else{
            v.vd = ((VarDeclSymbol) vs).vd;
            if (v.vd.type == null){
                error("Empty Type");
                return null;
            }
            if (v.vd.type instanceof StructType){
                Symbol nvs;
                if (scope.getOuter()!=null)
                    nvs = scope.getOuter().lookup(((StructType) v.vd.type).structName);
                else
                    nvs = scope.lookup(((StructType) v.vd.type).structName);
                if (nvs != null)
                    v.std = ((StructSymbol) nvs).std;
                else {
                    v.std = null;
                    error("Not Declared Struct");
                }
            }
        }
        return null;
    }

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
	    aae.arr.accept(this);
	    aae.idx.accept(this);
	    return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fae) {
	    fae.structure.accept(this);
		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr vae) {
	    vae.val.accept(this);
		return null;
	}

	@Override
	public Void visitTypecastExpr(TypecastExpr te) {
	    te.expr.accept(this);
		return null;
	}

    @Override
    public Void visitSizeOfExpr(SizeOfExpr soe) { return null; }

    @Override
    public Void visitIf(If i) {
        ifWhileCommon(i.condition, i.stmt);
        if (i.elseStmt != null)
            i.elseStmt.accept(this);
        return null;
    }

    @Override
    public Void visitWhile(While w) {
        ifWhileCommon(w.expr, w.stmt);
        return null;
    }

    private void ifWhileCommon(Expr expr, Stmt stmt) {
	    expr.accept(this);
        if (stmt instanceof Block)
            stmt.accept(this);
        else {
            Scope old = scope;
            scope = new Scope(old);
            stmt.accept(this);
            scope = old;
        }
    }


	@Override
	public Void visitAssign(Assign a) {
        a.lhs.accept(this);
        a.rhs.accept(this);
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt est) {
	    est.expr.accept(this);
		return null;
	}


	@Override
	public Void visitReturn(Return r) {
	    if (r.optionReturn != null)
	        r.optionReturn.accept(this);
		return null;
	}


}
