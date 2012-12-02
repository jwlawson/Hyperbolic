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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.geometry.hyperbolic.HypLineFactory;
import uk.co.jwlawson.hyperbolic.client.group.IdealTorusOrbit;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author John
 * 
 */
public class Tiling implements CanvasHolder {

	private static final String MOUNT_ID = "tilingcanvas";
	private static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	private static final Logger log = Logger.getLogger("Tiling");
	private ArrayList<Line> mLineList;
	private ArrayList<Point> mPointList;

	private Canvas canvas;
	private Context2d context;

	// canvas size, in px
	private int height = 400;
	private int width = 400;
	private final CssColor redrawColor = CssColor.make("rgb(255,255,255)");

	public Tiling() {
		mLineList = new ArrayList<Line>();

		canvas = Canvas.createIfSupported();
		if (canvas == null) {
			RootPanel.get(MOUNT_ID).add(new Label(upgradeMessage));
			return;
		}

		context = canvas.getContext2d();
	}

	private void initPoints() {
		mPointList = new ArrayList<Point>();

		log.info("loadng points");
		// Change this bit!
		final HypLineFactory factory = new HypLineFactory(width / 2);
		// final HypOrbitPoints orbit = new HypOrbitPoints();
		// ----------------------
		// final EuclLine.Factory factory = new Factory(width, height);
		// TorusOrbitPoints orbit = new TorusOrbitPoints(width, height);
		final IdealTorusOrbit orbit = new IdealTorusOrbit(1 / Math.sqrt(2), 1 / Math.sqrt(2));

		while (orbit.hasNext()) {
			mPointList.add(orbit.next());
		}
		doUpdate();

		mLineList = new ArrayList<Line>();
		log.info("Iterating over points");
		final Point p1 = new Point(0, 0);
		for (int i = 1; i <= 4; i++) {
			final Point p2 = mPointList.get(i);
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {

					Line line = factory.getPerpendicularBisector(p1, p2);
					if (!mLineList.contains(line)) {
						mLineList.add(line);
						doUpdate();
					}
				}

			});
		}
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
		for (Point point : mPointList) {
			point.draw(context);
		}
		context.restore();
	}

	@Override
	public void initHandlers() {

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
