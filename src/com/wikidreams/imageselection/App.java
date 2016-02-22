package com.wikidreams.imageselection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class App {

	Rectangle captureRect;

	App(final BufferedImage screen) {
final BufferedImage screenCopy = new BufferedImage(
				screen.getWidth(),
				screen.getHeight(),
				screen.getType());
		final JLabel screenLabel = new JLabel(new ImageIcon(screenCopy));
		JScrollPane screenScroll = new JScrollPane(screenLabel);

		screenScroll.setPreferredSize(new Dimension(
				(int)(screen.getWidth()/3),
				(int)(screen.getHeight()/3)));

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(screenScroll, BorderLayout.CENTER);

		final JLabel selectionLabel = new JLabel(
				"Drag a rectangle in the screen shot!");
		panel.add(selectionLabel, BorderLayout.SOUTH);

		repaint(screen, screenCopy);
		screenLabel.repaint();

		screenLabel.addMouseMotionListener(new MouseMotionAdapter() {

			Point start = new Point();

			@Override
			public void mouseMoved(MouseEvent me) {
				start = me.getPoint();
				repaint(screen, screenCopy);
				selectionLabel.setText("Start Point: " + start);
				screenLabel.repaint();
			}

			@Override
			public void mouseDragged(MouseEvent me) {
				Point end = me.getPoint();
				captureRect = new Rectangle(start,
						new Dimension(end.x-start.x, end.y-start.y));
				repaint(screen, screenCopy);
				screenLabel.repaint();
				selectionLabel.setText("Rectangle: " + captureRect);
			}
		});

		JOptionPane.showMessageDialog(null, panel);

		System.out.println("Rectangle of interest: " + captureRect);
	}

	public void repaint(BufferedImage orig, BufferedImage copy) {
		Graphics2D g = copy.createGraphics();
		g.drawImage(orig,0,0, null);
		if (captureRect!=null) {
			g.setColor(Color.RED);
			g.draw(captureRect);
			g.setColor(new Color(255,255,255,150));
			g.fill(captureRect);
		}
		g.dispose();
	}

	public static void main(String[] args) throws Exception {
		/*
		Robot robot = new Robot();
		final Dimension screenSize = Toolkit.getDefaultToolkit().
				getScreenSize();
		final BufferedImage screen = robot.createScreenCapture(
				new Rectangle(screenSize));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new App(screen);
			}
		});
		*/
		new Selection();
	}
}
