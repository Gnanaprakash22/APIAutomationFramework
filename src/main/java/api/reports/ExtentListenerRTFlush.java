/* (C) Games24x7 */
package api.reports;

import api.logger.ILogger;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtentListenerRTFlush
    implements ITestListener, ILogger, ISuiteListener, IConfigurationListener {

  public static Logger Log = LogManager.getLogger(ExtentListenerRTFlush.class);

  private static final ThreadLocal<String> suiteName = new ThreadLocal<>();
  private static final ThreadLocal<ExtentTest> parentTest = new ThreadLocal<>();
  private static final ThreadLocal<ExtentTest> childTest = new ThreadLocal<>();
  private static final AtomicInteger passCount = new AtomicInteger(0);
  private static final AtomicInteger failCount = new AtomicInteger(0);
  private static final AtomicInteger skipCount = new AtomicInteger(0);
  private long testCount = 0;
  private static final ThreadLocal<Boolean> configFailure = new ThreadLocal<>();
  private static final Map<String, String> method = new HashMap<>();
  private static final Map<String, ExtentTest> parentTestMap = new HashMap<>();
  private static final AtomicInteger counter = new AtomicInteger(0);

  public static ExtentTest getInstance() {
    return childTest.get();
  }

  /**
   * Configurations at the start of execution To instantiate extent report Also to showcase number
   * of test cases, planned for the execution
   *
   * @param suite ISuite
   */
  @Override
  public synchronized void onStart(ISuite suite) {
    suiteName.set(suite.getName());
    ExtentManager.getInstance(suiteName.get());
    List<ITestNGMethod> testMethods = suite.getAllMethods();
    this.testCount = testMethods.size();
  }

  /**
   * Configurations at the end of execution To end the Extent report instance Also to showcase total
   * number of Test cases that actually ran in execution
   */
  @Override
  public synchronized void onFinish(ISuite suite) {
    ExtentManager.getInstance(suiteName.get()).flush();
    int totalTCs = passCount.intValue() + skipCount.intValue() + failCount.intValue();
    Log.info("Total number of TCs came for execution :: " + testCount);
    Log.info("Total Number of Passed TCs :: " + passCount);
    Log.info("Total number of skipped TCs :: " + skipCount);
    Log.info("Total number of failed TCs :: " + failCount);
    Log.info("Total Number of TCs executed :: " + totalTCs);
  }

  /**
   * Configures test details when test execution starts Also, showcase test case name in logs
   *
   * @param result ITestResult
   */
  @Override
  public synchronized void onTestStart(ITestResult result) {
    if (!method.containsKey(result.getName())) {
      method.put(result.getName(), "1");
    }
    String methodValue = method.get(result.getName());
    if (methodValue.equalsIgnoreCase("1")) {
      setParentTestData(result);
      method.put(result.getName(), "0");
    } else {
      setChildTest(result);
    }
    getInstance().info("Starting Execution for Test Case : " + result.getName());
    Log.info("Starting Execution for TC :: " + result.getName());
  }

  /**
   * Action, post a Test case successfully PASSED
   *
   * @param result ITestResult
   */
  @Override
  public synchronized void onTestSuccess(ITestResult result) {
    if (getInstance() != null && result != null) {
      getInstance()
          .pass(
              MarkupHelper.createLabel(
                  "Test :: "
                      + result.getName()
                      + " -- "
                      + " - Passed successfully. \n Execution time : "
                      + (result.getEndMillis() - result.getStartMillis())
                      + "ms.",
                  ExtentColor.GREEN));
      Log.info("TC passed successfully :: " + result.getName());
    }
    Log.info("Passed test case count :: " + passCount.incrementAndGet());
    getInstance().getExtent().flush();
  }

  /**
   * Action, post a test case got SKIPPED during execution
   *
   * @param result ITestResult
   */
  @Override
  public synchronized void onTestSkipped(ITestResult result) {
    if (configFailure.get()) {
      setParentTestData(result);
    }
    if (getInstance() != null && result != null) {
      getInstance()
          .skip(
              MarkupHelper.createLabel(
                  "Test :: " + result.getName() + " -- " + " - skipped.", ExtentColor.YELLOW));
      Log.info("TC got skipped :: " + result.getName());
      getInstance().skip(result.getThrowable());
    }
    Log.info("skipped test case count :: " + skipCount.getAndIncrement());
    getInstance().getExtent().flush();
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

  }

  @Override
  public void onStart(ITestContext context) {

  }

  @Override
  public void onFinish(ITestContext context) {

  }

  /**
   * Action, post a test case failed
   *
   * @param result ITestResult
   */
  @Override
  public synchronized void onTestFailure(ITestResult result) {
    if (getInstance() != null && result != null) {
      getInstance()
          .fail(
              MarkupHelper.createLabel(
                  "Test :: "
                      + result.getName()
                      + " -- "
                      + " - FAILED. \n Execution Time: "
                      + (result.getEndMillis() - result.getStartMillis() + "ms."),
                  ExtentColor.RED));
      Log.info("TC got failed :: " + result.getName());
      childTest.get().fail(result.getThrowable());
    }
    Log.info("failed test case count :: " + failCount.incrementAndGet());
    getInstance().getExtent().flush();
  }

  /**
   * Create ParentTag along with first child
   *
   * @param result ITestResult
   */
  public synchronized void setParentTestData(ITestResult result) {
    String execMethod = result.getMethod().getMethodName();
    String tcDescription = "";
    try {
      tcDescription = result.getMethod().getDescription();
    } catch (Exception e) {
      Log.info(e.getMessage());
    }
    ExtentTest parentTestObject;
    String parentTestName = execMethod.concat(":" + tcDescription);
    ExtentReports reporter = ExtentManager.getInstance(suiteName.get());
    parentTest.set(ExtentTestManager.getExtentTest(reporter, parentTestName));
    parentTest.get().assignCategory(result.getTestClass().getRealClass().getName());
    String childTestLbl = getChildTestName(result);
    parentTestObject = parentTest.get();
    ExtentTest test = ExtentTestManager.getChildExtentTest(parentTestObject, childTestLbl);
    childTest.set(test);
    parentTestMap.put(result.getName(), parentTestObject);
  }

  /**
   * Create child tag taking parent object and appending child node to it.
   *
   * @param result ITestResult
   */
  public synchronized void setChildTest(ITestResult result) {
    String childTestLbl = getChildTestName(result);
    ExtentTest test =
        ExtentTestManager.getChildExtentTest(parentTestMap.get(result.getName()), childTestLbl);
    childTest.set(test);
  }

  public synchronized String getChildTestName(ITestResult result) {
    Object[] params = result.getParameters();
    String childTestLbl = null;
    String frmParam = null;
    if (params.length > 0) {
      if (params[0].toString().contains("NAME")) {
        if (params[0].toString().contains("NAME = ")) {
          frmParam = params[0].toString().replace("NAME = ", "").trim();
          childTestLbl = frmParam;
        } else if (params[0].toString().contains("NAME=")) {
          frmParam = params[0].toString().replace("NAME=", "").trim();
          childTestLbl = frmParam;
        }
      } else childTestLbl = getChildName(params);
    } else childTestLbl = getChildName(params);
    return childTestLbl;
  }

  public synchronized String getChildName(Object[] params) {
    String childTestLbl = null;
    try {
      JSONObject jsonObject = new JSONObject(params[0]);
      childTestLbl = jsonObject.get("name").toString();
    } catch (Exception e) {
      try {
        childTestLbl = params[0].toString();
      } catch (Exception ex) {
        childTestLbl = "TestCase:" + counter.incrementAndGet();
      }
    }
    return childTestLbl;
  }

  @Override
  public synchronized void onConfigurationSuccess(ITestResult itr) {
    configFailure.set(false);
  }

  @Override
  public synchronized void onConfigurationFailure(ITestResult itr) {
    configFailure.set(true);
  }

  @Override
  public synchronized void onConfigurationSkip(ITestResult itr) {
    configFailure.set(true);
  }

  public static boolean isJSONValid(String test) {
    try {
      new JSONObject(test);
    } catch (JSONException ex) {
      try {
        new JSONArray(test);
      } catch (JSONException ex1) {
        return false;
      }
    }
    return true;
  }

  public static boolean isJSONValid(Object[] params) {
    try {
      new JSONObject(params[0]);
    } catch (JSONException ex) {
      try {
        new JSONArray(params[0]);
      } catch (JSONException ex1) {
        return false;
      }
    }
    return true;
  }
}
