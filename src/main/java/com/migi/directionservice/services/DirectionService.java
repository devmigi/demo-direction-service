package com.migi.directionservice.services;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.isNaN;

@Service
@Slf4j
public class DirectionService {

    @Value("${map.apiKey}")
    private String key;

    @Value("${map.distanceInterval}")
    private double distanceInterval;


    /**
     * Returns path on a map
     * @param origin
     * @param destination
     * @return
     */
    public List<LatLng> getDirection(LatLng origin, LatLng destination) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(key)
                .build();

        DirectionsResult result;
        try {
            result = DirectionsApi.newRequest(context)
                            .mode(TravelMode.DRIVING)
                            .origin(origin)
                            .destination(destination)
                            .await();

            List<LatLng> path = new ArrayList();
            path.add(origin);

            if(result.routes.length > 0){
                for (DirectionsStep step: result.routes[0].legs[0].steps) {
                    path.add(step.startLocation);

                    EncodedPolyline points = step.polyline;
                    if (points != null) {
                        //Decode polyline and add points to list of route coordinates
                        List<LatLng> coords = points.decodePath();
                        for (LatLng coord : coords) {
                            path.add(coord);
                        }
                    }
                }
                path.add(destination);
            }


            List<LatLng> markerPoints = new ArrayList();
            double nextDistance = distanceInterval;
            markerPoints.add(path.get(0));

            for (int i = 1; i < path.size(); i++) {
                LatLng start = path.get(i-1);
                LatLng end = path.get(i);
                var dist = calculateDistance(start, end);

                if(dist >= nextDistance){
                    log.debug("==>" + nextDistance);
                    while((dist - nextDistance) > 0){
                        markerPoints.add(moveTowards(start, end, nextDistance));
                        nextDistance = nextDistance + distanceInterval;
                    }

                    nextDistance = distanceInterval;
                }
                else{
                    log.debug("=>" + nextDistance);
                    nextDistance = nextDistance - dist;
                }
            }

            markerPoints.add(path.get(path.size()-1));
            return markerPoints;

        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            log.error("Error in fetching direction" + e.getMessage());
        }
        return null;
    }


    private LatLng moveTowards(LatLng start, LatLng end, double distance) {

        var lat1 = Math.toRadians(start.lat);
        var lon1 = Math.toRadians(start.lng);
        var lat2 = Math.toRadians(end.lat);
        var lon2 = Math.toRadians(end.lng);
        var dLon = Math.toRadians(end.lng - start.lng);

        // Find the bearing from this point to the next.
        var brng = Math.atan2(Math.sin(dLon) * Math.cos(lat2),
                Math.cos(lat1) * Math.sin(lat2) -
                        Math.sin(lat1) * Math.cos(lat2) *
                                Math.cos(dLon));

        var angDist = distance / 6371000;  // Earth's radius.

        // Calculate the destination point, given the source and bearing.
        lat2 = Math.asin(Math.sin(lat1) * Math.cos(angDist) +
                Math.cos(lat1) * Math.sin(angDist) *
                        Math.cos(brng));

        lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(angDist) *
                        Math.cos(lat1),
                Math.cos(angDist) - Math.sin(lat1) *
                        Math.sin(lat2));

        if (isNaN(lat2) || isNaN(lon2)) return null;

        return new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }


    /**
     * Helper function to calculate distance between two lat,long using haversine formula
     * @param old
     * @param newLatLng
     * @return
     */
    private double calculateDistance (LatLng old, LatLng newLatLng) {
        //var R = 6371; // km (change this constant to get miles)
        var R = 6378100; // meters
        var lat1 = old.lat;
        var lon1 = old.lng;
        var lat2 = newLatLng.lat;
        var lon2 = newLatLng.lng;

        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

}

