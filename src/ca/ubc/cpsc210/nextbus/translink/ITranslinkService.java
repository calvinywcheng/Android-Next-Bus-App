package ca.ubc.cpsc210.nextbus.translink;

import ca.ubc.cpsc210.exception.TranslinkException;
import ca.ubc.cpsc210.nextbus.model.BusRoute;
import ca.ubc.cpsc210.nextbus.model.BusStop;

public interface ITranslinkService {

	/**
     * Add current wait time estimates for a bus to particular bus stop
     * (replaces current wait time estimates).
     * @param stop  the bus stop to which wait time estimates must be added
     * @throws TranslinkException when an exception occurs obtaining or parsing data from Translink service
     */
    public abstract void addWaitTimeEstimatesToStop(BusStop stop)
            throws TranslinkException;

    /**
     * Add bus location information for buses currently serving a particular stop
     * (replaces current list of bus locations for stop).
     * @param stop  the bus stop to which bus location estimates are to be added
     * @throws TranslinkException when an exception occurs obtaining or parsing data from Translink service
     */
    public abstract void addBusLocationsForStop(BusStop stop)
            throws TranslinkException;

    /**
     * Gets a bus stop corresponding to given stop number
     * @param stopNum  the bus stop number
     * @return corresponding bus stop
     * @throws TranslinkException when an exception occurs obtaining or parsing data from Translink service
     */
    public abstract BusStop getBusStop(String stopNum)
            throws TranslinkException;
    
    /**
     * Get route information from KMZ file at URL specified in bus route and add to bus route
     * @param route  bus route containing URL and to which parsed route data is to be added
     * @throws TranslinkException when an exception occurs obtaining or parsing data from Translink service
     */
    public void parseKMZ(BusRoute route) 
            throws TranslinkException;

}