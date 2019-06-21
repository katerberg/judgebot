package com.github.elainecao93;

import org.javacord.api.*;
import org.javacord.api.entity.message.MessageAuthor;

import java.io.IOException;
import java.util.ArrayList;

public class init {

    public static void main(String[] args) {
        String token = args[0];

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        try {
            SearchProcessor.initialize();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        api.addMessageCreateListener(event -> {
            MessageAuthor ma = event.getMessageAuthor();
            boolean isBotOwner = (ma.asUser().isPresent() && ma.asUser().get().isBotOwner());
            if (isBotOwner && event.getMessageContent().toLowerCase().matches("judgebot, stop")) {
                event.getChannel().sendMessage("Shutting down by owner's request.");
                System.exit(0);
            }
            boolean isAdmin = ma.canBanUsersFromServer() || isBotOwner;
            ArrayList<String> commands = getCommand(event.getMessageContent());
            if (commands.size() != 0) {
                for (int i=0; i<commands.size(); i++) {
                    ArrayList<String> payload = parseCommand(commands.get(i), isAdmin);
                    for (int j = 0; j < payload.size(); j++)
                        event.getChannel().sendMessage(payload.get(j));
                }
            }
        });

        System.out.println("Initialized!");
    }

    public static ArrayList<String> getCommand(String input) {

        ArrayList<String> output = new ArrayList<>();
        int bound = 0;
        while (true) {
            int leftIndex = input.indexOf("{{", bound);
            int rightIndex = input.indexOf("}}", bound);
            if (leftIndex == -1 || rightIndex == -1 || rightIndex < leftIndex) {
                return output;
            }
            output.add(input.substring(leftIndex+2, rightIndex));
            bound = rightIndex + 1;
        }
    }

    public static ArrayList<String> parseCommand(String input, boolean isAdmin) {
        if (input.length() < 3) {
            ArrayList<String> output = new ArrayList<>();
            output.add("To prevent abuse, queries of less than three characters are not allowed.");
            return output;
        }
        if (input.indexOf("!") == 0) {
            HelpProcessor hp = new HelpProcessor(input, isAdmin);
            return hp.getOutput();
        }
        SearchProcessor sp = new SearchProcessor(input, isAdmin);
        return sp.getOutput();
    }

}
