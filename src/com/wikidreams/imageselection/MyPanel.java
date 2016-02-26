package com.wikidreams.imageselection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MyPanel extends JPanel {

	//private File dir;
	private File[] files;
	//private ArrayList<File> result;

	private JLabel screenLabel;
	private Image image;
	private BufferedImage screen;
	private BufferedImage screenCopy;
	private int currentSelectedImage;

	private Rectangle captureRect;
	private Rectangle captureRectCopy;

	public MyPanel() {
		this.captureRect = null;
		this.captureRectCopy = null;

		this.files = openDialog();
		//this.dir = new File("C:\\Development\\data\\images\\");
		//if (this.dir == null) {
		//JOptionPane.showMessageDialog(null, "Select a folder with images.");
		//return;
		//}

		//this.result = new ArrayList<File>();
		//this.displayDirectoryContents(this.dir, this.result);

		this.image = null;
		try {
			//image = ImageIO.read(new File(this.result.get(0).getAbsolutePath()));
			image = ImageIO.read(new File(this.files[0].getAbsolutePath()));
			this.currentSelectedImage = 1;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}

		this.screen = (BufferedImage) image;
		this.screenCopy = new BufferedImage(this.screen.getWidth(), this.screen.getHeight(), this.screen.getType());

		this.screenLabel = new JLabel(new ImageIcon(this.screenCopy));
		this.screenLabel.setPreferredSize(new Dimension(this.screen.getWidth(), this.screen.getHeight()));

		this.setLayout(new BorderLayout());
		this.add(screenLabel, BorderLayout.CENTER);

		final JLabel selectionLabel = new JLabel("Drag a rectangle in the image.");
		this.add(selectionLabel, BorderLayout.SOUTH);

		repaint(screen, screenCopy);
		screenLabel.repaint();

		screenLabel.addMouseMotionListener(new MouseMotionAdapter() {

			Point start = new Point();			

			@Override
			public void mouseMoved(MouseEvent me) {
				if (captureRect == null) {
					start = me.getPoint();
					repaint(screen, screenCopy);
					selectionLabel.setText("Start Point: " + start);
					screenLabel.repaint();
				}
			}

			@Override
			public void mouseDragged(MouseEvent me) {
				if (start != null) {
					Point end = me.getPoint();
					captureRect = new Rectangle(start, new Dimension(end.x-start.x, end.y-start.y));
					repaint(screen, screenCopy);
					screenLabel.repaint();
					selectionLabel.setText("Rectangle: " + captureRect);
					System.out.println(captureRect);
				}
			}
		});

		screenLabel.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent me) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent me) {
				if (captureRect != null) {
					Point start = me.getPoint();
					captureRectCopy = new Rectangle(start, captureRect.getSize());
					autoRepaint(screen, screenCopy);
					screenLabel.repaint();
				}
			}
		});
	}

	private void repaint(BufferedImage orig, BufferedImage copy) {
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

	private void autoRepaint(BufferedImage orig, BufferedImage copy) {
		Graphics2D g = copy.createGraphics();
		g.drawImage(orig,0,0, null);
		if (captureRectCopy!=null) {
			g.setColor(Color.RED);
			g.draw(captureRectCopy);
			g.setColor(new Color(255,255,255,150));
			g.fill(captureRectCopy);
		}
		g.dispose();
	}

	//private void displayDirectoryContents(File dir, ArrayList<File> result) {
	//		try {
	//File[] files = dir.listFiles();
	//for (File file : files) {
	//				if (file.isDirectory()) {
	////System.out.println("directory:" + file.getAbsolutePath());
	//displayDirectoryContents(file, result);
	//} else {
	//System.out.println("     file:" + file.getAbsolutePath());
	//result.add(new File(file.getAbsolutePath()));
	//}		
	//}
	//} catch (Exception e) {
	//			e.printStackTrace();
	//}
	//}

	public void nextImage() {
		if (this.captureRect != null) {

			//if (this.currentSelectedImage != this.result.size()) {
			if (this.currentSelectedImage != this.files.length) {
				try {
					//this.image = ImageIO.read(new File(this.result.get(this.currentSelectedImage).getAbsolutePath()));
					this.image = ImageIO.read(new File(this.files[this.currentSelectedImage].getAbsolutePath()));
					this.currentSelectedImage +=1;
					this.screen = (BufferedImage) this.image;
					this.screenCopy = new BufferedImage(this.screen.getWidth(), this.screen.getHeight(), this.screen.getType());
					this.screenLabel.setIcon(new ImageIcon(this.screenCopy));
					this.autoRepaint(this.screen, this.screenCopy);
					this.screenLabel.repaint();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "There are no more images to display.");
			}

		} else {
			JOptionPane.showMessageDialog(null, "Drag a rectangle in the image.");
		}
	}


	public void saveCoordinatesFromSelectedRegion() {
		try {

			String content = "";
			if (this.captureRectCopy == null) {
				content = captureRect.toString();
			} else {
				content = captureRectCopy.toString();
			}

			File file = new File("C:\\Development\\data\\images\\info.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			JOptionPane.showMessageDialog(null, "File saved.");
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "The system cannot find the path specified");
		}

	}


	private void createImageFromSelectedRegion(File file) {
		try {
			BufferedImage selectedImage = this.screen.getSubimage(this.captureRect.x, this.captureRect.y, this.captureRect.width, this.captureRect.height);
			//ImageIO.write(selectedImage, "jpg", new File("c:\\output.jpg"));
			ImageIO.write(selectedImage, "jpg", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File[] openDialog() {
		/*
		File folder = null;		
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			folder = fc.getSelectedFile();
		}
		return folder;
		 */
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.showOpenDialog(this);
		File[] files = chooser.getSelectedFiles();
		return files;
	}

	public void saveDialog() {
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File(this.files[this.currentSelectedImage].getName()));	
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			createImageFromSelectedRegion(fc.getSelectedFile());
		}
	}

}
