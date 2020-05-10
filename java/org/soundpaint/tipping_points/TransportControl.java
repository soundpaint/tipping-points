/*
 * @(#)TransportControl.java 1.00 20/05/10
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
import java.util.EventListener;
import java.util.List;
import java.util.Objects;

public class TransportControl
{
  public static interface Listener extends EventListener
  {
    public void statusChanged(final Status oldStatus,
                              final Status newStatus);
  }

  public enum Status {
    STOPPED,
    RUNNING
  };

  private final Simulation simulation;
  private final List<Listener> listeners;
  private Status status;

  private TransportControl()
  {
    throw new UnsupportedOperationException("unsupported default constructor");
  }

  public TransportControl(final Simulation simulation)
  {
    Objects.requireNonNull(simulation);
    this.simulation = simulation;
    listeners = new ArrayList<Listener>();
    status = Status.STOPPED;
  }

  public boolean addListener(final Listener listener)
  {
    return listeners.add(listener);
  }

  public boolean removeListener(final Listener listener)
  {
    return listeners.remove(listener);
  }

  public synchronized void stop() {
    if (status != Status.RUNNING) {
      throw new IllegalStateException("not running");
    }
    if (simulation.requestStop()) {
      changeStatus(Status.STOPPED);
    }
  }

  public synchronized void start() {
    if (status != Status.STOPPED) {
      throw new IllegalStateException("already running");
    }
    if (simulation.requestStart()) {
      changeStatus(Status.RUNNING);
    }
  }

  private void changeStatus(final Status newStatus)
  {
    final Status oldStatus = status;
    status = newStatus;
    for (final Listener listener : listeners) {
      listener.statusChanged(oldStatus, newStatus);
    }
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
