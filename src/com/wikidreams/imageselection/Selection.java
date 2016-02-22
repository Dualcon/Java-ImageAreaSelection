package com.wikidreams.imageselection;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Selection {

	private File dir;
	private ArrayList<File> result;

	private JFrame frame;
	private JLabel screenLabel;
	private ImageIcon image;

	public Selection() {
		this.dir = new File("c:\\images\\");
		this.result = new ArrayList<File>();
		this.displayDirectoryContents(this.dir, this.result);

		this.image = new ImageIcon(this.result.get(0).getAbsolutePath());
		this.screenLabel = new JLabel(this.image);
		this.screenLabel.setPreferredSize(new Dimension(this.image.getIconWidth(), this.image.getIconHeight()));

		this.frame = new JFrame("Image selection");
		this.frame.add(this.screenLabel);
		this.frame.setVisible(true);
		this.frame.pack();

		this.frame.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				JOptionPane.showMessageDialog(null, "Pressed key: "+e.getKeyChar());
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

	}


	private void displayDirectoryContents(File dir, ArrayList<File> result) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					//System.out.println("directory:" + file.getAbsolutePath());
					displayDirectoryContents(file, result);
				} else {
					//System.out.println("     file:" + file.getAbsolutePath());
					result.add(new File(file.getAbsolutePath()));
				}		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
