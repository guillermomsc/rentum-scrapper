import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.exit;

public class Main {
    static WebDriver driver = null;

    public static void main(final String[] args) {

       /* if (ArrayUtils.isEmpty(args) || args.length < 3) {
            throw new IllegalArgumentException("Faltan Argumnetos necesarios  java -jar scrapper.jar <rutaArchivo> <rutaArchivPaginmasBaneadas> <pagina> [verBrowser] ");
        }*/

        //final String filePath = args[0];
        final String filePath = "/home/guille/Dev/scrapper.txt";
        //final String filePathBannedIp = args[1];
        //final int page = Integer.parseInt(args[2]);
        final int page = 1;
        final boolean showBrowser = args[3] == null || Boolean.parseBoolean(args[3]);


        try {

            final File file = new File(filePath);
            //final File fileBannedIPPages = new File(filePathBannedIp);
            driver = getWebDriver(showBrowser);
            navigateTo("https://listado.mercadolibre.com.uy/inmuebles/alquiler/dueno/");


            Thread.sleep(4000);

            int currentPage = getCurrentPageIndex();
            while (page != currentPage) {
                getNextPageWebElement().click();
                currentPage = getCurrentPageIndex();
            }

            System.out.println("Página=" + page);
            int publish = 0;
            while (publish < 48) {

                Thread.sleep(2000);

                //Me paro en la publicacion i(publish)
                getRealEstatesPublishesWebElement(publish).click();

                //espero
                Thread.sleep(3000);

                final String barrio = getBarrio();
                final String moneda = getMoneda();
                final String precio = getPrecio();
                final String cuartos = getCuartos();
                final String url = getUrl();


                //hago clic en ver teléfono
                getShowPhoneNumberWebElement().click();

                //espero
                Thread.sleep(8000);

                //obtengo la data
                final String phoneData = getPhoneNumbers();

                if (StringUtils.isEmpty(phoneData)) {

                    final String bannedData = "IP baneada para la página " + page + " " + "en el item número " + publish;
                    System.out.println(bannedData);
                    //writeFile(fileBannedIPPages, bannedData);
                    exit(1);
                }

                final String resultado = StringUtils.join(Arrays.asList(phoneData, barrio, moneda.concat(precio), cuartos, url), "|");
                System.out.println(resultado);
                writeFile(file, resultado);
                System.out.println("\tPublicación=" + publish);
                publish++;
                navigateBack();
            }

            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
            exit(1);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        exit(0);
    }

    private static void navigateBack() {
        driver.navigate().back();
    }

    private static void writeFile(final File file, final String data) {
        try (FileWriter fr = new FileWriter(file, true)) {
            fr.write(data + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getPhoneNumbers() {
        return driver.findElements(By.cssSelector(".profile-info-phone-value"))
                .stream().map(WebElement::getText).collect(Collectors.joining(";"));
    }

    private static WebDriver getWebDriver(boolean showBrowser) {

        WebDriverManager.chromedriver().setup();
        final ChromeOptions options = getWebDriverChromeOptions(showBrowser);
        final WebDriver driver = new ChromeDriver(options);

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("source", "Object.defineProperty(navigator, 'webdriver', { get: () => undefined })");
        ((ChromeDriver) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", params);

        return driver;
    }

    private static ChromeOptions getWebDriverChromeOptions(final boolean showBrowser) {

        final ChromeOptions options = new ChromeOptions()
                .addArguments("--disable-blink-features")
                .addArguments("--disable-blink-features=AutomationControlled")
                .setExperimentalOption("useAutomationExtension", false)
                .setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"))
                .addArguments("user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.50 Safari/537.36");

        return showBrowser
                ? options
                : options.addArguments("headless");
    }

    public static WebElement getNextPageWebElement() {
        return driver.findElement(By.cssSelector(".andes-pagination__button.andes-pagination__button--next > a"));
    }

    private static WebElement getRealEstatesPublishesWebElement(final int index) {
        return driver.findElements(By.cssSelector("li.ui-search-layout__item")).get(index);
    }

    private static String getBarrio(){
         WebElement webElement = driver.findElement(By.cssSelector("#root-app > div.vip-nav-bounds > div > div.layout-col.layout-col--left > " +
                 "div.item-map.vip-card > section > div.section-map-title > div > h3"));
                webElement = webElement != null ? webElement : driver.findElement(By.xpath("//*[@id=\"root-app\"]/div[1]/div/div[1]/div[3]/section/div[1]/div/h3"));
                return webElement != null ? webElement.getText() : "";
    }

    private static String getPrecio(){
        WebElement webElement = driver.findElement(By.cssSelector("#productInfo > fieldset > span > span.price-tag-fraction"));
        webElement = webElement != null ? webElement : driver.findElement(By.xpath("//*[@id=\"productInfo\"]/fieldset/span/span[2]"));
        return webElement != null ? webElement.getText() : "";
    }

    private static String getMoneda(){
        WebElement webElement = driver.findElement(By.cssSelector("#productInfo > fieldset > span > span.price-tag-symbol"));
        webElement = webElement != null ? webElement : driver.findElement(By.xpath("//*[@id=\"productInfo\"]/fieldset/span/span[1]"));
        return  webElement != null ? webElement.getText() : "";
    }

    private static String getCuartos(){
        WebElement webElement = driver.findElement(By.cssSelector("#productInfo > div.item-attributes > dl:nth-child(2) > dd"));
        webElement = webElement != null ? webElement : driver.findElement(By.xpath("//*[@id=\"productInfo\"]/div[1]/dl[2]/dd"));
        return webElement != null ? webElement.getText() : "";
    }

    private static String getUrl(){
        WebElement webElement = driver.findElement(By.cssSelector("#productInfo > input[type=hidden]:nth-child(2)"));
        webElement = webElement != null ? webElement : driver.findElement(By.xpath("//*[@id=\"productInfo\"]/input[2]"));
        return webElement != null ? webElement.getAttribute("value") : "";
    }

    private static WebElement getShowPhoneNumberWebElement() {
        return driver.findElement(By.cssSelector(".show-info-phone-link"));
    }

    private static void navigateTo(final String url) {
        driver.navigate().to(url);
    }

    private static int getCurrentPageIndex() {
        return Integer.parseInt(driver.findElement(By.cssSelector(".andes-pagination__button.andes-pagination__button--current > a")).getText());
    }


}