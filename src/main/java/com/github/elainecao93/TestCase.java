package com.github.elainecao93;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TestCase {

    public static void main(String[] args) {
        InputStream in = TestCase.class.getResourceAsStream("/MTR.txt");
        byte[] buffer = new byte[1000000];
        try {
            in.read(buffer);
        } catch (IOException e) {
            System.out.println("couldn't read");
            e.printStackTrace();
            System.exit(1);
        }
        ArrayList<String> output = null;
        try {
            output = SearchProcessor.parseFile(buffer);
        } catch (IOException e) {
            System.out.println("couldn't parse");
            e.printStackTrace();
            System.exit(1);
        }
        for (int i=0; i<output.size(); i++) {
            System.out.println(output.get(i));
            System.out.println("break");
        }
    }
}
