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