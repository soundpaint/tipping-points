/*
 * @(#)SimulationControlPane.java 1.00 20/05/10
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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.SwingConstants;

public class SimulationControlPane extends JPanel
{
  private static final long serialVersionUID = -4328669704695312462L;

  private final HysteresisSlider progressControl;
  private final SpeedControl speedControl;
  private final AccelerationControl accelerationControl;

  private SimulationControlPane()
  {
    throw new UnsupportedOperationException("unsupported default constructor");
  }

  public SimulationControlPane(final Simulation simulation,
                               final HysteresisModel hysteresis)
  {
    progressControl =
      new HysteresisSlider(hysteresis, SwingConstants.HORIZONTAL, 0, 100, 0);
    speedControl = new SpeedControl();
    speedControl.setEnabled(false);
    simulation.addSpeedChangeListener(speedControl);
    accelerationControl = new AccelerationControl();
    accelerationControl.addChangeListener(simulation);

    final JLabel lblProgress = new JLabel("Progress");
    final JLabel lblSpeed = new JLabel("Speed");
    final JLabel lblAcceleration = new JLabel("Acceleration");
    final GroupLayout layout = new GroupLayout(this);
    setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
    hGroup.addGroup(layout.createParallelGroup().
                    addComponent(lblProgress).
                    addComponent(lblSpeed).
                    addComponent(lblAcceleration));
    hGroup.addGroup(layout.createParallelGroup().
                    addComponent(progressControl).
                    addComponent(speedControl).
                    addComponent(accelerationControl));
    layout.setHorizontalGroup(hGroup);
    GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
    vGroup.addGroup(layout.
                    createParallelGroup(GroupLayout.Alignment.BASELINE).
                    addComponent(lblProgress).
                    addComponent(progressControl));
    vGroup.addGroup(layout.
                    createParallelGroup(GroupLayout.Alignment.BASELINE).
                    addComponent(lblSpeed).
                    addComponent(speedControl));
    vGroup.addGroup(layout.
                    createParallelGroup(GroupLayout.Alignment.BASELINE).
                    addComponent(lblAcceleration).
                    addComponent(accelerationControl));
    layout.setVerticalGroup(vGroup);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
