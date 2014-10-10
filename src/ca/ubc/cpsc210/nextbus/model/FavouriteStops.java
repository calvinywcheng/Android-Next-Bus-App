package ca.ubc.cpsc210.nextbus.model;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import ca.ubc.cpsc210.nextbus.storage.FavouriteStopsJSONSerializer;

/**
 * Favourite bus stops
 * 
 * Design Pattern: Singleton
 */
public class FavouriteStops {

	private static final String LOG_TAG = "FavouriteStops";
	private static final String FILENAME = "favs.json";
	private FavouriteStopsJSONSerializer serializer;
	private ArrayList<BusStop> stops;
	private int indexOfSelected;
	private static FavouriteStops instance;

	/**
	 * Constructor
	 * 		Reads favourite stops from file if saved from previous execution
	 *      of app.  Otherwise, list of favourite stops is empty.
	 * @param c  application context
	 */
	private FavouriteStops(Context c) {
		indexOfSelected = -1;
		
		serializer = new FavouriteStopsJSONSerializer(c, FILENAME);

		try {
			stops = serializer.readFavourites();
		} catch (Exception e) {
			stops = new ArrayList<BusStop>();
			Log.e(LOG_TAG, "Error loading favourite bus stops");
		}
	}

	/**
	 * Gets single instance of this class
	 * @param c  application context
	 * @return instance (the only one) of FavouriteStops
	 */
	public static FavouriteStops getInstance(Context c) {
		if (instance == null) {
			instance = new FavouriteStops(c);
		}

		return instance;
	}

	/**
	 * Adds a bus stop to favourite stops only if stop is not null
	 * and is not already on list of favourite stops
	 * 
	 * @param stop   the bus stop to add
	 * @return true if stop was added, false otherwise
	 */
	public boolean addStop(BusStop stop) {
		if (stop != null && !stops.contains(stop)) {
			stops.add(stop);
			return true;
		}
		return false;
	}

	/**
	 * Remove a stop from list of favourite stops
	 * 
	 * @param stop  the bus stop to remove
	 */
	public void removeStop(BusStop stop) {
		stops.remove(stop);
	}

	/**
	 * Get list of favourite stops
	 * @return  list of favourite stops
	 */
	public ArrayList<BusStop> getFavourites() {
		return stops;
	}
	
	/**
	 * Set the index into the list of the selected stop
	 * 
	 * @param indexOfSelected  index of selected stop
	 */
	public void setIndexOfSelected(int indexOfSelected) {
		this.indexOfSelected = indexOfSelected;
	}
	
	/**
	 * Produce the selected bus stop
	 * 
	 * @return selected bus stop or null if none selected
	 */
	public BusStop getSelectedStop() {
		if(indexOfSelected != -1)
			return stops.get(indexOfSelected);
		
		return null;
	}
	
	/**
	 * Clears the list of favourite stops.
	 */
	public void clear() {
		stops.clear();
		indexOfSelected = -1;
	}

	/**
	 * Write favourite stops to file
	 * @return true if successful, false otherwise
	 */
	public boolean saveFavourites() {
		try {
			serializer.writeFavourites(stops);
			Log.i(LOG_TAG, "Favourites written to file");
			return true;
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error saving favourites to file");
			return false;
		}
	}
}
