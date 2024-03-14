/* (C) Games24x7 */
package api.logger;

import api.config.ConfigUtils;
import api.reports.ExtentListener;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class LoggerUtil implements ILogger {

  public static org.apache.logging.log4j.Logger Log = LogManager.getLogger(LoggerUtil.class);
  private static final AtomicInteger jsonCount = new AtomicInteger(2000000);
  private static final AtomicInteger markupCount = new AtomicInteger(3000000);
  private static LoggerUtil LOG = null;

  public static LoggerUtil getLogger() {
    if (LOG == null) LOG = new LoggerUtil();
    ThreadContext.put("threadName", "LogsFile");
    return LOG;
  }


  public void info(String text) {

    if (ConfigUtils.getConfig("frameworklog") == null
        || ConfigUtils.getConfig("frameworklog").equals("true")) {
      Log.info(text);
    }

    if (ExtentListener.getInstance() != null) ExtentListener.getInstance().log(Status.INFO, text);
  }

  public void jsonInfo(String heading, String body) {
    int json = jsonCount.incrementAndGet();
    if (ConfigUtils.getConfig("frameworklog") == null
        || ConfigUtils.getConfig("frameworklog").equals("true")) {
      Log.info(body);
    }
    if (ExtentListener.getInstance() != null)
      if (ExtentListener.isJSONValid(body)) {
        ObjectMapper mapper = new ObjectMapper();
        try {
          Object jsonObject = mapper.readValue(body, Object.class);
          ExtentListener.getInstance()
              .log(
                  Status.INFO,
                  MarkupHelper.createLabel(
                      "<a href='#"
                          + json
                          + "' data-featherlight='#"
                          + json
                          + "' style='color:white;'> Click here for : "
                          + heading
                          + " <div style='display:none;'><div id='"
                          + json
                          + "'><pre>"
                          + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject)
                          + "</pre></div></div></a>",
                      ExtentColor.BLUE));
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else ExtentListener.getInstance().log(Status.INFO, heading + " : " + body);
  }

  public void withMarkupInfo(String heading, String body) {
    int json = markupCount.incrementAndGet();
    Log.info(body);
    if (ExtentListener.getInstance() != null)
      ExtentListener.getInstance()
          .log(
              Status.INFO,
              MarkupHelper.createLabel(
                  "<a href='#"
                      + json
                      + "' data-featherlight='#"
                      + json
                      + "' style='color:white;'> Click here for : "
                      + heading
                      + " <div style='display:none;'><div id='"
                      + json
                      + "'><pre>"
                      + body
                      + "</pre></div></div></a>",
                  ExtentColor.BLUE));
  }

  public void warn(String text) {
    Log.warn(text);
    if (ExtentListener.getInstance() != null) ExtentListener.getInstance().warning(text);
  }

  public void error(String text) {
    Log.error(text);
    if (ExtentListener.getInstance() != null) ExtentListener.getInstance().fail(text);
  }

  public void fatal(String text) {
    Log.fatal(text);
    if (ExtentListener.getInstance() != null) ExtentListener.getInstance().warning(text);
  }

  public void debug(String text) {
    Log.debug(text);
//    ReportPortal.emitLog(text, "DEBUG", new Date());
    if (ExtentListener.getInstance() != null) ExtentListener.getInstance().info(text);
  }

  public void pass(String text) {
    Log.info(text);
    if (ExtentListener.getInstance() != null) ExtentListener.getInstance().pass(text);
  }

  public void fail(String text) {
    Log.fatal(text);
    if (ExtentListener.getInstance() != null) ExtentListener.getInstance().fail(text);
  }
}
