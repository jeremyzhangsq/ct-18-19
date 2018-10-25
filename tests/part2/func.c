struct a{
    int c;
};
char b(int a, int b[10], char c){
    return 'a';
}
char* ab(){
    char a;
    struct a as;
    a = 'a';
//    int d[20];
    int d[10];
    char c;
    int* i;
    a = b(as.c,d,*((char *)i));
//    return (char*)b((int)a,d,(char*)b((int)a,d,c));

}