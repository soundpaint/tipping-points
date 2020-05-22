/*
 * @(#)Slider.java 1.00 20/05/08
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

import java.awt.BorderLayout;
import java.util.Hashtable;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Slider extends JPanel implements ChangeListener
{
  private static final long serialVersionUID = 5824895163027170151L;

  private final JSlider slider;

  public Slider()
  {
    this(SwingConstants.HORIZONTAL);
  }

  public Slider(final int orientation)
  {
    this(orientation, 0, 100, 50);
  }

  public Slider(final int min,
                final int max)
  {
    this(min, max, (min + max) / 2);
  }

  public Slider(final int min,
                final int max,
                final int value)
  {
    this(SwingConstants.HORIZONTAL, min, max, value);
  }

  public Slider(final int orientation,
                final int min,
                final int max,
                final int value)
  {
    super(new BorderLayout());
    slider = new JSlider(orientation, min, max, value);
    var labels = new Hashtable<Integer, JComponent>();
    labels.put(0, new JLabel("min"));
    labels.put(100, new JLabel("max"));
    slider.setLabelTable(labels);
    slider.setPaintLabels(true);
    slider.addChangeListener(this);
    add(slider);
  }

  public void addChangeListener(final ChangeListener listener)
  {
    listenerList.add(ChangeListener.class, listener);
  }

  public void removeChangeListener(final ChangeListener listener)
  {
    listenerList.remove(ChangeListener.class, listener);
  }

  public int getMinimum()
  {
    return slider.getMinimum();
  }

  public int getMaximum()
  {
    return slider.getMaximum();
  }

  public int getValue()
  {
    return slider.getValue();
  }

  public void setValue(final int value)
  {
    slider.setValue(value);
  }

  /**
   * This method is called when the underlying JSlider has changed its
   * value (ususally due to the user moving the slider).  When called,
   * this method updates the hysteresis state accordingly.
   */
  @Override
  public void stateChanged(final ChangeEvent e)
  {
    final ChangeEvent event = new ChangeEvent(this);
    final Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ChangeListener.class) {
        ((ChangeListener)listeners[i + 1]).stateChanged(e);
      }
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
