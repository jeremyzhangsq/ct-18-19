package gen;

import ast.*;

import java.io.PrintWriter;


public class DataVisitor extends BaseGenVisitor<Integer> {
    private Program program;

    public DataVisitor(PrintWriter writer, Program program){
        super(writer);
        this.program = program;
    }

    @Override
    public Integer visitProgram(Program p) {
        writer.println(".data");
        for (VarDecl vd : p.varDecls) {
            vd.isGlobal = true;
            writer.print(vd.varName+":");
            int space = vd.type.accept(this);
            if (space%4 != 0)
                space = 4*(space/4+1);
            writer.println(".space "+Integer.toString(space));
        }
        for (FunDecl fd : p.funDecls) {
            fd.accept(this);
        }
        return null;
    }


    @Override
    public Integer visitBaseType(BaseType bt) {
        if (bt == BaseType.INT)
            return 4;
        else if (bt == BaseType.CHAR)
            return 1;
        return null;
    }

    @Override
    public Integer visitPointerType(PointerType pt) {
        return 4;
    }

    @Override
    public Integer visitStructType(StructType st) {
        int size = 0;
        StructTypeDecl std = null;
        for (StructTypeDecl s : program.structTypeDecls) {
            if (s.stype.structName .equals(st.structName)){
                std = s;
                break;
            }
        }
        if (std!=null){
            for (VarDecl vd: std.vars)
                if (vd.type == BaseType.CHAR)
                    size += 4;
                else
                    size += vd.type.accept(this);
        }
        return size;
    }

    @Override
    public Integer visitArrayType(ArrayType at) {
        int type = at.type.accept(this);
        return type*at.arrSize;
    }

    @Override
    public Integer visitStrLiteral(StrLiteral sl) {
        if (!freeRegs.Strs.keySet().contains(sl.val)){
            String key = "str"+Integer.toString(freeRegs.Strs.size());
            writer.println(key+":\t.asciiz \""+sl.val+"\"");
            freeRegs.Strs.put(sl.val,key);
        }
        return null;
    }

    @Override
    public Integer visitChrLiteral(ChrLiteral cl) {
        if (!freeRegs.Chrs.keySet().contains(cl.val)){
            String key = "chr"+Integer.toString(freeRegs.Chrs.size());
            writer.println(key+":\t.byte \'"+cl.val+"\'");
            freeRegs.Chrs.put(cl.val,key);
        }
        return null;
    }
    @Override
    public Integer visitVarExpr(VarExpr v) {
        return v.type.accept(this);
    }

}
