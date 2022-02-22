CREATE OR REPLACE AND RESOLVE JAVA SOURCE NAMED "QRBarcode" AS
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

public class CreateBarcode {
    public static Blob getBarcode(String value) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:default:connection:");
        Blob retBlob = conn.createBlob();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Barcode bcode = BarcodeFactory.createCode128(value);
			bcode.setPreferredBarHeight(50);
			bcode.setBarWidth(2);
			bcode.setAlignmentX(10F);
			bcode.setAlignmentY(10F);
			bcode.setDrawingText(true);
            Image bcodeImage = BarcodeImageHandler.getImage(bcode);

            ImageIO.write((RenderedImage) bcodeImage, "jpeg", out);
            out.flush();
            byte[] imageInByte = out.toByteArray();
            out.close();

            java.io.OutputStream outStr = retBlob.setBinaryStream(0);
            outStr.write(imageInByte);
            outStr.flush();
        } finally {
            out.close();
        }
        return retBlob;
    }

    public static void setBarcode() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:default:connection:");
        ByteArrayInputStream in = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String sqlQuery = "SELECT BCODE_DATA FROM BQRCODE.BCODE_TPL WHERE BCODE IS NULL";
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            int counter = 1;
            while (rs.next()) {
                String tempBCODE_DATA = rs.getString(1);
                if (tempBCODE_DATA == null) {
                    throw new RuntimeException("BCODE_DATA retuned can't be null.");
                }
                Barcode bcode = BarcodeFactory.createCode128(tempBCODE_DATA);
				bcode.setPreferredBarHeight(50);
				bcode.setBarWidth(2);
				bcode.setAlignmentX(10F);
				bcode.setAlignmentY(10F);
				bcode.setDrawingText(true);
                Image bcodeImage = BarcodeImageHandler.getImage(bcode);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write((RenderedImage) bcodeImage, "jpeg", out);
                out.flush();
                in = new ByteArrayInputStream(out.toByteArray());
                saveBcImageIntoDB(tempBCODE_DATA, in, conn);
                counter++;
                if (counter % 10 == 0) {
                    conn.commit();
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if ((conn != null) && (!conn.isClosed())) {
                conn.commit();
                conn.close();
            }
        }
    }

    private static void saveBcImageIntoDB(String key, InputStream in, Connection conn)
            throws SQLException, IOException {
        String sql = "UPDATE BQRCODE.BCODE_TPL SET BCODE = ? WHERE BCODE_DATA = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {

            pstmt.setBinaryStream(1, in);
            pstmt.setString(2, key);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }

    public static Blob getQrcode(String value) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:default:connection:");
        Blob retBlob = conn.createBlob();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out = QRCode.from(value).to(ImageType.JPG).stream();
        try {
            java.io.OutputStream outStr = retBlob.setBinaryStream(0);
            outStr.write(out.toByteArray());
            outStr.flush();
        } finally {
            out.close();
        }
        return retBlob;
    }

    public static void setQrcode() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:default:connection:");
        ByteArrayInputStream in = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String sqlQuery = "SELECT ID, QRCODE_DATA FROM BQRCODE.QRCODE_TPL WHERE QRCODE IS NULL";
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            int counter = 1;
            while (rs.next()) {
                String tempQRCODE_DATA = rs.getString(2);
                String ID = rs.getString(1);
                if (tempQRCODE_DATA == null) {
                    throw new RuntimeException("QRCODE_DATA retuned can't be null.");
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out = QRCode.from(tempQRCODE_DATA).to(ImageType.JPG).stream();
                out.flush();
                in = new ByteArrayInputStream(out.toByteArray());

                saveQRImageIntoDB(ID, in, conn);
                counter++;
                if (counter % 10 == 0) {
                    conn.commit();
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if ((conn != null) && (!conn.isClosed())) {
                conn.commit();
                conn.close();
            }
        }
    }

    private static void saveQRImageIntoDB(String key, InputStream in, Connection conn)
            throws SQLException, IOException {
        String sql = "UPDATE BQRCODE.QRCODE_TPL SET QRCODE = ? WHERE ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {

            pstmt.setBinaryStream(1, in);
            pstmt.setString(2, key);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }

    public static void GenQrcodeToFile(String qrcodetext, String imagefilename)
            throws Exception {
        ByteArrayOutputStream out = QRCode.from(qrcodetext).to(ImageType.JPG).stream();
        FileOutputStream fout = new FileOutputStream(new File(imagefilename));
        fout.write(out.toByteArray());
        fout.flush();
        fout.close();
    }

    public static void GenBarcodeToFile(String barrcodetext, String imagefilename)
            throws Exception {
        Barcode bcode = BarcodeFactory.createCode128(barrcodetext);
        bcode.setPreferredBarHeight(50);
        bcode.setBarWidth(2);
        bcode.setAlignmentX(10F);
        bcode.setAlignmentY(10F);
        bcode.setDrawingText(true);
        Image bcodeImage = BarcodeImageHandler.getImage(bcode);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) bcodeImage, "jpeg", out);
        FileOutputStream fout = new FileOutputStream(new File(imagefilename));
        fout.write(out.toByteArray());
        fout.flush();
        fout.close();
    }
}
/