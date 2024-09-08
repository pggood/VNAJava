package com.l2fprod.common.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class PercentLayout implements LayoutManager2 {
   public static final int HORIZONTAL = 0;
   public static final int VERTICAL = 1;
   private static final PercentLayout.Constraint REMAINING_SPACE = new PercentLayout.Constraint("*", (PercentLayout.Constraint)null);
   private static final PercentLayout.Constraint PREFERRED_SIZE = new PercentLayout.Constraint("", (PercentLayout.Constraint)null);
   private int orientation;
   private int gap;
   private Hashtable<Component, PercentLayout.Constraint> componentToConstraint;

   public PercentLayout() {
      this(0, 0);
   }

   public PercentLayout(int orientation, int gap) {
      this.setOrientation(orientation);
      this.gap = gap;
      this.componentToConstraint = new Hashtable();
   }

   public void setGap(int gap) {
      this.gap = gap;
   }

   public int getGap() {
      return this.gap;
   }

   public void setOrientation(int orientation) {
      if (orientation != 0 && orientation != 1) {
         throw new IllegalArgumentException("Orientation must be one of HORIZONTAL or VERTICAL");
      } else {
         this.orientation = orientation;
      }
   }

   public int getOrientation() {
      return this.orientation;
   }

   public PercentLayout.Constraint getConstraint(Component component) {
      return (PercentLayout.Constraint)this.componentToConstraint.get(component);
   }

   public void setConstraint(Component component, Object constraints) {
      if (constraints instanceof PercentLayout.Constraint) {
         this.componentToConstraint.put(component, (PercentLayout.Constraint)constraints);
      } else if (constraints instanceof Number) {
         this.setConstraint(component, new PercentLayout.NumberConstraint(((Number)constraints).intValue()));
      } else if ("*".equals(constraints)) {
         this.setConstraint(component, REMAINING_SPACE);
      } else if ("".equals(constraints)) {
         this.setConstraint(component, PREFERRED_SIZE);
      } else if (constraints instanceof String) {
         String s = (String)constraints;
         if (s.endsWith("%")) {
            float value = Float.valueOf(s.substring(0, s.length() - 1)) / 100.0F;
            if (value > 1.0F || value < 0.0F) {
               throw new IllegalArgumentException("percent value must be >= 0 and <= 100");
            }

            this.setConstraint(component, new PercentLayout.PercentConstraint(value));
         } else {
            this.setConstraint(component, new PercentLayout.NumberConstraint(Integer.valueOf(s)));
         }
      } else {
         if (constraints != null) {
            throw new IllegalArgumentException("Invalid Constraint");
         }

         this.setConstraint(component, PREFERRED_SIZE);
      }

   }

   public void addLayoutComponent(Component component, Object constraints) {
      this.setConstraint(component, constraints);
   }

   public float getLayoutAlignmentX(Container target) {
      return 0.5F;
   }

   public float getLayoutAlignmentY(Container target) {
      return 0.5F;
   }

   public void invalidateLayout(Container target) {
   }

   public void addLayoutComponent(String name, Component comp) {
   }

   public void removeLayoutComponent(Component comp) {
      this.componentToConstraint.remove(comp);
   }

   public Dimension minimumLayoutSize(Container parent) {
      return this.preferredLayoutSize(parent);
   }

   public Dimension maximumLayoutSize(Container parent) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public Dimension preferredLayoutSize(Container parent) {
      Component[] components = parent.getComponents();
      Insets insets = parent.getInsets();
      int width = 0;
      int height = 0;
      boolean firstVisibleComponent = true;
      int i = 0;

      for(int c = components.length; i < c; ++i) {
         if (components[i].isVisible()) {
            Dimension componentPreferredSize = components[i].getPreferredSize();
            if (this.orientation == 0) {
               height = Math.max(height, componentPreferredSize.height);
               width += componentPreferredSize.width;
               if (firstVisibleComponent) {
                  firstVisibleComponent = false;
               } else {
                  width += this.gap;
               }
            } else {
               height += componentPreferredSize.height;
               width = Math.max(width, componentPreferredSize.width);
               if (firstVisibleComponent) {
                  firstVisibleComponent = false;
               } else {
                  height += this.gap;
               }
            }
         }
      }

      return new Dimension(width + insets.right + insets.left, height + insets.top + insets.bottom);
   }

   public void layoutContainer(Container parent) {
      Insets insets = parent.getInsets();
      Dimension d = parent.getSize();
      d.width = d.width - insets.left - insets.right;
      d.height = d.height - insets.top - insets.bottom;
      Component[] components = parent.getComponents();
      int[] sizes = new int[components.length];
      int totalSize = (this.orientation == 0 ? d.width : d.height) - (components.length - 1) * this.gap;
      int availableSize = totalSize;
      int remainingSize = 0;

      int i;
      for(i = components.length; remainingSize < i; ++remainingSize) {
         if (components[remainingSize].isVisible()) {
            PercentLayout.Constraint constraint = (PercentLayout.Constraint)this.componentToConstraint.get(components[remainingSize]);
            if (constraint != null && constraint != PREFERRED_SIZE) {
               if (constraint instanceof PercentLayout.NumberConstraint) {
                  sizes[remainingSize] = ((PercentLayout.NumberConstraint)constraint).intValue();
                  availableSize -= sizes[remainingSize];
               }
            } else {
               sizes[remainingSize] = this.orientation == 0 ? components[remainingSize].getPreferredSize().width : components[remainingSize].getPreferredSize().height;
               availableSize -= sizes[remainingSize];
            }
         }
      }

      remainingSize = availableSize;
      i = 0;

      int currentOffset;
      for(currentOffset = components.length; i < currentOffset; ++i) {
         if (components[i].isVisible()) {
            PercentLayout.Constraint constraint = (PercentLayout.Constraint)this.componentToConstraint.get(components[i]);
            if (constraint instanceof PercentLayout.PercentConstraint) {
               sizes[i] = (int)((float)remainingSize * ((PercentLayout.PercentConstraint)constraint).floatValue());
               availableSize -= sizes[i];
            }
         }
      }

      ArrayList<Integer> remaining = new ArrayList();
      currentOffset = 0;

      i = 0;
      for(i = components.length; currentOffset < i; ++currentOffset) {
         if (components[currentOffset].isVisible()) {
            PercentLayout.Constraint constraint = (PercentLayout.Constraint)this.componentToConstraint.get(components[currentOffset]);
            if (constraint == REMAINING_SPACE) {
               remaining.add(currentOffset);
               sizes[currentOffset] = 0;
            }
         }
      }

      if (!remaining.isEmpty()) {
         currentOffset = availableSize / remaining.size();

         for(Iterator iter = remaining.iterator(); iter.hasNext(); sizes[(Integer)iter.next()] = currentOffset) {
         }
      }

      currentOffset = this.orientation == 0 ? insets.left : insets.top;
      i = 0;

      for(int c = components.length; i < c; ++i) {
         if (components[i].isVisible()) {
            if (this.orientation == 0) {
               components[i].setBounds(currentOffset, insets.top, sizes[i], d.height);
            } else {
               components[i].setBounds(insets.left, currentOffset, d.width, sizes[i]);
            }

            currentOffset += this.gap + sizes[i];
         }
      }

   }

   static class Constraint {
      protected Object value;

      private Constraint(Object value) {
         this.value = value;
      }

      // $FF: synthetic method
      Constraint(Object var1, PercentLayout.Constraint var2) {
         this(var1);
      }

      // $FF: synthetic method
      Constraint(Object var1, PercentLayout.Constraint var2, PercentLayout.Constraint var3) {
         this(var1);
      }
   }

   static class NumberConstraint extends PercentLayout.Constraint {
      public NumberConstraint(int d) {
         super(d);
      }

      public NumberConstraint(Integer d) {
         super(d, (PercentLayout.Constraint)null, (PercentLayout.Constraint)null);
      }

      public int intValue() {
         return (Integer)this.value;
      }
   }

   static class PercentConstraint extends PercentLayout.Constraint {
      public PercentConstraint(float d) {
         super(d, (PercentLayout.Constraint)null, (PercentLayout.Constraint)null);
      }

      public float floatValue() {
         return (Float)this.value;
      }
   }
}
