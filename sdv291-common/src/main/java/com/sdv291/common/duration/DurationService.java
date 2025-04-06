package com.sdv291.common.duration;

import com.sdv291.common.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class DurationService {

  private static final int MAX_NAME_LENGTH = 50;
  private static final int MAX_PARAM_LENGTH = 20;
  private static final String HEADERS = String.join(StringUtils.EMPTY,
    String.format("%1$-" + MAX_NAME_LENGTH + "s", "Name"),
    String.format("%1$-" + MAX_PARAM_LENGTH + "s", "TotalTime(ms)"),
    String.format("%1$-" + MAX_PARAM_LENGTH + "s", "AverageTime(ms)"),
    String.format("%1$-" + MAX_PARAM_LENGTH + "s", "ExecCount"),
    String.format("%1$-" + MAX_PARAM_LENGTH + "s", "Effectivity"),
    "Threads"
  );
  private boolean printColumnDescriptions = true;
  private final ConcurrentMap<String, Duration> durationMap = new ConcurrentHashMap<>();

  public void setPrintColumnDescriptions(boolean printColumnDescriptions) {
    this.printColumnDescriptions = printColumnDescriptions;
  }

  public boolean hasDuration(String name) {
    return durationMap.containsKey(name);
  }

  public Duration getDuration(String name) {
    return durationMap.computeIfAbsent(name, Duration::new);
  }

  public void print(Logger logger) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Duration> entry : durationMap.entrySet()) {
      Duration duration = entry.getValue();
      if (duration.getCount() == 0) {
        continue;
      }
      if (sb.length() == 0) {
        if (printColumnDescriptions) {
          printColumnDescriptions = false;
          sb.append("Description of columns:\n");
          sb.append("Name - process name;\n");
          sb.append("TotalTime(ms) - total time in milliseconds spent interacting with this process;\n");
          sb.append("AverageTime(ms) - average time in milliseconds spent interacting with this process, formula [TotalTime / ExecCount];\n");
          sb.append("ExecCount - count of calls;\n");
          sb.append("Effectivity - how many ExecCount were made with the maximum package size, formula [(ProcessedCount / (ExecCount * MaxPackSize)) * 100] (the packet size is displayed after the percentage value);\n");
          sb.append("Threads - how many and what threads interacted with this process;\n");
        }
        sb.append("Duration info:\n");
        sb.append(HEADERS);
        sb.append("\n");
      }

      String strQuality = StringUtils.EMPTY;
      if (duration.getMaxPackSize() > 0 || duration.getEffectivity() > 0) {
        strQuality += (int) duration.getEffectivity() + "%|" + duration.getMaxPackSize();
      }

      sb.append(String.format("%1$-" + MAX_NAME_LENGTH + "s", duration.getName()));
      sb.append(String.format("%1$-" + MAX_PARAM_LENGTH + "s", duration.getInteractionTime()));
      sb.append(String.format("%1$-" + MAX_PARAM_LENGTH + "s", duration.getInteractionTime() / duration.getCount()));
      sb.append(String.format("%1$-" + MAX_PARAM_LENGTH + "s", duration.getCount()));
      sb.append(String.format("%1$-" + MAX_PARAM_LENGTH + "s", strQuality));
      sb.append(duration.getThreadNames());
      sb.append("\n");
      duration.reset();
    }
    if (sb.length() > 0) {
      sb.setLength(sb.length() - 1);
      logger.info(sb.toString());
    }
  }
}
