package com.wikidreams.imageselection;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App {

	public static void main(String[] args) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//new LabelImage();
				JFrame frame = new JFrame("Window");
				frame.setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				MyPanel panel = new MyPanel();
				frame.add(panel, BorderLayout.CENTER);
				frame.setVisible(true);
				frame.pack();
				frame.addKeyListener(new KeyListener() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (e.getKeyChar() == 'd') {
							panel.nextImage();	
						}
						if (e.getKeyChar() == 's') {
							panel.saveCoordinatesFromSelectedRegion();	
						}
						if (e.getKeyChar() == 'c') {
							panel.createImageFromSelectedRegion();	
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
