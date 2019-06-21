package com.github.elainecao93;

import java.util.*;
import java.io.*;

public class SearchProcessor {

    private static final int MAX_RESULTS = 5;
    private static final int MAX_RESULTS_IF_ADMIN = 10;

    private static ArrayList<Rule> rules;

    protected String query;
    private String filter;

    /*
    1: User is admin and used an admin command.
    0: User is not admin or didn't use an admin command.
    -1: User is not admin and tried to use admin command.
     */
    protected int userIsAdmin;

    protected ArrayList<String> output;

    public static void initialize() throws IOException {

        rules = new ArrayList<>();

        readFile("/CR.txt", 750000, " ", RuleSource.CR, false);
        readFile("/CR_glossary.txt", 100000, "\n", RuleSource.CRG, true);
        readFile("/JAR.txt", 10000, null, RuleSource.JAR, true);

    }

    private static void readFile(String filename, int bufferlen, String breakchar, RuleSource source, boolean breakline) throws IOException {
        InputStream in = SearchProcessor.class.getResourceAsStream(filename);
        byte[] buffer = new byte[bufferlen];
        System.out.println(in.read(buffer));

        String[] inputArray = new String(buffer, "UTF-8").split("\\r?\\n\\r?\\n");
        for (int i=0; i<inputArray.length; i++) {
            if (inputArray[i].length() > 1) {
                rules.add(new Rule(source, inputArray[i], breakchar, breakline));
            }
        }
    }

    private ArrayList<String> parseFile(byte[] buffer) throws IOException {
        ArrayList<String> output = new ArrayList<String>();
        String[] inputString = new String(buffer, "UTF-8").split("\\r\\n");
        ArrayList<String> input = new ArrayList<>(Arrays.asList(inputString));

        return output;
    }

    public SearchProcessor(String input, boolean isAdmin) {
        this.output = new ArrayList<>();
        input = input.toLowerCase();
        boolean usedAdminCommand = (input.indexOf("@") == 0);
        this.userIsAdmin = (isAdmin && usedAdminCommand) ? 1 : (usedAdminCommand ? -1 : 0);

        if (userIsAdmin==-1) return;

        int filterIndex = input.indexOf("|");
        if (filterIndex == -1) {
            this.query = input.substring(userIsAdmin);
            this.filter = null;
        }
        else {
            this.query = input.substring(userIsAdmin, filterIndex);
            this.filter = input.substring(filterIndex+1);
        }
    }

    public ArrayList<String> getOutput() {

        //bad user
        if (this.userIsAdmin == -1) {
            this.output.add("Use of administrator commands is reserved for server administrators. I mean, duh.");
            return this.output;
        }

        //easter egg
        if (this.query.contains("dog")) {
            this.output.add("Ain't no rule says a dog can't play Magic!");
            return this.output;
        }

        long computeStart = System.currentTimeMillis();

        this.output.clear();

        //add all possible outputs
        System.out.println("Query: " + this.query);
        HashMap<String, Double> possibleOutputs = new HashMap<>();
        for (int i=0; i<rules.size(); i++) {
            if (this.filter != null && (!this.filter.matches(rules.get(i).getSource().toString().toLowerCase())))
                continue;
            String ruleStr = rules.get(i).toString();
            double relevancy = rules.get(i).relevancy(this.query);
            if (relevancy < 99.0) {
                possibleOutputs.put(ruleStr, relevancy);
            }
        }

        int resultsNum = userIsAdmin == 0 ? MAX_RESULTS : MAX_RESULTS_IF_ADMIN;

        //sort for relevancy
        Set<Map.Entry<String, Double>> outputs = possibleOutputs.entrySet();
        Comparator<Map.Entry<String, Double>> comparator = new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> e1, Map.Entry<String, Double> e2) {
                return e1.getValue().compareTo(e2.getValue());
            }
        };
        ArrayList<Map.Entry<String, Double>> outputList = new ArrayList<>(outputs);
        outputList.sort(comparator);

        //return results
        for (int i=0; i<outputList.size(); i++) {
            this.output.add(outputList.get(i).getKey());
            if (i > resultsNum-2)
                break;
        }

        Collections.sort(this.output);

        if (outputList.size() > resultsNum){
            if (userIsAdmin == 1)
                this.output.add(0, outputList.size() + " results were found. Displaying the " + resultsNum + " most relevant results because an administrator command was run. Use !filter to filter.");
            else
                this.output.add(0, outputList.size() + " results were found. Displaying the " + resultsNum + " most relevant results. Use !filter to filter.");
        }

        this.output.add("Computed in " + (System.currentTimeMillis() - computeStart + " ms"));

        return this.output;

    }

}
