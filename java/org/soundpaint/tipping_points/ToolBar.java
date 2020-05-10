/*
 * @(#)ToolBar.java 1.00 20/05/10
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

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class ToolBar extends JToolBar
  implements TransportControl.Listener
{
  private static final long serialVersionUID = -4063899286688416971L;

  private final TransportControl transportControl;
  private JButton start;
  private JButton stop;

  private ToolBar()
  {
    throw new UnsupportedOperationException("unsupported default constructor");
  }

  public ToolBar(final TransportControl transportControl)
  {
    Objects.requireNonNull(transportControl);
    this.transportControl = transportControl;
    transportControl.addListener(this);
    addButtons();
  }

  private void addButtons()
  {
    start = createToolButton("play32x32.png", "Start simulation");
    start.setToolTipText("Start simulation");
    start.addActionListener((final ActionEvent event) -> {
        transportControl.start();
      });
    start.setEnabled(true);
    add(start);

    stop = createToolButton("stop32x32.png", "Stop simulation");
    stop.setToolTipText("Stop simulation");
    stop.addActionListener((final ActionEvent event) -> {
        transportControl.stop();
      });
    stop.setEnabled(false);
    add(stop);
  }

  private static JButton createToolButton(final String imageFileName,
                                          final String altText)
  {
    final String imagePath = "/images/" + imageFileName;
    final URL imageURL = AppControl.class.getResource(imagePath);
    final JButton button = new JButton();
    if (imageURL != null) {
      button.setIcon(new ImageIcon(imageURL, altText));
    } else {
      button.setText(altText);
      System.err.println("Resource not found: " + imagePath);
    }
    return button;
  }

  public void statusChanged(final TransportControl.Status oldStatus,
                            final TransportControl.Status newStatus)
  {
    switch (newStatus) {
    case STOPPED:
      stop.setEnabled(false);
      start.setEnabled(true);
      break;
    case RUNNING:
      stop.setEnabled(true);
      start.setEnabled(false);
      break;
    default:
      throw new IllegalStateException("unknown transport control status");
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:java
 * End:
 */
