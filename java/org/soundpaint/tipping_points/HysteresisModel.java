/*
 * @(#)HysteresisModel.java 1.00 20/05/04
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

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class HysteresisModel
{
  public enum State {
    ON_LOWER_CURVE(0.0),
    ON_UPPER_CURVE(1.0);

    private final double value;

    State(final double value)
    {
      this.value = value;
    }

    public double getValue()
    {
      return value;
    }
  };

  private final List<HysteresisListener> listeners;
  private double minValue;
  private double maxValue;
  private double leftLimbLowerValue;
  private double leftLimbUpperValue;
  private double rightLimbLowerValue;
  private double rightLimbUpperValue;
  private double value;
  private State state;

  public HysteresisModel()
  {
    this(0.0, 1.0, 0.30, 0.45, 0.55, 0.70);
  }

  public HysteresisModel(final double minValue,
                    final double maxValue,
                    final double leftLimbLowerValue,
                    final double leftLimbUpperValue,
                    final double rightLimbLowerValue,
                    final double rightLimbUpperValue)
  {
    listeners = new ArrayList<HysteresisListener>();
    checkAndSetNewValueRange(minValue, maxValue);
    checkAndSetNewLimbs(leftLimbLowerValue, leftLimbUpperValue,
                        rightLimbLowerValue, rightLimbUpperValue);
    checkAndSetValue(minValue);
  }

  private void initListener(final HysteresisListener listener)
  {
    listener.limbsChanged(leftLimbLowerValue, leftLimbUpperValue,
                          rightLimbLowerValue, rightLimbUpperValue);
    listener.stateChanged(null, value, state);
  }

  public void addListener(final HysteresisListener listener)
  {
    listeners.add(listener);
    SwingUtilities.invokeLater(() -> initListener(listener));
  }

  public boolean removeListener(final HysteresisListener listener)
  {
    return listeners.remove(listener);
  }

  public double getMinValue()
  {
    return minValue;
  }

  public double getMaxValue()
  {
    return maxValue;
  }

  private void checkAndSetNewValueRange(final double minValue,
                                        final double maxValue)
  {
    if (minValue > maxValue) {
      throw new IllegalArgumentException("minValue must be beneath maxValue");
    }
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  private void checkAndSetNewLimbs(final double leftLimbLowerValue,
                                   final double leftLimbUpperValue,
                                   final double rightLimbLowerValue,
                                   final double rightLimbUpperValue)
  {
    if (minValue > leftLimbLowerValue) {
      throw new IllegalArgumentException("min value must be beneath lower value of left limb");
    }
    if (leftLimbLowerValue > leftLimbUpperValue) {
      throw new IllegalArgumentException("lower value of left limb must be beneath upper value of left limb");
    }
    if (leftLimbLowerValue > rightLimbLowerValue) {
      throw new IllegalArgumentException("lower value of left limb must be beneath lower value of right limb");
    }
    if (leftLimbUpperValue > rightLimbUpperValue) {
      throw new IllegalArgumentException("upper value of left limb must be beneath upper value of right limb");
    }
    if (rightLimbLowerValue > rightLimbUpperValue) {
      throw new IllegalArgumentException("lower value of right limb must be beneath upper value of right limb");
    }
    if (rightLimbUpperValue > maxValue) {
      throw new IllegalArgumentException("upper value of right limb must be beneath max value");
    }
    this.leftLimbLowerValue = leftLimbLowerValue;
    this.leftLimbUpperValue = leftLimbUpperValue;
    this.rightLimbLowerValue = rightLimbLowerValue;
    this.rightLimbUpperValue = rightLimbUpperValue;
  }

  public void checkAndSetLeftLimbLowerValue(final double leftLimbLowerValue)
  {
    checkAndSetNewLimbs(leftLimbLowerValue, leftLimbUpperValue,
                        rightLimbLowerValue, rightLimbUpperValue);
  }

  public void checkAndSetLeftLimbUpperValue(final double leftLimbUpperValue)
  {
    checkAndSetNewLimbs(leftLimbLowerValue, leftLimbUpperValue,
                        rightLimbLowerValue, rightLimbUpperValue);
  }

  public void checkAndSetRightLimbLowerValue(final double rightLimbLowerValue)
  {
    checkAndSetNewLimbs(leftLimbLowerValue, leftLimbUpperValue,
                        rightLimbLowerValue, rightLimbUpperValue);
  }

  public void checkAndSetRightLimbUpperValue(final double rightLimbUpperValue)
  {
    checkAndSetNewLimbs(leftLimbLowerValue, leftLimbUpperValue,
                        rightLimbLowerValue, rightLimbUpperValue);
  }

  public double getValue()
  {
    return value;
  }

  private void checkAndSetValue(final double value)
  {
    if (value < minValue) {
      throw new IllegalArgumentException("value must not be beneath minValue");
    }
    if (value > maxValue) {
      throw new IllegalArgumentException("value must not be beyond maxValue");
    }
    setValue(value);
  }

  /**
   * Returns <code>true</code> if the new value caused this object's
   * internal state to change.
   */
  public boolean setValue(final double value)
  {
    final double newValue = Math.min(Math.max(value, minValue), maxValue);
    final State newState;
    if (newValue <= leftLimbLowerValue) {
      newState = State.ON_LOWER_CURVE;
    } else if (newValue > rightLimbUpperValue) {
      newState = State.ON_UPPER_CURVE;
    } else {
      newState = state;
    }
    final boolean changed =
      (newValue != this.value) || (newState != this.state);
    this.value = newValue;
    this.state = newState;
    return changed;
  }

  public void valueChanged(final HysteresisView source, final double value)
  {
    if (setValue(value)) {
      for (final HysteresisListener listener : listeners) {
        if (listener != source) {
          listener.stateChanged(source, this.value, this.state);
        }
      }
    }
  }

  public State getState()
  {
    return state;
  }

  private void limbsChanged()
  {
    for (final HysteresisListener listener : listeners) {
      listener.limbsChanged(leftLimbLowerValue, leftLimbUpperValue,
                            rightLimbLowerValue, rightLimbUpperValue);
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
