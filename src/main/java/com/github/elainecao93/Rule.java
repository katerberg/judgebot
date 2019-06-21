package com.github.elainecao93;

public class Rule{


    private RuleSource source;
    private String citation;
    private String ruleText;

    public Rule(RuleSource source, String rawText, String breakchar, boolean breakline) {
        this.source = source;
        if (breakchar == null) {
            this.ruleText = rawText;
            this.citation = "";
        }
        else {
            int breakIndex = rawText.indexOf(breakchar);
            try {
                this.citation = rawText.substring(0, breakIndex - 1);
                this.ruleText = rawText.substring(breakIndex + 1);
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("error: " + rawText.length());
                System.out.println(rawText);
            }
        }
        if (breakline) {
            this.ruleText = this.ruleText.replace("\n", " ").replace("\r", " ").replace("  ", " ");
        }

    }

    //lower output is more relevant.
    public double relevancy(String query) {
        String[] queryWords = query.split(" ");
        String thisLower = this.toString().toLowerCase();
        int firstAppearance = thisLower.indexOf(queryWords[0]);
        double relevancy = firstAppearance / (double)this.toString().length();
        for (int i=0; i<queryWords.length; i++) {
            if (!thisLower.contains(queryWords[i]))
                return 99;
            if (!thisLower.matches(".*\\W" + queryWords[i] + "\\W.*"))
                relevancy++;
        }
        if (this.citation.toLowerCase().contains("example"))
            relevancy+=50;
        return relevancy;
    }

    public RuleSource getSource() {
        return this.source;
    }

    @Override
    public String toString() {
        return source.toString() + " " + citation + ": " + ruleText;
    }

}
