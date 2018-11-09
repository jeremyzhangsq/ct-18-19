int multiplyNumbers(int n)
{
    if (n >= 1)
        return n*multiplyNumbers(n-1);
    else
        return 1;
}

int main()
{   int n;
    print_s((char *)"Enter a positive integer: \n");
    n = read_i();
    print_s((char *)"Factorial of ");
    print_i(n);
    print_s((char *)" = ");
//    n = multiplyNumbers(n);
    print_i(n);
    return 0;
}
