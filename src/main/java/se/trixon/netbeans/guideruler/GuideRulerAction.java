/*
 * Copyright 2023 Patrik Karlström.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.trixon.netbeans.guideruler;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

@ActionID(
        category = "Tools",
        id = "se.trixon.netbeans.GuideRulerAction"
)
@ActionRegistration(
        displayName = "#CTL_GuideRulerAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 0),
    @ActionReference(path = "Shortcuts", name = "OS-G")
})
@Messages("CTL_GuideRulerAction=Guide ruler")
public final class GuideRulerAction implements ActionListener {

    private boolean mEnabled;
    private Point mPreviousMousePosition;
    private JComponent mRootComponent;
    private final RulerLineBorder mRulerLineBorder = new RulerLineBorder();
    private Timer mTimer;

    public GuideRulerAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (mRootComponent == null) {
            init();
        }

        mEnabled = !mEnabled;

        if (mEnabled) {
            mTimer.start();
            mRootComponent.setBorder(mRulerLineBorder);
        } else {
            mTimer.stop();
            mRootComponent.setBorder(null);
        }
    }

    private void init() {
        mRootComponent = (JComponent) WindowManager.getDefault().getMainWindow().getComponent(0);

        mTimer = new Timer(100, actionEvent -> {
            var pointerInfo = MouseInfo.getPointerInfo();
            if (!pointerInfo.getLocation().equals(mPreviousMousePosition)) {
                mPreviousMousePosition = pointerInfo.getLocation();
                mRulerLineBorder.setMousePosition(new Point(pointerInfo.getLocation()));
                mRootComponent.repaint();
                mRootComponent.revalidate();
            }
        });
    }

    class RulerLineBorder extends LineBorder {

        public static final int MAX_COLS = 9;
        public static final int MAX_ROWS = 7;
        private final Color mAlternateColor = Color.YELLOW;
        private final Color mBaseColor = Color.BLACK;
        private Point mMousePosition = new Point();

        public RulerLineBorder() {
            super(Color.BLACK, 20);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            super.paintBorder(c, g, x, y, width, height);
            var g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = 20;
            int colWidth = width / MAX_COLS;

            for (int col = 0; col < MAX_COLS; col++) {
                if ((col & 1) == 0) {
                    g2.setColor(mAlternateColor);
                    g2.fillRect(col * colWidth, 0, colWidth, size);
                    g2.fillRect(col * colWidth, height - size, colWidth, size);
                    g2.setColor(mBaseColor);
                } else {
                    g2.setColor(mAlternateColor);
                }

                var stringX = (int) (col * colWidth + 0.5 * colWidth);
                var s = String.valueOf(Character.toChars('A' + col));
                g2.drawString(s, stringX, size - 4);
                g2.drawString(s, stringX, height - 4);
            }

            int rowHeight = height / MAX_ROWS;
            for (int row = 0; row < MAX_ROWS; row++) {
                if ((row & 1) == 0) {
                    g2.setColor(mAlternateColor);
                    g2.fillRect(0, row * rowHeight, size, rowHeight);
                    g2.fillRect(width - size, row * rowHeight, size, rowHeight);
                    g2.setColor(mBaseColor);
                } else {
                    g2.setColor(mAlternateColor);
                }

                var stringY = (int) (row * rowHeight + 0.5 * rowHeight);
                var s = String.valueOf(row + 1);
                g2.drawString(s, 4, stringY);
                g2.drawString(s, width - size + 4, stringY);
            }

            var oldStroke = g2.getStroke();
            var stroke = new BasicStroke(8);
            g2.setStroke(stroke);
            g2.setColor(Color.RED);
            g2.drawLine(0, mMousePosition.y, size, mMousePosition.y);
            g2.drawLine(width, mMousePosition.y, width - size, mMousePosition.y);
            g2.drawLine(mMousePosition.x, 0, mMousePosition.x, size);
            g2.drawLine(mMousePosition.x, height, mMousePosition.x, height - size);
            g2.setStroke(oldStroke);
        }

        private void setMousePosition(Point mousePosition) {
            SwingUtilities.convertPointFromScreen(mousePosition, mRootComponent);
            mMousePosition = mousePosition;
        }
    }
}
