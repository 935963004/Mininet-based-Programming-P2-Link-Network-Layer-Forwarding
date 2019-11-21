package edu.wisc.cs.sdn.vnet.sw;

import net.floodlightcontroller.packet.Ethernet;
import edu.wisc.cs.sdn.vnet.Device;
import edu.wisc.cs.sdn.vnet.DumpFile;
import edu.wisc.cs.sdn.vnet.Iface;
import net.floodlightcontroller.packet.MACAddress;

import java.util.HashMap;

/**
 * @author Aaron Gember-Jacobson
 */
public class Switch extends Device
{
	class ForwardingEntry
	{
		Iface inIface;
		long time;
		public ForwardingEntry(Iface inIface, long time)
		{
			this.inIface = inIface;
			this.time = time;
		}
	}

	private HashMap<MACAddress, ForwardingEntry> forwardingTable = new HashMap<>();
	/**
	 * Creates a router for a specific host.
	 * @param host hostname for the router
	 */
	public Switch(String host, DumpFile logfile)
	{
		super(host,logfile);
	}

	/**
	 * Handle an Ethernet packet received on a specific interface.
	 * @param etherPacket the Ethernet packet that was received
	 * @param inIface the interface on which the packet was received
	 */
	public void handlePacket(Ethernet etherPacket, Iface inIface)
	{
		System.out.println("*** -> Received packet: " +
                etherPacket.toString().replace("\n", "\n\t"));
		
		/********************************************************************/
		/* TODO: Handle packets                                             */
		ForwardingEntry sourceEntry = forwardingTable.get(etherPacket.getSourceMAC());
		if (sourceEntry == null) forwardingTable.put(etherPacket.getSourceMAC(), new ForwardingEntry(inIface, System.currentTimeMillis()));
		else if (System.currentTimeMillis() - sourceEntry.time > 15000) sourceEntry.time = System.currentTimeMillis();
		ForwardingEntry forwardingEntry = forwardingTable.get(etherPacket.getDestinationMAC());
		if (forwardingEntry == null || System.currentTimeMillis() - forwardingEntry.time > 15000) {
			for (Iface iface : interfaces.values()) {
				if (!iface.equals(inIface)) sendPacket(etherPacket, iface);
			}
		}
		else sendPacket(etherPacket, forwardingEntry.inIface);
		/********************************************************************/
	}
}
