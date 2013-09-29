
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Workshop2UI extends JFrame {

	private JPopupMenu viewportPopup;
	private JLabel infoLabel = new JLabel("");

	public Workshop2UI() {
		super("COMP 7502 - Workshop 2");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JScrollPane scroller = new JScrollPane(new ImagePanel());
		this.add(scroller);
		this.add(infoLabel, BorderLayout.SOUTH);
		this.setSize(500, 500);
		this.setVisible(true);
	}

	public static void main(String args[]) {
		new Workshop2UI();
	}

	private class ImagePanel extends JPanel implements MouseListener, ActionListener {
		private BufferedImage img;
		private Workshop2 imgProcessor;

		public ImagePanel() {
			imgProcessor = new Workshop2();
			this.addMouseListener(this);
		}

		public Dimension getPreferredSize() {
			if (img != null) return (new Dimension(img.getWidth(), img.getHeight()));
			else return (new Dimension(0, 0));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (img != null)
				g.drawImage(img, 0, 0, this);
		}

		private void showPopup(MouseEvent e) {
			JPopupMenu.setDefaultLightWeightPopupEnabled(false);
			viewportPopup = new JPopupMenu();

			JMenuItem openImageMenuItem = new JMenuItem("open image ...");
			openImageMenuItem.addActionListener(this);
			openImageMenuItem.setActionCommand("open image");
			viewportPopup.add(openImageMenuItem);

			JMenuItem loadDefaultImageMenuItem = new JMenuItem("load default image");
			loadDefaultImageMenuItem.addActionListener(this);
			loadDefaultImageMenuItem.setActionCommand("load default image");
			viewportPopup.add(loadDefaultImageMenuItem);
			
			viewportPopup.addSeparator();
			
			JMenuItem showHistogramMenuItem = new JMenuItem("show histogram");
			showHistogramMenuItem.addActionListener(this);
			showHistogramMenuItem.setActionCommand("show histogram");
			viewportPopup.add(showHistogramMenuItem);
			
			viewportPopup.addSeparator();

			JMenuItem negativeMenuItem = new JMenuItem("negative");
			negativeMenuItem.addActionListener(this);
			negativeMenuItem.setActionCommand("negative");
			viewportPopup.add(negativeMenuItem);
			
			JMenuItem histogramEqualizationMenuItem = new JMenuItem("histogram equalization");
			histogramEqualizationMenuItem.addActionListener(this);
			histogramEqualizationMenuItem.setActionCommand("histogram equalization");
			viewportPopup.add(histogramEqualizationMenuItem);
			
			viewportPopup.addSeparator();

			JMenuItem exitMenuItem = new JMenuItem("exit");
			exitMenuItem.addActionListener(this);
			exitMenuItem.setActionCommand("exit");
			viewportPopup.add(exitMenuItem);

			viewportPopup.show(e.getComponent(), e.getX(), e.getY());
		}

		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}

		public void mousePressed(MouseEvent e) {
			if (viewportPopup != null) {
				viewportPopup.setVisible(false);
				viewportPopup = null;
			} else
				showPopup(e);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("open image")) {
				final JFileChooser fc = new JFileChooser();
				FileFilter imageFilter = new FileNameExtensionFilter("Image files", "bmp", "gif", "jpg");
				fc.addChoosableFileFilter(imageFilter);
				fc.setDragEnabled(true);
				fc.setMultiSelectionEnabled(false);
				fc.showOpenDialog(this);
				File file = fc.getSelectedFile();
				try {
					long start = System.nanoTime();
					img = colorToGray((ImageIO.read(file)));
					double seconds = (System.nanoTime() - start) / 1000000000.0;
					infoLabel.setText(seconds+"");
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			} else if (e.getActionCommand().equals("load default image")) {
				try {
					long start = System.nanoTime();
					img = colorToGray(ImageIO.read(new URL("http://www.cs.hku.hk/~sdirk/georgesteinmetz.jpg")));
					double seconds = (System.nanoTime() - start) / 1000000000.0;
					infoLabel.setText(seconds+"");
				} catch (Exception ee) {
					JOptionPane.showMessageDialog(this, "Unable to fetch image from URL", "Error",
							JOptionPane.ERROR_MESSAGE);
					ee.printStackTrace();
				}
			} else if (e.getActionCommand().equals("show histogram")) {
				if (img!=null) {
					JFrame frame = new JFrame();
					frame.setTitle("Histogram");					
					byte[] imgData = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
					frame.add(new HistogramPanel(imgProcessor.histogram(imgData)));
					frame.pack();
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.setResizable(false);
					frame.setVisible(true);
				}
			} else if (e.getActionCommand().equals("negative")) {
				if (img!= null) {
					byte[] imgData = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
					long start = System.nanoTime();
					imgProcessor.negativeTransformation(imgData);
					double seconds = (System.nanoTime() - start) / 1000000000.0;
					infoLabel.setText(seconds+"");
				}
			} else if (e.getActionCommand().equals("histogram equalization")) {
				if (img!=null) {
					byte[] imgData = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
					long start = System.nanoTime();
					imgProcessor.histogramEqualization(imgData);
					double seconds = (System.nanoTime() - start) / 1000000000.0;
					infoLabel.setText(seconds+"");	
				}
			} 
			else if (e.getActionCommand().equals("exit")) {
				System.exit(0);
			}
			viewportPopup = null;
			this.updateUI();

		}
		
		public BufferedImage colorToGray(BufferedImage source) {
	        BufferedImage returnValue = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
	        Graphics g = returnValue.getGraphics();
	        g.drawImage(source, 0, 0, null);
	        //g.dispose();
	        return returnValue;
	    }
	}
	
	private class HistogramPanel extends JPanel {
		
		private int[] histogram;
		int width = 512;
		int height = 256;
		
		public HistogramPanel(int[] histogram) {
			this.histogram = histogram;
		}
		
		public Dimension getPreferredSize() {
			return (new Dimension(width, height));
		}
		
		public int rescale(int in, int max) {
			return (int)(in*(255.0)/(max));
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			
			int max = 0;
			for (int i=0;i<histogram.length;i++) {
				if (histogram[i]>max)
					max = histogram[i];
			}
			
			for (int i=0;i<histogram.length;i++) {
				g2.draw(new Line2D.Double(2*i, height, 2*i, height-rescale(histogram[i], max)));
				g2.draw(new Line2D.Double(2*i+1, height, 2*i+1, height-rescale(histogram[i], max)));
			}	
		}	
	}
}

