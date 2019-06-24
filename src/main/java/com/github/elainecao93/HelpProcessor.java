package com.github.elainecao93;

import java.util.ArrayList;

public class HelpProcessor extends SearchProcessor {

    public HelpProcessor(String input, boolean isAdmin) {
        super(input.substring(1), isAdmin, false);
    }

    @Override
    public ArrayList<String> getOutput() {
        ArrayList<String> output = new ArrayList<>();
        if (this.query.matches("help")) {
            output.add("TODO This pulls up a help file.");
        } else if (this.query.matches("filter")) {
            output.add("TODO This pulls up help on filtering.");
        } else if (this.query.matches("add")) {
            output.add("TODO This prints a link to add this bot to another server.");
        } else if (this.query.matches("github") || this.query.matches("code") || this.query.matches("readme")){
            output.add("Code for this bot is available here: https://github.com/elainecao93/judgebot");
        } else if (this.query.matches("quit")) {
            if (this.userIsAdmin == 1)
                output.add("TODO This forces the bot to leave this server.");
            else
                output.add("Only an administrator can do that. I thought you liked me :(");
        } else {
            output.add("That doesn't match any file. Use !help for help");
        }
        return output;
    }

}