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
            output.add("Call this bot by simply putting your query inside curly braces like {{this}}. This bot searches the Comprehensive Rules (CR), the Infraction Procedure Guide (IPG), the Judging at Regular document (JAR), and the Magic Tournament Rules (MTR). For details on filtering, use !filter. Other valid commands are: !add, !feedback, !github");
        } else if (this.query.matches("filter")) {
            output.add("You may filter results by simply adding more terms to the search query; JudgeBot matches words, not entire strings, unless you use quotation marks. You may also filter by rule source by using | like this: {{summoning sickness|CR}}");
        } else if (this.query.matches("add")) {
            output.add("Add this bot to your server by using this link: https://discordapp.com/oauth2/authorize?client_id=590184543684788253&scope=bot&permissions=346112");
        } else if (this.query.matches("github") || this.query.matches("code") || this.query.matches("readme")) {
            output.add("Code for this bot is available here: https://github.com/elainecao93/judgebot");
        } else if (this.query.matches("feedback")){
            output.add("DM bugs, weird behavior, and other feedback to Oritart#2698.");
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