package com.github.elainecao93;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TestCase {

    public static void main(String[] args) {
        try {
            SearchProcessor.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchProcessor testCase = new SearchProcessor("summoning sickness", false);
        System.out.println(testCase.toString());
    }
}
