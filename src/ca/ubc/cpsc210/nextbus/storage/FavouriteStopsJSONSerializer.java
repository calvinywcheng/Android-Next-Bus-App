package ca.ubc.cpsc210.nextbus.storage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import ca.ubc.cpsc210.nextbus.model.BusStop;

/**
 * Streams favourite stops to/from file in JSON format.
 * 
 * Based on CriminalIntentJSONSerializer, Ch 17,
 * "Android Programming - The Big Nerd Ranch Guide"
 * by Phillips and Hardy.
 */
public class FavouriteStopsJSONSerializer {
	private Context context;
	private String filename;
	
	public FavouriteStopsJSONSerializer(Context c, String f) {
		context = c;
		filename = f;
	}
	
	/**
	 * Write list of favourite bus stops to file
	 * @param favs  list of favourite bus stops
	 * @throws JSONException
	 * @throws IOException
	 */
	public void writeFavourites(List<BusStop> favs) throws JSONException, IOException {
		JSONArray favsAsJSON = new JSONArray();

		for(BusStop next : favs) {
			favsAsJSON.put(next.toJSON());
		}
		
		Writer writer = null;
		try {
			OutputStream out = context.openFileOutput(filename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(favsAsJSON.toString());
		} finally {
			if(writer != null)
				writer.close();
		}
	}
	
	/**
	 * Read list of favourite bus stops from file
	 * @return list of favourite bus stops
	 * @throws IOException
	 * @throws JSONException
	 */
	public ArrayList<BusStop> readFavourites() throws JSONException, IOException {
		ArrayList<BusStop> favs = new ArrayList<BusStop>();
		BufferedReader reader = null;
		
		try {
			InputStream in = context.openFileInput(filename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			
			for(int i = 0; i < array.length(); i++) {
				favs.add(new BusStop(array.getJSONObject(i)));
			}
		} catch (FileNotFoundException e) {
			// ignore: will get thrown first time application is run
		} 
		
		return favs;
	}
}
