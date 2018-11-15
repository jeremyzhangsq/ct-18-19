package gen;

import ast.Expr;
import ast.FunDecl;
import ast.VarDecl;

import java.util.*;

public class FreeRegs {
    private static FreeRegs ourInstance = new FreeRegs();
    protected Stack<Register> freeRegs = new Stack<Register>();
    private List<Register> occupyRegs = new ArrayList<>();
    private int controlIdx;
    protected  List<FunDecl> functions = new ArrayList<>();
    protected List<VarDecl> vars = new ArrayList<>(); // global variable list: for shadowing update offset for existing vars
    protected Map<String,List<VarDecl>> varDecls = new HashMap<>();
    protected Map<String,String> Strs;
    protected Map<Character,String> Chrs;
    protected List<Register> earlyReturn; //store occupied registers for return;
    public static FreeRegs getInstance() {
        return ourInstance;
    }

    private FreeRegs() {
        freeRegs.addAll(Register.tmpRegs);
        List<Register> tmp = new ArrayList<>();
        Strs = new HashMap<>();
        Chrs = new HashMap<>();
        controlIdx = 0;
    }
    private class RegisterAllocationError extends Error {}

    protected Register getRegister() {
        try {
            Register r = freeRegs.pop();
            occupyRegs.add(r);
            return r;
        } catch (EmptyStackException ese) {
            throw new RegisterAllocationError(); // no more free registers, bad luck!
//			throw new Error();
        }
    }
    protected void freeRegister(Register reg) {
        boolean contain = false;
        List<Register> tmp = new ArrayList<>(freeRegs);
        for (Register r:tmp){
            if (r.toString().equals(reg.toString())){
                contain = true;
                break;
            }
        }
        if (Register.tmpSet.contains(reg.toString()) && !contain && !reg.forParam){
            freeRegs.push(reg);
            occupyRegs.remove(reg);
        }
    }
    protected List<Register> getOccupyRegs(){
        List<Register> a = new ArrayList<Register>();
        a.addAll(this.occupyRegs);
        return a;
    }
    protected void setOccupied(Register r){
        if (freeRegs.remove(r)){
            occupyRegs.add(r);
        }
    }
    protected void restoreRegister(List<Register> o){
        for (Register r : o){
            setOccupied(r);
        }
    }
    protected void freeAll(){
        freeRegs.clear();
        occupyRegs.clear();
        for (Register r:Register.tmpRegs)
            r.forParam = false;
        freeRegs.addAll(Register.tmpRegs);
    }

    public int getControlIdx() {
        return this.controlIdx++;
    }

}
