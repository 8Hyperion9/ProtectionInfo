package com.company;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Lab1
{
    private int step;

    private final ArrayList<Row> rowArray;

    public Lab1()
    {
        rowArray = new ArrayList();

        step = (int) (5 + Math.random() * 69);
        step %= 33;

        start(readFile());
    }

    private void start(String input)
    {
        StringBuilder s = new StringBuilder();

        char c;
        for (int i=0; i< input.length(); i++)
        {
            c = input.charAt(i);
            if ((int) c >= 1040 && (int) c <= 1071)
                c = large(c);
            else if ((int) c >= 1072 && (int) c <= 1103)
                c = small(c);

            s.append(c);
        }


        analyze(s.toString());
        writeFile(s.toString());
    }

    private char small (char c)
    {
        int buf = (int) c + step;
        if (buf > 1103)
        {
            buf -= 1103;
            buf = (int) 'a' + buf;
        }
        return (char) buf;
    }

    private char large (char c)
    {
        int buf = (int) c + step;
        if (buf > 1071)
        {
            buf -= 1071;
            buf = (int) 'А' + buf;
        }
        return (char) buf;
    }

    private String readFile()
    {
        StringBuilder input = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("./src/com/company/read.txt"), StandardCharsets.UTF_8)))
        {
            String line;

            while ((line = reader.readLine()) != null)
                input.append(line).append('\n');
        }
        catch (IOException e) { System.err.println(e.getMessage()); }

        return input.toString();
    }

    private void writeFile(String output)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./src/com/company/write.txt"))) { writer.write(output); }
        catch (IOException e) { System.err.println(e.getMessage());}
    }



    private void analyze(String output)
    {
        char character;
        for(int i = 0 ; i < output.length() ; i ++)
        {
            character = output.charAt(i);

            if(!checkAnalyze(character))
                rowArray.add(new Row(character));

        }

        printAnalyze();
    }

    private boolean checkAnalyze(char character)
    {
        for (Row row : rowArray)
            if (row.getCharacter() == character)
            {
                row.increaseCharacterCount();
                return true;
            }

        return false;
    }


    private void printAnalyze()
    {
        for (Row row : rowArray)
            System.out.println(row.getCharacter() + "  " + row.getCharacterCount());
    }
}


class Row
{
    private char character;
    private int characterCount;

    Row(char character)
    {
        this.character = character;
        this.characterCount = 0;
    }


    public void increaseCharacterCount() { this.characterCount++; }
    public char getCharacter() { return character; }
    public int getCharacterCount() { return characterCount; }
}