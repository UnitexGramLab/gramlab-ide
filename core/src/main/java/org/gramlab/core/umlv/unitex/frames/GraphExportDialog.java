/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */
package org.gramlab.core.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.gramlab.core.umlv.unitex.graphrendering.DrawGraphParams;
import org.gramlab.core.umlv.unitex.graphrendering.GenericGraphicalZone;
import org.w3c.dom.Element;

import java.awt.Dimension;
import javax.swing.SwingConstants;

/**
 * This class defines the dialog for exporting a graph to an image.
 * 
 * @author Nebojša Vasiljević
 */
public class GraphExportDialog extends JDialog {

	public static final int FORMAT_PNG = 1;
	public static final int FORMAT_JPEG = 2;
	public static final int FORMAT_SVG = 3;

	private final JPanel contentPanel = new JPanel();

	private final GenericGraphicalZone grZone;
	protected JTextField fldFileName;
	private JButton btnBrowse;
	private JSpinner fldZoom;
	private JSpinner fldDpi;
	private JCheckBox fldAntialiasing;
	private JCheckBox fldCrop;
	private JSpinner fldCropMarginW;
	private JSpinner fldCropMarginH;
	private JLabel lblCropMarginW;
	private JLabel lblCropMarginH;
	protected int format;

	public static String formatName(int format) {
		switch (format) {
		case FORMAT_PNG:
			return "PNG";
		case FORMAT_JPEG:
			return "JPEG";
		case FORMAT_SVG:
			return "SVG";
		}
		return null;
	}

	private static final FileFilter FILE_FILTER_PNG = new FileNameExtensionFilter(
			"PNG Images", "PNG");

	private static final FileFilter FILE_FILTER_JPEG = new FileNameExtensionFilter(
			"JPEG Images", "JPG", "JPEG");

	private static final FileFilter FILE_FILTER_SVG = new FileNameExtensionFilter(
			"SVG Images", "SVG");
	private JLabel lblDpi;
	private JSpinner fldQuality;
	private JLabel lblQuality;
	private JLabel lblZoom;

	public static FileFilter formatFileFilter(int format) {
		switch (format) {
		case FORMAT_PNG:
			return FILE_FILTER_PNG;
		case FORMAT_JPEG:
			return FILE_FILTER_JPEG;
		case FORMAT_SVG:
			return FILE_FILTER_SVG;
		}
		return null;
	}

	public static String formatDefaultExtension(int format) {
		switch (format) {
		case FORMAT_PNG:
			return "png";
		case FORMAT_JPEG:
			return "jpg";
		case FORMAT_SVG:
			return "svg";
		}
		return null;
	}

	public static boolean isBitmapFormat(int format) {
		return format == FORMAT_PNG || format == FORMAT_JPEG;
	}

	public static void openDialog(GenericGraphicalZone grZone, int format) {
		DrawGraphParams params = grZone.getExportBitmapParams();
		GraphExportDialog dialog = new GraphExportDialog(grZone, format);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setTitle("Export Graph as " + formatName(format) + " Image");
		dialog.setModal(true);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		File imgFile = null;
		if (grZone.getParentFrame() instanceof GraphFrame) {
			GraphFrame gf = (GraphFrame) grZone.getParentFrame();
			File f = gf.getGraph();
			if (f != null) {
				String s = f.getPath();
				int i = s.lastIndexOf('.');
				if (i != -1) {
					imgFile = new File(s.substring(0, i + 1)
							+ formatDefaultExtension(format));
				}
			}
		}
		if (imgFile != null) {
			dialog.fldFileName.setText(imgFile.getPath());
		} else {
			dialog.fldFileName.setText("");
		}
		dialog.fldZoom
				.setValue((int) Math.round(params.getScaleFactor() * 100));
		dialog.fldDpi.setValue(params.getDpi());
		dialog.fldQuality.setValue(params.getCompressionQuality());
		dialog.fldAntialiasing.setSelected(params.isAntialiasing());
		dialog.fldCrop.setSelected(params.isCrop());
		dialog.fldCropMarginH.setValue(params.getCropMarginH());
		dialog.fldCropMarginW.setValue(params.getCropMarginW());
		dialog.updateCropEnability();
		dialog.updateVisibility();

		dialog.setVisible(true);
	}

	protected void updateCropEnability() {
		fldCropMarginH.setEnabled(fldCrop.isSelected());
		fldCropMarginW.setEnabled(fldCrop.isSelected());
		lblCropMarginH.setEnabled(fldCrop.isSelected());
		lblCropMarginW.setEnabled(fldCrop.isSelected());
	}

	protected void updateVisibility() {
		boolean isBitmap = isBitmapFormat(format);
		boolean isJpeg = format == FORMAT_JPEG;

		fldZoom.setVisible(isBitmap);
		lblZoom.setVisible(isBitmap);
		fldDpi.setVisible(isBitmap);
		fldCropMarginH.setVisible(isBitmap);
		fldCropMarginW.setVisible(isBitmap);
		lblCropMarginH.setVisible(isBitmap);
		lblCropMarginW.setVisible(isBitmap);
		fldCrop.setVisible(isBitmap);
		fldAntialiasing.setVisible(isBitmap);
		fldDpi.setVisible(isBitmap);
		lblDpi.setVisible(isBitmap);
		fldQuality.setVisible(isJpeg);
		lblQuality.setVisible(isJpeg);

	}

	/**
	 * Create the dialog.
	 */
	public GraphExportDialog(GenericGraphicalZone grZone, int format) {
		this.grZone = grZone;
		this.format = format;
		setBounds(100, 100, 677, 328);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			fldFileName = new JTextField();
			fldFileName.setColumns(50);
		}
		{
			btnBrowse = new JButton("Browse...");
			btnBrowse.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final JFileChooser fc = new JFileChooser();
					fc.setSelectedFile(new File(fldFileName.getText()));
					FileFilter imgFilter = formatFileFilter(GraphExportDialog.this.format);
					fc.addChoosableFileFilter(imgFilter);
					fc.setFileFilter(imgFilter);
					int r = fc.showOpenDialog(GraphExportDialog.this);
					if (r == JFileChooser.APPROVE_OPTION) {
						fldFileName.setText(fc.getSelectedFile()
								.getAbsolutePath());
					}
				}
			});
		}

		JLabel lblImageFile = new JLabel("Image file:");

		lblZoom = new JLabel("Zoom (%):");

		fldZoom = new JSpinner();
		fldZoom.setModel(new SpinnerNumberModel(100, 10, 1000, 10));

		fldDpi = new JSpinner();
		fldDpi.setModel(new SpinnerNumberModel(72, 10, 999, 1));
		lblDpi = new JLabel("DPI:");

		fldAntialiasing = new JCheckBox("Antialiasing: ");
		fldAntialiasing.setHorizontalTextPosition(SwingConstants.LEFT);

		fldCrop = new JCheckBox("Crop to graph: ");
		fldCrop.setHorizontalAlignment(SwingConstants.TRAILING);
		fldCrop.setHorizontalTextPosition(SwingConstants.LEFT);
		fldCrop.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateCropEnability();
			}
		});

		fldCropMarginW = new JSpinner();
		fldCropMarginW.setModel(new SpinnerNumberModel(0, 0, 999, 1));

		lblCropMarginW = new JLabel("Crop with horizontal margin (pixels):");

		fldCropMarginH = new JSpinner();
		fldCropMarginH.setModel(new SpinnerNumberModel(0, 0, 999, 1));

		lblCropMarginH = new JLabel("Crop with vertical margin (pixels):");

		fldQuality = new JSpinner();
		fldQuality.setModel(new SpinnerNumberModel(new Float(0), new Float(0),
				new Float(1), new Float(0.05)));
		fldQuality.setEditor(new JSpinner.NumberEditor(fldQuality, "0.00"));

		lblQuality = new JLabel("Compression quality:");
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblQuality)
								.addComponent(lblZoom)
								.addComponent(lblImageFile))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(fldFileName, GroupLayout.PREFERRED_SIZE, 369, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnBrowse))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(fldQuality, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(fldZoom, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
									.addGap(34)
									.addComponent(lblDpi)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(fldDpi, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(29)
									.addComponent(fldAntialiasing))))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(21)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(fldCrop, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(lblCropMarginH)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(fldCropMarginH, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(lblCropMarginW)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(fldCropMarginW, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(ComponentPlacement.RELATED, 313, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(96, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(21)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(fldFileName, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse)
						.addComponent(lblImageFile))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(fldZoom, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
						.addComponent(fldDpi, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblDpi)
						.addComponent(fldAntialiasing)
						.addComponent(lblZoom))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(fldQuality, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblQuality))
					.addGap(13)
					.addComponent(fldCrop)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCropMarginW)
						.addComponent(fldCropMarginW, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(fldCropMarginH, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblCropMarginH))
					.addContainerGap(31, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.CENTER);
			fl_buttonPane.setVgap(10);
			fl_buttonPane.setHgap(10);
			buttonPane.setLayout(fl_buttonPane);
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setPreferredSize(new Dimension(80, 23));
				okButton.setMinimumSize(new Dimension(80, 23));
				okButton.setMaximumSize(new Dimension(80, 23));
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setCursor(new Cursor(Cursor.WAIT_CURSOR));
						try {
							doExport();
						} finally {
							new Cursor(Cursor.DEFAULT_CURSOR);
						}
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setPreferredSize(new Dimension(80, 23));
				cancelButton.setMinimumSize(new Dimension(80, 23));
				cancelButton.setMaximumSize(new Dimension(80, 23));
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	protected void doExport() {
		DrawGraphParams params = grZone.getExportBitmapParams();

		params.setScaleFactor(((Integer) fldZoom.getValue()) / 100.0);
		params.setDpi((Integer) fldDpi.getValue());
		params.setCompressionQuality((Float) fldQuality.getValue());
		params.setAntialiasing(fldAntialiasing.isSelected());
		params.setCrop(fldCrop.isSelected());
		params.setCropMarginH((Integer) fldCropMarginH.getValue());
		params.setCropMarginW((Integer) fldCropMarginW.getValue());

		File imageFile = new File(fldFileName.getText());

		if (isBitmapFormat(format)) {
			BufferedImage img = createBitmap(params);
			try {
				saveBitmap(img, params.getDpi(),
						params.getCompressionQuality(), imageFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else if(format == FORMAT_SVG) {
			GraphFrame gf = (GraphFrame) grZone.getParentFrame();
			gf.saveGraphAsAnSVG(imageFile);
		}
	}

	private BufferedImage createBitmap(DrawGraphParams params) {
		int w = (int) Math.round(grZone.getWidth() * params.getTotalScale());
		int h = (int) Math.round(grZone.getHeight() * params.getTotalScale());

		final BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setBackground(params.getBackgroundColor());
		g.clearRect(0, 0, w, h);
		grZone.drawGraph(g, params);
		BufferedImage cropedImage = image;
		if (params.isCrop()) {
			int bgcolor = params.getBackgroundColor().getRGB();
			int width = image.getWidth();
			int height = image.getHeight();
			int minX = w;
			int maxX = -1;
			int minY = h;
			int maxY = -1;
			for (int x = 0; x < width; x++)
				for (int y = 0; y < height; y++)
					if (image.getRGB(x, y) != bgcolor) {
						if (x < minX)
							minX = x;
						if (x > maxX)
							maxX = x;
						if (y < minY)
							minY = y;
						if (y > maxY)
							maxY = y;
					}
			if (maxX != -1) {
				int cropX, cropW, cropY, cropH;
				if (minX > params.getCropMarginW())
					cropX = minX - params.getCropMarginW();
				else
					cropX = 0;
				if (maxX + params.getCropMarginW() < width)
					cropW = maxX + params.getCropMarginW() - cropX + 1;
				else
					cropW = width - cropX;
				if (minY > params.getCropMarginH())
					cropY = minY - params.getCropMarginH();
				else
					cropY = 0;
				if (maxY + params.getCropMarginH() < height)
					cropH = maxY + params.getCropMarginH() - cropY + 1;
				else
					cropH = height - cropY;
				cropedImage = image.getSubimage(cropX, cropY, cropW, cropH);
			}
		}

		return cropedImage;
	}

	private void saveBitmap(BufferedImage sourceImage, int dpi, float quality,
			File output) throws IOException {

		Iterator<ImageWriter> it = ImageIO
				.getImageWritersByFormatName(formatName(format));
		boolean found = false;
		while (it.hasNext() && !found) {
			ImageWriter w = it.next();
			ImageWriteParam wp = w.getDefaultWriteParam();
			ImageTypeSpecifier ts = ImageTypeSpecifier
					.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
			IIOMetadata meta = w.getDefaultImageMetadata(ts, wp);
			if (!meta.isReadOnly() && meta.isStandardMetadataFormatSupported()) {
				found = true;

				if (format == FORMAT_PNG) {
					double dpmm = dpi / 25.4;

					IIOMetadataNode hor = new IIOMetadataNode(
							"HorizontalPixelSize");
					hor.setAttribute("value", Double.toString(dpmm));

					IIOMetadataNode ver = new IIOMetadataNode(
							"VerticalPixelSize");
					ver.setAttribute("value", Double.toString(dpmm));

					IIOMetadataNode dim = new IIOMetadataNode("Dimension");
					dim.appendChild(hor);
					dim.appendChild(ver);

					IIOMetadataNode root = new IIOMetadataNode(
							"javax_imageio_1.0");
					root.appendChild(dim);

					meta.mergeTree("javax_imageio_1.0", root);
				} else if (format == FORMAT_JPEG) {
					Element tree = (Element) meta
							.getAsTree("javax_imageio_jpeg_image_1.0");
					Element jfif = (Element) tree.getElementsByTagName(
							"app0JFIF").item(0);
					jfif.setAttribute("Xdensity", Integer.toString(dpi));
					jfif.setAttribute("Ydensity", Integer.toString(dpi));
					jfif.setAttribute("resUnits", "1"); // density is dots per
														// inch
					wp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					wp.setCompressionQuality(quality);
					meta.mergeTree("javax_imageio_jpeg_image_1.0", tree);
				}

				ImageOutputStream stream = ImageIO
						.createImageOutputStream(output);
				try {
					w.setOutput(stream);
					w.write(meta, new IIOImage(sourceImage, null, meta), wp);
				} finally {
					stream.close();
				}

			}
		}
	}
}
