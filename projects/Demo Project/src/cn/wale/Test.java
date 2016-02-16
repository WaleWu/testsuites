package cn.wale;

import com.calix.automation.component.web.Browsers;
import com.calix.automation.component.web.WebBrowser;
import com.calix.automation.execute.Parameter;
import com.calix.automation.keyword.SSHKeyword;
import com.calix.automation.log.ILogger;
import com.calix.automation.log.Level;
import com.calix.automation.log.Logger;
import com.calix.automation.param.BrowserType;
import com.calix.automation.param.Reference;

public class Test
{
    private static ILogger logger = Logger.getLogger();

    public static void test(Parameter param)
    {
        //print a log with INFO level.
        logger.info("It is an info log.");
        //print a log with ERROR level.
        logger.error("It is an error log.");
        //print a log with detailed info
        logger.detail(Level.WARN, "It is an text log with detail information.", "Line 1\r\nLine 2 Info\r\nLast Line.");
        //print a log with a snapshot
        logger.snapshot(Level.INFO);
        //print a log with a stacktrace
        logger.stacktrace(Level.INFO, new Exception("It is a test exception"));

    	//How to get the parameter from the test case.
    	String sParam1 = param.getString("param1");
    	int iParam2 = param.getInt("param2");
    	logger.debug("param1 : " + sParam1);
    	logger.error("param2 : " + iParam2);
        
        //How to use a reference in source code
        String sValue = Reference.getStringValue("VAR_TEMP");
        logger.info("The value of Reference <VAR_TEMP> is " + sValue);
        
        //How to use an existing Keyword in source code.
        SSHKeyword.connect("", "10.245.252.104", 22, "root", "cmsroot", "\\[root@cdtaut04 .*?\\]#", 30000);
        
        WebBrowser browser = Browsers.create("", BrowserType.Firefox);
        browser.open("http://www.baidu.com");
    }
}
