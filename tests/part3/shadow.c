
int main(){
    int x;
    x = 20;
    int sum;
    sum = 0;
    int i;
    i = 0;
    while(i < 5) {
        int x;
        x = i;
        sum = sum + x;
        i = i+1;
    }

    print_i(sum);
    print_s((char*)"\n");
    print_i(x);

    return 0;
}
