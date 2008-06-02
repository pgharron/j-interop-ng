/**j-Interop (Pure Java implementation of DCOM protocol)  
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional, 
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY; 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.jinterop.dcom.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ndr.NdrBuffer;
import ndr.NdrException;
import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIJavaCoClass;
import org.jinterop.dcom.common.JISystem;

/**<p> Class representing a Marshalled Interface Pointer. You will never use the members of this 
 * class directly, but always as an implementation of <code>IJIComObject</code> interface.
 * <br>
 * Sample Usage:-
 * <br><code>
 * IJIComObject connectionPointContainer = (IJIComObject)ieObject.queryInterface("B196B284-BAB4-101A-B69C-00AA00341D07"); <br>
 * JICallObject object = new JICallObject(connectionPointContainer.getIpid(),true); <br>
 * object.setOpnum(1); <br>
 * object.addInParamAsUUID("34A715A0-6587-11D0-924A-0020AFC7AC4D",JIFlags.FLAG_NULL); <br>
 * object.addOutParamAsObject(JIInterfacePointer.class,JIFlags.FLAG_NULL); <br>
 * Object[] objects = (Object[])connectionPointContainer.call(object); //find connection point <br>
 * JIInterfacePointer connectionPtr = (JIInterfacePointer)objects[0]; <br>
 * IJIComObject connectionPointer = JIComFactory.createCOMInstance(connectionPointContainer,connectionPtr); <br>
 * </code>
 * </p>
 * @since 1.0
 */
public final class JIInterfacePointer implements Serializable {

//	static boolean inTest = true;
	
	private JIPointer member = null;
	private static final long serialVersionUID = 2508592294719469453L;
	static final byte[] OBJREF_SIGNATURE = {0x4d,0x45,0x4f,0x57};  // 'MEOW'
	static final int  OBJREF_STANDARD = 0x1;  // standard marshaled objref
    static final int OBJREF_HANDLER  = 0x2;  // handler marshaled objref
    static final int OBJREF_CUSTOM   = 0x4;  // custom marshaled objref
	
    // Flag values for a STDOBJREF (standard part of an OBJREF).
    // SORF_OXRES1 - SORF_OXRES8 are reserved for the object exporters
    // use only, object importers must ignore them and must not enforce MBZ.
    static final int SORF_OXRES1     = 0x1;  // reserved for exporter
    static final int SORF_OXRES2     = 0x20; // reserved for exporter
    static final int SORF_OXRES3     = 0x40; // reserved for exporter
    static final int SORF_OXRES4     = 0x80; // reserved for exporter
    static final int SORF_OXRES5     = 0x100;// reserved for exporter
    static final int SORF_OXRES6     = 0x200;// reserved for exporter
    static final int SORF_OXRES7     = 0x400;// reserved for exporter
    static final int SORF_OXRES8     = 0x800;// reserved for exporter
    static final int SORF_NULL       = 0x0;   // convenient for initializing SORF
    static final int SORF_NOPING     = 0x1000;// Pinging is not required
    
    
    private JIInterfacePointer() {}
    
    /** Called from Oxid Resolver master, the resolver address are put in here itself
     * 
     * @param iid
     * @param ipid
     * @param oxid
     * @param oid
     */
    JIInterfacePointer(String iid,int port,JIStdObjRef objref)
    {
    	member = new JIPointer(new JIInterfacePointerBody(iid,port,objref),false);
    }
    
    JIInterfacePointer(String iid, JIInterfacePointer interfacePointer)	
	{
    	member = new JIPointer(new JIInterfacePointerBody(iid,interfacePointer),false);
    }
    
    void setDeffered(boolean deffered)
	{
    	member.setDeffered(true);
	}
    
	static JIInterfacePointer decode(NetworkDataRepresentation ndr,List defferedPointers, int FLAG,Map additionalData)
	{
		JIInterfacePointer ptr = new JIInterfacePointer();
		if((FLAG & JIFlags.FLAG_REPRESENTATION_INTERFACEPTR_DECODE2) == JIFlags.FLAG_REPRESENTATION_INTERFACEPTR_DECODE2)
		{
			ptr.member = (JIPointer)JIUtil.deSerialize(ndr,new JIPointer(JIInterfacePointerBody.class,true),defferedPointers,FLAG,additionalData);
		}
		else
		{
			ptr.member = (JIPointer)JIUtil.deSerialize(ndr,new JIPointer(JIInterfacePointerBody.class),defferedPointers,FLAG,additionalData);
		}
		//the pointer is null, no point of it's wrapper being present, so return null from here as well
		if (ptr.member.isNull())
		{
			ptr = null;
		}
		return ptr;
	}
	
	/**
	 * @exclude
	 * @return
	 */
    int getObjectType()
    {
    	return ((JIInterfacePointerBody)(member.getReferent())).getObjectType();
    }

    /**
     * @exclude
     * @param objectType
     * @return
     */
    Object getObjectReference(int objectType)
    {
    	return ((JIInterfacePointerBody)(member.getReferent())).getObjectReference(objectType);
    }
    
    /**Returns the Interface Identifier for this MIP. 
     * 
     * @return String representation of 128 bit uuid. 
     */
    public String getIID()
    {
    	return ((JIInterfacePointerBody)(member.getReferent())).getIID();
    }
    
    /**
     * @exclude
     * @return
     */
    public String getIPID()
    {
    	return ((JIInterfacePointerBody)(member.getReferent())).getIPID();
    }
    
    /**
     * @exclude
     * @return
     */
    public byte[] getOID()
    {
    	return ((JIStdObjRef)((JIInterfacePointerBody)(member.getReferent())).getObjectReference(JIInterfacePointer.OBJREF_STANDARD)).getObjectId();
    }
    
    /**
     * @exclude
     * @return
     */
    public byte[] getOXID()
    {
    	return ((JIStdObjRef)((JIInterfacePointerBody)(member.getReferent())).getObjectReference(JIInterfacePointer.OBJREF_STANDARD)).getOxid();
    }
    
    /**
     * @exclude
     * @return
     */
    JIDualStringArray getStringBindings()
    {
    	return ((JIInterfacePointerBody)(member.getReferent())).getStringBindings();
    }
    
    /**
     * @exclude
     * @return
     */
    int getLength()
    {
    	return ((JIInterfacePointerBody)(member.getReferent())).getLength();
    }
    
    
    void encode (NetworkDataRepresentation ndr,List defferedPointers, int FLAG)
    {
    	JIUtil.serialize(ndr,member.getClass(),member,defferedPointers,FLAG);	
    }
    
    
 
    /** Returns an Interface Pointer representation for the Java Component
     * 
     * @param javaComponent
     * @return
     */
    public static JIInterfacePointer getInterfacePointer(JISession session,JIJavaCoClass javaComponent) throws JIException
    {
    	return JIComOxidRuntime.getInterfacePointer(session,javaComponent);
    }

    /** Returns an Interface Pointer representation from raw bytes.
     * 
     * @param session
     * @param rawBytes
     * @return
     * @throws JIException
     */
    public static JIInterfacePointer getInterfacePointer(JISession session,byte[] rawBytes) throws JIException
    {
    	NetworkDataRepresentation ndr = new NetworkDataRepresentation();
		NdrBuffer ndrBuffer = new NdrBuffer(rawBytes,0);
		ndr.setBuffer(ndrBuffer);
		ndrBuffer.length = rawBytes.length;
    	
    	return JIInterfacePointer.decode(ndr, new ArrayList(), JIFlags.FLAG_REPRESENTATION_INTERFACEPTR_DECODE2, new HashMap());
    }
    
    public String toString()
	{
		String retVal = "MEOW:" + getIID() + " , " + getObjectReference(JIInterfacePointer.OBJREF_STANDARD);
		return retVal;
	}


//    public static void main(String[] args) {
//    	
//    	
//		byte[] buffer = new byte[183];
//		FileInputStream inputStream;
//		try {
//			inputStream = new FileInputStream("F:/tmp/experiment/rawip2");
//			inputStream.read(new byte[13],0,13);
//			inputStream.read(buffer,0,183);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		NetworkDataRepresentation ndr = new NetworkDataRepresentation();
//		NdrBuffer ndrBuffer = new NdrBuffer(buffer,0);
//		ndr.setBuffer(ndrBuffer);
//		ndrBuffer.length = 183;
//    	
//    	JIInterfacePointer ptr = JIInterfacePointer.decode(ndr, new ArrayList(), 0, new HashMap());
//    	try {
//    		JISystem.getLogger().setLevel(Level.FINEST);
//			JISystem.setInBuiltLogHandler(false);
//			
//		} catch (SecurityException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//    	
//    	JISession session = JISession.createSession("deepspace9", "administrator", "enterprise");
//    	session.useSessionSecurity(true);
//    	try {
//    		JIComServer comServer = new JIComServer(session,ptr,null);
//			IJIComObject comObject = comServer.getInstance();
//			comObject.queryInterface("87bc18dc-c8b3-11d5-ae96-00b0d0e93ca1");
//		} catch (JIException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}


}

/**
 * @exclude
 */
class JIInterfacePointerBody implements Serializable
{
		private static final long serialVersionUID = 2597456459096838320L;
		private String iid = null; 
	    private int objectType = -1;
	    private JIStdObjRef stdObjRef = null;
	    private int length = -1;
	    private JIDualStringArray resolverAddr = null;
	    private int port = -1; //to be used when doing local resolution.
	    
	    private JIInterfacePointerBody() {}
	    
	    /** Called from Oxid Resolver master, the resolver address are put in here itself
	     * 
	     * @param iid
	     * @param ipid
	     * @param oxid
	     * @param oid
	     */
	    JIInterfacePointerBody(String iid,int port,JIStdObjRef objref)
	    {
	    	this.iid = iid;
	    	this.stdObjRef = objref;
	    	this.port = port;
	    	resolverAddr = new JIDualStringArray(port);
	    	length = 40 + 4 + 4 + 16 + resolverAddr.getLength();
	    }
	    
	    JIInterfacePointerBody(String iid, JIInterfacePointer interfacePointer)	
		{
			this.iid = iid;
			stdObjRef = (JIStdObjRef)interfacePointer.getObjectReference(JIInterfacePointer.OBJREF_STANDARD);
			resolverAddr = interfacePointer.getStringBindings();
			length = 40 + 4 + 4 + 16 + resolverAddr.getLength();
		}
	    
		static JIInterfacePointerBody decode(NetworkDataRepresentation ndr, int Flags)
		{
			if((Flags & JIFlags.FLAG_REPRESENTATION_INTERFACEPTR_DECODE2) == JIFlags.FLAG_REPRESENTATION_INTERFACEPTR_DECODE2)
			{
				return decode2(ndr);
			}
			int length = ndr.readUnsignedLong();
			ndr.readUnsignedLong();//length
			
			JIInterfacePointerBody ptr = new JIInterfacePointerBody();
			ptr.length = length;
			//check for MEOW
			byte b[] = new byte[4];
			ndr.readOctetArray(b,0,4);
			
			int i = 0;
			while (i != 4)
			{
				//not MEOW then what ?
				if (b[i] != JIInterfacePointer.OBJREF_SIGNATURE[i])
				{
					return null;
				}
				i++;
			}
			
			//TODO only STDOBJREF supported for now
			
			if ((ptr.objectType = ndr.readUnsignedLong()) != JIInterfacePointer.OBJREF_STANDARD)
			{
				return null;
			}
			
			try {
				rpc.core.UUID ipid2 = new rpc.core.UUID();
				ipid2.decode(ndr,ndr.getBuffer());
				ptr.iid = ipid2.toString();
			} catch (NdrException e) {
				JISystem.getLogger().throwing("JIInterfacePointer","decode",e);  
			}
			
			ptr.stdObjRef = JIStdObjRef.decode(ndr);
			
			ptr.resolverAddr = JIDualStringArray.decode(ndr);
			
			return ptr;
		}
		
		static JIInterfacePointerBody decode2(NetworkDataRepresentation ndr)
		{
		
			
			JIInterfacePointerBody ptr = new JIInterfacePointerBody();
			
			//check for MEOW
			byte b[] = new byte[4];
			ndr.readOctetArray(b,0,4);
			
			int i = 0;
			while (i != 4)
			{
				//not MEOW then what ?
				if (b[i] != JIInterfacePointer.OBJREF_SIGNATURE[i])
				{
					return null;
				}
				i++;
			}
			
			//TODO only STDOBJREF supported for now
			
			if ((ptr.objectType = ndr.readUnsignedLong()) != JIInterfacePointer.OBJREF_STANDARD)
			{
				return null;
			}
			
			try {
				rpc.core.UUID ipid2 = new rpc.core.UUID();
				ipid2.decode(ndr,ndr.getBuffer());
				ptr.iid = ipid2.toString();
			} catch (NdrException e) {
				JISystem.getLogger().throwing("JIInterfacePointer","decode",e);  
			}
			
			ptr.stdObjRef = JIStdObjRef.decode(ndr);
			
			ptr.resolverAddr = JIDualStringArray.decode(ndr);
			
			return ptr;
		}
		
		/**
		 * @exclude
		 * @return
		 */
	    int getObjectType()
	    {
	    	return objectType;
	    }

	    /**
	     * @exclude
	     * @param objectType
	     * @return
	     */
	    Object getObjectReference(int objectType)
	    {
	    	if (objectType == JIInterfacePointer.OBJREF_STANDARD)
	    	{
	    		return stdObjRef;
	    	}
	    	else
	    	{
	    		return null;
	    	}
	    }
	    
	    /**Returns the Interface Identifier for this MIP. 
	     * 
	     * @return String representation of 128 bit uuid. 
	     */
	    String getIID()
	    {
	    	return iid;
	    }
	    
	    /**
	     * @exclude
	     * @return
	     */
	    String getIPID()
	    {
	    	return stdObjRef.getIpid();
	    }
	    
	    /**
	     * @exclude
	     * @return
	     */
	    byte[] getOID()
	    {
	    	return stdObjRef.getObjectId();
	    }
	    
	    /**
	     * @exclude
	     * @return
	     */
	    JIDualStringArray getStringBindings()
	    {
	    	return resolverAddr;
	    }
	    
	    /**
	     * @exclude
	     * @return
	     */
	    int getLength()
	    {
	    	return length;
	    }
	    
	    
	    void encode(NetworkDataRepresentation ndr)
	    {
	    	
	    	//now for length  
	    	//the length for STDOBJREF is fixed 40 bytes : 4,4,8,8,16.
	    	//Dual string array has to be computed, since that can vary. MEOW = 4., flag stdobjref = 4
	    	// + 16 bytes of ipid
	    	int length = 40 + 4 + 4 + 16 + resolverAddr.getLength();
	    	
	    	ndr.writeUnsignedLong(length);
	    	ndr.writeUnsignedLong(length);
	    	
	    	ndr.writeOctetArray(JIInterfacePointer.OBJREF_SIGNATURE,0,4);    	
	    	//std ref
	    	ndr.writeUnsignedLong(JIInterfacePointer.SORF_OXRES1);
	    	
	    	try {
				rpc.core.UUID ipid2 = new rpc.core.UUID(iid);
				ipid2.encode(ndr,ndr.getBuffer());
			} catch (NdrException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			stdObjRef.encode(ndr);
			
			resolverAddr.encode(ndr);
			
			
	    }
	 
	    
}