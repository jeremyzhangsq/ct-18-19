int isPrime(int num,int i){

    if(i==1){
        return 1;
    }else{
       if(num%i==0)
         return 0;
       else
         isPrime(num,i-1);
    }
}

int main(){
    int num;
    int prime;
    print_s((char *)"Enter a positive number: ");
    num = read_i();

    prime = isPrime(num,num/2);
    print_i(num);
   if(prime==1)
        print_s((char*)" is a prime number");
   else
      print_s((char*)" is not a prime number");

   return 0;
}
