/*
 * @(#)SeeSaw.java 1.00 20/05/04
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class SeeSaw extends HysteresisView
{
  private static final long serialVersionUID = 3948668519558785935L;

  public static final Stroke DEFAULT_STROKE = new BasicStroke();
  public static final Stroke DEFAULT_LINE_STROKE =
    new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  public static final Stroke DEFAULT_THIN_LINE_STROKE =
    new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

  private final Dimension dimension;

  public SeeSaw(final String title, final HysteresisModel hysteresis)
  {
    super(hysteresis);
    Objects.requireNonNull(title);
    dimension = new Dimension(450, 440);
    final Border loweredEtched =
      BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    final TitledBorder titled =
      BorderFactory.createTitledBorder(loweredEtched, title);
    titled.setTitleJustification(TitledBorder.RIGHT);
    setBorder(titled);
  }

  public Dimension getPreferredSize() {
    return dimension;
  }

  private static final double MIN_PADDING = 10.0;
  private static final double PADDING_FRACTION = 0.05;

  private void drawSeeSaw(final Graphics2D g2d, final boolean tipped)
  {
    final double normalizedCodomainValue = getCodomainValue() - 0.5;
    final double width = getWidth();
    final double height = getHeight();
    final double displayWidth = Math.max(0.0, width - 2.0 * MIN_PADDING);
    final double displayHeight = Math.max(0.0, height - 2.0 * MIN_PADDING);
    final double squareSideLength =
      (1.0 - 2.0 * PADDING_FRACTION) * Math.min(displayWidth, displayHeight);
    final double tx = 0.5 * (displayWidth - squareSideLength);
    final double ty = displayHeight - 0.5 * (displayHeight - squareSideLength);
    final AffineTransform data2display =
      AffineTransform.getTranslateInstance(MIN_PADDING + tx, MIN_PADDING + ty);
    data2display.scale(squareSideLength, -squareSideLength);


    // pivot
    final double pivotX = 0.50;
    final double pivotY = 0.50;

    // board
    final double boardLength = 0.90;
    final double boardHeight = 0.40;
    final double boardLeftX = -0.5 * boardLength;
    final double boardRightX = +0.5 * boardLength;
    final double boardLowerY = pivotY - 0.5 * boardHeight;
    final double boardUpperY = pivotY + 0.5 * boardHeight;
    final double θ =
      -normalizedCodomainValue * Math.sin(boardHeight / boardLength);
    final AffineTransform seeSaw2display = new AffineTransform();
    seeSaw2display.translate(pivotX, pivotY);
    seeSaw2display.rotate(2.0 * θ);
    seeSaw2display.preConcatenate(data2display);

    // hidden weight
    final double ballRadius = 20.0;
    final double rollLength = 0.70 * boardLength;
    final double ballValue;
    switch (getState()) {
    case ON_LOWER_CURVE:
      ballValue = Math.max(0.0, 2.0 * normalizedCodomainValue) - 0.5;
      break;
    case ON_UPPER_CURVE:
      ballValue = Math.min(0.5, 2.0 * normalizedCodomainValue + 0.5);
      break;
    default:
      throw new IllegalStateException("unexpected case fall-through");
    }
    final double ballX = rollLength * ballValue;
    final Point2D pBall = transform(seeSaw2display, ballX, 0.0);
    g2d.setColor(Color.BLACK);
    drawCircle(g2d, pBall, ballRadius, true);

    // weights
    final double rightWeight = getNormValue();
    final Point2D pRightWeightLowerLeft =
      transform(data2display, 0.85, boardLowerY);
    final Point2D pRightWeightUpperRight =
      transform(data2display, 0.95, boardLowerY + rightWeight * boardHeight);
    drawRect(g2d, pRightWeightLowerLeft, pRightWeightUpperRight, true);

    final double leftWeight = 1.0 - rightWeight;
    final Point2D pLeftWeightLowerLeft =
      transform(data2display, 0.05, boardLowerY);
    final Point2D pLeftWeightUpperRight =
      transform(data2display,
                0.15, boardLowerY + leftWeight * (boardUpperY - boardLowerY));
    drawRect(g2d, pLeftWeightLowerLeft, pLeftWeightUpperRight, true);

    // board
    final Point2D pBoardLeft = transform(seeSaw2display, boardLeftX, 0.0);
    final Point2D pBoardRight = transform(seeSaw2display, boardRightX, 0.0);
    g2d.setColor(Color.ORANGE.darker().darker());
    g2d.setStroke(DEFAULT_LINE_STROKE);
    drawLine(g2d, pBoardLeft, pBoardRight);

    // mount case
    final Point2D pMountUpperLeft = transform(data2display, 0.48, 0.60);
    final Point2D pMountUpperRight = transform(data2display, 0.52, 0.60);
    final Point2D pMountLowerLeft = transform(data2display, 0.45, boardLowerY);
    final Point2D pMountLowerRight = transform(data2display, 0.55, boardLowerY);
    drawLine(g2d, pMountLowerLeft, pMountUpperLeft);
    drawLine(g2d, pMountUpperLeft, pMountUpperRight);
    drawLine(g2d, pMountUpperRight, pMountLowerRight);
    drawLine(g2d, pMountLowerLeft, pMountLowerRight);

    // pivot
    final Point2D pPivot = transform(data2display, pivotX, pivotY);
    drawDot(g2d, pPivot);

    // border
    final Point2D pBorderLowerLeft = transform(data2display, 0.0, 0.0);
    final Point2D pBorderUpperRight = transform(data2display, 1.0, 1.0);
    g2d.setColor(Color.GRAY);
    drawRoundRect(g2d, pBorderLowerLeft, pBorderUpperRight, 10, 10);
  }

  public void paintComponent(final Graphics g)
  {
    super.paintComponent(g);
    final Graphics2D g2d = (Graphics2D)g;
    final boolean tipped = getState() == HysteresisModel.State.ON_UPPER_CURVE;
    drawSeeSaw(g2d, tipped);
  }
}

/*
 * Local Variables:
 *   coding:utf-8
 *   mode:Java
 * End:
 */
