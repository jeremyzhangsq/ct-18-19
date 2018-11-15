
int main(){
    print_s((char *)"string\n");
    char *a;
    a = (char *)"astring\n";
    char b[2];
    b[0] = 'a';
    b[1] = 'b';
    print_s(a);
    print_s((char*)b);
    return 0;
}