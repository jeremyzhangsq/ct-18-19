struct name{
    int first;
    char next;
};

int foo(int a, char b, int *c, int d, int i){
    int e;
    e = a;
    char f;
    f = b;
    int g;
    g = *c;
    int h;
    h = d;
    int j;
    j = i;
    print_i(e);
    print_c(f);
    print_i(g);
    print_i(h);
    print_i(j);
    return 0;
}
int main(){
    int a;
    a = 2;
    int arr[10];
    arr[0] = 2;
    int *b;
    b = (int *)arr;
    foo(1,'a',b,3,a);
    return 0;
}
