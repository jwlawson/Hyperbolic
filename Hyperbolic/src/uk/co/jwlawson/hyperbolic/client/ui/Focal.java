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

import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.LineFactory;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author John
 * 
 */
public class Focal extends SquareCanvasHolder implements PointHandler {

	private static final Logger log = Logger.getLogger(Focal.class.getName());

	private List<Point> mPointList;
	private List<Line> mLineList;
	private Line[] mLineArr;
	private Point[] mPointArr;

	private Point mOrigin;

	private LineFactory mFactory;

	public Focal() {
		mLineList = new ArrayList<Line>();
		mPointList = new ArrayList<Point>();

		mLineArr = new Line[0];
		mPointArr = new Point[0];
	}

	public void setLineFactory(LineFactory factory) {
		mFactory = factory;
	}

	@Override
	public void clear() {
		mLineList.clear();
		mPointList.clear();
		mLineArr = new Line[0];
		mPointArr = new Point[0];
		doUpdate();
	}

	@Override
	public void addInitialPoint(Point p) {
		mOrigin = new Point(p);
		log.info("Start: " + mOrigin);
		p.scale(width / 2);
		mPointList.add(p);
		drawDrawables(p);
	}

	@Override
	public void addPoint(Point next) {
//		next.scale(width / 8.5);
		log.finer(mOrigin + " " + next);
		Line line = mFactory.getPerpendicularBisector(mOrigin, next);
		System.out.println(line);
		mLineList.add(line);
		next.scale(width / 2);
		mPointList.add(next);

		drawDrawables(next, line);
	}

	@Override
	public void pointsAdded() {
		log.fine("Compute done!");

		mLineArr = new Line[mLineList.size()];
		mPointArr = new Point[mPointList.size()];

		doUpdate();
	}

	@Override
	public void doUpdate() {

		clearCanvas();

		drawDrawables(mLineList.toArray(mLineArr));
		drawDrawables(mPointList.toArray(mPointArr));

	}

	@Override
	public void initHandlers() {

	}

}
