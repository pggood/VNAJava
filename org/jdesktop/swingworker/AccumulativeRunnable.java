package org.jdesktop.swingworker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

abstract class AccumulativeRunnable<T> implements Runnable {
   private List<T> arguments = null;

   protected abstract void run(List<T> var1);

   public final void run() {
      this.run(this.flush());
   }

   @SafeVarargs
   public final synchronized void add(boolean isPrepend, T... args) {
      boolean isSubmitted = true;
      if (this.arguments == null) {
         isSubmitted = false;
         this.arguments = new ArrayList();
      }

      if (isPrepend) {
         this.arguments.addAll(0, Arrays.asList(args));
      } else {
         Collections.addAll(this.arguments, args);
      }

      if (!isSubmitted) {
         this.submit();
      }

   }

   @SafeVarargs
   public final void add(T... args) {
      this.add(false, args);
   }

   protected void submit() {
      SwingUtilities.invokeLater(this);
   }

   private final synchronized List<T> flush() {
      List<T> list = this.arguments;
      this.arguments = null;
      return list;
   }
}
