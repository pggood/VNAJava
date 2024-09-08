package krause.util;

import java.util.Calendar;

public class CalendarHelper {
   public static void setCalendarToEndOfDay(Calendar theCal) {
      setCalendarToStartOfDay(theCal);
      theCal.set(11, theCal.getActualMaximum(11));
      theCal.set(12, theCal.getActualMaximum(12));
      theCal.set(13, theCal.getActualMaximum(13));
      theCal.set(14, theCal.getActualMaximum(14));
   }

   public static void setCalendarToEndOfMonth(Calendar theCal) {
      setCalendarToStartOfMonth(theCal);
      theCal.set(5, theCal.getActualMaximum(5));
      setCalendarToEndOfDay(theCal);
   }

   public static void setCalendarToStartOfDay(Calendar theCal) {
      theCal.set(11, theCal.getActualMinimum(11));
      theCal.set(12, theCal.getActualMinimum(12));
      theCal.set(13, theCal.getActualMinimum(13));
      theCal.set(14, theCal.getActualMinimum(14));
   }

   public static void setCalendarToStartOfWeek(Calendar theCal) {
      setCalendarToStartOfDay(theCal);
      theCal.set(7, 2);
   }

   public static void setCalendarToEndOfWeek(Calendar theCal) {
      setCalendarToStartOfWeek(theCal);
      setCalendarToEndOfDay(theCal);
      theCal.add(7, 6);
   }

   public static void setCalendarToStartOfMonth(Calendar theCal) {
      theCal.set(5, theCal.getActualMinimum(5));
      setCalendarToStartOfDay(theCal);
   }

   public static void setCalendarToStartOfYear(Calendar theCal) {
      theCal.set(2, theCal.getActualMinimum(2));
      setCalendarToStartOfMonth(theCal);
   }

   public static void setCalendarToEndOfYear(Calendar theCal) {
      setCalendarToStartOfYear(theCal);
      theCal.set(2, theCal.getActualMaximum(2));
      setCalendarToEndOfMonth(theCal);
   }
}
