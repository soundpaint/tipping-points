/*
 * @(#)Slider.java 1.00 20/05/15
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
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class HysteresisSlider extends HysteresisView implements ChangeListener
{
  private static final long serialVersionUID = 6124033528861678396L;

  private final Slider slider;

  public HysteresisSlider(final HysteresisModel hysteresis)
  {
    this(hysteresis, SwingConstants.HORIZONTAL);
  }

  public HysteresisSlider(final HysteresisModel hysteresis,
                          final int orientation)
  {
    this(hysteresis, orientation, 0, 100, 50);
  }

  public HysteresisSlider(final HysteresisModel hysteresis,
                          final int min,
                          final int max)
  {
    this(hysteresis, min, max, (min + max) / 2);
  }

  public HysteresisSlider(final HysteresisModel hysteresis,
                          final int min,
                          final int max,
                          final int value)
  {
    this(hysteresis, SwingConstants.HORIZONTAL, min, max, value);
  }

  public HysteresisSlider(final HysteresisModel hysteresis,
                          final int orientation,
                          final int min,
                          final int max,
                          final int value)
  {
    super(hysteresis);
    setLayout(new BorderLayout());
    slider = new Slider(orientation, min, max, value);
    slider.addChangeListener(this);
    add(slider);
  }

  /**
   * This method is called when the underlying JSlider has changed its
   * value (ususally due to the user moving the slider).  When called,
   * this method updates the hysteresis state accordingly.
   */
  @Override
  public void stateChanged(final ChangeEvent e)
  {
    final int sliderMinValue = slider.getMinimum();
    final int sliderMaxValue = slider.getMaximum();
    final int sliderExtent = sliderMaxValue - sliderMinValue;
    final int sliderValue = slider.getValue();
    final double minValue = getMinValue();
    final double maxValue = getMaxValue();
    final double normalizedValue =
      sliderExtent != 0 ?
      ((double)(sliderValue - sliderMinValue)) / sliderExtent : 0.0;
    domainValueChanged(minValue + normalizedValue * (maxValue - minValue));
  }

  /**
   * This method is called when the underlying JSlider's value needs
   * to be updated by an external event, such as from the simulation
   * control.
   */
  @Override
  public void stateChanged(final HysteresisView source,
                           final double value,
                           final HysteresisModel.State state)
  {
    if (slider == null) {
      return; // not yet initialized
    }
    final int sliderMinValue = slider.getMinimum();
    final int sliderMaxValue = slider.getMaximum();
    final int sliderExtent = sliderMaxValue - sliderMinValue;
    final double minValue = getMinValue();
    final double maxValue = getMaxValue();
    final int sliderValue =
      sliderMinValue +
      (int)Math.round(sliderExtent *
                      (value - minValue) / (maxValue - minValue));
    slider.setValue(sliderValue);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
