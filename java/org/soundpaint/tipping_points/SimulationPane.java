/*
 * @(#)SimulationPane.java 1.00 20/05/10
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
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class SimulationPane extends Box
{
  private static final long serialVersionUID = 2493459370905685507L;

  private final Simulation simulation;

  public SimulationPane(final HysteresisModel hysteresis)
  {
    super(SwingConstants.VERTICAL);
    simulation = new Simulation(hysteresis);
    final Box toolBarPane = new Box(SwingConstants.HORIZONTAL);
    add(toolBarPane);
    toolBarPane.add(new ToolBar(new TransportControl(simulation)));
    toolBarPane.add(Box.createGlue());
    add(new SimulationControlPane(simulation, hysteresis));

    final Border loweredEtched =
      BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    final TitledBorder titled =
      BorderFactory.createTitledBorder(loweredEtched, "Simulation");
    titled.setTitleJustification(TitledBorder.RIGHT);
    setBorder(titled);
    simulation.startThread();
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
