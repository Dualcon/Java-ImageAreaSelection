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
import javax.swing.JPasswordField;
import javax.swing.JTextField;

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
	private Boolean rectangleIsCreated;

	/* Resize the images on screen */
	private Double windowWidth;
	private Double windowHeight;


	public MyPanel() {
		this.image = null;
		this.captureRect = null;
		this.rectangleIsCreated = false;
		this.windowWidth = 1024.0;
		this.windowHeight = 768.0;

		this.setLayout(new BorderLayout());
		this.screenLabel = new JLabel(this.icon = new ImageIcon());
		this.add(screenLabel, BorderLayout.CENTER);

		final JLabel selectionLabel = new JLabel("Drag a rectangle in the image.");
		this.add(selectionLabel, BorderLayout.SOUTH);

		screenLabel.addMouseMotionListener(new MouseMotionAdapter() {

			Point start = new Point();			

			@Override
			public void mouseMoved(MouseEvent me) {
				if (! rectangleIsCreated) {
					start = me.getPoint();
					repaint(screen, screenCopy);
					selectionLabel.setText("Start Point: " + start);
					screenLabel.repaint();
				}
			}

			@Override
			public void mouseDragged(MouseEvent me) {
				if (! rectangleIsCreated) {
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
			this.image = scaleImageInRatio(ImageIO.read(new File(this.files[this.currentSelectedImage].getAbsolutePath())), this.windowWidth, this.windowHeight);
			this.screen = (BufferedImage) this.image;
			this.screenCopy = new BufferedImage(this.screen.getWidth(), this.screen.getHeight(), this.screen.getType());
			this.screenLabel.setPreferredSize(new Dimension(this.screen.getWidth(), this.screen.getHeight()));
			this.icon.setImage(this.screenCopy);
			this.screenLabel.setIcon(this.icon);
			this.repaint(this.screen, this.screenCopy);					
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



	private BufferedImage createImageFromSelectedRegion() {
		BufferedImage selectedRegion = this.screen.getSubimage(this.captureRect.x, this.captureRect.y, this.captureRect.width, this.captureRect.height);
		System.out.println("Selected region - W: " + selectedRegion.getWidth() + " H: " + selectedRegion.getHeight());
		return selectedRegion;
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
		fc.setSelectedFile(new File(this.files[this.currentSelectedImage - 1].getName()));	
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			BufferedImage imageFromSelection = this.createImageFromSelectedRegion();
			try {
				ImageIO.write(imageFromSelection, "jpg", fc.getSelectedFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



	private BufferedImage scaleImageInRatio(Image image, Double width, Double height) {
		BufferedImage originalImage = (BufferedImage) image;
		if ((originalImage.getWidth() <= width) && (originalImage.getWidth() <= height)) {
			return originalImage;
		}

		double windowRatio = width / height;
		double imageRatio = (double) originalImage.getWidth() / originalImage.getHeight();
		double scaleRatio = 0.0;
		if (windowRatio > imageRatio) {
			scaleRatio = height / originalImage.getHeight();
		} else {
			scaleRatio = width / originalImage.getWidth();
		}

		BufferedImage scaledBI = null;
		if(scaleRatio < 1) {
			double wr = originalImage.getWidth() * scaleRatio;
			double hr = originalImage.getHeight() * scaleRatio;
			int w = (int) wr;
			int h = (int) hr;
			System.out.println("New w: " + w + " h: " + h);
			scaledBI = this.scaleImage(originalImage, w, h);
		}
		return scaledBI;
	}




	private BufferedImage scaleImage(BufferedImage image, int w, int h) {
		Boolean preserveAlpha = true;
		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaledBI = new BufferedImage(w, h, imageType);
		Graphics2D g = scaledBI.createGraphics();
		if (preserveAlpha) {
			g.setComposite(AlphaComposite.Src);
		}
		g.drawImage(image, 0, 0, w, h, null); 
		g.dispose();
		return scaledBI;
	}



	public void marqRectangle() {
		this.rectangleIsCreated = true;
	}



	public void getImageProperties() {
		for (File file : this.files) {
			try {
				BufferedImage image = ImageIO.read(file);
				System.out.println(file.getName() + " - Width: " + image.getWidth() + " Height: " + image.getHeight());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



	public void exportSelectedRegionWithNewDimensions() {
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File(this.files[this.currentSelectedImage - 1].getName()));	
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// Get selected region.
			BufferedImage imageFromSelection = this.createImageFromSelectedRegion();
			// Resize selected region.
			BufferedImage resizedImageFromSelection = this.scaleImageInRatio(imageFromSelection, 50.0, 50.0);			
			try {
				ImageIO.write(resizedImageFromSelection, "jpg", fc.getSelectedFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



	public void resizeAllImagesToASpecificResolution() {
		File dir = new File("c:\\neg");
		if (! dir.exists()) {
			dir.mkdir();
		}
		for (File file : this.files) {
			try {
				BufferedImage image = ImageIO.read(file);
				BufferedImage resizedImage = this.scaleImageInRatio(image, 320.0, 240.0);
				ImageIO.write(resizedImage, "jpg", new File(dir + "\\" + file.getName()));
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}



	public void resizeAllImagesToDefaultResolution() {
		// Create the input dialog.
		JTextField width = new JTextField();
		width.setToolTipText("Width");
		JTextField height = new JTextField();
		height.setToolTipText("height");
		Object[] message = {
				"Width:", width,
				"Height:", height 
		};
		int option = JOptionPane.showConfirmDialog(null, message, "Enter the new dimensions.", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			int w = 0;
			int h = 0;
			try {
				w = Integer.parseInt(width.getText());
				h = Integer.parseInt(height.getText());
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
				return;
			}

			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				for (File file : this.files) {
					try {
						BufferedImage image = this.scaleImage(ImageIO.read(file), w, h);
						ImageIO.write(image, "jpg", new File(fc.getSelectedFile() + "\\" + file.getName()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
