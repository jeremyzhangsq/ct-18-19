int binarySearch(int arr[5], int l, int r, int x)
{
   if (r >= l)
   {
        int mid;
        mid = l + (r - l)/2;
        if (arr[mid] == x)
            return mid;

        if (arr[mid] > x)
            return binarySearch(arr, l, mid-1, x);

        return binarySearch(arr, mid+1, r, x);
   }

   return -1;
}

int main()
{
   int arr[5];
   arr[0] = 0;
    arr[1] = 1;
    arr[2] = 2;
    arr[3] = 3;
    arr[4] = 4;
   int n;
    n = 5;
   int x;
    x = read_i();
   int result;
    result = binarySearch(arr, 0, n-1, x);
    if(result == -1)
         print_s((char*)"not Found\n");
    else
        print_s((char*)"Found\n");
   return 0;
}

//int main()
//{
//    int c;
//    int first;
//    int last;
//    int middle;
//    int n;
//    int search;
//    int array[5];
//    n = 5;
//    c = 0;
//   while (c < n){
//    array[c] = read_i();
//    c = c+1;
//   }
//
//
//   print_s((char*)"Enter value to find\n");
//   search = read_i();
//
//   first = 0;
//   last = n - 1;
//   middle = (first+last)/2;
//
//   while (first <= last) {
//      if (array[middle] < search)
//         first = middle + 1;
//      else if (array[middle] == search) {
//         print_s((char *)"found.\n");
//         return 0;
//      }
//      else
//         last = middle - 1;
//
//      middle = (first + last)/2;
//   }
//   if (first > last)
//      print_s((char *)"Not found!\n");
//
//   return 0;
//}