package JavaTwitterBot;

import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChessTwitterBot {


    public static Map<String, ChessGame> gamesInProgress = new HashMap<String, ChessGame>();

    private static final String CONSUMER_KEY        = System.getenv("TWITTERBOT_CONSUMER_KEY");
    private static final String CONSUMER_SECRET     = System.getenv("TWITTERBOT_CONSUMER_SECRET");
    private static final String ACCESS_TOKEN        = System.getenv("TWITTERBOT_ACCESS_TOKEN");
    private static final String ACCESS_TOKEN_SECRET = System.getenv("TWITTERBOT_ACCESS_SECRET");

    public static final String BOT_HANDLE = "@Ch3ssB0t";

    private static void logMessage(String text) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-M-d K:mm:ss a");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("[" + dtf.format(now) + "] " + text);
    }

    public static void main(String[] args) throws TwitterException {

        // disable internal logging
        System.setProperty("twitter4j.loggerFactory", "twitter4j.NullLoggerFactory");

        // build auth config
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
        Configuration conf = cb.build();

        // log into the API
        TwitterFactory tf = new TwitterFactory(conf);
        Twitter twitter = tf.getInstance();
        User bot = twitter.verifyCredentials();
        logMessage("Logged in as @" + bot.getScreenName() + "\n----------------");

        // array of terms that will trigger the onStatus function
        ArrayList<String> triggerWords = new ArrayList<String>();
        triggerWords.add("@" + bot.getScreenName());
        // create tweet listener
        TwitterStream twitterStream = new TwitterStreamFactory(conf).getInstance().addListener(new StatusListener() {

            @Override
            public void onStatus(Status status) {

                User user = status.getUser();
                String message = status.getText();
                message = message.substring(ChessTwitterBot.BOT_HANDLE.length(), message.length()).trim();
                String name = user.getName().trim();
                String authorHandle = user.getScreenName();
                String profilePicURL = user.getBiggerProfileImageURL();
                URL url = null;
                try {
                    url = new URL(profilePicURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Image profilePic = null;
                try {
                    profilePic = ImageIO.read(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // log incoming tweet
                logMessage("Received: \"" + message + "\" from @" + authorHandle);
                File tweetImage = null;
                String tweetMessage = "";
                //Check if the user has a game in progress
                if (ChessTwitterBot.gamesInProgress.containsKey(authorHandle)) {
                    ChessGame userGame = ChessTwitterBot.gamesInProgress.get(authorHandle);
                    //There is a game in progress
                    //Check if the user sent a valid move
                    ChessLocation[] move = ChessTwitterBot.parseMove(message);
                    if (move == null) {
                        tweetMessage = "Unknown move\nPlease use the format A1 to B3";
                    } else {
                        ChessGame.MoveResult result = userGame.playerMove(move[0], move[1]);
                        tweetMessage = result.getResultMessage();
                        tweetImage = result.getMoveImage();
                        //If you won or lost end the game
                        if ((result.getResult() == ChessGame.MoveResult.RESULT_LOSS) || (result.getResult() == ChessGame.MoveResult.RESULT_WIN)) {
                            ChessTwitterBot.gamesInProgress.remove(authorHandle);
                        }
                    }
                } else {
                    //No game in progress
                    //Create a new one;
                    ChessGame newPlayerGame = new ChessGame(new RandomBot(), authorHandle, profilePic);
                    ChessTwitterBot.gamesInProgress.put(authorHandle, newPlayerGame);
                    //newPlayerGame.playerMove(new ChessLocation("E2"), new ChessLocation("E4"));
                    //newPlayerGame.playerMove(new ChessLocation("F1"), new ChessLocation("E4"));
                    //newPlayerGame.playerMove(new ChessLocation("D1"), new ChessLocation("C5"));
                    //newPlayerGame.playerMove(new ChessLocation("C5"), new ChessLocation("E7"));
                    tweetImage = newPlayerGame.generateImage(null, null);
                    tweetMessage = "New Game\nYour Go! (You are White)";
                }

                try {

                    // this needs to include the person's username at the beginning
                    StatusUpdate newTweet = new StatusUpdate("@" + authorHandle + " " + tweetMessage);
                    //newTweet.setAttachmentUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
                    newTweet.setMedia(tweetImage);
                    newTweet.inReplyToStatusId(status.getId());
                    twitter.updateStatus(newTweet);
                    logMessage("Responded to @" + authorHandle);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }

                // check if "hi" or "hello" is in the tweet (case insensitive)
                /*if (message.toLowerCase().contains("hi") || message.toLowerCase().contains("hello")) {

                    // respond with "Hello!"
                    String reply = name + " is new best friend!";//"Hello! " + name;
                    try {

                        // this needs to include the person's username at the beginning
                        StatusUpdate newTweet = new StatusUpdate("@" + authorHandle + " " + reply);
                        //newTweet.setAttachmentUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
                        newTweet.setMedia(newImage);
                        newTweet.inReplyToStatusId(status.getId());
                        twitter.updateStatus(newTweet);

                        logMessage("Responded to @" + authorHandle);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }


                }*/

            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
            public void onScrubGeo(long userId, long upToStatusId) {}
            public void onStallWarning(StallWarning warning) {}

        });
        twitterStream.filter(new FilterQuery(0, new long[0], triggerWords.toArray(new String[triggerWords.size()])));

    }

    public static ChessLocation[] parseMove(String moveString) {
        moveString = moveString.toLowerCase();
        moveString = moveString.replace(" to ", " ");
        System.out.println(moveString);
        if (moveString.matches("([A-Za-z][1-9]) ([A-Za-z][1-9])")) {
            String[] moveArray = moveString.split(" ");
            return new ChessLocation[]{new ChessLocation(moveArray[0]), new ChessLocation(moveArray[1])};
        } else {
            return null;
        }
    }
}
