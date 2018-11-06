int main(){
    int a;
    int b;
    a = 5;
    b = 3;
    while (b>1 && a < 6){
        b = b - 1;
        a = a + 1;
    }
    if (a<b || b>4)
        b = 6;
//    else
//        b = 4;
    return 0;
}
