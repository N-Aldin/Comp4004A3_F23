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

    int timeout = 50;

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
            TimeUnit.MILLISECONDS.sleep(timeout);
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
            driver.findElement(By.id("usernameBtn")).click();

            browsers.add(driver);
        }


        // RESIZE WINDOWS
        int width = 1920/2;
        int height = (1080-40)/2;

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
    public void TestRow25() throws InterruptedException {

        rigTestRow25();

        browsers.get(0).findElement(By.id("startBtn")).click();

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("4C")).getAttribute("class").contains("topCard"));
        }

        browsers.get(0).findElement(By.id("3C")).click();

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("3C")).getAttribute("class").contains("topCard"));
        }

        // Check the player turn is 2 for all the browsers and the direction is going right
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
            assert (browsers.get(i).findElement(By.id("direction")).getText().contains("left"));
        }

        assert browsers.get(1).findElement(By.id("draw")).isEnabled();

        TimeUnit.MILLISECONDS.sleep(timeout);
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
    public void TestRow26() throws InterruptedException {

        rigTestRow26();

        browsers.get(0).findElement(By.id("startBtn")).click();

        TimeUnit.MILLISECONDS.sleep(500);

        browsers.get(0).findElement(By.id("AH")).click();

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("AH")).getAttribute("class").contains("topCard"));
        }

        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check the player turn is 4 for all the browsers and the direction is going left
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("4"));
            assert (browsers.get(i).findElement(By.id("direction")).getText().contains("right"));
        }

        TimeUnit.MILLISECONDS.sleep(timeout);

        // check the players draw button is enabled
        assert browsers.get(3).findElement(By.id("draw")).isEnabled();

        browsers.get(3).findElement(By.id("7H")).click();

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("7H")).getAttribute("class").contains("topCard"));
        }

        TimeUnit.MILLISECONDS.sleep(timeout);

        // checking that player 3's draw button is enabled signifying that it is their turn
        assert browsers.get(2).findElement(By.id("draw")).isEnabled();

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

        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(0).findElement(By.id("QC")).click();

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("QC")).getAttribute("class").contains("topCard"));
        }

        TimeUnit.MILLISECONDS.sleep(timeout);

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
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("3C")).getAttribute("class").contains("topCard"));
        }

        browsers.get(1).findElement(By.id("4C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("4C")).getAttribute("class").contains("topCard"));
        }

        browsers.get(2).findElement(By.id("5C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("5C")).getAttribute("class").contains("topCard"));
        }

        browsers.get(3).findElement(By.id("6C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("6C")).getAttribute("class").contains("topCard"));
        }

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
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("3H")).getAttribute("class").contains("topCard"));
        }

        browsers.get(1).findElement(By.id("4H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("4H")).getAttribute("class").contains("topCard"));
        }

        browsers.get(2).findElement(By.id("6H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("6H")).getAttribute("class").contains("topCard"));
        }

        browsers.get(3).findElement(By.id("AH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("AH")).getAttribute("class").contains("topCard"));
        }

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("3"));
            assert (browsers.get(i).findElement(By.id("direction")).getText().contains("right"));
        }

        assert (browsers.get(2).findElement(By.id("draw")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("draw")).isEnabled());

        browsers.get(2).findElement(By.id("7H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("7H")).getAttribute("class").contains("topCard"));
        }

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
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("3C")).getAttribute("class").contains("topCard"));
        }

        browsers.get(1).findElement(By.id("4C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("4C")).getAttribute("class").contains("topCard"));
        }

        browsers.get(2).findElement(By.id("5C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("5C")).getAttribute("class").contains("topCard"));
        }

        browsers.get(3).findElement(By.id("QC")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("QC")).getAttribute("class").contains("topCard"));
        }

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

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("KC")).getAttribute("class").contains("topCard"));
        }

        // play cards
        browsers.get(0).findElement(By.id("KH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

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

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("KC")).getAttribute("class").contains("topCard"));
        }

        // play cards
        browsers.get(0).findElement(By.id("7C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

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
    @DisplayName("top card is KC and player1 plays 8H  and interface prompts for new suit")
    public void TestRow37() throws InterruptedException {
        rigTestRow37();

        browsers.get(0).findElement(By.id("startBtn")).click();

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("KC")).getAttribute("class").contains("topCard"));
        }

        // play cards
        browsers.get(0).findElement(By.id("8H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check top card
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("1"));
            assert (browsers.get(i).findElement(By.id("8H")).getAttribute("class").contains("topCard"));
        }

        assert (browsers.get(0).findElement(By.id("8Played")).isDisplayed());
        assert !(browsers.get(1).findElement(By.id("draw")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("draw")).isEnabled());

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

        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("KC")).getAttribute("class").contains("topCard"));
        }

        // play cards
        browsers.get(0).findElement(By.id("5S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

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
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"6H", "9C", "6S", "7C", "6D"};

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

        TimeUnit.MILLISECONDS.sleep(timeout);

        // play cards until P1 only has H3 left in their hand
        browsers.get(0).findElement(By.id("4C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check the new top card is updated for all players
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("4C")).getAttribute("class").contains("topCard"));
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
        }

        browsers.get(1).findElement(By.id("4H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("5H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("6H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("7H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("KH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("9H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("9C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("9S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("KS")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("3S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("6S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("5S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("4S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("7S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("7C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // P1 draws
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 6C is drawn
        assert (browsers.get(0).findElement(By.id("6C")).isDisplayed());

        // Check other cards are disabled since 6C was drawn, only other card is 3H
        assert !(browsers.get(0).findElement(By.id("3H")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("draw")).isEnabled());

        // Play the card 6C
        browsers.get(0).findElement(By.id("6C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check top card and its players 2s turn
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
            assert (browsers.get(i).findElement(By.id("6C")).getAttribute("class").contains("topCard"));
        }
    }

    public void rigTestRow42(){

        String[] p1Hand = {"4C", "7H", "9S", "5S", "3H"};
        String[] p2Hand = {"4H", "KH", "KS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"6H", "9C", "6S", "7C", "6D"};

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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("top card is 7C and p1 has {3H} as hand: must draw, draws 6D then 5C and must play it")
    public void TestRow43() throws InterruptedException {
        rigTestRow43();

        String[] p1Hand = {"4C", "7H", "9S", "5S", "3H"};
        String[] p2Hand = {"4H", "KH", "KS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"6H", "9C", "6S", "7C", "KD"};

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
        TimeUnit.MILLISECONDS.sleep(timeout);

        // play cards until P1 only has H3 left in their hand
        browsers.get(0).findElement(By.id("4C")).click();

        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check the new top card is updated for all players
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("4C")).getAttribute("class").contains("topCard"));
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
        }

        browsers.get(1).findElement(By.id("4H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("5H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("6H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("7H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("KH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("9H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("9C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("9S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("KS")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("3S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("6S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("5S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("4S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("7S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("7C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // P1 draws
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 6D is drawn
        assert (browsers.get(0).findElement(By.id("6D")).isDisplayed());
        assert (browsers.get(0).findElement(By.id("draw")).isEnabled());

        // Draw again for 5C
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 5C is drawn
        assert (browsers.get(0).findElement(By.id("5C")).isDisplayed());

        // Check other cards and draw button are disabled
        assert !(browsers.get(0).findElement(By.id("3H")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("6D")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("draw")).isEnabled());

        // Play the card 5C
        browsers.get(0).findElement(By.id("5C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check top card and its players 2s turn
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
            assert (browsers.get(i).findElement(By.id("5C")).getAttribute("class").contains("topCard"));
        }
    }

    public void rigTestRow43(){

        String[] p1Hand = {"4C", "7H", "9S", "5S", "3H"};
        String[] p2Hand = {"4H", "KH", "KS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"6H", "9C", "6S", "7C", "KD"};

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

        // Add 6D and 5C to top of draw deck
        rCard.add(new Card("D", "6"));
        rCard.add(new Card("C", "5"));

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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("top card is 7C and p1 has {3H} as hand: must draw, draws 6D, 5S then 7H and must play it")
    public void TestRow44() throws InterruptedException {
        rigTestRow44();

        String[] p1Hand = {"4C", "KD", "9S", "TS", "3H"};
        String[] p2Hand = {"4H", "KH", "KS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"5D", "9C", "6S", "7C", "KD"};

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

        // play cards until P1 only has H3 left in their hand
        browsers.get(0).findElement(By.id("4C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check the new top card is updated for all players
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("4C")).getAttribute("class").contains("topCard"));
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
        }

        browsers.get(1).findElement(By.id("4H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("5H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("5D")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("KD")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("KH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("9H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("9C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("9S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("KS")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("3S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("6S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("TS")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("4S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("7S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("7C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // P1 draws
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 6D is drawn
        assert (browsers.get(0).findElement(By.id("6D")).isDisplayed());
        assert (browsers.get(0).findElement(By.id("draw")).isEnabled());

        // Draw again for 5S
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 5S is drawn
        assert (browsers.get(0).findElement(By.id("5S")).isDisplayed());

        // Draw again for 7H
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 7H is drawn
        assert (browsers.get(0).findElement(By.id("7H")).isDisplayed());

        // Check other cards and draw button are disabled
        assert !(browsers.get(0).findElement(By.id("3H")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("6D")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("5S")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("draw")).isEnabled());

        // Play the card 7H
        browsers.get(0).findElement(By.id("7H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check top card and its players 2s turn
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
            assert (browsers.get(i).findElement(By.id("7H")).getAttribute("class").contains("topCard"));
        }
    }

    public void rigTestRow44(){

        String[] p1Hand = {"4C", "KD", "9S", "TS", "3H"};
        String[] p2Hand = {"4H", "KH", "KS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"5D", "9C", "6S", "7C", "KD"};

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

        // Add 6D and 5C to top of draw deck
        rCard.add(new Card("D", "6"));
        rCard.add(new Card("S", "5"));
        rCard.add(new Card("H", "7"));

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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("top card is 7C and p1 has {3H} as hand: must draw, draws 6D, 5S, 4H; still can't play: turn ends (ie max 3 cards drawn)")
    public void TestRow45() throws InterruptedException {
        rigTestRow45();

        String[] p1Hand = {"TC", "KD", "9S", "TS", "3H"};
        String[] p2Hand = {"TH", "KH", "KS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"5D", "9C", "6S", "7C", "KD"};

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

        // play cards until P1 only has H3 left in their hand
        browsers.get(0).findElement(By.id("TC")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check the new top card is updated for all players
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("TC")).getAttribute("class").contains("topCard"));
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
        }

        browsers.get(1).findElement(By.id("TH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("5H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("5D")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("KD")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("KH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("9H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("9C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("9S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("KS")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("3S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("6S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("TS")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("4S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("7S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("7C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // P1 draws
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 6D is drawn
        assert (browsers.get(0).findElement(By.id("6D")).isDisplayed());
        assert (browsers.get(0).findElement(By.id("draw")).isEnabled());

        // Draw again for 5S
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 5S is drawn
        assert (browsers.get(0).findElement(By.id("5S")).isDisplayed());

        // Draw again for 4D
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 4H is drawn
        assert (browsers.get(0).findElement(By.id("4H")).isDisplayed());

        // Check top card and its players 2s turn
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
            assert (browsers.get(i).findElement(By.id("7C")).getAttribute("class").contains("topCard"));
        }
    }

    public void rigTestRow45(){

        String[] p1Hand = {"TC", "KD", "9S", "TS", "3H"};
        String[] p2Hand = {"TH", "KH", "KS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"5D", "9C", "6S", "7C", "KD"};

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

        // Add 6D and 5C to top of draw deck
        rCard.add(new Card("D", "6"));
        rCard.add(new Card("S", "5"));
        rCard.add(new Card("H", "4"));

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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("top card is 7C and p1 has {3H} as hand: must draw, draws 6D then 8H; must play 8H and declare new suit")
    public void TestRow46() throws InterruptedException {
        rigTestRow46();

        String[] p1Hand = {"TC", "KD", "9S", "TS", "3H"};
        String[] p2Hand = {"TH", "KH", "KS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"5D", "9C", "6S", "7C", "KD"};

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

        // play cards until P1 only has H3 left in their hand
        browsers.get(0).findElement(By.id("TC")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check the new top card is updated for all players
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("TC")).getAttribute("class").contains("topCard"));
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
        }

        browsers.get(1).findElement(By.id("TH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("5H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("5D")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("KD")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("KH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("9H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("9C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("9S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("KS")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("3S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("6S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("TS")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("4S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("7S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("7C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // P1 draws
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 6D is drawn
        assert (browsers.get(0).findElement(By.id("6D")).isDisplayed());
        assert (browsers.get(0).findElement(By.id("draw")).isEnabled());

        // Draw again for 8H
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 8H is drawn
        assert (browsers.get(0).findElement(By.id("8H")).isDisplayed());
        assert !(browsers.get(0).findElement(By.id("3H")).isEnabled());

        // Check other cards are disabled
        assert !(browsers.get(0).findElement(By.id("3H")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("6D")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("draw")).isEnabled());

        // Play 8H
        browsers.get(0).findElement(By.id("8H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Choose a suit
        browsers.get(0).findElement(By.id("club")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check top card and its players 2s turn
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
            assert (browsers.get(i).findElement(By.id("C")).getAttribute("class").contains("topCard"));
        }
    }

    public void rigTestRow46(){

        String[] p1Hand = {"TC", "KD", "9S", "TS", "3H"};
        String[] p2Hand = {"TH", "KH", "KS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"5D", "9C", "6S", "7C", "KD"};

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

        // Add 6D and 5C to top of draw deck
        rCard.add(new Card("D", "6"));
        rCard.add(new Card("H", "8"));

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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("top card is 7C and p1 has {KS, 3C} as hand: chooses to draw, draws 6C and must play 6C")
    public void TestRow47() throws InterruptedException {
        rigTestRow47();

        String[] p1Hand = {"TC", "KD", "9S", "KS", "3C"};
        String[] p2Hand = {"TH", "KH", "TS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"5D", "9C", "6S", "7C", "KD"};

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

        // play cards until P1 only has H3 left in their hand
        browsers.get(0).findElement(By.id("TC")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);


        // Check the new top card is updated for all players
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("TC")).getAttribute("class").contains("topCard"));
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
        }

        browsers.get(1).findElement(By.id("TH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("5H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("5D")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("KD")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("KH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("9H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("9C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("9S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("TS")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("3S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("6S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // P1 chooses to draw
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 6C is drawn
        assert (browsers.get(0).findElement(By.id("6C")).isDisplayed());

        // Check other cards are disabled
        assert !(browsers.get(0).findElement(By.id("KS")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("3C")).isEnabled());
        assert !(browsers.get(0).findElement(By.id("draw")).isEnabled());

        // play 6C
        browsers.get(0).findElement(By.id("6C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check top card and its players 2s turn
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("2"));
            assert (browsers.get(i).findElement(By.id("6C")).getAttribute("class").contains("topCard"));
        }
    }

    public void rigTestRow47(){

        String[] p1Hand = {"TC", "KD", "9S", "KS", "3C"};
        String[] p2Hand = {"TH", "KH", "TS", "4S", "4D"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"5D", "9C", "6S", "7C", "KD"};

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

        // Add 6D and 5C to top of draw deck
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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("p1 plays 2C, p2 has only  {4H} thus must draw 2 cards {6C and 9D} then plays 6C")
    public void TestRow51() throws InterruptedException {
        rigTestRow51();

        String[] p1Hand = {"3C", "KD", "9S", "KS", "2C"};
        String[] p2Hand = {"TH", "KH", "TS", "4S", "4H"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"5D", "9C", "6S", "7C", "KD"};

        browsers.get(0).findElement(By.id("startBtn")).click();

        ArrayList<String[]> cards = new ArrayList<>();

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

        cards.add(p1Hand);
        cards.add(p2Hand);
        cards.add(p3Hand);
        cards.add(p4Hand);

        // P1 draws
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // picks up TC and plays it
        browsers.get(0).findElement(By.id("TC")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("TC")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("TH")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("TH")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(2).findElement(By.id("5H")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("5H")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(3).findElement(By.id("5D")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("5D")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        int counter = 0;

        for (int i = 1; i < 4; ++i){
            for (WebDriver d : browsers){
                d.findElement(By.id(cards.get(counter)[i])).click();

                // Check the new top card is updated for all players
                for (int j = 0; j < 4; ++j)
                    assert (browsers.get(j).findElement(By.id(cards.get(counter)[i])).getAttribute("class").contains("topCard"));

                counter++;
                TimeUnit.MILLISECONDS.sleep(timeout);
            }
            counter = 0;
        }

        // P1 plays 2C
        browsers.get(0).findElement(By.id("2C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("2C")).getAttribute("class").contains("topCard"));

        // Verify P2 received the correct cards
        assert browsers.get(1).findElement(By.id("6C")).isDisplayed();
        assert browsers.get(1).findElement(By.id("9D")).isDisplayed();

        // P2 plays 6C
        browsers.get(1).findElement(By.id("6C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check top card and its players 3s turn
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("3"));
            assert (browsers.get(i).findElement(By.id("6C")).getAttribute("class").contains("topCard"));
        }
    }

    public void rigTestRow51(){
        String[] p1Hand = {"3C", "KD", "9S", "KS", "2C"};
        String[] p2Hand = {"TH", "KH", "TS", "4S", "4H"};
        String[] p3Hand = {"5H", "9H", "3S", "7S", "5D"};
        String[] p4Hand = {"5D", "9C", "6S", "7C", "KD"};

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

        // Add 6D and 5C to top of draw deck
        rCard.add(new Card("C", "T"));
        rCard.add(new Card("C", "6"));
        rCard.add(new Card("D", "9"));

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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("p1 plays 2C, p2 has only {4H}, draws {6S and 9D}, still can't play, then draws 9H then 6C and then must play 6C")
    public void TestRow52() throws InterruptedException {
        rigTestRow52();

        String[] p1Hand = {"3C", "KD", "TD", "KS", "2C"};
        String[] p2Hand = {"TH", "4D", "TS", "4S", "4H"};
        String[] p3Hand = {"5H", "6D", "3S", "7S", "5D"};
        String[] p4Hand = {"5D", "7D", "5S", "7C", "AD"};

        browsers.get(0).findElement(By.id("startBtn")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        ArrayList<String[]> cards = new ArrayList<>();

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

        cards.add(p1Hand);
        cards.add(p2Hand);
        cards.add(p3Hand);
        cards.add(p4Hand);

        // P1 draws
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // picks up TC and plays it
        browsers.get(0).findElement(By.id("TC")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("TC")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("TH")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("TH")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(2).findElement(By.id("5H")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("5H")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(3).findElement(By.id("5D")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("5D")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        int counter = 0;

        for (int i = 1; i < 4; ++i){
            for (WebDriver d : browsers){
                d.findElement(By.id(cards.get(counter)[i])).click();

                // Check the new top card is updated for all players
                for (int j = 0; j < 4; ++j)
                    assert (browsers.get(j).findElement(By.id(cards.get(counter)[i])).getAttribute("class").contains("topCard"));

                counter++;
                TimeUnit.MILLISECONDS.sleep(timeout);
            }
            counter = 0;
        }

        // P1 plays 2C
        browsers.get(0).findElement(By.id("2C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("2C")).getAttribute("class").contains("topCard"));

        // Verify P2 received the correct cards
        assert browsers.get(1).findElement(By.id("6S")).isDisplayed();
        assert browsers.get(1).findElement(By.id("9D")).isDisplayed();

        // Try playing a invalid card
        browsers.get(1).findElement(By.id("6S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check if the alert message pops up
        try {
            browsers.get(1).switchTo().alert().dismiss();
        }catch (Exception e){
            assert false;
        }

        // P2 draws 2 more cards
        browsers.get(1).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 9H and 6C are drawn
        assert browsers.get(1).findElement(By.id("6C")).isDisplayed();
        assert browsers.get(1).findElement(By.id("9H")).isDisplayed();

        // Verify all other cards disabled
        assert !(browsers.get(1).findElement(By.id("6S")).isEnabled());
        assert !(browsers.get(1).findElement(By.id("9D")).isEnabled());
        assert !(browsers.get(1).findElement(By.id("4H")).isEnabled());
        assert !(browsers.get(1).findElement(By.id("9H")).isEnabled());
        assert !(browsers.get(1).findElement(By.id("draw")).isEnabled());

        // P2 plays 6C
        browsers.get(1).findElement(By.id("6C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check top card and its players 3s turn
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("3"));
            assert (browsers.get(i).findElement(By.id("6C")).getAttribute("class").contains("topCard"));
        }
    }

    public void rigTestRow52(){
        String[] p1Hand = {"3C", "KD", "TD", "KS", "2C"};
        String[] p2Hand = {"TH", "4D", "TS", "4S", "4H"};
        String[] p3Hand = {"5H", "6D", "3S", "7S", "5D"};
        String[] p4Hand = {"5D", "7D", "5S", "7C", "AD"};

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

        // Add 6D and 5C to top of draw deck
        rCard.add(new Card("C", "T"));
        rCard.add(new Card("S", "6"));
        rCard.add(new Card("D", "9"));
        rCard.add(new Card("H", "9"));
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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("p1 plays 2C, p2 has only {4H} draws {6S and 9D} then draws 9H, 7S, 5H and then  turns end (without playing a card)")
    public void TestRow53() throws InterruptedException {
        rigTestRow53();

        String[] p1Hand = {"3C", "3H", "TD", "KS", "2C"};
        String[] p2Hand = {"TH", "3D", "TS", "KC", "4H"};
        String[] p3Hand = {"7H", "6D", "3S", "4C", "5D"};
        String[] p4Hand = {"6H", "7D", "5S", "7C", "AD"};

        browsers.get(0).findElement(By.id("startBtn")).click();

        ArrayList<String[]> cards = new ArrayList<>();

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

        cards.add(p1Hand);
        cards.add(p2Hand);
        cards.add(p3Hand);
        cards.add(p4Hand);

        // P1 draws
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // picks up TC and plays it
        browsers.get(0).findElement(By.id("TC")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("TC")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("TH")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("TH")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(2).findElement(By.id("7H")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("7H")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(3).findElement(By.id("6H")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("6H")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        int counter = 0;

        for (int i = 1; i < 4; ++i){
            for (WebDriver d : browsers){
                d.findElement(By.id(cards.get(counter)[i])).click();

                // Check the new top card is updated for all players
                for (int j = 0; j < 4; ++j)
                    assert (browsers.get(j).findElement(By.id(cards.get(counter)[i])).getAttribute("class").contains("topCard"));

                counter++;
                TimeUnit.MILLISECONDS.sleep(timeout);
            }
            counter = 0;
        }

        // P1 plays 2C
        browsers.get(0).findElement(By.id("2C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("2C")).getAttribute("class").contains("topCard"));

        // Verify P2 received the correct cards
        assert browsers.get(1).findElement(By.id("6S")).isDisplayed();
        assert browsers.get(1).findElement(By.id("9D")).isDisplayed();

        // P2 draws 2 more cards
        browsers.get(1).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Verify 9H and 7S and 5H are drawn
        assert browsers.get(1).findElement(By.id("7S")).isDisplayed();
        assert browsers.get(1).findElement(By.id("9H")).isDisplayed();
        assert browsers.get(1).findElement(By.id("5H")).isDisplayed();

        // Check top card and its players 3s turn
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("3"));
            assert (browsers.get(i).findElement(By.id("2C")).getAttribute("class").contains("topCard"));
        }
    }

    public void rigTestRow53(){
        String[] p1Hand = {"3C", "3H", "TD", "KS", "2C"};
        String[] p2Hand = {"TH", "3D", "TS", "KC", "4H"};
        String[] p3Hand = {"7H", "6D", "3S", "4C", "5D"};
        String[] p4Hand = {"6H", "7D", "5S", "7C", "AD"};

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

        // Add 6D and 5C to top of draw deck
        rCard.add(new Card("C", "T"));
        rCard.add(new Card("S", "6"));
        rCard.add(new Card("D", "9"));
        rCard.add(new Card("H", "9"));
        rCard.add(new Card("S", "7"));
        rCard.add(new Card("H", "5"));

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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("p1 plays 2C, p2 has {4H} draws {2H and 9D} then plays 2H (forcing next player to immediately play or draw 4 cards)" +
            "then p3 having only {7D} p3 draws {5S, 6D, 6H and 7C} and then  plays 6H")
    public void TestRow54() throws InterruptedException {
        rigTestRow54();

        String[] p1Hand = {"3C", "3H", "9S", "KS", "2C"};
        String[] p2Hand = {"TH", "3C", "TS", "KC", "4H"};
        String[] p3Hand = {"7H", "6C", "3S", "4C", "7D"};
        String[] p4Hand = {"5H", "9C", "6S", "5C", "AD"};

        browsers.get(0).findElement(By.id("startBtn")).click();

        ArrayList<String[]> cards = new ArrayList<>();

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

        cards.add(p1Hand);
        cards.add(p2Hand);
        cards.add(p3Hand);
        cards.add(p4Hand);

        // P1 draws
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // picks up TC and plays it
        browsers.get(0).findElement(By.id("TC")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("TC")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("TH")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("TH")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(2).findElement(By.id("7H")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("7H")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(3).findElement(By.id("5H")).click();
        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("5H")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        int counter = 0;

        for (int i = 1; i < 4; ++i){
            for (WebDriver d : browsers){
                d.findElement(By.id(cards.get(counter)[i])).click();

                // Check the new top card is updated for all players
                for (int j = 0; j < 4; ++j)
                    assert (browsers.get(j).findElement(By.id(cards.get(counter)[i])).getAttribute("class").contains("topCard"));

                counter++;
                TimeUnit.MILLISECONDS.sleep(timeout);
            }
            counter = 0;
        }

        // P1 plays 2C
        browsers.get(0).findElement(By.id("2C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("2C")).getAttribute("class").contains("topCard"));

        // Verify P2 received the correct cards
        assert browsers.get(1).findElement(By.id("2H")).isDisplayed();
        assert browsers.get(1).findElement(By.id("9D")).isDisplayed();

        // P2 plays 2H
        browsers.get(1).findElement(By.id("2H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("2H")).getAttribute("class").contains("topCard"));

        // Verify 5S and 6D and 6H and 7C are drawn
        assert browsers.get(2).findElement(By.id("5S")).isDisplayed();
        assert browsers.get(2).findElement(By.id("6D")).isDisplayed();
        assert browsers.get(2).findElement(By.id("6H")).isDisplayed();
        assert browsers.get(2).findElement(By.id("7C")).isDisplayed();

        // P3 plays 6H
        browsers.get(2).findElement(By.id("6H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check top card and its players 3s turn
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("turnID")).getText().contains("4"));
            assert (browsers.get(i).findElement(By.id("6H")).getAttribute("class").contains("topCard"));
        }
    }

    public void rigTestRow54(){
        String[] p1Hand = {"3C", "3H", "9S", "KS", "2C"};
        String[] p2Hand = {"TH", "3C", "TS", "KC", "4H"};
        String[] p3Hand = {"7H", "6C", "3S", "4C", "7D"};
        String[] p4Hand = {"5H", "9C", "6S", "5C", "AD"};

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

        // Add 6D and 5C to top of draw deck
        rCard.add(new Card("C", "T"));
        rCard.add(new Card("H", "2"));
        rCard.add(new Card("D", "9"));
        rCard.add(new Card("S", "5"));
        rCard.add(new Card("D", "6"));
        rCard.add(new Card("H", "6"));
        rCard.add(new Card("C", "7"));

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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }


    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("p1 plays 2C, p2 has {4C, 4S} then p2 plays 4C and 4S and ends round (because p2 played all their cards)")
    public void TestRow57() throws InterruptedException {
        rigTestRow57();

        String[] p1Hand = {"5D", "7S", "KC", "2C", "3H"};
        String[] p2Hand = {"5H", "9S", "TC", "4C", "4S"};
        String[] p3Hand = {"5S", "TS", "9C", "AC", "7D"};
        String[] p4Hand = {"6S", "KS", "7C", "AH", "KD"};

        browsers.get(0).findElement(By.id("startBtn")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        ArrayList<String[]> cards = new ArrayList<>();

        // Check initial top card
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("AD")).getAttribute("class").contains("topCard"));
        }

        // Check players received the correct cards
        for (int i = 0; i < 5; ++i){
            assert (browsers.get(0).findElement(By.id(p1Hand[i])).isDisplayed());
            assert (browsers.get(1).findElement(By.id(p2Hand[i])).isDisplayed());
            assert (browsers.get(2).findElement(By.id(p3Hand[i])).isDisplayed());
            assert (browsers.get(3).findElement(By.id(p4Hand[i])).isDisplayed());
        }

        cards.add(p1Hand);
        cards.add(p2Hand);
        cards.add(p3Hand);
        cards.add(p4Hand);

        int counter = 0;

        for (int i = 0; i < 3; ++i){
            for (WebDriver d : browsers){
                d.findElement(By.id(cards.get(counter)[i])).click();

                // Check the new top card is updated for all players
                for (int j = 0; j < 4; ++j)
                    assert (browsers.get(j).findElement(By.id(cards.get(counter)[i])).getAttribute("class").contains("topCard"));

                counter++;
                TimeUnit.MILLISECONDS.sleep(timeout);
            }
            counter = 0;
        }

        // P1 plays 2C
        browsers.get(0).findElement(By.id("2C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("2C")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        // P2 plays 4C and 4S
        browsers.get(1).findElement(By.id("4C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        for (int i = 0; i < 4; ++i)
            assert (browsers.get(i).findElement(By.id("4C")).getAttribute("class").contains("topCard"));
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(1).findElement(By.id("4S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
    }

    public void rigTestRow57(){
        String[] p1Hand = {"5D", "7S", "KC", "2C", "3H"};
        String[] p2Hand = {"5H", "9S", "TC", "4C", "4S"};
        String[] p3Hand = {"5S", "TS", "9C", "AC", "7D"};
        String[] p4Hand = {"6S", "KS", "7C", "AH", "KD"};

        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        int counter = 0;
        ArrayList<Card> rCard = new ArrayList<>();

        // Top card
        rCard.add(new Card("D", "A"));

        for (int i = 0; i < p1Hand.length; ++i) rCard.add(new Card(p1Hand[i].substring(1), p1Hand[i].substring(0,1)));
        for (int i = 0; i < p2Hand.length; ++i) rCard.add(new Card(p2Hand[i].substring(1), p2Hand[i].substring(0,1)));
        for (int i = 0; i < p3Hand.length; ++i) rCard.add(new Card(p3Hand[i].substring(1), p3Hand[i].substring(0,1)));
        for (int i = 0; i < p4Hand.length; ++i) rCard.add(new Card(p4Hand[i].substring(1), p4Hand[i].substring(0,1)));

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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("first round ends with p1 with {1S}, p2 with no cards, p3 with {8H, JH, 6H, KH, KS}, p4 with {8C, 8D, 2D}" +
            " then game is over with p1 scores 1, p2 scores 0, p3 scores 86 and p4 scores 102")
    public void TestRow62() throws InterruptedException {
        rigTestRow62();

        String[] p1Hand = {"JC", "TC", "3H", "5H", "AS"};
        String[] p2Hand = {"4C", "QC", "QH", "8H", "4S"}; // CHANGE TO SPADE
        String[] p3Hand = {"8H", "JH", "6H", "KH", "KS"};
        String[] p4Hand = {"9C", "8S", "8C", "8D", "2D"}; // 8H CHANGE TO HEART AND SKIP P3

        browsers.get(0).findElement(By.id("startBtn")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        ArrayList<String[]> cards = new ArrayList<>();

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

        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("3C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("4C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("5C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("9C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(0).findElement(By.id("TC")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("QC")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("8S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("heart")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(0).findElement(By.id("3H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("QH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("4H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(0).findElement(By.id("5H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("8H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("spade")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("5S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("QS")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Assert the cards in correct
        // p1
        assert browsers.get(0).findElement(By.id("AS")).isDisplayed();

        // p2 only has the one card remaining before it is played
        assert browsers.get(1).findElement(By.id("4S")).isDisplayed();

        // p3
        assert browsers.get(2).findElement(By.id("8H")).isDisplayed();
        assert browsers.get(2).findElement(By.id("JH")).isDisplayed();
        assert browsers.get(2).findElement(By.id("6H")).isDisplayed();
        assert browsers.get(2).findElement(By.id("KH")).isDisplayed();
        assert browsers.get(2).findElement(By.id("KS")).isDisplayed();

        // p4
        assert browsers.get(3).findElement(By.id("8C")).isDisplayed();
        assert browsers.get(3).findElement(By.id("8D")).isDisplayed();
        assert browsers.get(3).findElement(By.id("2D")).isDisplayed();

        browsers.get(1).findElement(By.id("4S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Assert winner and scores for every player
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("winMSG")).getText().contains("WINNER IS PLAYER 2"));
            assert (browsers.get(i).findElement(By.id("p1")).getText().contains("11"));
            assert (browsers.get(i).findElement(By.id("p2")).getText().contains("0"));
            assert (browsers.get(i).findElement(By.id("p3")).getText().contains("86"));
            assert (browsers.get(i).findElement(By.id("p4")).getText().contains("102"));
        }
        TimeUnit.MILLISECONDS.sleep(timeout);
    }

    public void rigTestRow62(){
        String[] p1Hand = {"JC", "TC", "3H", "5H", "AS"};
        String[] p2Hand = {"4C", "QC", "QH", "8H", "4S"}; // CHANGE TO SPADE
        String[] p3Hand = {"8H", "JH", "6H", "KH", "KS"};
        String[] p4Hand = {"9C", "8S", "8C", "8D", "2D"}; // 8H CHANGE TO HEART AND SKIP P3

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

        // Add 6D and 5C to top of draw deck
        rCard.add(new Card("C", "3")); // P1 chooses to draw and has to play
        rCard.add(new Card("C", "5")); // P3 has to draw and plays
        rCard.add(new Card("H", "4")); // P4
        rCard.add(new Card("S", "5"));
        rCard.add(new Card("S", "Q")); // P4

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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("testing a complete game")
    public void TestRow64() throws InterruptedException {

        rigTestRow64R1();

        String[] p1Hand = {"4H", "7S", "5D", "6D", "9D"};
        String[] p2Hand = {"4S", "6S", "KC", "8H", "TD"};
        String[] p3Hand = {"9S", "6C", "9C", "JD", "3H"};
        String[] p4Hand = {"7D", "JH", "QH", "KH", "5C"};

        browsers.get(0).findElement(By.id("startBtn")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        ArrayList<String[]> cards = new ArrayList<>();

        // Check initial top card
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("4D")).getAttribute("class").contains("topCard"));
        }

        // Check players received the correct cards
        for (int i = 0; i < 5; ++i){
            assert (browsers.get(0).findElement(By.id(p1Hand[i])).isDisplayed());
            assert (browsers.get(1).findElement(By.id(p2Hand[i])).isDisplayed());
            assert (browsers.get(2).findElement(By.id(p3Hand[i])).isDisplayed());
            assert (browsers.get(3).findElement(By.id(p4Hand[i])).isDisplayed());
        }

        browsers.get(0).findElement(By.id("4H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("4S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("9S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(0).findElement(By.id("7S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("6S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("6C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("2C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(0).findElement(By.id("JC")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("KC")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("9C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("3C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("7C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("8H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("diamond")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("JD")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("7D")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(0).findElement(By.id("9D")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("TD")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Assert winner and scores for every player
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("p1")).getText().contains("21"));
            assert (browsers.get(i).findElement(By.id("p2")).getText().contains("0"));
            assert (browsers.get(i).findElement(By.id("p3")).getText().contains("3"));
            assert (browsers.get(i).findElement(By.id("p4")).getText().contains("39"));
        }

        rigTestRow64R2();

        String[] p1Handr2 = {"7D", "4S", "7C", "4H", "5D"};
        String[] p2Handr2 = {"9D", "3S", "9C", "3H", "JC"};
        String[] p3Handr2 = {"3D", "9S", "3C", "9H", "5H"};
        String[] p4Handr2 = {"4D", "7S", "4C", "5S", "8D"};

        browsers.get(1).findElement(By.id("startBtn")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Check initial top card
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("TD")).getAttribute("class").contains("topCard"));
        }

        // Check players received the correct cards
        for (int i = 0; i < 5; ++i){
            assert (browsers.get(0).findElement(By.id(p1Handr2[i])).isDisplayed());
            assert (browsers.get(1).findElement(By.id(p2Handr2[i])).isDisplayed());
            assert (browsers.get(2).findElement(By.id(p3Handr2[i])).isDisplayed());
            assert (browsers.get(3).findElement(By.id(p4Handr2[i])).isDisplayed());
        }

        browsers.get(1).findElement(By.id("9D")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("3D")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("4D")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(0).findElement(By.id("4S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("3S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("9S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("7S")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(0).findElement(By.id("7C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("9C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("3C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("4C")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(0).findElement(By.id("4H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("3H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(2).findElement(By.id("9H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(3).findElement(By.id("KH")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(0).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);
        browsers.get(1).findElement(By.id("draw")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Assert the cards in correct
        // p1
        assert browsers.get(0).findElement(By.id("5D")).isDisplayed();
        assert browsers.get(0).findElement(By.id("6D")).isDisplayed();
        assert browsers.get(0).findElement(By.id("7D")).isDisplayed();
        assert browsers.get(0).findElement(By.id("JD")).isDisplayed();
        assert browsers.get(0).findElement(By.id("QD")).isDisplayed();

        // p2
        assert browsers.get(1).findElement(By.id("JC")).isDisplayed();
        assert browsers.get(1).findElement(By.id("6S")).isDisplayed();
        assert browsers.get(1).findElement(By.id("TS")).isDisplayed();
        assert browsers.get(1).findElement(By.id("JS")).isDisplayed();

        // p3 only has the one card remaining before it is played
        assert browsers.get(2).findElement(By.id("5H")).isDisplayed();

        // p4
        assert browsers.get(3).findElement(By.id("5S")).isDisplayed();
        assert browsers.get(3).findElement(By.id("8D")).isDisplayed();
        assert browsers.get(3).findElement(By.id("KS")).isDisplayed();
        assert browsers.get(3).findElement(By.id("QS")).isDisplayed();

        browsers.get(2).findElement(By.id("5H")).click();
        TimeUnit.MILLISECONDS.sleep(timeout);

        // Assert winner and scores for every player
        for (int i = 0; i < 4; ++i){
            assert (browsers.get(i).findElement(By.id("winMSG")).getText().contains("WINNER IS PLAYER 3"));
            assert (browsers.get(i).findElement(By.id("p1")).getText().contains("59"));
            assert (browsers.get(i).findElement(By.id("p2")).getText().contains("36"));
            assert (browsers.get(i).findElement(By.id("p3")).getText().contains("3"));
            assert (browsers.get(i).findElement(By.id("p4")).getText().contains("114"));
        }
        TimeUnit.MILLISECONDS.sleep(timeout);
    }

    public void rigTestRow64R1(){
        String[] p1Hand = {"4H", "7S", "5D", "6D", "9D"};
        String[] p2Hand = {"4S", "6S", "KC", "8H", "TD"};
        String[] p3Hand = {"9S", "6C", "9C", "JD", "3H"};
        String[] p4Hand = {"7D", "JH", "QH", "KH", "5C"};

        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        int counter = 0;
        ArrayList<Card> rCard = new ArrayList<>();

        // Top card
        rCard.add(new Card("D", "4"));

        for (int i = 0; i < p1Hand.length; ++i) rCard.add(new Card(p1Hand[i].substring(1), p1Hand[i].substring(0,1)));
        for (int i = 0; i < p2Hand.length; ++i) rCard.add(new Card(p2Hand[i].substring(1), p2Hand[i].substring(0,1)));
        for (int i = 0; i < p3Hand.length; ++i) rCard.add(new Card(p3Hand[i].substring(1), p3Hand[i].substring(0,1)));
        for (int i = 0; i < p4Hand.length; ++i) rCard.add(new Card(p4Hand[i].substring(1), p4Hand[i].substring(0,1)));

        // Add 6D and 5C to top of draw deck
        rCard.add(new Card("C", "2"));
        rCard.add(new Card("C", "3"));
        rCard.add(new Card("C", "4"));
        rCard.add(new Card("C", "T"));
        rCard.add(new Card("C", "J"));
        rCard.add(new Card("C", "7"));

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

        gd.setCards(rCard);
        gd.setTopCard(game.startGame(gd.getCards(), gd.getPlayers()));
    }

    public void rigTestRow64R2(){
        String[] p1Hand = {"7D", "4S", "7C", "4H", "5D"};
        String[] p2Hand = {"9D", "3S", "9C", "3H", "JC"};
        String[] p3Hand = {"3D", "9S", "3C", "9H", "5H"};
        String[] p4Hand = {"4D", "7S", "4C", "5S", "8D"};

        String [] suit = {"S","C","D","H"};
        String [] rank = {"A","2","3","4","5","6","7","8","9","T","J","Q","K"};

        int counter = 0;
        ArrayList<Card> rCard = new ArrayList<>();

        // Top card
        rCard.add(new Card("D", "T"));

        for (int i = 0; i < p1Hand.length; ++i) rCard.add(new Card(p1Hand[i].substring(1), p1Hand[i].substring(0,1)));
        for (int i = 0; i < p2Hand.length; ++i) rCard.add(new Card(p2Hand[i].substring(1), p2Hand[i].substring(0,1)));
        for (int i = 0; i < p3Hand.length; ++i) rCard.add(new Card(p3Hand[i].substring(1), p3Hand[i].substring(0,1)));
        for (int i = 0; i < p4Hand.length; ++i) rCard.add(new Card(p4Hand[i].substring(1), p4Hand[i].substring(0,1)));

        // Add 6D and 5C to top of draw deck
        rCard.add(new Card("S", "K"));
        rCard.add(new Card("S", "Q"));
        rCard.add(new Card("H", "K"));
        rCard.add(new Card("D", "6"));
        rCard.add(new Card("D", "Q"));
        rCard.add(new Card("D", "J"));
        rCard.add(new Card("S", "6"));
        rCard.add(new Card("S", "J"));
        rCard.add(new Card("S", "T"));


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
