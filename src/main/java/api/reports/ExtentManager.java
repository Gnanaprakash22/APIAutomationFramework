/* (C) Games24x7 */
package api.reports;

import api.clients.ApiDetails;
import api.clients.BaseRestClient;
import api.config.ConfigUtils;
import api.logger.ILogger;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentKlovReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class ExtentManager extends BaseRestClient implements ILogger {

  private static Logger Log = LogManager.getLogger(ExtentManager.class);
  private static ExtentReports extent;
  private static String suiteName;

  public static ExtentReports getInstance(String suite_Name) {
    suiteName = suite_Name;
    if (extent == null) {
      createInstance();
    }
    return extent;
  }

  public static ExtentReports createInstance() {
    ExtentSparkReporter sparkReporter = null;
    File htmlFile = new File("src/test/resources/html-config.xml");
    if (htmlFile.exists()) {
      sparkReporter = new ExtentSparkReporter("test-output/HtmlReport/ExtentHtml.html");
      try {
        sparkReporter.loadXMLConfig(htmlFile.getPath());
      } catch (Exception e) {
        // TODO: handle exception
        e.printStackTrace();
      }
    } else {
      deletePreviousFiles();
      sparkReporter =
          new ExtentSparkReporter(
              System.getProperty("user.dir")
                  .concat(
                      "/test-output/" + suiteName + "_" + java.time.LocalDateTime.now() + ".html"));
      sparkReporter.config().setTheme(Theme.STANDARD);
      sparkReporter.config().setEncoding("utf-8");
      sparkReporter.config().setReportName(suiteName);
    }
    ExtentKlovReporter klovReporter = setupKlovServer();
    extent = new ExtentReports();
    extent.attachReporter(sparkReporter, klovReporter);
    return extent;
  }

  public static ExtentKlovReporter setupKlovServer() {
    ExtentManager extentManager = new ExtentManager();
    ExtentKlovReporter klovReporter = null;
    if (ConfigUtils.getConfig("klovServer") != null
        && !ConfigUtils.getConfig("klovServer").equals("false")) {
      if (extentManager.checkKlovServer()) {
        klovReporter = new ExtentKlovReporter(suiteName);
        klovReporter.initMongoDbConnection("10.24.75.253", 27017);
        klovReporter.setProjectName(suiteName);
        klovReporter.setReportName(suiteName);
        klovReporter.initKlovServerConnection("http://10.24.75.253");
      }
    }
    return klovReporter;
  }

  public boolean checkKlovServer() {
    boolean flag;
    try {
      setServiceEndPoint("http://10.24.75.253:80/");
      FilterableRequestSpecification request = getRequestSpecificationWoCurl(ContentType.JSON);
      Response response = request.get(getServiceEndPoint());
      ApiDetails apiDetails = new ApiDetails(request, response);
      if ((apiDetails.getResponse().getStatusCode() == 200)) flag = true;
      else flag = false;
    } catch (Exception e) {
      flag = false;
    }
    return flag;
  }

  public static boolean deletePreviousFiles() {
    try {
      if (ConfigUtils.getConfig("DELETEREPORTFLAG").equalsIgnoreCase("true")) {
        // Log.info("******** Deleting previous reports
        // as flag is:
        // "+ConfigUtils.getConfig("DELETEREPORTFLAG")+"
        // and day window is:
        // "+ConfigUtils.getConfig("DELETEWINDOW"));
        File directory = new File("/test-results/");
        File[] listFiles = directory.listFiles();
        try {
          int i = Integer.parseInt(ConfigUtils.getConfig("DELETEWINDOW"));
          long purgeTime = System.currentTimeMillis() - (i * 24 * 60 * 60 * 1000);
          for (File listFile : listFiles) {
            if ((listFile.lastModified()) < purgeTime) {
              listFile.delete();
            }
          }
        } catch (NumberFormatException ex) {
          for (File listFile : listFiles) {
            listFile.delete();
          }
        }
      }
      return true;
    } catch (NullPointerException ex) {
      // Log.info("No reports will be deleted");
      return false;
    }
  }
}
