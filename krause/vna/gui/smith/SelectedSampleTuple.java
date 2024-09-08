package krause.vna.gui.smith;

import java.awt.Color;

public class SelectedSampleTuple {
   private int index;
   private Color color;
   private String name;

   public SelectedSampleTuple(int idx, Color col, String pName) {
      this.index = idx;
      this.color = col;
      this.name = pName;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public int getIndex() {
      return this.index;
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public Color getColor() {
      return this.color;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}
