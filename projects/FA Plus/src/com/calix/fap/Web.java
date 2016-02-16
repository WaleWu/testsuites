package com.calix.fap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.calix.automation.component.common.Utility;
import com.calix.automation.component.web.Browsers;
import com.calix.automation.component.web.Combo;
import com.calix.automation.component.web.Select;
import com.calix.automation.config.RuntimeSetting;
import com.calix.automation.execute.Parameter;
import com.calix.automation.log.ILogger;
import com.calix.automation.log.Logger;
import com.calix.automation.param.ElementLocator;

public class Web
{
	private static ILogger logger = Logger.getLogger();
	private static SimpleDateFormat moDateFormat = new SimpleDateFormat("MM-dd-yyyy");
	
	public static void navigation(Parameter param)
	{
		String parent = param.getString("parent");
		String path = param.getString("path");
		String delimiter = param.getString("delimiter");

		navigation(parent, path, delimiter);
	}

    private static void navigation(String psParent, String psTreePath, String psDelimiter)
    {
    	WebElement parent = Browsers.current().findElement(ElementLocator.id("content-navigation"));

    	if (psTreePath == null || psTreePath.equals(""))
    	{
    		WebElement element = parent.findElement(By.xpath("./ul/li/a[@title='" + psParent + "']/span"));
    		element.click();
    	}
    	else
    	{
    		parent = parent.findElement(By.xpath("./ul/li/a[@title='" + psParent + "']/parent::li"));

            String sClass = parent.getAttribute("class");
            //if the branch is collapsed, expand it
            if (sClass == null || !sClass.contains("open"))
            {
                WebElement icon = parent.findElement(By.xpath("./a/b/em"));
                icon.click();

                Utility.sleep(1000);
            }

            //Split the tree path
            String[] sTreeNodes = psTreePath.split(psDelimiter);

            for (int i = 0; i < sTreeNodes.length - 1; i++)
            {
                logger.info("Prepare to find the tree branch: " + sTreeNodes[i]);
                //find the tree item on the navigation tree
                WebElement element = parent.findElement(By.xpath(".//a[text()='" + sTreeNodes[i].trim() + "']/parent::span/parent::a/parent::li"));

                sClass = element.getAttribute("class");
                //if the branch is collapsed, expand it
                if (sClass == null || !sClass.contains("open"))
                {
                    WebElement icon = element.findElement(By.xpath("./a/b/em"));
                    icon.click();

                    Utility.sleep(1000);
                }
                
                parent = element;
            }

            logger.info("Prepare to find the tree node: " + sTreeNodes[sTreeNodes.length - 1]);
            WebElement node = parent.findElement(By.xpath(".//a[text()='" + sTreeNodes[sTreeNodes.length - 1].trim() + "']"));
            node.click();
            Utility.sleep(1000);
    	}
    }

	public static void selectMenu(Parameter param)
	{
		String path = param.getString("path");
		String delimiter = param.getString("delimiter");

		selectMenu(path, delimiter);
	}
	
	private static void selectMenu(String psMenuPath, String psDelimiter)
	{
		WebElement parent = Browsers.current().findElement(ElementLocator.id("content"));

        //Split the menu path
        String[] sMenuNodes = psMenuPath.split(psDelimiter);
        
        for (int i = 0; i < sMenuNodes.length; i++)
        {
            logger.info("Prepare to find the menu item: " + sMenuNodes[i]);
            
            WebElement element = parent.findElement(By.xpath("./section[" + (i + 1) + "]//a[text()='" + sMenuNodes[i] + "']"));

            element.click();

            Utility.sleep(1000);
        }
	}

	public static void setValue(Parameter param)
	{
		String name = param.getString("name");
		String value = param.getString("value").trim();

		waitForLoading(200000);

		WebElement parent = Browsers.current().findElement(ElementLocator.name("params"));
		
		List<WebElement> elements = parent.findElements(By.xpath("./ul/li/label"));
		
		for (WebElement element : elements)
		{
			String sText = element.getText();

			if (sText.startsWith(name))
			{
				WebElement oValueElement = element.findElement(By.xpath("./*"));
				String sTag = oValueElement.getTagName();

				if (sTag.equalsIgnoreCase("div"))
		        {
		        	String sClass = oValueElement.getAttribute("class");
		        	if (sClass.contains("datetimepicker"))
		        	{
		        		oValueElement = oValueElement.findElement(By.xpath("./input"));
		        		sTag = oValueElement.getTagName();

						try
						{
							Calendar currentDate = Calendar.getInstance();
							if (value.startsWith("DAY"))
							{
								String sCount = value.substring(3).replaceAll("\\s", "");
								int iCount = Integer.parseInt(sCount);
								currentDate.add(Calendar.DAY_OF_MONTH, iCount);
								value = moDateFormat.format(currentDate.getTime());
							}
							else if (value.startsWith("MONTH"))
							{
								String sCount = value.substring(5).replaceAll("\\s", "");
								int iCount = Integer.parseInt(sCount);
								currentDate.add(Calendar.MONTH, iCount);
								value = moDateFormat.format(currentDate.getTime());
							}
							else if (value.startsWith("YEAR"))
							{
								String sCount = value.substring(4).replaceAll("\\s", "");
								int iCount = Integer.parseInt(sCount);
								currentDate.add(Calendar.YEAR, iCount);
								value = moDateFormat.format(currentDate.getTime());
							}
						}
						catch (NumberFormatException e)
						{
						}
		        	}
		        	else if (sClass.contains("bootstrap-touchspin"))
		        	{
		        		oValueElement = oValueElement.findElement(By.xpath("./input"));
		        		sTag = oValueElement.getTagName();
		        	}
		        }

		        if (sTag.equalsIgnoreCase("select"))
		        {
		            Combo oCombo = new Combo(oValueElement);
		            
		            oCombo.selectByVisibleText(value);
		        }
		        else if (sTag.equalsIgnoreCase("input"))
		        {
		            String sType = oValueElement.getAttribute("type");
		            
		            if (sType.equalsIgnoreCase("text")
		                || sType.equalsIgnoreCase("password"))
		            {
		            	oValueElement.sendKeys(Keys.SHIFT, Keys.HOME, Keys.NULL, value, Keys.ENTER);
		            }
		            else if (sType.equalsIgnoreCase("button")
		                    || sType.equalsIgnoreCase("reset")
		                    || sType.equalsIgnoreCase("submit"))
		            {
		            	oValueElement.click();
		            }
		            else if (sType.equalsIgnoreCase("checkbox")
		                    || sType.equalsIgnoreCase("radio"))
		            {
		                Select checkBox = new Select(oValueElement);

		                checkBox.setSelected(value.equalsIgnoreCase("true"));
		            }
		            else
		            {
		                RuntimeSetting.setTestFailed("Unrecognized element: " + sTag + "[type='" + sType + "']");
		            }
		        }
			}
		}
	}

	public static void clickButton(Parameter param)
	{
		String text = param.getString("text");

		waitForLoading(200000);

		WebElement button = Browsers.current().findElement(ElementLocator.xpath("//button[text()='" + text + "']"));
		
		button.click();
	}

	public static void export(Parameter param)
	{
		String type = param.getString("type");
		
		waitForLoading(200000);
		
		WebElement export = Browsers.current().findElement(ElementLocator.xpath("//a[@class='export_menu']"));
		
		export.click();
		
		if (type.equalsIgnoreCase("CSV"))
		{
			WebElement exportCSV = export.findElement(By.xpath("./parent::div/div/a[2]"));
			exportCSV.click();
		}
		else if (type.equalsIgnoreCase("PDF"))
		{
			WebElement exportCSV = export.findElement(By.xpath("./parent::div/div/a[1]"));
			exportCSV.click();
		}
		
		waitForLoading(200000);
	}
	
	public static void waitForLoading(Parameter param)
	{
		int timeout = param.getInt("timeout");
		
		waitForLoading(timeout);
	}
	
	private static void waitForLoading(int timeout)
	{
        WebElement oElement = Browsers.current().findElement(ElementLocator.className("loading_mask"));

        Utility.sleep(500);
        
        long start = System.currentTimeMillis();

        do
        {
            if (oElement.isDisplayed())
            {
                Utility.sleep(200);
            }
            else
            {
                Utility.sleep(500);
                return;
            }
        }
        while (System.currentTimeMillis() - start < timeout);
        
        logger.warn("Time out for waiting load icon.");
	}
}