/*
 * @(#)MenuBar.java 1.00 20/05/06
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
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class MenuBar extends JMenuBar
{
  private static final long serialVersionUID = -4802889517366608185L;

  private AppControl appControl;
  private JMenuItem quit;
  private JMenuItem about;

  private MenuBar()
  {
    throw new UnsupportedOperationException("unsupported default constructor");
  }

  public MenuBar(final AppControl appControl)
  {
    Objects.requireNonNull(appControl);
    this.appControl = appControl;
    add(createFileMenu());
    add(createHelpMenu());
  }

  private JMenu createFileMenu()
  {
    final JMenu file = new JMenu("File");
    file.setMnemonic(KeyEvent.VK_F);

    final JMenuItem quit = createImageItem("quit16x16.png", "Quit");
    quit.setMnemonic(KeyEvent.VK_Q);
    quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                                               ActionEvent.ALT_MASK));
    quit.getAccessibleContext().setAccessibleDescription("Quit app");
    quit.addActionListener((final ActionEvent event) -> {
        appControl.quit();
      });
    file.add(quit);
    return file;
  }

  private JMenu createHelpMenu()
  {
    final JMenu help = new JMenu("Help");
    help.setMnemonic(KeyEvent.VK_H);

    final JMenuItem about = new JMenuItem("About…");
    about.setMnemonic(KeyEvent.VK_A);
    about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                                                ActionEvent.ALT_MASK));
    about.getAccessibleContext().setAccessibleDescription("About this app");
    about.addActionListener((final ActionEvent event) -> {
        JOptionPane.showMessageDialog(this,
                                      appControl.getAboutText(),
                                      "About",
                                      JOptionPane.INFORMATION_MESSAGE);
      });
    help.add(about);
    return help;
  }

  private static JMenuItem createImageItem(final String imageFileName,
                                           final String label)
  {
    final String imagePath = "/images/" + imageFileName;
    final URL imageURL = Main.class.getResource(imagePath);
    final JMenuItem item = new JMenuItem(label);
    if (imageURL != null) {
      item.setIcon(new ImageIcon(imageURL, label));
    } else {
      System.err.println("resource not found: " + imagePath);
    }
    return item;
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
