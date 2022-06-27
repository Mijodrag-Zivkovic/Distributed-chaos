package servent.message;

import java.io.Serializable;
import java.util.List;

import app.ServentInfo;

/**
 * This is your basic message. It should cover most needs.
 * It supports the following stuff:
 * <ul>
 * 	<li>Basic attributes:<ul>
 * 		<li>Message ID - unique on a single servent.</li>
 * 		<li>Message type</li>
 * 		<li>Info about the initial message sender</li>
 * 		<li>Receiver info</li>
 * 		<li>Route list (constructed via <code>makeMeASender</code> )</li>
 * 		<li>Arbitrary message text</li>
 * 		</ul>
 * 	<li>Is serializable</li>
 * 	<li>Is immutable</li>
 * 	<li>Modification methods:<ul>
 * 		<li>makeMeASender - adds the current servent to the route list</li>
 * 		<li>changeReceiver - changes the receiver info attribute</li>
 * 		<li>IMPORTANT: if your subclass adds an attribute that you need copied,
 * 		and you want to use these methods, make sure to override them to include your attribute.</li>
 * 		</ul>
 * 	<li>Equality and hashability based on message id and original sender id</li>
 * </ul>
 * @author bmilojkovic
 *
 */
public interface Message extends Serializable {

	/**
	 * Port number on which the sender of this message listens for new messages. Use this to reply.
	 */
	int getSenderPort();

	/**
	 * Port number of the receiver of the message.
	 */
	int getReceiverPort();

	/**
	 * IP address of the receiver.
	 */
	String getReceiverIpAddress();

	/**
	 * Message type. Mainly used to decide which handler will work on this message.
	 */
	MessageType getMessageType();

	/**
	 * The body of the message. Use this to see what your neighbors have sent you.
	 */
	String getMessageText();

	/**
	 * An id that is unique per servent. Combined with servent id, it will be unique
	 * in the system.
	 */
	int getMessageId();

}

