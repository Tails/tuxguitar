package org.herac.tuxguitar.io.pdf;

import java.io.OutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.herac.tuxguitar.gui.editors.chord.ChordSelector;
import org.herac.tuxguitar.io.base.TGFileFormatException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFWriter {

  /** The Logger for this class. */
  public static final transient Logger LOG = Logger
      .getLogger(PDFWriter.class);
  
  /**
   * Crea un com.lowagie.text.Image a partir de un
   * org.eclipse.swt.graphics.ImageData
   * 
   * @param data
   * @return
   */
  public static Image convertToIText(ImageData data) {
    try {
      PaletteData palette = data.palette;
      int width = data.width;
      int height = data.height;

      byte bytes[] = new byte[(width * height * 3)];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          RGB rgb = palette.getRGB(data.getPixel(x, y));
          bytes[y * (width * 3) + (x * 3)] = (byte) rgb.red;
          bytes[y * (width * 3) + (x * 3) + 1] = (byte) rgb.green;
          bytes[y * (width * 3) + (x * 3) + 2] = (byte) rgb.blue;
        }
      }
      return Image.getInstance(width, height, 3, 8, bytes);
    } catch (BadElementException e) {
      LOG.error(e);
    }
    return null;
  }

  /**
   * Escribe el documento PDF en el OutputStream
   * 
   * @param out
   * @param printDocument
   * @throws TGFileFormatException
   */
  public static void write(OutputStream out, List<ImageData> pages)
      throws TGFileFormatException {
    try {
      Document document = new Document();
      PdfWriter.getInstance(document, out);
      document.open();
      for (final ImageData data : pages) {
        document.newPage();
        document.add(convertToIText(data));
      }
      document.close();
      out.flush();
      out.close();
    } catch (Throwable throwable) {
      throw new TGFileFormatException("Could not write song!.", throwable);
    }
  }
}
