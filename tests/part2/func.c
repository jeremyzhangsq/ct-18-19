char[7] b(int a, int b[10], char* c){
    return "aaaaaa";
}
char* a(){
    char a;
    a = 'a';
//    int d[20];
    int d[10];
    char* c;
    return (char*)b((int)a,d,(char*)b((int)a,d,c));
}