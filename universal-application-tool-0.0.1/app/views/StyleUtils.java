package views;

import com.google.common.collect.ImmutableList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StyleUtils {
  public static String EVEN = "even";
  public static String FOCUS = "focus";
  public static String HOVER = "hover";

  public static String RESPONSIVE_SM = "sm";
  public static String RESPONSIVE_MD = "md";
  public static String RESPONSIVE_LG = "lg";
  public static String RESPONSIVE_XL = "xl";
  public static String RESPONSIVE_2XL = "2xl";

  public static String applyUtilityClass(String utility, String... styles) {
    return applyUtilityClass(utility, Stream.of(styles));
  }

  public static String applyUtilityClass(String utility, ImmutableList<String> styles) {
    return applyUtilityClass(utility, styles.stream());
  }

  public static String applyUtilityClass(String utility, Stream<String> styles) {
    return styles.map(entry -> utility + ":" + entry).collect(Collectors.joining(" "));
  }

  public static String even(ImmutableList<String> styles) {
    return applyUtilityClass(EVEN, styles);
  }

  public static String even(String... styles) {
    return applyUtilityClass(EVEN, styles);
  }

  public static String focus(ImmutableList<String> styles) {
    return applyUtilityClass(FOCUS, styles);
  }

  public static String focus(String... styles) {
    return applyUtilityClass(FOCUS, styles);
  }

  public static String hover(String... styles) {
    return applyUtilityClass(HOVER, styles);
  }

  public static String hover(ImmutableList<String> styles) {
    return applyUtilityClass(HOVER, styles);
  }

  public static String joinStyles(String... styles) {
    return String.join(" ", styles);
  }
}
