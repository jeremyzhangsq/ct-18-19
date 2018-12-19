//#define DEBUG_TYPE "opCounter"
#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/Instructions.h"
#include "llvm/Transforms/Utils/Local.h"
#include "llvm/Support/raw_ostream.h"
#include <vector>
#include <map>
//#include "llvm/IR/LegacyPassManager.h"
//#include "llvm/Transforms/IPO/PassManagerBuilder.h"
using namespace llvm;
using namespace std;
namespace {
  struct SimpleDCE : public FunctionPass {
    std::vector<Instruction*> DCL;
    static char ID;
    SimpleDCE() : FunctionPass(ID) {}
    virtual bool runOnFunction(Function &F){
     while(true){	
      for (Function::iterator bb = F.begin(), e = F.end(); bb != e; ++bb) {
        for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; ++i) {
   
              Instruction *inst = &*i;
              if (llvm :: isInstructionTriviallyDead(inst, nullptr)){
                DCL.push_back(inst);
              }
        }
      
      }
      if (DCL.empty())
        break;
      while (!DCL.empty()){
        Instruction *i = DCL.back();
        DCL.pop_back();
        // errs() << "remove instruction which opcode is: "<< i->getOpcodeName() << "\n";
        i -> eraseFromParent();
      }
    }
      // errs() << "remove success\n";
      return false;
    }
  };
}
char SimpleDCE :: ID = 0;
__attribute__((unused)) static RegisterPass<SimpleDCE>
    X("skeletonpass", "Simple dead code elimination");
//
// cmd ~/ug3-ct/build/bin/opt -load ~/dce-pass/build/dce/libSkeletonPass.so -mem2reg ./dead.ll -o nodce.ll -S
