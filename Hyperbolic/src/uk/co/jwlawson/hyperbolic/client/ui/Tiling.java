/**
 * Copyright 2012 John Lawson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.jwlawson.hyperbolic.client.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;

import uk.co.jwlawson.hyperbolic.client.framework.Drawable;
import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;

/**
 * @author John
 * 
 */
public class Tiling extends SquareCanvasHolder implements PointHandler {

	private static final Logger log = Logger.getLogger(Tiling.class.getSimpleName());

	private ArrayList<Line> mLineList;
	private ArrayList<Point> mPointList;
	private LinkedList<Point> mScaledPointList;

	public Tiling() {
		mLineList = new ArrayList<Line>();
		mPointList = new ArrayList<Point>();
		mScaledPointList = new LinkedList<Point>();
	}

	@Override
	public void doUpdate() {

		clearCanvas();

		drawDrawables((Drawable[]) mLineList.toArray());
		drawDrawables((Drawable[]) mPointList.toArray());
	}

	@Override
	public void initHandlers() {

	}

	@Override
	public void addInitialPoint(Point p) {

	}

	@Override
	public void addPoint(Point p) {
		mPointList.add(p);
		Point scaled = new Point(p);
		scaled.scale(width / 2);
		mScaledPointList.add(scaled);

		drawDrawables(scaled);
	}

	@Override
	public void pointsAdded() {
		// TODO Compute tiling
	}

	@Override
	public void clear() {
		mPointList.clear();
		mLineList.clear();
	}

}
