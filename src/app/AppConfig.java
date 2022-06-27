package app;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class contains all the global application configuration stuff.
 * @author bmilojkovic
 *
 */

//Ovo je prazan projekat, koji moze da vam koristi kao dobra pocetna tacka za projekat :D
public class AppConfig {

	/**
	 * Convenience access for this servent's information
	 */
	public static ServentInfo myServentInfo;

	public static int SERVENT_COUNT;
	public static int BS_PORT;

	//locks
	public static Object addNeghboursLock = new Object();
	public static Object fractalIdLock = new Object();
	public static Object resultsLock = new Object();
	public static Object startingCoordinatesLock = new Object();

	public static int numberOfVertices = -1;
	public static BigDecimal proportion = new BigDecimal("-1");
	public static double proportion2 = -1;
	public static ArrayList<CoordinatesModel> startingCoordinates = new ArrayList<>();
	public static ArrayList<CoordinatesModel> myStartingCoordinates = new ArrayList<>();
	public static CoordinatesModel startingPoint = null;

	//public static Map<Integer,ArrayList<CoordinatesModel>> resultsPerPort = new HashMap<>();
	public static ConcurrentHashMap<Integer,ArrayList<CoordinatesModel>> resultsPerPort = new ConcurrentHashMap<>();

	public static int imageHeight = 0;
	public static int imageWidth = 0;

	public static ArrayList<String> fractalIds = new ArrayList<>();
	public static ArrayList<Integer> portPerFractalId = new ArrayList<>();
	public static Integer myFractalIdIndex = -1;

	public static int softLimit = -1;
	public static int hardLimit = -1;
	public static int suspiciousPort = -1;
	public static int suspectorPort = -1;
	public static boolean didReply = false;
	public static boolean helperDidReply = false;
	public static long timeOfSending = 0;
	public static Object suspiciousLock = new Object();
	/**
	 * If this is true, the system is a clique - all nodes are each other's
	 * neighbors. 
	 */
	public static boolean IS_CLIQUE;
	
	/**
	 * Print a message to stdout with a timestamp
	 * @param message message to print
	 */
	public static void timestampedStandardPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		
		System.out.println(timeFormat.format(now) + " - " + message);
	}
	
	/**
	 * Print a message to stderr with a timestamp
	 * @param message message to print
	 */
	public static void timestampedErrorPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		
		System.err.println(timeFormat.format(now) + " - " + message);
	}
	
	/**
	 * Reads a config file. Should be called once at start of app.
	 * The config file should be of the following format:
	 * <br/>
	 * <code><br/>
	 * servent_count=3 			- number of servents in the system <br/>
	 * clique=false 			- is it a clique or not <br/>
	 * fifo=false				- should sending be fifo
	 * servent0.port=1100 		- listener ports for each servent <br/>
	 * servent1.port=1200 <br/>
	 * servent2.port=1300 <br/>
	 * servent0.neighbors=1,2 	- if not a clique, who are the neighbors <br/>
	 * servent1.neighbors=0 <br/>
	 * servent2.neighbors=0 <br/>
	 * 
	 * </code>
	 * <br/>
	 * So in this case, we would have three servents, listening on ports:
	 * 1100, 1200, and 1300. This is not a clique, and:<br/>
	 * servent 0 sees servent 1 and 2<br/>
	 * servent 1 sees servent 0<br/>
	 * servent 2 sees servent 0<br/>
	 * 
	 * @param configName name of configuration file
	 */
	public static void readConfig(String configName, int id){
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(configName)));
			
		} catch (IOException e) {
			timestampedErrorPrint("Couldn't open properties file. Exiting...");
			System.exit(0);
		}


		try {
			BS_PORT = Integer.parseInt(properties.getProperty("BS.port"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading bootstrap port. Exiting...");
			System.exit(0);
		}

		//servent count
		try {
			SERVENT_COUNT = Integer.parseInt(properties.getProperty("servent_count"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading servent_count. Exiting...");
			System.exit(0);
		}
		//////////////////////////
		//clique
		IS_CLIQUE = Boolean.parseBoolean(properties.getProperty("clique", "false"));
		//////////////////////////
		

		String portProperty = "servent"+id+".port";

		int serventPort = -1;

		try {
			serventPort = Integer.parseInt(properties.getProperty(portProperty));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading " + portProperty + ". Exiting...");
			System.exit(0);
		}

		myServentInfo = new ServentInfo("localhost", serventPort);

		try {
			numberOfVertices = Integer.parseInt(properties.getProperty("N"));
			//timestampedStandardPrint("N = " + numberOfVertices);
		}catch (NumberFormatException e){
			timestampedErrorPrint("Problem reading number of vertices");
			System.exit(0);
		}
		try {
			proportion = new BigDecimal(properties.getProperty("P"));
			proportion2 = Double.parseDouble(properties.getProperty("P"));
			//timestampedStandardPrint("P = " + proportion);
			//timestampedStandardPrint("P2 = " + proportion2);
		}catch (NumberFormatException e){
			timestampedErrorPrint("Los parametar za proporciju");
		}

		try{

			imageHeight = Integer.parseInt(properties.getProperty("image_height"));
			imageWidth = Integer.parseInt(properties.getProperty("image_width"));
			//timestampedStandardPrint("image height: " + imageHeight);
			//timestampedStandardPrint("image width: " + imageWidth);

		}catch(NumberFormatException e){
			timestampedErrorPrint("neuspesno citanje visine i sirine slike");
		}

		for (int i = 0;i < numberOfVertices; i++)
		{
			String query = "V"+i;
			String property = properties.getProperty(query);
			//AppConfig.timestampedStandardPrint("V" + i + " = " + property);
			String array[] = property.split(",");
			try{
				int x = Integer.parseInt(array[0]);
				int y = Integer.parseInt(array[1]);
				startingCoordinates.add(new CoordinatesModel(x,y));
				//AppConfig.timestampedStandardPrint("x = " + String.valueOf(x));
				//AppConfig.timestampedStandardPrint("y = " + String.valueOf(y));

			}catch (NumberFormatException e){
				AppConfig.timestampedErrorPrint("Lose koordinate pocetnih tacaka");
			}

		}

		String startingPointCoordinates[] = properties.getProperty("starting_point").split(",");
		try{
			int x = Integer.parseInt(startingPointCoordinates[0]);
			int y = Integer.parseInt(startingPointCoordinates[1]);
			startingPoint = new CoordinatesModel(x,y);
			//AppConfig.timestampedStandardPrint("Starting point: ");
			//AppConfig.timestampedStandardPrint("x = " + String.valueOf(x));
			//AppConfig.timestampedStandardPrint("y = " + String.valueOf(y));

		}catch (NumberFormatException e){
			AppConfig.timestampedErrorPrint("Lose koordinate pocetne tacke");
		}

		try{
			softLimit = Integer.parseInt(properties.getProperty("SoftLimit"));
			hardLimit = Integer.parseInt(properties.getProperty("HardLimit"));
		}catch (NumberFormatException e){
			AppConfig.timestampedErrorPrint("Losi hard i soft limiti");
		}


	}
	//no need for lock, function will be exclusively called from synchronized blocks
	public static void addZeroesToFractalIds()
	{
		AppConfig.timestampedStandardPrint("dodajem nule svima");
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0;i<fractalIds.size();i++)
		{
			String newFractalId = fractalIds.get(i).concat("0");
			fractalIds.set(i,newFractalId);
		}
//		AppConfig.timestampedStandardPrint("FractalId lista na kraju dodavanja nula: ");
//		for (String id : AppConfig.fractalIds)
//		{
//			AppConfig.timestampedStandardPrint(id);
//		}
	}


	
}
