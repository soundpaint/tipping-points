/*
 * @(#)SpeedControl.java 1.00 20/05/21
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

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpeedControl extends SpringControl implements ChangeListener
{
  private static final long serialVersionUID = -5725883259743817595L;

  public SpeedControl()
  {
    super(100, SwingConstants.HORIZONTAL, false);
  }

  @Override
  public void stateChanged(final ChangeEvent e)
  {
    final Object source = e.getSource();
    if (source instanceof Simulation) {
      final Simulation simulation = (Simulation)source;
      final double speed = simulation.getSpeed();
      setNormalizedValue(0.5 * speed + 0.5);
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
