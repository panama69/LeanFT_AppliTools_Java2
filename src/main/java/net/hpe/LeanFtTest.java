package net.hpe;

import static org.junit.Assert.*;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.images.Eyes;
import com.hp.lft.report.ReportException;
import com.hp.lft.report.Reporter;
import com.hp.lft.report.Status;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.hp.lft.sdk.*;
import com.hp.lft.sdk.web.*;
import com.hp.lft.verifications.*;

import unittesting.*;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class LeanFtTest extends UnitTestClassBase {

    public LeanFtTest() {
        //Change this constructor to private if you supply your own public constructor
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        instance = new LeanFtTest();
        globalSetup(LeanFtTest.class);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        globalTearDown();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws GeneralLeanFtException, IOException, CloneNotSupportedException, InterruptedException, ReportException {
        String appUrl = "www.advantageonlineshopping.com";
        BufferedImage img;
        RenderedImage pageImg;

        Eyes eyes = new Eyes();

        // Initialize the eyes SDK and set your private API key.
        eyes.setApiKey("Z2Q1xi5kYILL8o6XavpjYnYtvCfV3111SSR2U102Rdfse1s110");

        // Define the OS and hosting application to identify the baseline.
        eyes.setHostOS(java.net.Inet4Address.getLocalHost().getHostName());
        eyes.setHostApp(appUrl);

        Browser browser = BrowserFactory.launch(BrowserType.CHROME);
        browser.navigate(appUrl);
        browser.sync();
        //Thread.sleep(5000);
        browser.describe(Link.class, new LinkDescription.Builder()
                .tagName("SPAN").innerText("SPEAKERS").build()).click();

        Thread.sleep(5000);

        ImageDescription targetImages = new ImageDescription.Builder().src(new RegExpProperty(".*fetchImage.*")).type(com.hp.lft.sdk.web.ImageType.NORMAL).tagName("IMG").build();

        Image[] myImages = browser.findChildren(Image.class, targetImages);

        // Start the test
        eyes.open ("AOS", "Speaker Images");

        pageImg = browser.getSnapshot();
        Reporter.reportEvent("Page Screen Shot","image of page", Status.Passed, pageImg);

        //Little trickery  to change from RenderedImage to BufferedImage  there were other ideas on the web
        //this seemed easiest though not the most efficient
        File outputfile = new File("saved.png");
        ImageIO.write(pageImg, "png", outputfile);
        InputStream input = new FileInputStream("saved.png");
        ImageInputStream imageInput = ImageIO.createImageInputStream(input);
        BufferedImage bufImage = ImageIO.read(imageInput);

        eyes.checkImage(bufImage, "Check whole visible page");

        // Loop through all speaker images and check them
        for (Image myImage: myImages){
            System.out.println(myImage.getSrc().substring(myImage.getSrc().lastIndexOf("?")+1) +"  "+myImage.getSrc());
            if (myImage.getSrc().contains("fetchImage")){
                myImage.highlight();
                img = ImageIO.read(new URL(myImage.getSrc()));

                // Visual validation.
                eyes.checkImage(img, myImage.getSrc().substring(myImage.getSrc().lastIndexOf("?")+1));
            }
        }
        // End visual testing
        eyes.close();
        browser.close();

    }

}