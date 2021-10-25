// Java boilerplate code
// See https://github.com/csclubiu/twitter-bot-challenge/blob/master/java_bot/README.md
// -----------------------

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import twitter4j.*;
import twitter4j.conf.*;

import javax.imageio.ImageIO;

public class JavaTwitterBot {

    private static final String CONSUMER_KEY        = System.getenv("TWITTERBOT_CONSUMER_KEY");
    private static final String CONSUMER_SECRET     = System.getenv("TWITTERBOT_CONSUMER_SECRET");
    private static final String ACCESS_TOKEN        = System.getenv("TWITTERBOT_ACCESS_TOKEN");
    private static final String ACCESS_TOKEN_SECRET = System.getenv("TWITTERBOT_ACCESS_SECRET");

    public static String lastUserName = null;
    public static Image lastProfilePic = null;
    public static String lastProfilePicLocation = null;
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
        //File image = new File("src/images/testImage.jpeg");
        //System.out.println(image.getAbsolutePath());
        //System.out.println(image.exists());
        // create tweet listener
        JavaTwitterBot.load();
        TwitterStream twitterStream = new TwitterStreamFactory(conf).getInstance().addListener(new StatusListener() {

            @Override
            public void onStatus(Status status) {

                User user = status.getUser();
                String message = status.getText();
                String name = user.getName();
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

                // check if "hi" or "hello" is in the tweet (case insensitive)
                if (message.toLowerCase().contains("hi") || message.toLowerCase().contains("hello")) {

                    // respond with "Hello!"
                    String reply = name + " is new best friend!";//"Hello! " + name;
                    File image = new File("src/images/testImage.jpeg");
                    File newImage = JavaTwitterBot.generateImage(name, profilePic);
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


                }

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

    public static File generateImage(String newUserName, Image profilePic) {
        try {
            File tempImageLocation = new File("src/images/tempFriendshipImage.jpeg");
            String imagePath = "src/images/FriendshipEndedWithTemplate.png";
            final int TEMPLATE_HEIGHT = 500;
            final int TEMPLATE_WIDTH = 372;
            int width = 110;
            int height = 200;
            Image oldProfilePic = null;
            String oldUserName = (JavaTwitterBot.lastUserName == null ? "Twitter" : JavaTwitterBot.lastUserName);
            if (JavaTwitterBot.lastProfilePic == null) {
                String twitterImagePath = "src/images/TwitterBird.png";
                oldProfilePic = ImageIO.read(new File(twitterImagePath));
            } else {
                oldProfilePic = lastProfilePic;
            }
            BufferedImage myPicture = ImageIO.read(new File(imagePath));
            Graphics2D g = (Graphics2D) myPicture.getGraphics();
            //BufferedImage tThumbImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
            //Graphics2D tGraphics2D = tThumbImage.createGraphics(); //create a graphics object to paint to
            g.setBackground( Color.WHITE );
            //g.setPaint( Color.WHITE );
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
            g.setColor(Color.MAGENTA);
            g.drawString(newUserName, TEMPLATE_WIDTH / 2, (TEMPLATE_HEIGHT / 2) - 120);
            g.setColor(Color.cyan);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
            g.drawString(oldUserName, 340, 50);
            //g.fillRect( 0, 0, tThumbWidth, tThumbHeight );
            //g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
            g.drawImage( profilePic, 0, 200, width, height, null );
            g.drawImage( oldProfilePic, 350, 230, 150, 170, null );//draw the image scaled
            ImageIO.write(myPicture, "PNG", tempImageLocation);
            //Image scaledImage = yourImage.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
            JavaTwitterBot.lastUserName = newUserName;
            JavaTwitterBot.lastProfilePicLocation = tempImageLocation.getAbsolutePath();
            JavaTwitterBot.lastProfilePic = ImageIO.read(tempImageLocation);
            JavaTwitterBot.save();
            return tempImageLocation;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void logMessage(String text) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-M-d K:mm:ss a");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("[" + dtf.format(now) + "] " + text);
    }

    public static void load() {
        try {
            FileReader reader = new FileReader("savedData.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println("SS: " + line.substring(0, 21));
                if (line.substring(0, 21).equals("Last Image Location: ")) {
                    //System.out.println("Location Found: ");
                    File savedImageLocation = new File(line.substring(21, line.length()));
                    //System.out.println("Path: " + line.substring(21, line.length()));
                    JavaTwitterBot.lastProfilePic = ImageIO.read(savedImageLocation);
                }
                if (line.substring(0, 16).equals("Last User Name: ")) {
                    //System.out.println("User name Found: ");
                    JavaTwitterBot.lastUserName = line.substring(16, line.length());
                }
            }
            //System.out.println("Loaded");
            //System.out.println("PP: " + JavaTwitterBot.lastProfilePic);
            //System.out.println("UN: " + JavaTwitterBot.lastUserName);
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            FileWriter writer = new FileWriter("savedData.txt", false);
            if (JavaTwitterBot.lastProfilePicLocation != null) writer.write("Last Image Location: " + JavaTwitterBot.lastProfilePicLocation);
            writer.write("\r\n");
            if (JavaTwitterBot.lastUserName != null) writer.write("Last User Name: " + JavaTwitterBot.lastUserName);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}