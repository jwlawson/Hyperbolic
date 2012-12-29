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

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import uk.co.jwlawson.hyperbolic.client.framework.Drawable;
import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.LineFactory;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.geometry.euclidean.EuclLineFactory;
import uk.co.jwlawson.hyperbolic.client.group.IdealTorusOrbit;
import uk.co.jwlawson.hyperbolic.client.simplevoronoi.GraphEdge;
import uk.co.jwlawson.hyperbolic.client.simplevoronoi.GraphEdgeAdapter;
import uk.co.jwlawson.hyperbolic.client.simplevoronoi.Voronoi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author John
 * 
 */
public class Tiling implements CanvasHolder {

	private static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	private static final Logger log = Logger.getLogger("Tiling");
	private ArrayList<Line> mLineList = new ArrayList<Line>();
	private ArrayList<Point> mPointList = new ArrayList<Point>();
	private LinkedList<Point> mScaledPointList = new LinkedList<Point>();

	private Canvas canvas;
	private Context2d context;

	// canvas size, in px
	private int height = 400;
	private int width = 400;
	private final CssColor redrawColor = CssColor.make("rgb(255,255,255)");

	private double t = -1;

	public Tiling() {
		mLineList = new ArrayList<Line>();

		canvas = Canvas.createIfSupported();
		if (canvas == null) {
			Window.alert(upgradeMessage);
			return;
		}

		context = canvas.getContext2d();
	}

	public void setT(double t) {
		this.t = t;

		initPoints();
	}

	private void initPoints() {
		mPointList.clear();
		mLineList.clear();

		final IdealTorusOrbit orbit = new IdealTorusOrbit(t);

		log.info("Iterating over points");
		Scheduler.get().scheduleIncremental(new RepeatingCommand() {
			@Override
			public boolean execute() {
				Point next = orbit.next();

				mPointList.add(next);
				Point scaled = new Point(next);
				scaled.scale(width / 2);
				mScaledPointList.add(scaled);

				drawDrawables(scaled);

				if (!orbit.hasNext()) {
					pointsComputed();
				}

				return orbit.hasNext();
			}
		});
	}

	private void drawDrawables(Drawable... drawables) {
		context.save();
		context.translate(width, height);
		context.scale(2.0, -2.0);
		for (Drawable draw : drawables) {
			draw.draw(context);
		}
		context.restore();
	}

	private void pointsComputed() {
		final LineFactory factory = new EuclLineFactory(width, height);
		Voronoi vor = new Voronoi(0.0001);

		double[] xValues = getXValues();
		double[] yValues = getYValues();

		List<GraphEdge> list = vor.generateVoronoi(xValues, yValues, -1, 1, -1, 1);

		final Iterator<GraphEdge> iter = list.iterator();

		Scheduler.get().scheduleIncremental(new RepeatingCommand() {

			@Override
			public boolean execute() {

				GraphEdgeAdapter edge = new GraphEdgeAdapter(iter.next());
				Point start = edge.getStart();
				Point end = edge.getEnd();
				start.scale(width / 2);
				end.scale(width / 2);
				Line line = factory.getSegmentJoining(start, end);
				mLineList.add(line);
				// System.out.println("Added voronoi line " + line + " from " +
				// start + " to " + end);

				drawDrawables(line);

				if (!iter.hasNext()) {
					linesComputed();
				}

				return iter.hasNext();
			}
		});

	}

	protected void linesComputed() {
		doUpdate();
	}

	private double[] getYValues() {
		double[] arr = new double[mPointList.size()];
		for (int i = 0; i < mPointList.size(); i++) {
			arr[i] = mPointList.get(i).getY();
		}
		return arr;
	}

	private double[] getXValues() {
		double[] arr = new double[mPointList.size()];
		for (int i = 0; i < mPointList.size(); i++) {
			arr[i] = mPointList.get(i).getX();
		}
		return arr;
	}

	public void initSize() {
		Widget panel = canvas.getParent();
		width = panel.getOffsetWidth();
		height = width;

		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(2 * width);
		canvas.setCoordinateSpaceHeight(2 * height);

		initPoints();
	}

	@Override
	public void doUpdate() {

		context.setFillStyle(redrawColor);
		context.fillRect(-width, -height, width, height);

		context.save();
		context.translate(width, height);
		context.scale(2.0, -2.0);

		context.beginPath();
		context.arc(0, 0, width / 2, 0, 2 * Math.PI);
		context.closePath();
		context.setStrokeStyle("#000000");
		context.setLineWidth(0.5);
		context.stroke();

		for (Line line : mLineList) {
			line.draw(context);
		}
		for (Point point : mScaledPointList) {
			point.draw(context);
		}
		context.restore();
	}

	@Override
	public void initHandlers() {

	}

	@Override
	public void addToPanel(Panel panel) {
		panel.add(canvas);

		initSize();
		doUpdate();
	}

}
