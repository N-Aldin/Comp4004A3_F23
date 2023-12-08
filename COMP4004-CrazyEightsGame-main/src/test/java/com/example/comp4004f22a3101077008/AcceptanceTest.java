package com.example.comp4004f22a3101077008;

import com.beust.ah.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = Application.class)
public class AcceptanceTest {
    @Autowired
    GameData gd;
    @Autowired
    GameLogic game;
    @LocalServerPort
    int port;
    int[] pCardStrtInd = {2, 7, 12, 17, 22};

//    Browsers
    ArrayList<WebDriver> browsers;

    @BeforeEach
    public void initGame() throws InterruptedException {
        browsers = new ArrayList<>();

        String filePath = new File("").getAbsolutePath();
        filePath = filePath.concat("\\..\\driver\\chromedriver-win64\\chromedriver.exe");
        System.out.println("This is the path: " + filePath);


        System.setProperty("webdriver.chrome.driver", filePath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        WebDriver driver;

        for (int i = 0; i < 4; ++i){
            driver = new ChromeDriver(options);
            driver.get("http://localhost:" + port + "/");
//            TimeUnit.MILLISECONDS.sleep(1000);
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
            driver.findElement(By.id("usernameBtn")).click();

            browsers.add(driver);
        }


        // RESIZE WINDOWS
        int width = 1920/2;
        int height = (1080)/2;

        browsers.get(0).manage().window().setPosition(new org.openqa.selenium.Point(0, 0));
        browsers.get(0).manage().window().setSize(new Dimension(width, height));
        browsers.get(1).manage().window().setPosition(new org.openqa.selenium.Point(width, 0));
        browsers.get(1).manage().window().setSize(new Dimension(width, height));
        browsers.get(2).manage().window().setPosition(new org.openqa.selenium.Point(0, height));
        browsers.get(2).manage().window().setSize(new Dimension(width, height));
        browsers.get(3).manage().window().setPosition(new org.openqa.selenium.Point(width, height));
        browsers.get(3).manage().window().setSize(new Dimension(width, height));

    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("p1 plays 3C: assert next player is player 2")
    public void TestRow25(){

        rigTestRow25();

        browsers.get(0).findElement(By.id("startBtn")).click();

        browsers.get(0).findElement(By.id("3C")).click();

        // TODO: Check the top card ******

        // Check the player turn is 2 for all the browsers and the direction is going right
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
            assert (browsers.get(i).findElement(By.id("direction")).getText().contains("left"));
        }

        assert browsers.get(1).findElement(By.id("draw")).isEnabled();

        try {
            TimeUnit.MILLISECONDS.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    // Need player 1 to have 3C and the top card to be a C card
    public void rigTestRow25(){
        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        ArrayList<Card> rCard = new ArrayList<>();
        int counter = 1;

        // Will be top card
        rCard.add(new Card("C", "4"));

        // Will be dealt to player 1
        rCard.add(new Card("C", "3"));

        for (String s : suit) {
            for (String value : rank) {
                // skip the card that was added to be dealt to player 1
                if (s.equals("C") && value.equals("3")) continue;
                // chose a random club card to be the top card
                else if (s.equals("C") && value.equals("4")) continue;

                counter++;

                Card c = new Card(s, value);
                rCard.add(c);
            }
        }

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("p1 plays 1H assert next player is player 4 AND interface must show now playing in opposite direction (i.e., going right) " +
            "player4 plays 7H and next player is player 3")
    public void TestRow26(){

        rigTestRow26();

        browsers.get(0).findElement(By.id("startBtn")).click();

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        browsers.get(0).findElement(By.id("AH")).click();

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Check the player turn is 4 for all the browsers and the direction is going left
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("4"));
            assert (browsers.get(i).findElement(By.id("direction")).getText().contains("right"));
        }

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // check the players draw button is enabled
        assert browsers.get(3).findElement(By.id("draw")).isEnabled();

        browsers.get(3).findElement(By.id("7H")).click();

        // checking that player 3's draw button is enabled signifying that it is their turn
        assert browsers.get(2).findElement(By.id("draw")).isEnabled();

        try {
            TimeUnit.MILLISECONDS.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    // Need player 1 to have 1H to reverse the order, then player 4 plays 7H
    public void rigTestRow26(){
        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        ArrayList<Card> rCard = new ArrayList<>();
        int counter = 1;

        // Will be top card
        rCard.add(new Card("H", "4"));

        // Will be dealt to player 1
        rCard.add(new Card("H", "A"));

        for (String s : suit) {
            for (String value : rank) {
                // skip the card that was added to be dealt to player 1
                if (s.equals("H") && value.equals("A")) continue;
                    // chose a random club card to be the top card
                else if (s.equals("H") && value.equals("4")) continue;

                counter++;
                if (counter == 17) rCard.add(new Card("H", "7"));

                Card c = new Card(s, value);
                rCard.add(c);
            }
        }

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("p1 plays QC assert next player is player 3  (because player 2 is notified and loses their turn)")
    public void TestRow28() throws InterruptedException {

        rigTestRow28();

        browsers.get(0).findElement(By.id("startBtn")).click();

        browsers.get(0).findElement(By.id("QC")).click();

        // Check the player turn is 4 for all the browsers and the direction is going left
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("3"));
        }

        assert (browsers.get(2).findElement(By.id("draw")).isEnabled());

        TimeUnit.MILLISECONDS.sleep(5000);
    }

    // Need player 1 to have QC
    public void rigTestRow28(){
        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        ArrayList<Card> rCard = new ArrayList<>();
        int counter = 1;

        // Will be top card
        rCard.add(new Card("C", "4"));

        // Will be dealt to player 1
        rCard.add(new Card("C", "Q"));

        for (String s : suit) {
            for (String value : rank) {
                // skip the card that was added to be dealt to player 1
                if (s.equals("C") && value.equals("Q")) continue;
                    // chose a random club card to be the top card
                else if (s.equals("C") && value.equals("4")) continue;

                counter++;

                Card c = new Card(s, value);
                rCard.add(c);
            }
        }

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("p4 plays 3C: assert next player is player 1")
    public void TestRow29() throws InterruptedException {
        rigTestRow29();

        browsers.get(0).findElement(By.id("startBtn")).click();

        // play cards
        browsers.get(0).findElement(By.id("3C")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        browsers.get(1).findElement(By.id("4C")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        browsers.get(2).findElement(By.id("5C")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        browsers.get(3).findElement(By.id("6C")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        // Check the player turn is 4 for all the browsers and the direction is going left
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("1"));
        }

        assert (browsers.get(0).findElement(By.id("draw")).isEnabled());

    }

    public void rigTestRow29(){
        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        ArrayList<Card> rCard = new ArrayList<>();
        int counter = 1;

        // Will be top card
        rCard.add(new Card("C", "A"));

        // Will be dealt to player 1
        rCard.add(new Card("C", "3"));

        for (String s : suit) {
            for (String value : rank) {
                if (s.equals("C") && value.equals("A")) continue;
                else if (s.equals("C") && value.equals("3")) continue;
                else if (s.equals("C") && value.equals("4")) continue;
                else if (s.equals("C") && value.equals("5")) continue;
                else if (s.equals("C") && value.equals("6")) continue;


                counter++;
                if (counter == 7){
                    rCard.add(new Card("C", "4"));
                    continue;
                }else if (counter == 12){
                    rCard.add(new Card("C", "5"));
                    continue;
                }else if (counter == 17){
                    rCard.add(new Card("C", "6"));
                    continue;
                }

                Card c = new Card(s, value);
                rCard.add(c);
            }
        }

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("p4 plays 1H: assert next player is player 3 AND interface must show now playing in opposite direction (i.e., right)" +
            "player3 plays 7H and next player is player 2")
    public void TestRow30() throws InterruptedException {
        rigTestRow30();

        browsers.get(0).findElement(By.id("startBtn")).click();

        // play cards
        browsers.get(0).findElement(By.id("3H")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        browsers.get(1).findElement(By.id("4H")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        browsers.get(2).findElement(By.id("6H")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        browsers.get(3).findElement(By.id("AH")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("3"));
            assert (browsers.get(i).findElement(By.id("direction")).getText().contains("right"));
        }

        assert (browsers.get(2).findElement(By.id("draw")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("draw")).isEnabled());

        browsers.get(2).findElement(By.id("7H")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
        }

        assert (browsers.get(1).findElement(By.id("draw")).isEnabled());

    }

    public void rigTestRow30(){
        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        ArrayList<Card> rCard = new ArrayList<>();
        int counter = 1;

        // Will be top card
        rCard.add(new Card("H", "9"));

        // Will be dealt to player 1
        rCard.add(new Card("H", "3"));

        for (String s : suit) {
            for (String value : rank) {
                if (s.equals("H") && value.equals("9")) continue;
                else if (s.equals("H") && value.equals("4")) continue;
                else if (s.equals("H") && value.equals("7")) continue;
                else if (s.equals("H") && value.equals("3")) continue;
                else if (s.equals("H") && value.equals("A")) continue;
                else if (s.equals("H") && value.equals("6")) continue;


                counter++;
                if (counter == 7){
                    rCard.add(new Card("H", "4"));
                    continue;
                }else if (counter == 12){
                    rCard.add(new Card("H", "7"));
                    counter++;
                    rCard.add(new Card("H", "6"));
                    continue;
                }else if (counter == 17){
                    rCard.add(new Card("H", "A"));
                    continue;
                }

                Card c = new Card(s, value);
                rCard.add(c);
            }
        }

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("p4 plays QC assert next player is player 2  (because player 1 loses their turn)")
    public void TestRow32() throws InterruptedException {
        rigTestRow32();

        browsers.get(0).findElement(By.id("startBtn")).click();

        // play cards
        browsers.get(0).findElement(By.id("3C")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        browsers.get(1).findElement(By.id("4C")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        browsers.get(2).findElement(By.id("5C")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        browsers.get(3).findElement(By.id("QC")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        // Check the player turn is 4 for all the browsers and the direction is going left
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
        }

        assert (browsers.get(1).findElement(By.id("draw")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("draw")).isEnabled());
    }

    public void rigTestRow32(){
        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        ArrayList<Card> rCard = new ArrayList<>();
        int counter = 1;

        // Will be top card
        rCard.add(new Card("C", "A"));

        // Will be dealt to player 1
        rCard.add(new Card("C", "3"));

        for (String s : suit) {
            for (String value : rank) {
                if (s.equals("C") && value.equals("A")) continue;
                else if (s.equals("C") && value.equals("3")) continue;
                else if (s.equals("C") && value.equals("4")) continue;
                else if (s.equals("C") && value.equals("5")) continue;
                else if (s.equals("C") && value.equals("Q")) continue;


                counter++;
                if (counter == 7){
                    rCard.add(new Card("C", "4"));
                    continue;
                }else if (counter == 12){
                    rCard.add(new Card("C", "5"));
                    continue;
                }else if (counter == 17){
                    rCard.add(new Card("C", "Q"));
                    continue;
                }

                Card c = new Card(s, value);
                rCard.add(c);
            }
        }

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("top card is KC and player1 plays KH")
    public void TestRow35() throws InterruptedException {
        rigTestRow35();

        browsers.get(0).findElement(By.id("startBtn")).click();

        // play cards
        browsers.get(0).findElement(By.id("KH")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        // Check top card
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
            assert (browsers.get(i).findElement(By.id("KH")).getAttribute("class").contains("topCard"));
        }

        assert (browsers.get(1).findElement(By.id("draw")).isEnabled());
    }

    public void rigTestRow35(){
        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        ArrayList<Card> rCard = new ArrayList<>();
        int counter = 1;

        // Will be top card
        rCard.add(new Card("C", "K"));

        // Will be dealt to player 1
        rCard.add(new Card("H", "K"));

        for (String s : suit) {
            for (String value : rank) {
                if (s.equals("H") && value.equals("K")) continue;
                else if (s.equals("C") && value.equals("K")) continue;

                counter++;

                Card c = new Card(s, value);
                rCard.add(c);
            }
        }

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("top card is KC and player1 plays 7C")
    public void TestRow36() throws InterruptedException {
        rigTestRow36();

        browsers.get(0).findElement(By.id("startBtn")).click();

        // play cards
        browsers.get(0).findElement(By.id("7C")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        // Check top card
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
            assert (browsers.get(i).findElement(By.id("7C")).getAttribute("class").contains("topCard"));
        }

        assert (browsers.get(1).findElement(By.id("draw")).isEnabled());
    }

    public void rigTestRow36(){
        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        ArrayList<Card> rCard = new ArrayList<>();
        int counter = 1;

        // Will be top card
        rCard.add(new Card("C", "K"));

        // Will be dealt to player 1
        rCard.add(new Card("C", "7"));

        for (String s : suit) {
            for (String value : rank) {
                if (s.equals("C") && value.equals("K")) continue;
                else if (s.equals("C") && value.equals("7")) continue;

                counter++;

                Card c = new Card(s, value);
                rCard.add(c);
            }
        }

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("top card is KC and player1 plays 7C")
    public void TestRow37() throws InterruptedException {
        rigTestRow37();

        browsers.get(0).findElement(By.id("startBtn")).click();

        // play cards
        browsers.get(0).findElement(By.id("8H")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        // Check top card
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("1"));
            assert (browsers.get(i).findElement(By.id("8H")).getAttribute("class").contains("topCard"));
        }

        assert (browsers.get(0).findElement(By.id("8Played")).isDisplayed());
        assert !(browsers.get(1).findElement(By.id("draw")).isEnabled());
        assert (browsers.get(0).findElement(By.id("draw")).isEnabled());

    }

    public void rigTestRow37(){
        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        ArrayList<Card> rCard = new ArrayList<>();
        int counter = 1;

        // Will be top card
        rCard.add(new Card("C", "K"));

        // Will be dealt to player 1
        rCard.add(new Card("H", "8"));

        for (String s : suit) {
            for (String value : rank) {
                if (s.equals("C") && value.equals("K")) continue;
                else if (s.equals("H") && value.equals("8")) continue;

                counter++;

                Card c = new Card(s, value);
                rCard.add(c);
            }
        }

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("top card is KC and player1 tries to play 5S and interface prohibits this card being played (disabled or message)")
    public void TestRow38() throws InterruptedException {
        rigTestRow38();

        browsers.get(0).findElement(By.id("startBtn")).click();

        // play cards
        browsers.get(0).findElement(By.id("5S")).click();
        TimeUnit.MILLISECONDS.sleep(1000);

        try {
            browsers.get(0).switchTo().alert().dismiss();
        }catch (Exception e){
            assert false;
        }

        // Check top card
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("1"));
            assert (browsers.get(i).findElement(By.id("KC")).getAttribute("class").contains("topCard"));
        }

        assert (browsers.get(0).findElement(By.id("draw")).isEnabled());
        assert !(browsers.get(1).findElement(By.id("draw")).isEnabled());
    }

    public void rigTestRow38(){
        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        ArrayList<Card> rCard = new ArrayList<>();
        int counter = 1;

        // Will be top card
        rCard.add(new Card("C", "K"));

        // Will be dealt to player 1
        rCard.add(new Card("S", "5"));

        for (String s : suit) {
            for (String value : rank) {
                if (s.equals("C") && value.equals("K")) continue;
                else if (s.equals("S") && value.equals("5")) continue;

                counter++;

                Card c = new Card(s, value);
                rCard.add(c);
            }
        }

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("top card is 7C and p1 has only {3H} as hand: must draw, draws 6C and must play it")
    public void TestRow42() throws InterruptedException {
        rigTestRow42();

        String[] p1Hand = {"4C", "7H", "9S", "5S", "3H"};
        String[] p2Hand = {"4H", "KH", "KS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "7S", "3S", "5D"};
        String[] p4Hand = {"6H", "9C", "6S", "3C", "6D"};

        ArrayList<String[]> hands = new ArrayList<>();

        hands.add(p1Hand);
        hands.add(p2Hand);
        hands.add(p3Hand);
        hands.add(p4Hand);

        browsers.get(0).findElement(By.id("startBtn")).click();

        // Check initial top card
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("AC")).getAttribute("class").contains("topCard"));
        }

        // Check players received the correct cards
        for (int i = 0; i < 5; ++i){
            assert (browsers.get(0).findElement(By.id(p1Hand[i])).isDisplayed());
            assert (browsers.get(1).findElement(By.id(p2Hand[i])).isDisplayed());
            assert (browsers.get(2).findElement(By.id(p3Hand[i])).isDisplayed());
            assert (browsers.get(3).findElement(By.id(p4Hand[i])).isDisplayed());
        }

        TimeUnit.MILLISECONDS.sleep(5000);


        // play cards until P1 only has H3 left in their hand
        browsers.get(0).findElement(By.id("4C")).click();

        // Check the new top card is updated for all players
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("4C")).getAttribute("class").contains("topCard"));
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
        }

        browsers.get(1).findElement(By.id("4H")).click();
        browsers.get(2).findElement(By.id("5H")).click();
        browsers.get(3).findElement(By.id("6H")).click();
        browsers.get(0).findElement(By.id("7H")).click();

        browsers.get(1).findElement(By.id("KH")).click();
        browsers.get(2).findElement(By.id("9H")).click();
        browsers.get(3).findElement(By.id("9C")).click();
        browsers.get(0).findElement(By.id("9S")).click();

        browsers.get(1).findElement(By.id("KS")).click();
        browsers.get(2).findElement(By.id("7S")).click();
        browsers.get(3).findElement(By.id("6S")).click();
        browsers.get(0).findElement(By.id("5S")).click();

        browsers.get(1).findElement(By.id("4S")).click();
        browsers.get(2).findElement(By.id("3S")).click();
        browsers.get(3).findElement(By.id("3C")).click();

        TimeUnit.MILLISECONDS.sleep(5000);

        // P1 draws
        browsers.get(0).findElement(By.id("draw")).click();

        // Verify 6C is drawn
        assert (browsers.get(0).findElement(By.id("6C")).isDisplayed());

        // Check other cards are disabled since 6C was drawn, only other card is 3H
        assert !(browsers.get(0).findElement(By.id("3H")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("draw")).isEnabled());

        // Play the card 6C
        browsers.get(0).findElement(By.id("6C")).click();

        // Check top card and its players 2s turn
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
            assert (browsers.get(i).findElement(By.id("6C")).getAttribute("class").contains("topCard"));
        }
    }

    public void rigTestRow42(){

        String[] p1Hand = {"4C", "7H", "9S", "5S", "3H"};
        String[] p2Hand = {"4H", "KH", "KS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "7S", "3S", "5D"};
        String[] p4Hand = {"6H", "9C", "6S", "3C", "6D"};

        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        int counter = 0;
        ArrayList<Card> rCard = new ArrayList<>();

        // Top card
        rCard.add(new Card("C", "A"));

        for (int i = 0; i < p1Hand.length; ++i) rCard.add(new Card(p1Hand[i].substring(1), p1Hand[i].substring(0,1)));
        for (int i = 0; i < p2Hand.length; ++i) rCard.add(new Card(p2Hand[i].substring(1), p2Hand[i].substring(0,1)));
        for (int i = 0; i < p3Hand.length; ++i) rCard.add(new Card(p3Hand[i].substring(1), p3Hand[i].substring(0,1)));
        for (int i = 0; i < p4Hand.length; ++i) rCard.add(new Card(p4Hand[i].substring(1), p4Hand[i].substring(0,1)));

        // Add 6C to top of draw deck
        rCard.add(new Card("C", "6"));

        boolean skip = false;

        for (String s : suit){
            for (String value : rank){
                skip = false;
                // Check if the card already exists in the rigged deck
                for (Card c : rCard){
                    if (c.getSuit().equals(s) && c.getRank().equals(value)) {
                        skip = true;
                        break;
                    }
                }

                if (skip) continue;

                Card c = new Card(s, value);
                rCard.add(c);
            }
        }

//        System.out.println(rCard.size());
//        for (Card c : rCard){
//            System.out.println(c.getSuit() + c.getRank());
//        }

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @AfterEach
    public void tearDown(){
        for (int i = 0; i < 4; ++i){
            browsers.get(i).close();
        }
    }
}
