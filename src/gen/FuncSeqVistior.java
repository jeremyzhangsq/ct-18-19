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
        while (freeRegs.functions.size() < p.funDecls.size()-7){
            tmp = new ArrayList<>(freeRegs.functions.subList(father,freeRegs.functions.size()));
            father = freeRegs.functions.size();
            for (FunDecl fd : tmp){
                fd.accept(this);
            }
        }
        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr fce) {
        switch (fce.funcName) {
            case "read_c": {
                return null;
            }
            case "read_i": {
                return null;
            }
            case "print_c": {
                return null;
            }
            case "print_i": {
                return null;
            }
            case "print_s": {
                return null;
            }
            case "mcmalloc":
                return null;
            default:
                if (!freeRegs.functions.contains(fce.fd))
                    freeRegs.functions.add(fce.fd);
                return null;
        }
    }
}
