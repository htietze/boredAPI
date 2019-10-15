import com.google.gson.Gson;
import input.InputUtils;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

        /*
        https://www.boredapi.com/

    Use the Bored API to fetch random activities for the user.

    Print the details about an activity. Ask the user if they'd like to save that activity for later.

    If so, append the information about that activity to a file.

    Your program will create one file with all of the activities the user has saved.

    If not, get a new random activity.

    Repeat until the user is done.

    When the user runs the program again, they can add more activities to their file.

    return data example:
    {
      "activity": "Have a paper airplane contest with some friends",
      "accessibility": 0.05,
      "type": "social",
      "participants": 4,
      "price": 0.02,
      "link": "",
      "key": "8557562"
    }

         */

public class boooored {

    public static void main(String[] args) {

        // unirest pulls the data, then using Gson to map the data to objects? I think?
        Unirest.config().setObjectMapper(new ObjectMapper() {
            private Gson gson = new Gson();
            @Override
            public <T> T readValue(String s, Class<T> aClass) {
                return gson.fromJson(s, aClass);
            }

            @Override
            public String writeValue(Object o) {
                return gson.toJson(o);
            }
        });

        // saving the URL so it can be used in the loop
        String boredURL = "https://www.boredapi.com/api/activity";
        // but also so it can be used in the first call which gets the initial activity data
        Activity activityData = Unirest.get(boredURL).asObject(Activity.class).getBody();

        // a boolean used to start up the while loop
        boolean findActivity = true;
        // while it's true, the loop will keep finding activities and asking if they should be saved
        while (findActivity) {

            // first printing out the initial activity that got pulled, calling parts of the Activity class?
            System.out.println("Here's an activity for you to do:");
            System.out.println("Activity: " + activityData.activity);
            System.out.println("Accessibility: " + activityData.accessibility);
            System.out.println("Type: " + activityData.type);
            System.out.println("Participants: " + activityData.participants);
            System.out.println("Price: " + activityData.price);

            // asking the user if they want to save this activity:
            boolean saveActivity = InputUtils.yesNoInput("Would you like to save this activity?");
            // if they do, then it attempts to run the file writer:
            if (saveActivity) {
                // try-catch with resources, aiming for activities.txt, set to true so it doesn't just overwrite it!
                try (BufferedWriter bufWrite = new BufferedWriter(new FileWriter("activities.txt", true))) {
                    bufWrite.write("Activity: " + activityData.activity + "\n");
                    bufWrite.write("Accessibility: " + activityData.accessibility + "\n");
                    bufWrite.write("Type: " + activityData.type + "\n");
                    bufWrite.write("Participants: " + activityData.participants + "\n");
                    bufWrite.write("Price: " + activityData.price + "\n");
                    bufWrite.write("----------------------------------- \n");
                    // confirming that the data was saved at the end:
                    System.out.println("Saved!");
                // If something went wrong and it couldn't create the file, it'll tell the user:
                } catch (IOException ioe) {
                    System.out.println("File couldn't be found or made!");
                }
            }
            // after deciding if they save that activity or not, it'll ask if the user wants to find another one:
            findActivity = InputUtils.yesNoInput("Would you like to find a new activity?");
            // if the user wants another activity, the API is asked for new data and the loop wraps around
            // using that data in the same way the initial request was used.
            if (findActivity) {
                activityData = Unirest.get(boredURL).asObject(Activity.class).getBody();
            }

        }

    }
    // saving the API data into objects that can be called in other methods I think?
    static class Activity {
        String activity;
        Double accessibility;
        String type;
        Integer participants;
        Double price;
    }

}
