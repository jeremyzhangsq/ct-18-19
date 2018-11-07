package gen;

import ast.Expr;

import java.util.*;

public class FreeRegs {
    private static FreeRegs ourInstance = new FreeRegs();
    private Stack<Register> freeRegs = new Stack<Register>();
    private List<Register> occupyRegs = new ArrayList<>();
    private int controlIdx;
    protected Map<String,String> Strs;
    protected Map<Character,String> Chrs;

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
        if (Register.tmpSet.contains(reg.toString())){
            freeRegs.push(reg);
            occupyRegs.remove(reg);
        }
    }
    protected List<Register> getOccupyRegs(){
        List<Register> a = new ArrayList<Register>();
        a.addAll(this.occupyRegs);
        return a;
    }


    public int getControlIdx() {
        return this.controlIdx++;
    }

}
