package ar.edu.unicen.exa.bconmanager.Service.Algorithm

import android.support.v7.app.AppCompatActivity
import android.util.Log
import ar.edu.unicen.exa.bconmanager.Model.CustomMap
import ar.edu.unicen.exa.bconmanager.Model.BeaconDevice
import ar.edu.unicen.exa.bconmanager.Model.BeaconOnMap
import ar.edu.unicen.exa.bconmanager.Model.Json.JsonData
import ar.edu.unicen.exa.bconmanager.Model.Location

abstract class Algorithm : AppCompatActivity() {

    protected open var TAG : String = ""
    protected lateinit var customMap : CustomMap

    open fun startUp(map: CustomMap) {
        customMap = map
    }

    abstract fun getNextPosition(data: JsonData, nextTimestamp: Number): Location


    /**
     * Converts jsonData beacons to BeaconOnMap list
     */
    protected fun getBeacons(data: JsonData) : List<BeaconOnMap> {
        var savedBeacons: MutableList<BeaconOnMap> = mutableListOf<BeaconOnMap>()
        Log.d("GETBEACONS", "${data.beacons!!}")
        Log.d("GETBEACONS", "${customMap.savedBeacons}")


        for (beacon in data.beacons!!) {
            var beaconDev = BeaconDevice(beacon.mac!!, beacon.rssi!!, null)
            var beaconLoc = Location(0.0, 0.0, customMap)

            for (it in customMap.savedBeacons) {
                if (it.beacon == beaconDev) {
                    beaconLoc = it.position
                }
            }
            var beaconMap = BeaconOnMap(beaconLoc, beaconDev)
            savedBeacons.add(beaconMap)
        }
        return savedBeacons
    }

    fun euclideanDistance(location1: Location, location2: Location): Double {
        var distance = 0.00
        distance = Math.sqrt(Math.pow(location1.x - location2.x, 2.00) + Math.pow(location1.y - location2.y, 2.00))
        return distance
    }

    fun getError(loc: Location, simulatedLoc: Location) : Double{
        val error = euclideanDistance(loc,simulatedLoc)
        Log.d("SIMULATION","ERROR IS: $error")
        return error

    }
}