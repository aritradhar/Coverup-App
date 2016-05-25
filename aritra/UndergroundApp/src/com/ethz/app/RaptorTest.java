package com.ethz.app;

import static net.fec.openrq.parameters.ParameterChecker.maxAllowedDataLength;
import static net.fec.openrq.parameters.ParameterChecker.minDataLength;

import java.util.Arrays;
import java.util.Random;

import net.fec.openrq.ArrayDataDecoder;
import net.fec.openrq.ArrayDataEncoder;
import net.fec.openrq.EncodingPacket;
import net.fec.openrq.OpenRQ;
import net.fec.openrq.decoder.SourceBlockDecoder;
import net.fec.openrq.decoder.SourceBlockState;
import net.fec.openrq.encoder.DataEncoder;
import net.fec.openrq.encoder.SourceBlockEncoder;
import net.fec.openrq.parameters.FECParameters;
import net.fec.openrq.util.datatype.SizeOf;


public class RaptorTest {

    // Fixed value for the payload length
    private static final int PAY_LEN = 1500 - 20 - 8; // UDP-Ipv4 payload length

    // Fixed value for the maximum decoding block size
    private static final int MAX_DEC_MEM = 8 * 1024 * 1024; // 8 MiB

    // The maximum allowed data length, given the parameters above
    public static final long MAX_DATA_LEN = maxAllowedDataLength(PAY_LEN, MAX_DEC_MEM);


    /**
     * Returns FEC parameters given a data length.
     * 
     * @param dataLen
     *            The length of the source data
     * @return a new instance of <code>FECParameters</code>
     * @throws IllegalArgumentException
     *             If the provided data length is non-positive or surpasses
     *             <code>MAX_DATA_LEN</code>
     */
    public static FECParameters getParameters(long dataLen) {

        if (dataLen < minDataLength())
            throw new IllegalArgumentException("data length is too small");
        if (dataLen > MAX_DATA_LEN)
            throw new IllegalArgumentException("data length is too large");

        return FECParameters.deriveParameters(dataLen, PAY_LEN, MAX_DEC_MEM);
    }
    

    private static void encodeSourceBlock(SourceBlockEncoder sbEnc) {

        // send all source symbols
        for (EncodingPacket pac : sbEnc.sourcePacketsIterable()) {
            sendPacket(pac);
        }

        // number of repair symbols
    }


    private static void sendPacket(EncodingPacket pac) {

        // send the packet to the receiver
    }
    public static void encodeData(DataEncoder dataEnc) {

        for (SourceBlockEncoder sbEnc : dataEnc.sourceBlockIterable()) {
            encodeSourceBlock(sbEnc);
        }
    }

    /**
     * Encodes a specific source block from a data encoder.
     * 
     * @param dataEnc
     *            A data encoder
     * @param sbn
     *            A "source block number": the identifier of the source block to be encoded
     */
    public static void encodeBlock(DataEncoder dataEnc, int sbn) {

        SourceBlockEncoder sbEnc = dataEnc.sourceBlock(sbn);
        encodeSourceBlock(sbEnc);
    }
    
    
    
    
    
    public static void main(String[] args) {
		
    	FECParameters param = getParameters(1000000000);
    	
    	System.out.println(param);
    	
    	byte[] data = new byte[1000000000];
    	new Random().nextBytes(data);
    	
    	ArrayDataEncoder enc = OpenRQ.newEncoder(data, param);   	
    	ArrayDataDecoder dec = OpenRQ.newDecoder(param, 0);
    	
    	
    	int i = 0, j = 0;
    	
    	SourceBlockState sbState = SourceBlockState.INCOMPLETE;
    	
    	
    	for (SourceBlockEncoder sbEnc : enc.sourceBlockIterable()) 
    	{
            i++; j = 0;
            
    		final SourceBlockDecoder sbDec = dec.sourceBlock(sbEnc.sourceBlockNumber());
            
    		System.out.println(sbEnc.numberOfSourceSymbols());
    		
    		for (EncodingPacket srcPacket : sbEnc.sourcePacketsIterable())
    		{
                j++;      
                sbState = sbDec.putEncodingPacket(srcPacket);
                /*if(sbState == SourceBlockState.DECODED)
                {
                	System.out.println(i + "," + j);
                	break;
                }*/
            }
        }

    	System.out.println(Arrays.equals(data, dec.dataArray()));
    	System.out.println(i + ","+ j);
    	//System.out.println(enc);
	}
}