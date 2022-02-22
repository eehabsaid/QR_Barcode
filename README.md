1- open cmd on windwos, terminal on linux
2- type (sqlplus / as sysdba) and press enter
3- execute the following commands in order to create the required DB_USER and tables:
	A- create user bqrcode identified by bqrcode;
	B- gratn dba to bqrcode; --this command must be executed from sqlplus to grant DBA role in the right way.
	C- exite;
	D- type (sqlplus bqrcode/bqrcode@orcl) and press enter
	E- create table bcode_tpl(id integer, bcode_data varchar2(255),bcode blob);
	F- create table qrcode_tpl(id integer, qrcode_data varchar2(4000),qrcode blob);
--now you have ready DB-Schema and tables.
4- open cmd on windwos, terminal on linux and type the following commands:
	A- cd C:\Barcode\Resources and press enter
	B- execute the following commands in command prompet in the same order:
	
loadjava -force -resolve -genmissing -user BQRCODE/bqrcode@orcl -verbose barbecue-2.0-dev.jar

/*
you'll find the following three invalid java objects don't worry just drop them won't need them:
	net/sourceforge/barbecue/Main
	net/sourceforge/barbecue/formatter/SVGFormatter
	net/sourceforge/barbecue/output/SVGOutput
*/

loadjava -force -resolve -genmissing -user BQRCODE/bqrcode@orcl -verbose core-1.7.jar

loadjava -force -resolve -genmissing -user BQRCODE/bqrcode@orcl -verbose javase-1.7.jar

loadjava -force -resolve -genmissing -user BQRCODE/bqrcode@orcl -verbose qrgen-1.2.jar

5- open cmd on windwos, terminal on linux and type the following commands:
	A- sqlplus bqrcode/bqrcode@orcl --and press enter
	B- @01_QRBarcode.java; --and press enter
	C- @02_QR_BARCODE_PKG.sql; --and press enter;
	D- exec dbms_java.grant_permission( 'bqrcode', 'SYS:java.io.FilePermission', 'D:\*', 'read,write' );

now all things almost done

you can test thru oracle SQL Developer just open it connect to the schema and execute the following queries:

SELECT GET_BARCODE('TEST') FROM DUAL;

SELECT GET_QRCODE('TEST') FROM DUAL;

you can save the returning value as image in your HD

if you want to test the other DB_Procedures you have to populate (BCODE_TPL and QRCODE_TPL) tables first.

6- open cmd on windwos, terminal on linux and type the following commands:
	A- sqlplus bqrcode/bqrcode@orcl --and press enter
	B- @03_QRCODE_TPL.sql; --and press enter;
	C- @04_BCODE_TPL.sql --and press enter
	
7- open Oracle SQL Developer and connect to the schema and execute the following commands:


SELECT QR_BARCODE_PKG.GET_BARCODE('TEST') FROM DUAL;

begin
QR_BARCODE_PKG.SET_BARCODE;
end;
/

SELECT QR_BARCODE_PKG.GET_QRCODE('TEST') FROM DUAL;

begin
QR_BARCODE_PKG.SET_QRCODE;
end;
/
begin
QR_BARCODE_PKG.GEN_QR_CODE_TO_FILE('TEST_QRCODE','D:\TESTQR.jpg');
end;
/
begin
QR_BARCODE_PKG.GEN_BARCODE_TO_FILE('TEST_Barcode','D:\TESTBarcode.jpg');
end;
/

now your tables are ready and have Barcode and QRCode iamges data and you can use in your GUIs.
Enjoy.