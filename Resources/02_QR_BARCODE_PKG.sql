CREATE OR REPLACE PACKAGE QR_BARCODE_PKG
AS
	FUNCTION GET_BARCODE (PARAM1 VARCHAR2)
		RETURN BLOB
	AS
		LANGUAGE JAVA
		NAME 'CreateBarcode.getBarcode(java.lang.String) return java.sql.Blob';

	PROCEDURE SET_BARCODE
	AS
		LANGUAGE JAVA
		NAME 'CreateBarcode.setBarcode()';

	FUNCTION GET_QRCODE (PARAM1 VARCHAR2)
		RETURN BLOB
	AS
		LANGUAGE JAVA
		NAME 'CreateBarcode.getQrcode(java.lang.String) return java.sql.Blob';

	PROCEDURE SET_QRCODE
	AS
		LANGUAGE JAVA
		NAME 'CreateBarcode.setQrcode()';

	PROCEDURE GEN_QR_CODE_TO_FILE (PARAM1 VARCHAR2, PARAM2 VARCHAR2)
	AS
		LANGUAGE JAVA
		NAME 'CreateBarcode.GenQrcodeToFile(java.lang.String, java.lang.String)'

;

	PROCEDURE GEN_BARCODE_TO_FILE (PARAM1 VARCHAR2, PARAM2 VARCHAR2)
	AS
		LANGUAGE JAVA
		NAME 'CreateBarcode.GenBarcodeToFile(java.lang.String, java.lang.String)'

;
END;
/