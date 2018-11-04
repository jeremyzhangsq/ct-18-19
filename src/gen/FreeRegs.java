package gen;

import java.util.EmptyStackException;
import java.util.Stack;

public class FreeRegs {
    private static FreeRegs ourInstance = new FreeRegs();
    private Stack<Register> freeRegs = new Stack<Register>();
    public static FreeRegs getInstance() {
        return ourInstance;
    }

    private FreeRegs() {
        freeRegs.addAll(Register.tmpRegs);
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
}
