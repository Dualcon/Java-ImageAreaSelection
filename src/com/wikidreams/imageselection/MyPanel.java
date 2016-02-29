package com.wikidreams.imageselection;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MyPanel extends JPanel {


	private File[] files;

	private JLabel screenLabel;
	private ImageIcon icon;
	private Image image;
	private BufferedImage screen;
	private BufferedImage screenCopy;
	private int currentSelectedImage;

	private Rectangle captureRect;


	public MyPanel() {
		this.image = null;
		this.captureRect = null;

		this.setLayout(new BorderLayout());
		this.screenLabel = new JLabel(this.icon = new ImageIcon());
		this.add(screenLabel, BorderLayout.CENTER);

		final JLabel selectionLabel = new JLabel("Drag a rectangle in the image.");
		this.add(selectionLabel, BorderLayout.SOUTH);

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
				Point start = me.getPoint();
				captureRect.setLocation(start);
				repaint(screen, screenCopy);
				screenLabel.repaint();
			}
		});

		this.files = openDialog();
		if (this.files.length == 0) {
			System.exit(0);	
		}

		nextImage();

	}


	public void nextImage() {

		if (this.currentSelectedImage == this.files.length) {
			JOptionPane.showMessageDialog(null, "There are no more images to display.");
			return;
		}

		try {
			this.image = imageResize(ImageIO.read(new File(this.files[this.currentSelectedImage].getAbsolutePath())));
			this.screen = (BufferedImage) this.image;
			this.screenCopy = new BufferedImage(this.screen.getWidth(), this.screen.getHeight(), this.screen.getType());
			this.screenLabel.setPreferredSize(new Dimension(this.screen.getWidth(), this.screen.getHeight()));
			this.icon.setImage(this.screenCopy);
			this.screenLabel.setIcon(this.icon);
			repaint(this.screen, this.screenCopy);					
			this.screenLabel.repaint();
			this.currentSelectedImage +=1;
		} catch (IOException e) {
			e.printStackTrace();
		}

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


	private void createImageFromSelectedRegion(File file) {
		try {
			BufferedImage selectedImage = this.screen.getSubimage(this.captureRect.x, this.captureRect.y, this.captureRect.width, this.captureRect.height);
			ImageIO.write(selectedImage, "jpg", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private File[] openDialog() {
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


	private BufferedImage imageResize(Image image) {
		BufferedImage originalImage = (BufferedImage) image;

		if ((originalImage.getWidth() <= 1024) && (originalImage.getWidth() <= 768)) {
			return originalImage;
		}

		double windowRatio = (double) 1024 / 768;
		double imageRatio = (double) originalImage.getWidth() / originalImage.getHeight();
		System.out.println("original w:" + originalImage.getWidth() + " h:" + originalImage.getHeight());

		double scaleRatio = 0.0;
		if (windowRatio > imageRatio) {
			scaleRatio = 768.0/ originalImage.getHeight();
		} else {
			scaleRatio = 1024.0 / originalImage.getWidth();
		}

		BufferedImage scaledBI = null;
		if(scaleRatio < 1) {
			double wr = originalImage.getWidth() * scaleRatio;
			double hr = originalImage.getHeight() * scaleRatio;
			int w = (int) wr;
			int h = (int) hr;
			System.out.println("New w: " + w + " h: " + h);

			Boolean preserveAlpha = true;
			int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
			scaledBI = new BufferedImage(w, h, imageType);
			Graphics2D g = scaledBI.createGraphics();
			if (preserveAlpha) {
				g.setComposite(AlphaComposite.Src);
			}
			g.drawImage(originalImage, 0, 0, w, h, null); 
			g.dispose();
		}

		return scaledBI;
	}

	public void test() {
		System.out.println(this.captureRect);	
	}


	//public void saveCoordinatesFromSelectedRegion() {
	//		try {

	//String content = "";
	//if (this.captureRectCopy == null) {
	//content = captureRect.toString();
	//} else {
	//				content = captureRectCopy.toString();
	//}

	//File file = new File("C:\\Development\\data\\images\\info.txt");

	// if file doesnt exists, then create it
	//if (!file.exists()) {
	//file.createNewFile();
	//}

	//FileWriter fw = new FileWriter(file.getAbsoluteFile());
	//BufferedWriter bw = new BufferedWriter(fw);
	//bw.write(content);
	//bw.close();
	//JOptionPane.showMessageDialog(null, "File saved.");
	//} catch (HeadlessException e) {
	//			e.printStackTrace();
	//} catch (IOException e) {
	//			JOptionPane.showMessageDialog(null, "The system cannot find the path specified");
	//}

	//}


}
