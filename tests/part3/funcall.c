struct s{
    int first;
};
int foo(int a, int b, int c, int d, int e){
        print_i(a);
        print_i(b);
        print_i(c);
        print_i(d);
        print_i(e);
    return a+b+c+d+e;
}

int main(){
//      int arr[2];
//      struct s ast;
      int a;
      int b;
      int c;
      int d;
      struct s e;
      a = 1;
      b = 2;
      c = 3;
      d = 4;
      e.first = 5;
//      arr[0] = 5;
//      arr[1] = a;
//      ast.first = 10;
//      a = read_i();
      a = foo(a,b,c,d,e.first);
//      b = a+b;
      print_i(a);
      return 0;
}