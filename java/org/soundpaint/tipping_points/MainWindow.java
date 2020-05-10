/*
 * @(#)MainWindow.java 1.00 20/05/04
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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

public class MainWindow extends JFrame
{
  private static final long serialVersionUID = -1552990644309031427L;

  private final HysteresisModel hysteresis;
  private final HysteresisLoop hysteresisLoop;
  private final SeeSaw seeSaw;
  private final SimulationPane simulationPane;

  private MainWindow()
  {
    throw new UnsupportedOperationException("unsupported default constructor");
  }

  public MainWindow(final AppControl appControl)
  {
    super("Tipping Points");
    hysteresis = new HysteresisModel();
    hysteresisLoop = new HysteresisLoop(hysteresis);
    seeSaw = new SeeSaw("See Saw", hysteresis);
    simulationPane = new SimulationPane(hysteresis);
    setJMenuBar(new MenuBar(appControl));
    setLayout(new BorderLayout());
    add(simulationPane, BorderLayout.NORTH);
    add(createSplitPane(hysteresis), BorderLayout.CENTER);
    pack();
  }

  private JComponent createSplitPane(final HysteresisModel hysteresis)
  {
    final JSplitPane splitPane =
      new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, hysteresisLoop, seeSaw);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(150);
    return splitPane;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
