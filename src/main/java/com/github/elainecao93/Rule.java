package com.github.elainecao93;

public class Rule implements Comparable<Rule>{


    private RuleSource source;
    private String citation;
    private String ruleText;
    public int ruleNum;
    private static int ruleIndex = 0;

    public Rule(RuleSource source, String rawText, String breakchar, boolean breakline) {
        this.source = source;
        if (breakchar == null) {
            this.ruleText = rawText;
            this.citation = "";
        }
        else {
            int breakIndex = rawText.indexOf(breakchar);
            try {
                this.citation = rawText.substring(0, breakIndex);
                this.ruleText = rawText.substring(breakIndex + 1);
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("error: " + rawText.length());
                System.out.println(rawText);
            }
        }
        if (breakline) {
            this.ruleText = this.ruleText.replace("\n", " ").replace("\r", " ").replace("  ", " ");
        }
        this.ruleNum = ruleIndex;
        ruleIndex++;
    }

    //lower output is more relevant.
    public double relevancy(String query, boolean exactMatch) {
        String[] queryWords;
        if (exactMatch) {
            queryWords = new String[1];
            queryWords[0] = query;
        } else {
            queryWords = query.split(" ");
        }
        String thisString = this.toString().toLowerCase();
        int firstAppearance = thisString.indexOf(queryWords[0]);
        double relevancy = (double)firstAppearance / 1000;
        for (int i=0; i<queryWords.length; i++) {
            int indexOfQuery = thisString.indexOf(queryWords[i]);
            if (indexOfQuery == -1)
                return 99999;
            if (indexOfQuery < this.citation.length())
                relevancy--;
            if (!thisString.matches(".*\\W" + queryWords[i] + "\\W.*"))
                relevancy++;
        }
        return relevancy;
    }

    public RuleSource getSource() {
        return this.source;
    }

    @Override
    public String toString() {
        return source.toString() + " " + citation + ": " + ruleText;
    }

    @Override
    public int compareTo(Rule o) {
        return this.ruleNum - ((Rule)o).ruleNum;
    }
}
