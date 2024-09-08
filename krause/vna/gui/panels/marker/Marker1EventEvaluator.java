package krause.vna.gui.panels.marker;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class Marker1EventEvaluator implements IMarkerEventEvaluator {
   public boolean isMyMouseEvent(MouseEvent e) {
      return e.getButton() == 1 && (e.getModifiersEx() & 704) == 64;
   }

   public boolean isMyMouseWheelEvent(MouseWheelEvent e) {
      return (e.getModifiersEx() & 704) == 64;
   }
}
