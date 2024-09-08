package krause.vna.gui.generator.digit;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import krause.util.ras.logging.TraceHelper;

public class VNADigitTextFieldHandler implements MouseWheelListener, MouseListener {
   private long minValue;
   private long maxValue;
   private long value;
   private long oldValue;
   private HashMap<Long, VNADigitTextField> fieldMap = new HashMap(11);
   protected EventListenerList listenerList = new EventListenerList();
   private transient ChangeEvent changeEvent;

   public VNADigitTextFieldHandler(long minVal, long maxVal) {
      this.minValue = minVal;
      this.maxValue = maxVal;
   }

   private void changeFrequencyByField(VNADigitTextField fld, int counts) {
      long locFreq = this.getValue();
      locFreq += fld.getFactor() * (long)counts;
      if (locFreq > this.getMaxValue()) {
         Toolkit.getDefaultToolkit().beep();
      } else if (locFreq < this.getMinValue()) {
         Toolkit.getDefaultToolkit().beep();
      } else {
         this.setOldValue(this.getValue());
         this.setValue(locFreq);
      }

      this.fireStateChanged();
   }

   public void mouseWheelMoved(MouseWheelEvent e) {
      VNADigitTextField fld = (VNADigitTextField)e.getSource();
      int amt = e.getScrollAmount();
      if (e.getUnitsToScroll() != 0) {
         amt /= e.getUnitsToScroll();
      }

      this.changeFrequencyByField(fld, amt);
   }

   public void mouseClicked(MouseEvent e) {
      TraceHelper.entry(this, "mouseClicked", "" + e.getButton());
      TraceHelper.text(this, "mouseClicked", "" + e.getClickCount());
      VNADigitTextField fld = (VNADigitTextField)e.getSource();
      if (e.getClickCount() > 0) {
         if (e.getButton() == 1) {
            this.changeFrequencyByField(fld, 1);
         } else if (e.getButton() == 3) {
            this.changeFrequencyByField(fld, -1);
         }
      }

      TraceHelper.exit(this, "mouseClicked");
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }

   public VNADigitTextField registerField(VNADigitTextField textField) {
      this.fieldMap.put(textField.getFactor(), textField);
      textField.addMouseListener(this);
      textField.addMouseWheelListener(this);
      return textField;
   }

   public long getMinValue() {
      return this.minValue;
   }

   public void setMinValue(long minFreq) {
      this.minValue = minFreq;
   }

   public long getMaxValue() {
      return this.maxValue;
   }

   public void setMaxValue(long maxFreq) {
      this.maxValue = maxFreq;
   }

   public void setValue(long val) {
      if (val < this.minValue) {
         this.oldValue = this.value;
         this.value = this.minValue;
         Toolkit.getDefaultToolkit().beep();
      } else if (val > this.maxValue) {
         this.oldValue = this.value;
         this.value = this.maxValue;
         Toolkit.getDefaultToolkit().beep();
      } else {
         this.oldValue = this.value;
         this.value = val;
      }

      this.updateFields();
   }

   private void updateFields() {
      int decades = Double.valueOf(Math.log10((double)this.getMaxValue() * 1.0D)).intValue() + 1;
      long j = 1L;
      long frq = this.getValue();

      for(int i = 0; i < decades; ++i) {
         long currDigit = frq / j % 10L;
         VNADigitTextField currField = (VNADigitTextField)this.fieldMap.get(j);
         currField.setValue(currDigit);
         j *= 10L;
      }

   }

   public long getValue() {
      return this.value;
   }

   public void addChangeListener(ChangeListener l) {
      this.listenerList.add(ChangeListener.class, l);
   }

   public void removeChangeListener(ChangeListener l) {
      this.listenerList.remove(ChangeListener.class, l);
   }

   protected void fireStateChanged() {
      Object[] listeners = this.listenerList.getListenerList();

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ChangeListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((ChangeListener)listeners[i + 1]).stateChanged(this.changeEvent);
         }
      }

   }

   public void setOldValue(long oldValue) {
      this.oldValue = oldValue;
   }

   public long getOldValue() {
      return this.oldValue;
   }
}
