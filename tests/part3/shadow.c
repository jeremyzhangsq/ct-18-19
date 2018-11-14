struct name{
    int a;
};
int main(){
    struct name st;
    st.a = 10;
    int x;
    x = 20;
    char a;
    a = 'o';
    int sum;
    sum = 0;
    int i;
    i = 0;
    if (i<10){
        char a;
        struct name st;
        st.a = 20;
        a = 'i';
        while(i < 5) {
              int x;
              x = i;
              sum = sum + x;
              i = i+1;
        }
        print_c(a);
        print_s((char*)"\n");
        print_i(st.a);
    }
    print_s((char*)"\n");
    print_i(st.a);
    print_s((char*)"\n");
    print_c(a);
    print_s((char*)"\n");
    print_i(sum);
    print_s((char*)"\n");
    print_i(x);

    return 0;
}
