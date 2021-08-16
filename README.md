# Location Simulator
You are given two locations - A & B. You are provided with latitude & longitude of them (short form LatLngs). You need to calculate ‘real’ points on the road that connects A & B. You need to use data from google directions API .
\
\
*Sample Input*: LatLngs for point A and point B.
\
A is 12.93175, 77.62872 and B is 12.92662, 77.63696
\
\
*Sample Output*: LatLngs at a constant distance interval of 50 m between A & B on the road. 12.93175, 77.62872
12.93166, 77.62852
12.93125, 77.62870
.
.
12.92713, 77.63719 12.92668, 77.63717 12.92662, 77.63696

## Requirements
- JDK 11
- Maven
- Browser/Postman to test

## Important directories/files:
- **/[screenshots](https://github.com/devmigi/demo-direction-service/blob/master/screenshots)**: contains app screenshots
- **/[frontend](https://github.com/devmigi/demo-direction-service/blob/master/frontend/index.html)**: contains index.html to run simple html frontend
- **/src**: spring-boot REST api codes
- **[DirectionService.java](https://github.com/devmigi/demo-direction-service/blob/master/src/main/java/com/migi/directionservice/services/DirectionService.java)**: business logic to calculate distance interval marker
- **[LocationController.java](https://github.com/devmigi/demo-direction-service/blob/master/src/main/java/com/migi/directionservice/controllers/LocationController.java)**: RestController which exposes public get endpoint _http://localhost:8001/api/v1/direction?origin=12.9257673,77.634549&destination=12.9159047,77.6252829_


## Author
Mrigendra Kumar
\
<mrgndrakr@live.com>


