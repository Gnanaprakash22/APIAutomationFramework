/* (C) Games24x7 */
package api.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {

  public static ExtentTest getExtentTest(ExtentReports report, String testName) {
    ExtentTest test = report.createTest(testName);
    return test;
  }

  public static ExtentTest getChildExtentTest(ExtentTest parentTest, String testName) {
    return parentTest.createNode(testName);
  }
}
