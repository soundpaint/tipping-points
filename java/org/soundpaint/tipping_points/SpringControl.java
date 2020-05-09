/*
 * @(#)SpringControl.java 1.00 20/05/08
 *
 * Copyright (C) 2020 Jürgen Reuter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.soundpaint.tipping_points;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpringControl extends JPanel
{
  private static final long serialVersionUID = -1973529752957750131L;

  private static final int PIVOT_BG_RADIUS = 15;
  private static final int PIVOT_BG_DIAMETER = 2 * PIVOT_BG_RADIUS;
  private static final int PIVOT_FG_RADIUS = 5;
  private static final int PIVOT_FG_DIAMETER = 2 * PIVOT_FG_RADIUS;
  private static final int KNOB_RADIUS = 10;
  private static final int KNOB_DIAMETER = 2 * KNOB_RADIUS;
  private static final int SLIT_WIDTH = 5;
  private static final int SLIT_PADDING = 30;
  private static final String LABEL_DECREMENT = "-";
  private static final String LABEL_INCREMENT = "+";

  /** defines spring range of values [-maxValue .. +maxValue] */
  private final int maxValue;

  /** orientation */
  private final boolean horizontal;

  /** spring value in the range [-maxValue .. +maxValue] */
  private int value;

  private boolean mouseHoldActive;

  private SpringControl()
  {
    throw new UnsupportedOperationException("unsupported default constructor");
  }

  public SpringControl(final int maxValue)
  {
    this(maxValue, SwingConstants.HORIZONTAL);
  }

  public SpringControl(final int maxValue, final int orientation)
  {
    if (maxValue < 0) {
      throw new IllegalArgumentException("maxValue must be non-negative");
    }
    this.maxValue = maxValue;
    if ((orientation != SwingConstants.HORIZONTAL) &&
        (orientation != SwingConstants.VERTICAL)) {
      throw new IllegalArgumentException("orientation must be one of SwingConstants.HORIZONTAL or SwingConstants.VERTICAL");
    }
    horizontal = orientation == SwingConstants.HORIZONTAL;
    setPreferredSize(new Dimension(10, 30));
    final MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mousePressed(final MouseEvent e) {
          mouseHoldActive = true;
          if (SwingUtilities.isLeftMouseButton(e) &&
              cursorChanged(e.getX(), e.getY())) {
            SwingUtilities.getRoot(SpringControl.this).repaint();
            fireStateChanged();
          }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
          mouseHoldActive = false;
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
          if (SwingUtilities.isLeftMouseButton(e) &&
              cursorChanged(e.getX(), e.getY())) {
            SwingUtilities.getRoot(SpringControl.this).repaint();
            fireStateChanged();
          }
        }
      };
    addMouseMotionListener(mouseAdapter);
    addMouseListener(mouseAdapter);

    final ActionListener equilibrator = new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          if (!mouseHoldActive) {
            if (value != 0) {
              value /= 2;
              fireStateChanged();
              repaint();
            }
          }
        }
      };
    new Timer(50, equilibrator).start();
  }

  private boolean cursorChanged(final int mouseX, final int mouseY)
  {
    final int halfWidth = getWidth() >> 1;
    final int halfHeight = getHeight() >> 1;
    final int maxDisplayValue =
      horizontal ?
      halfWidth - SLIT_PADDING :
      halfHeight - SLIT_PADDING;
    final int unboundedDisplayValue =
      horizontal ?
      mouseX - halfWidth :
      -mouseY + halfHeight;
    final int displayValue =
      Math.max(Math.min(unboundedDisplayValue, maxDisplayValue),
               -maxDisplayValue);
    value = maxValue * displayValue / maxDisplayValue;
    return true;
  }

  private void fill3DRect(final Graphics2D g2d,
                          final int x, final int y,
                          final int width, final int height,
                          final boolean raised)
  {
    if (width < 0) {
      fill3DRect(g2d, x + width, y, -width, height, raised);
    } else if (height < 0) {
      fill3DRect(g2d, x, y + height, width, -height, raised);
    } else {
      g2d.fill3DRect(x, y, width, height, raised);
    }
  }


  @Override
  protected void paintComponent(final Graphics g)
  {
    super.paintComponent(g);
    final int width = getSize().width;
    final int height = getSize().height;

    final int centerX = width >> 1;
    final int centerY = height >> 1;
    final Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                         RenderingHints.VALUE_ANTIALIAS_ON);

    // slit base
    g2d.setColor(Color.WHITE);
    if (horizontal) {
      g2d.fill3DRect(SLIT_PADDING, centerY - (SLIT_WIDTH >> 1),
                     width - (SLIT_PADDING << 1), SLIT_WIDTH, true);
    } else {
      g2d.fill3DRect(centerX - (SLIT_WIDTH >> 1), SLIT_PADDING,
                     SLIT_WIDTH, height - (SLIT_PADDING << 1), true);
    }

    // thumb
    final int maxDisplayValue =
      horizontal ?
      (width >> 1) - SLIT_PADDING :
      (height >> 1) - SLIT_PADDING;
    final int displayValue = value * maxDisplayValue / maxValue;
    final int displayValueX = horizontal ? displayValue : 0;
    final int displayValueY = horizontal ? 0 : -displayValue;

    // slit highlighted
    g2d.setColor(Color.LIGHT_GRAY);
    if (horizontal) {
      fill3DRect(g2d, centerX, centerY - (SLIT_WIDTH >> 1),
                 displayValueX, SLIT_WIDTH, true);
    } else {
      fill3DRect(g2d, centerX - (SLIT_WIDTH >> 1), centerY,
                 SLIT_WIDTH, displayValueY, true);
    }

    // pivot background
    g2d.setColor(Color.LIGHT_GRAY);
    g2d.fillOval(centerX - PIVOT_BG_RADIUS,
                 centerY - PIVOT_BG_RADIUS,
                 PIVOT_BG_DIAMETER, PIVOT_BG_DIAMETER);

    // thumb
    g2d.setColor(Color.BLACK);
    g2d.fillOval(centerX - KNOB_RADIUS + displayValueX,
                 centerY - KNOB_RADIUS + displayValueY,
                 KNOB_DIAMETER, KNOB_DIAMETER);

    // pivot foreground
    g2d.setColor(Color.GRAY);
    g2d.fillOval(centerX - PIVOT_FG_RADIUS,
                 centerY - PIVOT_FG_RADIUS,
                 PIVOT_FG_DIAMETER, PIVOT_FG_DIAMETER);

    // labels
    final FontMetrics fontMetrics = g2d.getFontMetrics();
    final int fontHeight = fontMetrics.getHeight();
    final int incWidth = fontMetrics.stringWidth(LABEL_INCREMENT);
    final int decWidth = fontMetrics.stringWidth(LABEL_DECREMENT);
    g2d.setColor(Color.BLACK);
    if (horizontal) {
      g2d.drawString(LABEL_DECREMENT,
                     SLIT_PADDING / 2 - KNOB_RADIUS / 2,
                     centerY + fontHeight / 3);
      g2d.drawString(LABEL_INCREMENT,
                     width - incWidth - SLIT_PADDING / 2 + KNOB_RADIUS / 2,
                     centerY + fontHeight / 3);
    } else {
      g2d.drawString(LABEL_DECREMENT,
                     centerX - KNOB_RADIUS - decWidth,
                     fontHeight + SLIT_PADDING / 2);
      g2d.drawString(LABEL_INCREMENT,
                     centerX - KNOB_RADIUS - incWidth / 2,
                     height - SLIT_PADDING / 2);
    }
  }

  public void addChangeListener(final ChangeListener listener)
  {
    listenerList.add(ChangeListener.class, listener);
  }

  public void removeChangeListener(final ChangeListener listener)
  {
    listenerList.remove(ChangeListener.class, listener);
  }

  protected void fireStateChanged()
  {
    final ChangeEvent e = new ChangeEvent(this);
    final Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ChangeListener.class) {
        ((ChangeListener)listeners[i + 1]).stateChanged(e);
      }
    }
  }

  public int getValue()
  {
    return value;
  }

  public double getNormalizedValue()
  {
    return 0.5 * (value + maxValue) / maxValue;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
