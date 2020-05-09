/*
 * @(#)HyteresisLoop.java 1.00 20/05/04
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class HysteresisLoop extends HysteresisView
{
  private static final long serialVersionUID = -1811857102534851395L;

  public HysteresisLoop(final HysteresisModel hysteresis)
  {
    super(hysteresis);
    setPreferredSize(new Dimension(200, 200));
    final Border loweredEtched =
      BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    final TitledBorder titled =
      BorderFactory.createTitledBorder(loweredEtched, "Hysteresis");
    titled.setTitleJustification(TitledBorder.RIGHT);
    setBorder(titled);
  }

  private static final double PADDING_FRACTION = 0.1;
  private static final double EPSILON = 0.000001;

  private void drawArrow(final Graphics2D g2d,
                         final double startX,
                         final double startY,
                         final double endX,
                         final double endY,
                         final AffineTransform data2display)
  {
    final Point2D pTip = transform(data2display, endX, endY);
    final Point2D pBaseLeft =
      transform(data2display,
                startX + 0.2 * (endY - startY),
                startY + 0.2 * (endX - startX));
    final Point2D pBaseRight =
      transform(data2display,
                startX - 0.2 * (endY - startY),
                startY -0.2 * (endX - startX));
    final Point2D pBaseMiddle =
      transform(data2display,
                startX + 0.2 * (endX - startX),
                startY + 0.2 * (endY - startY));
    drawPolygon(g2d, true,
                new Point2D[] {pTip, pBaseRight, pBaseMiddle, pBaseLeft});
  }

  private void drawArrows(final Graphics2D g2d,
                          final double leftLimbLowerValue,
                          final double leftLimbUpperValue,
                          final double rightLimbUpperValue,
                          final double rightLimbLowerValue,
                          final AffineTransform data2display)
  {
    final double bottomY = 0.45;
    final double topY = 0.55;
    final double leftBottomX = leftLimbLowerValue +
      bottomY * (leftLimbUpperValue - leftLimbLowerValue);
    final double leftTopX = leftLimbLowerValue +
      topY * (leftLimbUpperValue - leftLimbLowerValue);
    final double rightBottomX = rightLimbLowerValue +
      bottomY * (rightLimbUpperValue - rightLimbLowerValue);
    final double rightTopX = rightLimbLowerValue +
      topY * (rightLimbUpperValue - rightLimbLowerValue);
    drawArrow(g2d, leftTopX, topY, leftBottomX, bottomY, data2display);
    drawArrow(g2d, rightBottomX, bottomY, rightTopX, topY, data2display);
  }

  public void paintComponent(final Graphics g)
  {
    super.paintComponent(g);
    final Graphics2D g2d = (Graphics2D)g;
    final double minValue = getMinValue();
    final double maxValue = getMaxValue();
    final double leftLimbLowerValue = getLeftLimbLowerValue();
    final double leftLimbUpperValue = getLeftLimbUpperValue();
    final double rightLimbLowerValue = getRightLimbLowerValue();
    final double rightLimbUpperValue = getRightLimbUpperValue();
    final double displayWidth = getWidth();
    final double displayHeight = getHeight();
    final double tx = PADDING_FRACTION * displayWidth;
    final double ty = (1.0 - PADDING_FRACTION) * displayHeight;
    final AffineTransform data2display =
      AffineTransform.getTranslateInstance(tx, ty);
    final double loopWidth = displayWidth * (1.0 - 2.0 * PADDING_FRACTION);
    final double loopHeight = displayHeight * (1.0 - 2.0 * PADDING_FRACTION);
    final double valueRange = Math.max(1.0, maxValue - minValue);
    final double sx = loopWidth / valueRange;
    final double sy = loopHeight;
    data2display.scale(sx, -sy);
    data2display.translate(-minValue, 0);

    // hysteresis shape
    final Point2D pMinValue = transform(data2display, minValue, 0.0);
    final Point2D pLeftLimbLowerValue =
      transform(data2display, leftLimbLowerValue, 0.0);
    final Point2D pLeftLimbUpperValue =
      transform(data2display, leftLimbUpperValue, 1.0);
    final Point2D pRightLimbLowerValue =
      transform(data2display, rightLimbLowerValue, 0.0);
    final Point2D pRightLimbUpperValue =
      transform(data2display, rightLimbUpperValue, 1.0);
    final Point2D pMaxValue = transform(data2display, maxValue, 1.0);
    g2d.setStroke(DEFAULT_LINE_STROKE);
    drawLine(g, pMinValue, pRightLimbLowerValue);
    drawLine(g, pLeftLimbLowerValue, pLeftLimbUpperValue);
    drawLine(g, pRightLimbLowerValue, pRightLimbUpperValue);
    drawLine(g, pLeftLimbUpperValue, pMaxValue);

    drawArrows(g2d,
               leftLimbLowerValue,
               leftLimbUpperValue,
               rightLimbUpperValue,
               rightLimbLowerValue,
               data2display);

    // status dot
    final Point2D pValue =
      transform(data2display, getDomainValue(), getCodomainValue());
    g.setColor(Color.RED);
    drawDot(g2d, pValue);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
