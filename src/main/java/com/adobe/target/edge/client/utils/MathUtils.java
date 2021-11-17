package com.adobe.target.edge.client.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathUtils {

  public static double roundDouble(double d, int places) {
    BigDecimal bigDecimal = new BigDecimal(Double.toString(d));
    bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
    return bigDecimal.doubleValue();
  }
}
