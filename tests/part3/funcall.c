
int foo(){
    int a;
    int b;
    a = 5;
    b = 4;
    return a+b;
}

int main(){
      int a;
      int b;
      a = read_i();
      b = foo();
      print_i(b);
      print_i(a);
      return 0;
}