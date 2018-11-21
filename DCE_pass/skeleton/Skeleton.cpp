#define DEBUG_TYPE "opCounter"
#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
#include "llvm/Support/raw_ostream.h"
#include <vector>
#include <map>
//#include "llvm/IR/LegacyPassManager.h"
//#include "llvm/Transforms/IPO/PassManagerBuilder.h"
using namespace llvm;
using namespace std;
namespace {
  struct CountOp : public FunctionPass {
    std::map<std::string, int> opCounter;
    static char ID;
    CountOp() : FunctionPass(ID) {}
    virtual bool runOnFunction(Function &F){
      errs() << "Function " <<F.getName() << "\n";
      for (Function::iterator bb = F.begin(), e = F.end(); bb != e; ++bb) {
        for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; ++i) {
          if (opCounter.find(i->getOpcodeName()) == opCounter.end()){
            opCounter[i->getOpcodeName()] = 1;
          }
          else{
            opCounter[i->getOpcodeName()] += 1;
          }
        }
      }

    }
  };
}

