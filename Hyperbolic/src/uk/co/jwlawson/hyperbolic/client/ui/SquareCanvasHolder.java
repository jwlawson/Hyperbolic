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

import java.util.logging.Logger;

import uk.co.jwlawson.hyperbolic.client.framework.Drawable;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Administrator
 * 
 */
public abstract class SquareCanvasHolder implements CanvasHolder {

	private static final Logger log = Logger.getLogger(SquareCanvasHolder.class.getName());
	private static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	protected Canvas canvas;
	private Context2d context;
	private SizeChangeListener sizeListener;

	protected int height = 400;
	protected int width = 400;

	private final CssColor redrawColor = CssColor.make("rgb(255,255,255)");

	@Override
	public abstract void doUpdate();

	@Override
	public abstract void initHandlers();

	public SquareCanvasHolder() {
		canvas = Canvas.createIfSupported();
		if (canvas == null) {
			Window.alert(upgradeMessage);
			return;
		}

		context = canvas.getContext2d();
	}

	public void setSizeListener(SizeChangeListener sizeListener) {
		this.sizeListener = sizeListener;
	}

	protected void clearCanvas() {
		context.setFillStyle(redrawColor);
		context.fillRect(0, 0, 2 * width, 2 * height);
	}

	protected void drawDrawables(Drawable... drawables) {
		context.save();
		context.translate(width, height);
		context.scale(2.0, -2.0);
		for (Drawable draw : drawables) {
			draw.draw(context);
		}
		context.restore();
	}

	/** Draws unit circle for hyperbolic disc. Should get moved to different class. */
	private void drawBoundingCircle() {
		context.save();
		context.translate(width, height);
		context.scale(2.0, -2.0);

		context.beginPath();
		context.arc(0, 0, width / 2, 0, 2 * Math.PI);
		context.closePath();
		context.setStrokeStyle("#000000");
		context.setLineWidth(0.5);
		context.stroke();

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
		log.finer("Size set. Height: " + height + " Width: " + width);

		sizeListener.sizeChanged(width, height);

		doUpdate();
	}

	@Override
	public void addToPanel(Panel panel) {
		panel.add(canvas);

		initSize();
	}

}
