struct s{
    int first;
};
int foo(int a, int d, int c){
    return a+d+c;
}

int main(){
      int arr;
      struct s ast;
      int b;
      arr = 5;
      ast.first = 10;
//      a = read_i();
      b = foo(1,arr,ast.first);
//      b = a+b;
      print_i(b);
      return 0;
}