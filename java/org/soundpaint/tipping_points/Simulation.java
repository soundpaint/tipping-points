/*
 * @(#)Simulation.java 1.00 20/05/09
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Simulation implements Runnable, ChangeListener
{
  private enum Status {
    START_REQUESTED,
    RUNNING,
    STOP_REQUESTED,
    SLEEPING
  };

  private final ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
  private HysteresisModel hysteresis;
  private Status status;
  private double speed;
  private double acceleration;
  private Thread thread;
  private List<ChangeListener> speedChangeListeners;

  public Simulation(final HysteresisModel hysteresis)
  {
    Objects.requireNonNull(hysteresis);
    this.hysteresis = hysteresis;
    status = Status.SLEEPING;
    speed = 0.002;
    acceleration = 0.0;
    thread = new Thread(this);
    speedChangeListeners = new ArrayList<ChangeListener>();
  }

  private static final double MAX_SPEED = 0.04;

  public double getSpeed()
  {
    return speed / MAX_SPEED;
  }

  public double getAcceleration()
  {
    return acceleration;
  }

  public void startThread()
  {
    thread.start();
  }

  public void skipToStart()
  {
    hysteresis.valueChanged(null, hysteresis.getMinValue());
  }

  public void skipToEnd()
  {
    hysteresis.valueChanged(null, hysteresis.getMaxValue());
  }

  public synchronized boolean requestStart()
  {
    if (status == Status.SLEEPING) {
      status = Status.START_REQUESTED;
      return true;
    }
    return false;
  }

  public synchronized boolean requestStop()
  {
    if (status == Status.RUNNING) {
      status = Status.STOP_REQUESTED;
      return true;
    }
    return false;
  }

  private void updateSpeed()
  {
    speed = Math.max(-MAX_SPEED, Math.min(MAX_SPEED, speed + acceleration));
    for (final ChangeListener listener : speedChangeListeners) {
      listener.stateChanged(CHANGE_EVENT);
    }
  }

  private void doContinue()
  {
    while (status != Status.START_REQUESTED) {
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
        // ignore
      }
    }
    status = Status.RUNNING;
    while (status == Status.RUNNING) {
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
        // ignore
      }
      updateSpeed();
      if (speed != 0.0) {
        final double value = hysteresis.getValue() + speed;
        SwingUtilities.
          invokeLater(() -> hysteresis.valueChanged(null, value));
      }
    }
  }

  private void doPause()
  {
    while (status != Status.STOP_REQUESTED) {
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
        // ignore
      }
    }
    status = Status.SLEEPING;
    while (status == Status.SLEEPING) {
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
        // ignore
      }
      updateSpeed();
    }
  }

  public void run()
  {
    status = Status.STOP_REQUESTED;
    while (true) {
      doPause();
      doContinue();
    }
  }

  public void addSpeedChangeListener(final ChangeListener listener)
  {
    Objects.requireNonNull(listener);
    speedChangeListeners.add(listener);
    listener.stateChanged(CHANGE_EVENT);
  }

  public void removeSpeedChangeListener(final ChangeListener listener)
  {
    speedChangeListeners.remove(listener);
  }

  @Override
  public void stateChanged(final ChangeEvent e)
  {
    final Object source = e.getSource();
    if (source == this) {
      return; // avoid endless loops
    }
    if (source instanceof SpringControl) {
      final SpringControl accelerationControl = (SpringControl)source;
      final double normalizedValue = accelerationControl.getNormalizedValue();
      final double symmetricValue = 2.0 * normalizedValue - 1.0;
      final double exponentialValue =
        1.0 / Math.E * symmetricValue *
        Math.exp(Math.pow(symmetricValue, 2.0));
      acceleration = 0.001 * exponentialValue;
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
