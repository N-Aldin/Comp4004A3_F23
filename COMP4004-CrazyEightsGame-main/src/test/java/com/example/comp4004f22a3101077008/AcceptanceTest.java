package com.example.comp4004f22a3101077008;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
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

    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    @DisplayName("p1 plays 3C: assert next player is player 2")
    public void TestRow25(){

        rigTestRow25();

        browsers.get(0).findElement(By.id("startBtn")).click();

//        try {
//            TimeUnit.MILLISECONDS.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

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

//        System.out.println(rCard.size());
//        for (int i = 0; i < rCard.size(); ++i){
//            System.out.println(rCard.get(i).getSuit() + rCard.get(i).getRank());
//        }

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

//        System.out.println(rCard.size());
//        for (int i = 0; i < rCard.size(); ++i){
//            System.out.println(rCard.get(i).getSuit() + rCard.get(i).getRank());
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
