package gen;

import ast.*;

import java.io.PrintWriter;

public class FuncSeqVistior extends BaseGenVisitor<Void> {
    public FuncSeqVistior(PrintWriter writer) {
        super(writer);
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
