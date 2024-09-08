package krause.vna.gui.panels.marker;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public interface IMarkerEventEvaluator {
   boolean isMyMouseEvent(MouseEvent var1);

   boolean isMyMouseWheelEvent(MouseWheelEvent var1);
}
