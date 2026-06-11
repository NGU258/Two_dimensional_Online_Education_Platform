package com.edu.platform;
import java.util.Scanner;

//面向智商编程
public class Main {

    public static void main(String[] args) {

        Scanner lyq = new Scanner(System.in);

        int t = lyq.nextInt();

        while(t!=0){

            int n = lyq.nextInt();

            int p = lyq.nextInt();

            String array = lyq.next();

            if(p == 2)
                System.out.println(2);
            else System.out.println(0);

            t--;
        }

    }
}