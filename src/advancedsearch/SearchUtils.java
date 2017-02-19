package advancedsearch;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchUtils {

	/* This will return assigned web driver type from properties file
	 * return driver
	 * */
	public WebDriver getDriverType() throws IOException{
		InputStream input = SearchUtils.class.getClassLoader().getResourceAsStream("data/config.properties");
		Properties prop = new Properties();
		prop.load(input);
		StringBuilder driverPath = new StringBuilder();
		driverPath.append(System.getProperty("user.dir"));
		driverPath.append(prop.getProperty("driverpath"));
		String browserType = prop.getProperty("browser");
		WebDriver driver;

		if (browserType=="chrome")
		{
			System.setProperty("webdriver.chrome.driver", driverPath.toString());
			driver =new ChromeDriver();
		}
		else if (browserType=="internetExplorer")
		{
			System.setProperty("webdriver.ie.driver", driverPath.toString());
			driver =new InternetExplorerDriver();
		}
		else
		{
			System.setProperty("webdriver.gecko.driver", driverPath.toString());
			driver = new FirefoxDriver();
		}
		return driver;
	}

	/* This method will select element from drop down
	 * @param driver
	 * @param element
	 * @param optionToSelect
	 * */
	public void selectElementFromDropdownAndClick(WebDriver driver, WebElement element, String optionToSelect) throws Exception {

		try {
			List<WebElement> list = element.findElements(By.tagName("li"));
			for (WebElement eachElement : list) {
				if(optionToSelect.split(" ")[0].equalsIgnoreCase(eachElement.getText().replaceAll(",", "").split(" ")[0])) {
					Actions actions = new Actions(driver);
					actions.moveToElement(eachElement);
					eachElement.click();
					break;
				}
			}
		}catch(NoSuchElementException e) {
			e.printStackTrace();
		}
	}

	/* This method will scroll to element and click
	 * @param driver
	 * @param element
	 * */
	public void scrollToElementAndClick(WebDriver driver, WebElement element) throws Exception {
		try 
		{
			Coordinates cordinates = ((Locatable)element).getCoordinates();
			cordinates.inViewPort();
			if(!element.isDisplayed()) {
				WebDriverWait wait = new WebDriverWait(driver, 20);
				wait.until(ExpectedConditions.visibilityOf(element));
				driver.findElement(By.id(element.getAttribute("id"))).click();
			} else {
				element.click();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/* This method will wait for an element until the visibility of Element is Located or time out exception occurs
	 * @param driver
	 * @param by
	 * throws TimeoutException
	 * @return element
	 * */
	public WebElement wait(WebDriver driver, By by) throws TimeoutException {
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		WebElement element = driver.findElement(by);
		return element;
	}

// TODO : Tried to use FluentWait but couldn't fully complete to work.
	public Wait<WebDriver> getWait(final WebDriver driver) {
		// 30 seconds to wait
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(30, TimeUnit.SECONDS)
				.pollingEvery(2, TimeUnit.MILLISECONDS)
				.ignoring(NoSuchElementException.class);

		//		// initialize wait Object
		//		if(null == wait) {
		//			wait = getWait(driver);
		//		}
		//		System.out.println("In fluent wait" + by);
		//		WebElement element = wait.until(new Function<WebDriver, WebElement>() {
		//			public WebElement apply(WebDriver driver) {
		//				WebElement element = driver.findElement(by);
		//				if(element.isDisplayed()) {
		//					return element;
		//				}
		//				return null;
		//			}
		//		});
		//
		//		return element;
		return wait;
	}
}

