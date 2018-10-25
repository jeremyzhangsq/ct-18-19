struct st{
    int a;
    struct st *next;
};
int main(){
    struct st as[10];
    int a;
    a = as[1].a;
    struct st *n;
    n = (*as[1].next).a;
    as[1].next = *as;

}

