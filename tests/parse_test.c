#include "minic-stdlib.h"


///** /**
//block comment test
//// nest one
//
///** nest two **/
//**/
// invalid type
//string a(){
//}
// invalid declare
//char sum [10);
//void lyth aa;

//// nest struct
//
//struct Info{
//        char name[30];
//        int age;
//        struct address{
//            char area_name[39];
//            int house_no;
//            char district[39];
//        };
//};


struct bstruct
{
    int a;
    int b;
    struct cstruct a[10];
};

struct account {
   int account_number;
   char * first_name;
   char * last_name;
   struct bstruct bs;
};

struct account as;
// multi dot
int main(){
    mul = as.b.c;
    return 1;
}

//// just main
//main(){
//
//}
//int d;


////
//// function declare
//void func(){
//    print("func");
//}
//void func1(void a, void * b, int c, char d, struct account c, struct account * d){
//    print("func1");
//}
//
//void func1(void a,){
//    print("func1");
//}
//
//int a;
//int b;
//int sum;
//struct account * a;
//char sum [10];
//
////// empty condition
////int main(){
////    while(){
////        if(a){
////                int a;
////            }
////    }
////
////    return 0;
////}
//void fun1(int a, int b){
//    sum = 0;
//    a.account_number = 1;
//
//    (int) a;
//    c = (char*) b;
////    d = (void[]) a;
//    a = (struct account) b;
////    d = (account) a;
////    d = (struct *) f;
//    d = (struct account*) ("a"<"d");
//    while (((5>3)||(a[10]!=1)))
//    {
//        void b;
//        struct account a;
//        a.account_number = 10;
//        x = x /10;
//        n = 0;
//        a();
////        a = (x+ba+a)*6+;
//        if (x<1){
//            c = '\\';
//            d = (int*) 10;
//        }
//        // only else
//        else {
//            fun1();
//            while ((fun1()+2)/2>(int) 2)
//                i = i + 1;
//        }
//
//        c = '\\';
//        d = (int*) 10;
//
//    }
//    if (a){
//        c = '\\';
//        d = (int*) 10;
//    } else fun1();
////    // exp as stmt
////   func1()
////   a < b
////   sizeof(int)
//
//}
