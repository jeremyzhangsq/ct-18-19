struct s{
    int first;
};
int foo(int a){
    return a;
}

int main(){
//      int arr[2];
//      struct s ast;
      int a;
      int b;
      int c;
      c = 10;
      a = 1;
//      arr[0] = 5;
//      arr[1] = a;
//      ast.first = 10;
//      a = read_i();
      b = foo(a+c);
//      b = a+b;
      print_i(b);
      return 0;
}