/** 
 * Copyright (c) 1998, 2021, Oracle and/or its affiliates. All rights reserved.
 * 
 */

/*
 */

/*
 * @(#)Wallet.java	1.11 06/01/03
 */

package com.oracle.jcclassic.samples.wallet;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.OwnerPIN;

public class Wallet extends Applet {
    /* constants declaration */

    // code of CLA byte in the command APDU header
    final static byte Wallet_CLA = (byte) 0x80;

    // codes of INS byte in the command APDU header
    final static byte VERIFY = (byte) 0x20;
    final static byte CREDIT = (byte) 0x30;
    final static byte DEBIT = (byte) 0x40;
    final static byte GET_BALANCE = (byte) 0x50;
    final static byte GET_GRADE = (byte) 0x51;
    final static byte SET_GRADE = (byte) 0x52;
    final static byte GET_COURSE_INFO = (byte) 0x53;
    
    // byte positions
    final static short SET_GRADE_COMMAND_COURSE_ID_POSITION = (short) 0;
    final static short SET_GRADE_COMMAND_GRADE_POSITION = (short) 1;
    final static short SET_GRADE_COMMAND_DAY_POSITION = (short) 2;
    final static short SET_GRADE_COMMAND_MONTH_POSITION = (short) 3;
    final static short SET_GRADE_COMMAND_YEAR_POSITION = (short) 4;

    // maximum balance
    final static short MAX_BALANCE = 0x7FFF;
    // maximum transaction amount
    final static byte MAX_TRANSACTION_AMOUNT = 127;

    // maximum number of incorrect tries before the
    // PIN is blocked
    final static byte PIN_TRY_LIMIT = (byte) 0x03;
    // maximum size PIN
    final static byte MAX_PIN_SIZE = (byte) 0x08;

    // signal that the PIN verification failed
    final static short SW_VERIFICATION_FAILED = 0x6300;
    // signal the the PIN validation is required
    // for a credit or a debit transaction
    final static short SW_PIN_VERIFICATION_REQUIRED = 0x6301;
    // signal invalid transaction amount
    // amount > MAX_TRANSACTION_AMOUNT or amount < 0
    final static short SW_INVALID_TRANSACTION_AMOUNT = 0x6A83;

    // signal that the balance exceed the maximum
    final static short SW_EXCEED_MAXIMUM_BALANCE = 0x6A84;
    // signal the the balance becomes negative
    final static short SW_NEGATIVE_BALANCE = 0x6A85;
    
    // signal that the course is not valid
    final static short SW_INVALID_COURSE_ID = 0x6A86;
    
    // signal that the grade is not valid
    final static short SW_INVALID_GRADE = 0x6A87;

    /* instance variables declaration */
    OwnerPIN pin;
    short balance;
    short studentId = 2;
    
    // courses
    
    // course 1
    short course_1_id = 1;
    short course_1_grade = 0;
    short course_1_date_day = 0;
    short course_1_date_month = 0;
    short course_1_date_year = 0;
    
    // course 2
    short course_2_id = 2;
    short course_2_grade = 0;
    short course_2_date_day = 0;
    short course_2_date_month = 0;
    short course_2_date_year = 0;
    
    // course 3
    short course_3_id = 3;
    short course_3_grade = 0;
    short course_3_date_day = 0;
    short course_3_date_month = 0;
    short course_3_date_year = 0;
    
    // course 4
    short course_4_id = 4;
    short course_4_grade = 0;
    short course_4_date_day = 0;
    short course_4_date_month = 0;
    short course_4_date_year = 0;
    
    // course 5
    short course_5_id = 5;
    short course_5_grade = 0;
    short course_5_date_day = 0;
    short course_5_date_month = 0;
    short course_5_date_year = 0;
    

    private Wallet(byte[] bArray, short bOffset, byte bLength) {

        // It is good programming practice to allocate
        // all the memory that an applet needs during
        // its lifetime inside the constructor
        pin = new OwnerPIN(PIN_TRY_LIMIT, MAX_PIN_SIZE);

        byte iLen = bArray[bOffset]; // aid length
        bOffset = (short) (bOffset + iLen + 1);
        byte cLen = bArray[bOffset]; // info length
        bOffset = (short) (bOffset + cLen + 1);
        byte aLen = bArray[bOffset]; // applet data length

        // The installation parameters contain the PIN
        // initialization value
        pin.update(bArray, (short) (bOffset + 1), aLen);
        register();

    } // end of the constructor
    
    

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        // create a Wallet applet instance
        new Wallet(bArray, bOffset, bLength);
    } // end of install method

    @Override
    public boolean select() {

        // The applet declines to be selected
        // if the pin is blocked.
        if (pin.getTriesRemaining() == 0) {
            return false;
        }

        return true;

    }// end of select method

    @Override
    public void deselect() {

        // reset the pin value
        pin.reset();

    }

    @Override
    public void process(APDU apdu) {

        // APDU object carries a byte array (buffer) to
        // transfer incoming and outgoing APDU header
        // and data bytes between card and CAD

        // At this point, only the first header bytes
        // [CLA, INS, P1, P2, P3] are available in
        // the APDU buffer.
        // The interface javacard.framework.ISO7816
        // declares constants to denote the offset of
        // these bytes in the APDU buffer

        byte[] buffer = apdu.getBuffer();
        // check SELECT APDU command

        if (apdu.isISOInterindustryCLA()) {
            if (buffer[ISO7816.OFFSET_INS] == (byte) (0xA4)) {
                return;
            }
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        // verify the reset of commands have the
        // correct CLA byte, which specifies the
        // command structure
        if (buffer[ISO7816.OFFSET_CLA] != Wallet_CLA) {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        switch (buffer[ISO7816.OFFSET_INS]) {
            case GET_BALANCE:
                getBalance(apdu);
                return;
            case GET_GRADE:
            	getGrade(apdu);
            	return;
            case GET_COURSE_INFO:
            	getCourseInfo(apdu);
            	return;
            case SET_GRADE:
            	setGrade(apdu);
            	return;
            case DEBIT:
                debit(apdu);
                return;
            case CREDIT:
                credit(apdu);
                return;
            case VERIFY:
                verify(apdu);
                return;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }

    } // end of process method
    
    private void setGrade(APDU apdu)
    {
    	// access authentication
        if (!pin.isValidated()) {
            ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
        }
        
        //		 	
        // courseId 1 bit
        // grade 1 bit
        // * date -> 1 biti per componenta
        // day -> 1 biti
        // month -> 1 biti
        // year -> 1 biti
        byte[] buffer = apdu.getBuffer();

        // Lc byte denotes the number of bytes in the
        // data field of the command APDU
        byte numBytes = buffer[ISO7816.OFFSET_LC];
        
        byte courseId = buffer[ISO7816.OFFSET_CDATA + SET_GRADE_COMMAND_COURSE_ID_POSITION];
        byte grade = buffer[ISO7816.OFFSET_CDATA + SET_GRADE_COMMAND_GRADE_POSITION];
        byte day = buffer[ISO7816.OFFSET_CDATA + SET_GRADE_COMMAND_DAY_POSITION];
        byte month = buffer[ISO7816.OFFSET_CDATA + SET_GRADE_COMMAND_MONTH_POSITION];
        byte year = buffer[ISO7816.OFFSET_CDATA + SET_GRADE_COMMAND_YEAR_POSITION];
        
        // set data for the specified course
        setGradeByCourseId(courseId, grade);
        setDayByCourseId(courseId, day);
        setMonthByCourseId(courseId, month);
        setYearByCourseId(courseId, year);
        
        // indicate that this APDU has incoming data
        // and receive data starting from the offset
        // ISO7816.OFFSET_CDATA following the 5 header
        // bytes.
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        // it is an error if the number of data bytes
        // read does not match the number in Lc byte
        if ((numBytes != 5) || (byteRead != 5)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        
        short le = apdu.setOutgoing();

        if (le < 2) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // informs the CAD the actual number of bytes
        // returned
        apdu.setOutgoingLength((byte) 1);
        
        buffer[0] = (byte) (grade);
        
        apdu.sendBytes((short) 0, (short) 1);
    }
    
    private void getCourseInfo(APDU apdu)
    {
    	// access authentication
        if (!pin.isValidated()) {
            ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
        }

        byte[] buffer = apdu.getBuffer();

        // Lc byte denotes the number of bytes in the
        // data field of the command APDU
        byte numBytes = buffer[ISO7816.OFFSET_LC];

        // indicate that this APDU has incoming data
        // and receive data starting from the offset
        // ISO7816.OFFSET_CDATA following the 5 header
        // bytes.
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        // it is an error if the number of data bytes
        // read does not match the number in Lc byte
        if ((numBytes != 1) || (byteRead != 1)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // get courseId from offset
        byte courseId = buffer[ISO7816.OFFSET_CDATA];
        
        if(!(courseId >= 1 && courseId <= 5))
    		ISOException.throwIt(SW_INVALID_COURSE_ID);
        
        short grade = getGradeByCourseId(courseId);
        short day = getDayByCourseId(courseId);
        short month = getMonthByCourseId(courseId);
        short year = getYearByCourseId(courseId);
        
        short le = apdu.setOutgoing();

        if (le < 2) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // informs the CAD the actual number of bytes
        // returned
        apdu.setOutgoingLength((byte) 4);
        
        buffer[0] = (byte) (grade);
        buffer[1] = (byte) (day);
        buffer[2] = (byte) (month);
        buffer[3] = (byte) (year);
        
        apdu.sendBytes((short) 0, (short) 4);
    }
    
    private void setGradeByCourseId(short courseId, short grade)
    {
    	switch (courseId) {
			case 1:
				course_1_grade = grade;
				break;
			case 2:
				course_2_grade = grade;
				break;
			case 3:
				course_3_grade = grade;
				break;
			case 4:
				course_4_grade = grade;
				break;
			case 5:
				course_5_grade = grade;
				break;
    	}
    }
    
    private void setDayByCourseId(short courseId, short day)
    {
    	switch (courseId) {
			case 1:
				course_1_date_day = day;
				break;
			case 2:
				course_2_date_day = day;
				break;
			case 3:
				course_3_date_day = day;
				break;
			case 4:
				course_4_date_day = day;
				break;
			case 5:
				course_5_date_day = day;
				break;
    	}
    }
    
    private void setMonthByCourseId(short courseId, short month)
    {
    	switch (courseId) {
			case 1:
				course_1_date_month = month;
				break;
			case 2:
				course_2_date_month = month;
				break;
			case 3:
				course_3_date_month = month;
				break;
			case 4:
				course_4_date_month = month;
				break;
			case 5:
				course_5_date_month = month;
				break;
    	}
    }
    
    private void setYearByCourseId(short courseId, short year)
    {
    	switch (courseId) {
			case 1:
				course_1_date_year = year;
				break;
			case 2:
				course_2_date_year = year;
				break;
			case 3:
				course_3_date_year = year;
				break;
			case 4:
				course_4_date_year = year;
				break;
			case 5:
				course_5_date_year = year;
				break;
    	}
    }
    
    private void getGrade(APDU apdu)
    {
    	// access authentication
        if (!pin.isValidated()) {
            ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
        }

        byte[] buffer = apdu.getBuffer();

        // Lc byte denotes the number of bytes in the
        // data field of the command APDU
        byte numBytes = buffer[ISO7816.OFFSET_LC];

        // indicate that this APDU has incoming data
        // and receive data starting from the offset
        // ISO7816.OFFSET_CDATA following the 5 header
        // bytes.
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        // it is an error if the number of data bytes
        // read does not match the number in Lc byte
        if ((numBytes != 1) || (byteRead != 1)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // get courseId from offset
        byte courseId = buffer[ISO7816.OFFSET_CDATA];
        
        if(!(courseId >= 1 && courseId <= 5))
    		ISOException.throwIt(SW_INVALID_COURSE_ID);
        
        short grade = getGradeByCourseId(courseId);
        
        short le = apdu.setOutgoing();

        if (le < 2) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // informs the CAD the actual number of bytes
        // returned
        apdu.setOutgoingLength((byte) 1);
        
//        buffer[0] = (byte) (grade >> 8);
        buffer[0] = (byte) (grade & 0xFF);
        
        apdu.sendBytes((short) 0, (short) 1);
    }
    
    private short getGradeByCourseId(short courseId)
    {
    	switch (courseId) {
			case 1:
				return course_1_grade;
			case 2:
				return course_2_grade;
			case 3:
				return course_3_grade;
			case 4:
				return course_4_grade;
			case 5:
				return course_5_grade;
    	}
		return 0;
    }
    
    private short getDayByCourseId(short courseId)
    {
    	switch (courseId) {
			case 1:
				return course_1_date_day;
			case 2:
				return course_2_date_day;
			case 3:
				return course_3_date_day;
			case 4:
				return course_4_date_day;
			case 5:
				return course_5_date_day;
    	}
		return 0;
    }
    
    private short getMonthByCourseId(short courseId)
    {
    	switch (courseId) {
			case 1:
				return course_1_date_month;
			case 2:
				return course_2_date_month;
			case 3:
				return course_3_date_month;
			case 4:
				return course_4_date_month;
			case 5:
				return course_5_date_month;
    	}
		return 0;
    }
    
    private short getYearByCourseId(short courseId)
    {
    	switch (courseId) {
			case 1:
				return course_1_date_year;
			case 2:
				return course_2_date_year;
			case 3:
				return course_3_date_year;
			case 4:
				return course_4_date_year;
			case 5:
				return course_5_date_year;
    	}
		return 0;
    }
    
    private void credit(APDU apdu) {

        // access authentication
        if (!pin.isValidated()) {
            ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
        }

        byte[] buffer = apdu.getBuffer();

        // Lc byte denotes the number of bytes in the
        // data field of the command APDU
        byte numBytes = buffer[ISO7816.OFFSET_LC];

        // indicate that this APDU has incoming data
        // and receive data starting from the offset
        // ISO7816.OFFSET_CDATA following the 5 header
        // bytes.
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        // it is an error if the number of data bytes
        // read does not match the number in Lc byte
        if ((numBytes != 1) || (byteRead != 1)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // get the credit amount
        byte creditAmount = buffer[ISO7816.OFFSET_CDATA];

        // check the credit amount
        if ((creditAmount > MAX_TRANSACTION_AMOUNT) || (creditAmount < 0)) {
            ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);
        }

        // check the new balance
        if ((short) (balance + creditAmount) > MAX_BALANCE) {
            ISOException.throwIt(SW_EXCEED_MAXIMUM_BALANCE);
        }

        // credit the amount
        balance = (short) (balance + creditAmount);

    } // end of deposit method

    private void debit(APDU apdu) {

        // access authentication
        if (!pin.isValidated()) {
            ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
        }

        byte[] buffer = apdu.getBuffer();

        byte numBytes = (buffer[ISO7816.OFFSET_LC]);

        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        if ((numBytes != 1) || (byteRead != 1)) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // get debit amount
        byte debitAmount = buffer[ISO7816.OFFSET_CDATA];

        // check debit amount
        if ((debitAmount > MAX_TRANSACTION_AMOUNT) || (debitAmount < 0)) {
            ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);
        }

        // check the new balance
        if ((short) (balance - debitAmount) < (short) 0) {
            ISOException.throwIt(SW_NEGATIVE_BALANCE);
        }

        balance = (short) (balance - debitAmount);

    } // end of debit method

    private void getBalance(APDU apdu) {

        byte[] buffer = apdu.getBuffer();

        // inform system that the applet has finished
        // processing the command and the system should
        // now prepare to construct a response APDU
        // which contains data field
        short le = apdu.setOutgoing();

        if (le < 2) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // informs the CAD the actual number of bytes
        // returned
        apdu.setOutgoingLength((byte) 2);

        // move the balance data into the APDU buffer
        // starting at the offset 0
        buffer[0] = (byte) (studentId >> 8);
        buffer[1] = (byte) (studentId & 0xFF);

        // send the 2-byte balance at the offset
        // 0 in the apdu buffer
        apdu.sendBytes((short) 0, (short) 2);

    } // end of getBalance method

    private void verify(APDU apdu) {

        byte[] buffer = apdu.getBuffer();
        // retrieve the PIN data for validation.
        byte byteRead = (byte) (apdu.setIncomingAndReceive());

        // check pin
        // the PIN data is read into the APDU buffer
        // at the offset ISO7816.OFFSET_CDATA
        // the PIN data length = byteRead
        if (pin.check(buffer, ISO7816.OFFSET_CDATA, byteRead) == false) {
            ISOException.throwIt(SW_VERIFICATION_FAILED);
        }
        
        short le = apdu.setOutgoing();

        if (le < 2) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }

        // informs the CAD the actual number of bytes
        // returned
        apdu.setOutgoingLength((byte) 1);

        // move the balance data into the APDU buffer
        // starting at the offset 0
        buffer[0] = (byte) (studentId);

        // send the 2-byte balance at the offset
        // 0 in the apdu buffer
        apdu.sendBytes((short) 0, (short) 1);

    } // end of validate method
} // end of class Wallet

