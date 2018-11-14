#include "minic-stdlib.h"
int main()
{
    int c;
    int first;
    int last;
    int middle;
    int n;
    int search;
    int array[5];
    n = 5;
    c = 0;
    while (c < n){
        array[c] = read_i();
        c = c + 1;
    }

    print_s((char*)"Enter value to find\n");
    search = read_i();

    first = 0;
    last = n - 1;
    middle = (first+last)/2;
    while (first <= last) {
        if (array[middle] < search)
            first = middle + 1;
        else {
            if (array[middle] == search) {
                print_i(search);
                print_s((char*)" found at location ");
                print_i(middle+1);
                print_s((char*)"\n");
                return 0;
            }
            else
                last = middle - 1;
        }
        middle = (first + last)/2;
    }
    if (first > last)
        print_s((char *)"Not found!\n");
    return 0;
}
