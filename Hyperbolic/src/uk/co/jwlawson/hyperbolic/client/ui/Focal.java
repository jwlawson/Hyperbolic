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
import java.util.List;
import java.util.logging.Logger;

import uk.co.jwlawson.hyperbolic.client.framework.Drawable;
import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.geometry.euclidean.EuclPoint;
import uk.co.jwlawson.hyperbolic.client.geometry.hyperbolic.HypLineFactory;
import uk.co.jwlawson.hyperbolic.client.group.IdealTorusOrbit;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author John
 * 
 */
public class Focal implements CanvasHolder {

	private static final Logger log = Logger.getLogger(Focal.class.getName());

	private static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	private Canvas canvas;

	private List<Point> mPointList;
	private List<Line> mLineList;

	// mouse positions relative to canvas
	private double mouseX, mouseY;
	private double scale = 1;

	// canvas size, in px
	private int height = 400;
	private int width = 400;

	private double y = 1;

	private CssColor redrawColor = CssColor.make("rgb(255,255,255)");
	private Context2d context;

	private boolean restart = false;

	public Focal() {
		canvas = Canvas.createIfSupported();
		if (canvas == null) {
			Window.alert(upgradeMessage);
			return;
		}
		context = canvas.getContext2d();
	}

	public void setY(double y) {
		this.y = y;

		initPoints();
	}

	private void initPoints() {
		restart = true;
		clearCanvas();
		log.info("Loading points");
		mPointList = new ArrayList<Point>();
		mLineList = new ArrayList<Line>();
		final Point origin = new Point((y - 1) / (y + 1), 0);

		// Change this bit!
		final HypLineFactory factory = new HypLineFactory(width / 2);
		// final HypOrbitPoints orbit = new HypOrbitPoints();
		// ----------------------
		// final EuclLine.Factory factory = new EuclLineFactory(width, height);
		// TorusOrbitPoints orbit = new TorusOrbitPoints(width, height);
		final IdealTorusOrbit orbit = new IdealTorusOrbit(y);
		orbit.setStart(origin);

		Point p = orbit.next();
		System.out.println("Origin: " + origin + " first: " + p);
		p.scale(width / 2);
		mPointList.add(new EuclPoint(p));
		restart = false;

		Scheduler.get().scheduleIncremental(new RepeatingCommand() {

			@Override
			public boolean execute() {
				Point next = orbit.next();
				Line line = factory.getPerpendicularBisector(origin, next);
				mLineList.add(line);
				next.scale(width / 2);
				mPointList.add(next);

				drawDrawables(next, line);

				if (!orbit.hasNext()) {
					computeFinished();
				}

				return orbit.hasNext() && !restart;
			}

			private void computeFinished() {
				System.out.println("Compute done!");
				doUpdate();
			}
		});

		log.fine("Points loaded");

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

	public void initSize() {
		Widget panel = canvas.getParent();
		width = panel.getOffsetWidth();
		height = width;

		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(2 * width);
		canvas.setCoordinateSpaceHeight(2 * height);

		initPoints();
		doUpdate();
	}

	@Override
	public void doUpdate() {

		clearCanvas();

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
		for (Point point : mPointList) {
			point.draw(context);
		}

		context.restore();
		System.out.println("Update done");

	}

	private void clearCanvas() {
		context.setFillStyle(redrawColor);
		context.fillRect(0, 0, 2 * width, 2 * height);
	}

	private void scale(double mX, double mY, double zoom) {
		context.translate(2 * mouseX, 2 * mouseY);
		context.scale(zoom, zoom);
		context.translate(-2 * (mX / scale + mouseX - mX / (scale * zoom)), -2
				* (mY / scale + mouseY - mY / (scale * zoom)));

		mouseX = (mX / scale + mouseX - mX / (scale * zoom));
		mouseY = (mY / scale + mouseY - mY / (scale * zoom));
		scale *= zoom;

		doUpdate();
	}

	@Override
	public void initHandlers() {

		canvas.addMouseWheelHandler(new MouseWheelHandler() {

			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				double mX = event.getRelativeX(canvas.getElement());
				double mY = event.getRelativeY(canvas.getElement());
				float wheel = (float) -event.getDeltaY() / 30;
				double zoom = Math.pow(1 + Math.abs(wheel) / 2, wheel > 0 ? 1 : -1);
				scale(mX, mY, zoom);
			}
		});

		canvas.addGestureStartHandler(new GestureStartHandler() {
			@Override
			public void onGestureStart(GestureStartEvent event) {
				event.preventDefault();
			}
		});
	}

	@Override
	public void addToPanel(Panel panel) {
		panel.add(canvas);

		initSize();
	}

}
