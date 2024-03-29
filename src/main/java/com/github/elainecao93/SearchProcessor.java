package com.github.elainecao93;

import java.util.*;
import java.io.*;

public class SearchProcessor {

    private static final int MAX_RESULTS = 5;
    private static final int MAX_RESULTS_IF_ADMIN = 10;
    private static final int MAX_MESSAGE_LENGTH = 2000;

    private static ArrayList<Rule> rules;

    protected String query;
    private String filter;
    private boolean exactMatch;

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
        readFile("/CR_glossary.txt", 100000, "\r\n", RuleSource.CRG, true);
        readFile("/JAR.txt", 10000, null, RuleSource.JAR, true);
        readFile("/IPG.txt", 100000, ":", RuleSource.IPG, false);
        readFile("/MTR.txt", 100000, ":", RuleSource.MTR, false);
    }

    private static void readFile(String filename, int bufferlen, String breakchar, RuleSource source, boolean breakline) throws IOException {
        InputStream in = SearchProcessor.class.getResourceAsStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        char[] buffer = new char[bufferlen];
        System.out.println(br.read(buffer));

        in.close();

        if (source == RuleSource.IPG || source == RuleSource.MTR) {
            ArrayList<String> input = parseFile(buffer);
            for (int i=0; i<input.size(); i++) {
                rules.add(new Rule(source, input.get(i), breakchar, breakline));
            }
        }
        else {
            String[] inputArray = new String(buffer).split("\\r?\\n\\r?\\n");
            for (int i = 0; i < inputArray.length; i++) {
                if (inputArray[i].length() > 1) {
                    rules.add(new Rule(source, inputArray[i], breakchar, breakline));
                }
            }
        }
    }

    public static ArrayList<String> parseFile(char[] buffer) throws IOException {
        ArrayList<String> output = new ArrayList<>();
        String[] inputString = new String(buffer).split("\\r\\n");
        ArrayList<String> input = new ArrayList<>(Arrays.asList(inputString));

        String currentTitle = "";
        String currentSubsection = "";
        String currentRule = "";

        for (int i=0; i<input.size(); i++) {
            int len = input.get(i).length();
            String elem = input.get(i);
            if (len < 3) ; //page number
            else if (!Character.isAlphabetic(elem.charAt(elem.length()-1)) && len < 90) { //end of paragraph
                currentRule += " " + elem;
                output.add(currentTitle + " " + currentSubsection + ": " + currentRule);
                currentRule = "";
            }
            else if (Character.isDigit(elem.charAt(0))) { // is a title
                if (currentRule.length() > 3)
                    output.add(currentTitle + " " + currentSubsection + ": " + currentRule);
                currentSubsection = "";
                currentRule = "";
                currentTitle = elem;
            }
            else if (len < 40) { //is a subsection header
                if (currentRule.length() > 3)
                    output.add(currentTitle + " " + currentSubsection + ": " + currentRule);
                currentSubsection = elem;
                currentRule = "";
            }
            else {
                currentRule += " " + elem;
            }
        }

        return output;
    }

    public SearchProcessor(String input, boolean isAdmin, boolean exactMatch) {
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

        this.exactMatch = exactMatch;
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

        this.output.clear();

        //add all possible outputs
        System.out.println("Query: " + this.query);
        HashMap<Rule, Double> possibleOutputs = new HashMap<>();
        for (int i=0; i<rules.size(); i++) {
            if (this.filter != null && (!this.filter.matches(rules.get(i).getSource().toString().toLowerCase())))
                continue;
            double relevancy = rules.get(i).relevancy(this.query, exactMatch);
            if (relevancy < 99999) {
                possibleOutputs.put(rules.get(i), relevancy);
            }
        }

        //sort for relevancy
        Set<Map.Entry<Rule, Double>> outputs = possibleOutputs.entrySet();
        Comparator<Map.Entry<Rule, Double>> relevancyComparator = new Comparator<Map.Entry<Rule, Double>>() {
            @Override
            public int compare(Map.Entry<Rule, Double> e1, Map.Entry<Rule, Double> e2) {
                int relevancyDiff = e1.getValue().compareTo(e2.getValue());
                if (relevancyDiff != 0)
                    return relevancyDiff;
                return e1.getKey().compareTo(e2.getKey());
            }
        };
        List<Map.Entry<Rule, Double>> outputList = new ArrayList<>(outputs);
        outputList.sort(relevancyComparator);

        //truncate to the correct number of results
        int resultsNum = userIsAdmin == 0 ? MAX_RESULTS : MAX_RESULTS_IF_ADMIN;
        int resultsFound = outputList.size();
        if (resultsNum < outputList.size()) {
            outputList = outputList.subList(0, resultsNum);
        }

        //sort remaining output
        ArrayList<Rule> outputRules = new ArrayList<>();
        for (int i=0; i<outputList.size(); i++) {
            outputRules.add(outputList.get(i).getKey());
        }
        Collections.sort(outputRules); //sorts by rule number

        //return results
        Rule lastRule = null;
        for (int i=0; i<outputRules.size(); i++) {
            Rule elem = outputRules.get(i);
            String lastString = null;
            if (lastRule != null)
                lastString = this.output.remove(this.output.size()-1);
            if (lastRule != null && lastRule.matchesSource(elem)) {
                if (lastString.length() + elem.getRuleText().length() + 1 < MAX_MESSAGE_LENGTH){
                    lastString = lastString + "\n" + elem.getRuleText();
                    this.output.add(lastString);
                } else {
                    this.output.add(lastString);
                    this.output.add(elem.getRuleText());
                }
            } else {
                if (lastRule != null && lastString.length() + elem.toString().length() + 1 < MAX_MESSAGE_LENGTH) {
                    lastString = lastString + "\n" + elem.toString();
                    this.output.add(lastString);
                } else {
                    this.output.add(lastString);
                    this.output.add(elem.toString());
                }
            }
            lastRule = elem;
        }

        if (resultsFound > resultsNum){
            if (userIsAdmin == 1)
                this.output.add(0, resultsFound + " results were found. Displaying the " + resultsNum + " most relevant results because an administrator command was run. Use !filter to filter.");
            else
                this.output.add(0, resultsFound + " results were found. Displaying the " + resultsNum + " most relevant results. Use !filter to filter.");
        }

        if (resultsFound == 0) {
            this.output.add("No results found for \"" + this.query +"\". Please check for misspellings or alternate spellings.");
        }

        return this.output;

    }

}
