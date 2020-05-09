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

import java.util.Objects;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

public class Simulation implements Runnable, SimulationControl
{
  private enum Status {
    START_REQUESTED,
    RUNNING,
    STOP_REQUESTED,
    SLEEPING
  };

  private HysteresisModel hysteresis;
  private Status status;
  private double speed;
  private double acceleration;

  public Simulation(final HysteresisModel hysteresis)
  {
    Objects.requireNonNull(hysteresis);
    this.hysteresis = hysteresis;
    status = Status.SLEEPING;
    speed = 0.002;
    acceleration = 0.0;
  }

  private static final double MAX_SPEED = 0.04;

  public void run()
  {
    status = Status.RUNNING;
    while (status != Status.STOP_REQUESTED) {
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
        // ignore
      }
      speed = Math.max(-MAX_SPEED, Math.min(MAX_SPEED, speed + acceleration));
      if (speed != 0.0) {
        final double value = hysteresis.getValue() + speed;
        SwingUtilities.
          invokeLater(() -> hysteresis.valueChanged(null, value));
      }
    }
    status = Status.SLEEPING;
  }

  @Override
  public void stateChanged(final ChangeEvent e)
  {
    final Object source = e.getSource();
    if (source instanceof SpringControl) {
      final SpringControl springControl = (SpringControl)source;
      final double normalizedValue = springControl.getNormalizedValue();
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
