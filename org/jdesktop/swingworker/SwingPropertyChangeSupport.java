package org.jdesktop.swingworker;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;

public final class SwingPropertyChangeSupport extends PropertyChangeSupport {
   static final long serialVersionUID = 7162625831330845068L;
   private final boolean notifyOnEDT;

   public SwingPropertyChangeSupport(Object sourceBean) {
      this(sourceBean, false);
   }

   public SwingPropertyChangeSupport(Object sourceBean, boolean notifyOnEDT) {
      super(sourceBean);
      this.notifyOnEDT = notifyOnEDT;
   }

   public void firePropertyChange(final PropertyChangeEvent evt) {
      if (evt == null) {
         throw new NullPointerException();
      } else {
         if (this.isNotifyOnEDT() && !SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  SwingPropertyChangeSupport.this.firePropertyChange(evt);
               }
            });
         } else {
            super.firePropertyChange(evt);
         }

      }
   }

   public final boolean isNotifyOnEDT() {
      return this.notifyOnEDT;
   }
}
