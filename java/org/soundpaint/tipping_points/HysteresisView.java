/*
 * @(#)HysteresisView.java 1.00 20/05/04
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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Objects;
import javax.swing.JPanel;

public abstract class HysteresisView
  extends JPanel implements HysteresisListener
{
  private static final long serialVersionUID = 4128714217577201669L;

  private final HysteresisModel hysteresis;
  private double leftLimbLowerValue;
  private double leftLimbUpperValue;
  private double rightLimbLowerValue;
  private double rightLimbUpperValue;
  //private double value;
  //private HysteresisModel.State state;

  private HysteresisView()
  {
    throw new UnsupportedOperationException("unsupported default constructor");
  }

  public HysteresisView(final HysteresisModel hysteresis)
  {
    Objects.requireNonNull(hysteresis);
    this.hysteresis = hysteresis;
    hysteresis.addListener(this);
  }

  public HysteresisModel getHysteresis()
  {
    return hysteresis;
  }

  /**
   * Override this method for reaction upon graphics, if a simple
   * repaint() won't be sufficient (e.g. if the paint method does not
   * inspect the hysteresis, but tracks its own state by internal
   * variables that need to be updated in response to the hysteresis
   * being changed).
   */
  public void stateChanged(final HysteresisView source,
                           final double value,
                           final HysteresisModel.State state)
  {
    // default implementation just calls repaint(), assuming that the
    // component will look into the hysteresis object for the current
    // state.
    repaint();
  }

  public void limbsChanged(final double leftLimbLowerValue,
                           final double leftLimbUpperValue,
                           final double rightLimbLowerValue,
                           final double rightLimbUpperValue)
  {
    this.leftLimbLowerValue = leftLimbLowerValue;
    this.leftLimbUpperValue = leftLimbUpperValue;
    this.rightLimbLowerValue = rightLimbLowerValue;
    this.rightLimbUpperValue = rightLimbUpperValue;
  }

  public double getMinValue()
  {
    return hysteresis.getMinValue();
  }

  public double getMaxValue()
  {
    return hysteresis.getMaxValue();
  }

  public double getLeftLimbLowerValue()
  {
    return leftLimbLowerValue;
  }

  public double getLeftLimbUpperValue()
  {
    return leftLimbUpperValue;
  }

  public double getRightLimbLowerValue()
  {
    return rightLimbLowerValue;
  }

  public double getRightLimbUpperValue()
  {
    return rightLimbUpperValue;
  }

  public double getDomainValue()
  {
    return hysteresis.getValue();
  }

  public void domainValueChanged(final double value)
  {
    hysteresis.valueChanged(this, value);
  }

  public double getCodomainValue()
  {
    final double value = getDomainValue();
    switch (getState()) {
    case ON_LOWER_CURVE:
      return value <= rightLimbLowerValue ?
        0.0 :
      (value - rightLimbLowerValue) /
        (rightLimbUpperValue - rightLimbLowerValue);
    case ON_UPPER_CURVE:
      return value >= leftLimbUpperValue ?
        1.0 :
      (value - leftLimbLowerValue) /
        (leftLimbUpperValue - leftLimbLowerValue);
    default:
      throw new IllegalStateException("unexpected case fall-through");
    }
  }

  /**
   * Returns value scaled and translated to range [0.0 .. 1.0].
   */
  public double getNormValue()
  {
    return getNormValue(getDomainValue());
  }

  /**
   * For any value in the range [minValue .. maxValue], returns value
   * scaled and translated to range [0.0 .. 1.0].  Values outside of
   * range [minValue .. maxValue] are extrapolated accordingly.
   */
  public double getNormValue(final double value)
  {
    return getDomainValue() / (getMaxValue() - getMinValue());
  }

  public HysteresisModel.State getState()
  {
    return hysteresis.getState();
  }

  public static final Stroke DEFAULT_STROKE = new BasicStroke();
  public static final Stroke DEFAULT_LINE_STROKE =
    new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  public static final Stroke DEFAULT_THIN_LINE_STROKE =
    new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

  private static final double DOT_RADIUS = 5.0;
  private static final double DOT_DIAMETER = 2.0 * DOT_RADIUS;

  protected static Point2D transform(final AffineTransform transform,
                                     final double x, final double y)
  {
    return transform.transform(new Point2D.Double(x, y), null);
  }

  protected static void drawDot(final Graphics2D g2d, final Point2D p)
  {
    drawCircle(g2d, p, DOT_RADIUS, true);
  }

  protected static void drawCircle(final Graphics2D g2d, final Point2D p,
                                   final double radius, final boolean filled)
  {
    final double diameter = 2.0 * radius;
    final Ellipse2D.Double circle =
      new Ellipse2D.Double(p.getX() - radius,
                           p.getY() - radius,
                           diameter,
                           diameter);
    if (filled) {
      g2d.fill(circle);
    }
    g2d.draw(circle);
  }

  protected static void drawPolygon(final Graphics g2d,
                                    final boolean filled,
                                    final Point2D... points)
  {
    final Polygon polygon = new Polygon();
    for (final Point2D point : points) {
      polygon.addPoint((int)point.getX(), (int)point.getY());
    }
    if (filled) {
      g2d.fillPolygon(polygon);
    }
    g2d.drawPolygon(polygon);
  }

  protected static void drawLine(final Graphics g,
                                 final Point2D p1, final Point2D p2)
  {
    g.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
  }

  protected static void drawRect(final Graphics g,
                                 final Point2D pLowerLeft,
                                 final Point2D pUpperRight,
                                 final boolean filled)
  {
    if (filled) {
      g.fillRect((int)pLowerLeft.getX(), (int)pUpperRight.getY(),
                 (int)(pUpperRight.getX() - pLowerLeft.getX()),
                 (int)(pLowerLeft.getY() - pUpperRight.getY()));
    } else {
      g.drawRect((int)pLowerLeft.getX(), (int)pUpperRight.getY(),
                 (int)(pUpperRight.getX() - pLowerLeft.getX()),
                 (int)(pLowerLeft.getY() - pUpperRight.getY()));
    }
  }

  protected static void drawRoundRect(final Graphics2D g2d,
                                      final Point2D pLowerLeft,
                                      final Point2D pUpperRight,
                                      final int arcWidth,
                                      final int arcHeight)
  {
    g2d.drawRoundRect((int)pLowerLeft.getX(), (int)pUpperRight.getY(),
                      (int)(pUpperRight.getX() - pLowerLeft.getX()),
                      (int)(pLowerLeft.getY() - pUpperRight.getY()),
                      arcWidth, arcHeight);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
