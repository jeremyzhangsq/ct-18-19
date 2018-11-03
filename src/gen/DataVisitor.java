package gen;

import ast.*;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;


public class DataVisitor extends BaseGenVisitor<Integer> {
    private PrintWriter writer;
    private Program program;
    private Set<String> Strs;
    private Set<Character> Chrs;
    public DataVisitor(PrintWriter writer, Program program){
        this.writer = writer;
        this.program = program;
        Strs = new HashSet<>();
        Chrs = new HashSet<>();
    }

    @Override
    public Integer visitProgram(Program p) {
        writer.println(".data");
        for (VarDecl vd : p.varDecls) {
            vd.accept(this);
        }
        for (FunDecl fd : p.funDecls) {
            fd.accept(this);
        }
        return null;
    }


    @Override
    public Integer visitVarDecl(VarDecl vd) {
        writer.println(vd.varName+":");
        int space = vd.type.accept(this);
        writer.println(".space "+Integer.toString(space));
        if (space%4 != 0)
            writer.println(".align 2");
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
        if (!Strs.contains(sl.val)){
            writer.println("str"+Integer.toString(Strs.size())+":\t.asciiz \""+sl.val+"\"");
            Strs.add(sl.val);
        }
        return null;
    }

    @Override
    public Integer visitChrLiteral(ChrLiteral cl) {
        if (!Chrs.contains(cl.val)){
            writer.println("chr"+Integer.toString(Chrs.size())+":\t.asciiz \'"+cl.val+"\'");
            Chrs.add(cl.val);
        }
        return null;
    }


}
