package gen;

import ast.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FuncSeqVistior extends BaseGenVisitor<Void> {
    public FuncSeqVistior(PrintWriter writer) {
        super(writer);
    }

    @Override
    public Void visitProgram(Program p) {
        for (int i = p.funDecls.size()-1; i>=0;i--) {
            if (p.funDecls.get(i).name.equals("main")){
                p.funDecls.get(i).accept(this);
                break;
            }
        }
        List<FunDecl> tmp;
        int father = 1;
        int old = 0;
        while (freeRegs.functions.size() != old){
            tmp = new ArrayList<>(freeRegs.functions.subList(father,freeRegs.functions.size()));
            father = freeRegs.functions.size();
            old = father;
            for (FunDecl fd : tmp){
                fd.accept(this);
            }
        }
        if(freeRegs.functions.size() < p.funDecls.size()-7){
            for (FunDecl fd : p.funDecls) {
                switch (fd.name) {
                    case "read_c":
                        break;
                    case "read_i":
                        break;
                    case "print_c":
                        break;
                    case "print_i":
                        break;
                    case "print_s":
                        break;
                    case "mcmalloc":
                        break;
                    case "main":
                        break;
                    default:
                        if (!freeRegs.functions.contains(fd))
                            freeRegs.functions.add(fd);
                        break;
                }
            }
        }
        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr fce) {
        switch (fce.funcName) {
            case "read_c":
                break;
            case "read_i":
                break;
            case "print_c":
                break;
            case "print_i":
                break;
            case "print_s":
                break;
            case "mcmalloc":
                break;
            default:
                if (!freeRegs.functions.contains(fce.fd))
                    freeRegs.functions.add(fce.fd);
                break;
        }
        for (Expr e: fce.params)
            e.accept(this);
        return null;
    }
}
