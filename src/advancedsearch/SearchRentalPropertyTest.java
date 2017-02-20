package advancedsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Range;
import com.google.common.collect.Table;

//Test Case Id: 1
//Test Case: Automate the advanced search functionality on Daft.ie

//Test Description: 
//WHEN: Input Search criteria is type of search, County and Area in county
//AND Click on Search
//AND Click on Advanced search
//AND Input Search criteria is select property Type, minimum and maximum of price, Bedrooms and Bathrooms.
//THEN: The Search Result Should have properties with Selected values.

public class SearchRentalPropertyTest extends SearchUtils {

	// CONSTANTS
	private final String URL = "http://www.daft.ie";
	private final String searchType = "To Rent";
	private final String cityToSelect = "Cork City";
	private final static String currency = "€";
	private final String propertyTypeApartment = "Apartment To Rent";
	private final static String bedrooms = " Bedrooms";
	private final static String bathrooms = " Bathrooms";
	private final static String price = "price";

	//Search Ranges
	private final static Table<String, Range<Integer>, Boolean> QUERY_CONSTANTS = HashBasedTable.create();
	static {
		QUERY_CONSTANTS.put(bedrooms, Range.closed(1, 3), true);
		QUERY_CONSTANTS.put(bathrooms, Range.closed(1, 2), true);
		QUERY_CONSTANTS.put(price, Range.closed(800, 1200), true);
	}

	final static Logger logger = Logger.getLogger(SearchRentalPropertyTest.class);

	private WebDriver driver;

	/* Launch Daft.ie
	 * Select To Rent
	 * Select Cork County
	 * Select Area in County
	 * Click Search
	 * Go to Advanced Search
	 * Select Property Type(Apartment to Rent),Price Range(800,1200), Bed Range(1,3)and Bath Range(1,2)
	 * Run Search
	 **/
	@Before
	public void setAndRunSearchQuery() throws Exception
	{
		try {
			PropertyConfigurator.configure("log4j2.properties");

			driver = getDriverType();
			logger.info("driver" + driver.getTitle());

			//driver Customizations
			driver.manage().window().maximize();

			//Launch the URL
			logger.info("Connecting to url" + URL);
			driver.get(URL);

			//Click on To Rent Link
			logger.info("searchType" + searchType);
			List<WebElement> toRentLink =  driver.findElements(By.linkText(searchType));
			toRentLink.get(1).click();

			//Select City
			logger.info("Select City" + cityToSelect);
			driver.findElement(By.className("jcf-select-text")).click();
			WebElement cityDropdown = driver.findElement(By.className("jcf-select-drop-content"));
			selectElementFromDropdownAndClick(driver, cityDropdown, cityToSelect);

			//Select Area in City
			logger.info("Select area" + 342);
			wait(driver, By.xpath(".//div[@id='choose-an-area']")).click();
			Thread.sleep(3000);
			driver.findElement(By.id("342")).click();

			//Run a Search
			logger.info("searching....");
			driver.findElement(By.xpath("//button[contains(text(),'Search')]")).click();

			//Click Advanced Search link
			logger.info("advance searching....");
			wait(driver, By.xpath(".//form[@class='search-form']//a")).click();

			//Set Price Range
			//Set Min price
			WebElement minPriceElement = wait(driver, By.xpath(".//dl[@id='min_price']"));
			scrollToElementAndClick(driver, minPriceElement);
			final String minPrice = currency+getRange(price).lowerEndpoint().toString();
			logger.info("min price" + minPrice);
			selectElementFromDropdownAndClick(driver, minPriceElement, minPrice);

			//Set Max Price
			WebElement maxPriceElement = driver.findElement(By.xpath(".//dl[@id='max_price']"));
			scrollToElementAndClick(driver, maxPriceElement);
			final String maxPrice = currency+getRange(price).upperEndpoint().toString();
			logger.info("max price" + maxPrice);
			selectElementFromDropdownAndClick(driver, maxPriceElement, maxPrice);

			//Beds range
			//Min beds
			WebElement minBedDropdownElement = driver.findElement(By.xpath(".//dl[@id='min_bed']"));
			scrollToElementAndClick(driver, minBedDropdownElement);
			String minBedrooms = getSingularOrPlural(getRange(bedrooms).lowerEndpoint(), bedrooms);
			logger.info("min beds" + minBedrooms);
			selectElementFromDropdownAndClick(driver, minBedDropdownElement, minBedrooms);

			// Max Beds
			WebElement maxBedDropdownElement = driver.findElement(By.xpath(".//dl[@id='max_bed']"));
			maxBedDropdownElement.click();
			String maxBedrooms = getSingularOrPlural(getRange(bedrooms).upperEndpoint(), bedrooms);
			logger.info("max beds" + maxBedrooms);
			selectElementFromDropdownAndClick(driver, maxBedDropdownElement, maxBedrooms);

			//Bathrooms range
			//Min Baths
			WebElement minBathDropdown = driver.findElement(By.xpath(".//dl[@id='min_bath']"));
			scrollToElementAndClick(driver, minBathDropdown);
			String minBathrooms = getSingularOrPlural(getRange(bathrooms).lowerEndpoint(), bathrooms);
			logger.info("min Bathrooms" + minBathrooms);
			selectElementFromDropdownAndClick(driver, minBathDropdown, minBathrooms);

			//Max Baths
			WebElement maxBathDropdown = driver.findElement(By.xpath(".//dl[@id='max_bath']"));
			maxBathDropdown.click();
			String maxBathrooms = getSingularOrPlural(getRange(bathrooms).upperEndpoint(), bathrooms);
			logger.info("max bathrooms" + maxBathrooms);
			selectElementFromDropdownAndClick(driver, maxBathDropdown, maxBathrooms);

			//Select Property Type
			scrollToElementAndClick(driver, driver.findElement(By.id("multi_title_container_ptId")));

			WebElement propertyTypeDropdown = driver.findElement(By.xpath(".//ul[@id='ptId_ul']"));
			List<WebElement> list = propertyTypeDropdown.findElements(By.tagName("li"));
			for (WebElement eachElement : list) {
				if(eachElement.findElement(By.xpath(".//label/span")).getText().equalsIgnoreCase(propertyTypeApartment)) {
					logger.info("property type" + propertyTypeApartment);
					eachElement.findElement(By.xpath(".//label")).click();
					break;
				}
			}

			//Run a Search
			logger.info("Running advanced search");
			scrollToElementAndClick(driver, driver.findElement(By.xpath(".//input[@class='btn-search']")));
		} catch(Exception e) {
			throw(e);
		}
	}

	/*
	 * Select Random Property from Search result 
	 * Verify that Price Range, bedrooms and bathrooms are equal to search criteria
	 * */
	@Test
	public void validateSearchResults() throws Exception {
		logger.info("start of result validation");
		WebElement resultTable = wait(driver, By.xpath(".//div[@class='box']"));
		if(resultTable.isDisplayed()) {
			logger.info("Got the result and it is not empty");
			WebElement propertyBox = resultTable.findElement(By.xpath(".//ul[@class='info']"));
			List<WebElement> criteriaList = propertyBox.findElements(By.tagName("li"));

			//Assert Property Type
			assertTrue(criteriaList.get(0).getText().replaceFirst("\\|", "").equalsIgnoreCase(propertyTypeApartment));
			logger.info("Passed Property Type assertion");

			//Assert No of Bedrooms
			String noOfBedString = criteriaList.get(1).getText().replaceFirst("\\|", "");
			assertEquals(true, assertResult(noOfBedString, "Bed", bedrooms));
			logger.info("Passed bedrooms assertion");

			//Assert No of Bathrooms
			String noOfBathsString = criteriaList.get(2).getText().replaceFirst("\\|", "");
			assertEquals(true, assertResult(noOfBathsString, "Bath", bathrooms));
			logger.info("Passed bathrooms assertion");

			//Assert Price
			String priceString = resultTable.findElement(By.className("price")).getText();
			String priceStrip = priceString.substring(priceString.indexOf(currency)+1, priceString.indexOf(" "));
			int housePrice = Integer.parseInt(priceStrip.replaceAll(",", ""));
			assertEquals(true, isInRange(price, housePrice));
			logger.info("Passed price assertion");

			logger.info("Test was Successfully");
		} else {
			logger.info("Empty Result");
		}
	}

	// Close the browser
	@After
	public void closeBrowsers() throws InterruptedException  {
		logger.info("Closing browser");
		driver.quit();
	}

	/*This method will verify against the search criteria
	 * @param resultString
	 * @param matchString
	 * @param key
	 * return boolean
	 * */
	private boolean assertResult(String resultString, String matchString, String key) {
		if(resultString.contains(matchString)) {
			String[] values = resultString.split(" ");
			int value = Integer.parseInt(values[0]);
			return isInRange(key, value);
		}
		return false;
	}

	/* This method will check if result value is in range
	 * @param key
	 * @param result
	 * returns true/false
	 */
	private boolean isInRange(String key, int result) {
		for (Entry<Range<Integer>, Boolean> ranges : QUERY_CONSTANTS.row(key).entrySet()) {
			Range<Integer> range = ranges.getKey();
			if (range.contains(result)) {
				return ranges.getValue();
			}
		}
		return false;
	}

	/* This method will return the range set
	 * @param key
	 * returns range set
	 */
	private Range<Integer> getRange(String key) {
		for (Entry<Range<Integer>, Boolean> ranges : QUERY_CONSTANTS.row(key).entrySet()) {
			return ranges.getKey();
		}
		return null;
	}
}
