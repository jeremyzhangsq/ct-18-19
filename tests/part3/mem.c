int main()
{
   char str[6];
//   str = (char *) mcmalloc(6);
   str[0] = 'a';
   str[1] = 'b';
   str[2] = 'c';
   str[3] = 'd';
   str[4] = 'e';
   str[5] = 'f';
   print_s((char*)str);
   return 0;
}