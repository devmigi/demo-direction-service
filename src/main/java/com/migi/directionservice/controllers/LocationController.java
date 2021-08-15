package com.migi.directionservice.controllers;

import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import com.migi.directionservice.services.DirectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/v1")
public class LocationController {
    @Autowired
    private DirectionService directionService;

    @GetMapping("/direction")
    public ResponseEntity<HashMap<String, Object>> getDirection(@RequestParam String origin, @RequestParam String destination){
        String[] originArr = origin.split(",");
        String[] destinationArr = destination.split(",");
        LatLng start = new LatLng(Double.parseDouble(originArr[0]),Double.parseDouble(originArr[1]));
        LatLng end = new LatLng(Double.parseDouble(destinationArr[0]),Double.parseDouble(destinationArr[1]));

        List<LatLng> steps = directionService.getDirection(start, end);

        HashMap<String, Object> map = new HashMap<>();
        map.put("origin", start);
        map.put("destination", end);
        map.put("steps", steps);

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

}
