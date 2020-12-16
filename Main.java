package com.company;

public class Main
{

    public static void main(String[] args)
    {
        try
        {
            new Lab1();

            System.out.println("\n-----Lab2-----\n");

            new Lab2();

            System.out.println("\n-----Lab3-----\n");

            new Lab3();

            System.out.println("\n-----Lab4-----\n");

            new Lab4();
        }
        catch (Exception e) { System.err.println(e.getMessage()); }


    }
}
