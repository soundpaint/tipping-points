/*
 * @(#)HysteresisListener.java 1.00 20/05/04
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

public interface HysteresisListener
{
  void limbsChanged(final double leftLimbLowerValue,
                    final double leftLimbUpperValue,
                    final double rightLimbLowerValue,
                    final double rightLimbUpperValue);

  void stateChanged(final HysteresisView source,
                    final double value, final HysteresisModel.State state);
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
