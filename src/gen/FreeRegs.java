package gen;

import java.util.*;

public class FreeRegs {
    private static FreeRegs ourInstance = new FreeRegs();
    private Stack<Register> freeRegs = new Stack<Register>();
    private Stack<Register> freeParamRegs = new Stack<Register>();
    protected Map<String,String> Strs;
    protected Map<Character,String> Chrs;
    public static FreeRegs getInstance() {
        return ourInstance;
    }

    private FreeRegs() {
        freeRegs.addAll(Register.tmpRegs);
        List<Register> tmp = new ArrayList<>();
        for (Register r : Register.paramRegs){
            tmp.add(r);
        }
        freeParamRegs.addAll(tmp);
        Strs = new HashMap<>();
        Chrs = new HashMap<>();
    }
    private class RegisterAllocationError extends Error {}

    protected Register getRegister() {
        try {
            return freeRegs.pop();
        } catch (EmptyStackException ese) {
            throw new RegisterAllocationError(); // no more free registers, bad luck!
//			throw new Error();
        }
    }
    protected void freeRegister(Register reg) {
        freeRegs.push(reg);
    }

    protected Register getParamRegister() {
        try {
            return freeParamRegs.pop();
        } catch (EmptyStackException ese) {
            throw new RegisterAllocationError(); // no more free registers, bad luck!
//			throw new Error();
        }
    }
    protected void freeParamRegister(Register reg) {
        freeParamRegs.push(reg);
    }
}
