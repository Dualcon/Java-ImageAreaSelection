package com.wikidreams.imageselection;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App {

	public static void main(String[] args) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("Window");
				frame.setPreferredSize(new Dimension(1024, 768));
				frame.setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				MyPanel panel = new MyPanel();
				frame.add(panel, BorderLayout.CENTER);
				frame.setVisible(true);
				frame.addKeyListener(new KeyListener() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (e.getKeyChar() == 'n') {
							panel.nextImage();
						}
						if (e.getKeyChar() == 'm') {
							panel.marqRectangle();
						}
						if (e.getKeyChar() == 'c') {
							panel.saveDialog();
						}
						if (e.getKeyChar() == 'p') {
							panel.getImageProperties();
						}
						if (e.getKeyChar() == 'r') {
							panel.exportSelectedRegionWithNewDimensions();
						}
						if (e.getKeyChar() == 'a') {
							panel.resizeAllImagesToDefaultResolution();
						}
						if (e.getKeyChar() == 'g') {
							panel.enableGrayScale();
						}
					}

					@Override
					public void keyReleased(KeyEvent e) {
					}

					@Override
					public void keyPressed(KeyEvent e) {
					}
				});
			}
		});
	}
}
