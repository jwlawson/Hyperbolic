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
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.group.HypOrbitPoints;
import uk.co.jwlawson.hyperbolic.client.hyperbolic.HypLineFactory;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author John
 * 
 */
public class Focal implements CanvasHolder {

	private static final Logger log = Logger.getLogger(Focal.class.getName());

	private static final String MOUNT_ID = "focalcanvas";
	private static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	private Canvas canvas;

	private ArrayList<Point> mPointList;
	private ArrayList<Line> mLineList;

	// mouse positions relative to canvas
	private int mouseX, mouseY;

	// canvas size, in px
	private int height = 400;
	private int width = 400;

	private final CssColor redrawColor = CssColor.make("rgb(255,255,255)");
	private Context2d context;

	public Focal() {
		canvas = Canvas.createIfSupported();
		if (canvas == null) {
			RootPanel.get(MOUNT_ID).add(new Label(upgradeMessage));
			return;
		}
		context = canvas.getContext2d();

	}

	private void initPoints() {
		log.info("Loading points");
		mPointList = new ArrayList<Point>();
		mLineList = new ArrayList<Line>();
		final Point origin = new Point(0, 0);

		final HypLineFactory factory = new HypLineFactory(width / 2);
		HypOrbitPoints orbit = new HypOrbitPoints();
		mPointList.add(orbit.next());
		while (orbit.hasNext()) {
			final Point next = orbit.next();
			log.info("adding point " + next);
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					mPointList.add(next);
					mLineList.add(factory.getPerpendicularBisector(next, origin));
					doUpdate();
				}
			});

		}
		// @formatter:off
		/*final EuclLine.Factory factory = new Factory(width, height);
		TorusOrbitPoints orbit = new TorusOrbitPoints(width, height);
		*/
		// @formatter:on

		log.fine("Points loaded");

	}

	public void initSize() {
		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(2 * width);
		canvas.setCoordinateSpaceHeight(2 * height);

		initPoints();
	}

	@Override
	public void doUpdate() {

		// update the back canvas
		context.setFillStyle(redrawColor);
		context.fillRect(-width, -height, width, height);

		context.save();
		context.translate(width, height);
		context.scale(2.0, 2.0);

		context.beginPath();
		context.arc(0, 0, 1, 0, 2 * Math.PI);
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
	}

	@Override
	public void initHandlers() {
		canvas.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				mouseX = event.getRelativeX(canvas.getElement());
				mouseY = event.getRelativeY(canvas.getElement());
			}
		});

		canvas.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				mouseX = -200;
				mouseY = -200;
			}
		});

		canvas.addTouchMoveHandler(new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				event.preventDefault();
				if (event.getTouches().length() > 0) {
					Touch touch = event.getTouches().get(0);
					mouseX = touch.getRelativeX(canvas.getElement());
					mouseY = touch.getRelativeY(canvas.getElement());
				}
				event.preventDefault();
			}
		});

		canvas.addTouchEndHandler(new TouchEndHandler() {
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				event.preventDefault();
				mouseX = -200;
				mouseY = -200;
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
	public void addToPanel() {
		RootPanel panel = RootPanel.get(MOUNT_ID);
		width = panel.getOffsetWidth();
		height = width;

		initSize();
		doUpdate();

		panel.add(canvas);
	}

}
