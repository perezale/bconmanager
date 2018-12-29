package ar.edu.unicen.exa.bconmanager.Service

import android.hardware.SensorManager
import android.util.Log
import android.widget.BaseAdapter
import ar.edu.unicen.exa.bconmanager.Model.Location

class PDRService : Algorithm(){

    private var sensorManager: SensorManager? = null
    private var stepDetectionHandler: StepDetectionHandler? = null
    private var deviceAttitudeHandler: DeviceAttitudeHandler? = null
    private var isWalking = true
    private var bearingAdjustment = 0.0f
    private var isRecordingAngle = false
    override var  TAG = "PDR Service"
    private var recordCount = 0
    private lateinit var nextPosition : Location
    private var mCurrentLocation: Location? = null
    lateinit var pdrAdapter: BaseAdapter
    private var PDREnabled = false
    private var angle = 0.0
    private var acceleration = 0.0F



    private object Holder {
        val INSTANCE = PDRService()
    }

    companion object {
        val instance: PDRService by lazy { Holder.INSTANCE }
    }


    private val mStepDetectionListener = StepDetectionHandler.StepDetectionListener { stepSize ->
        Log.d("PDRActivity", "INSIDE LISTENR PDRSERVICE")
        if (!isRecordingAngle) {
            Log.d("PDRActivity", "NOT RECORDING")
            angle = (deviceAttitudeHandler!!.orientationVals[0] + bearingAdjustment)*57.2958
            //in this case stepSize is acceleration
            acceleration = if (stepSize >= 0) stepSize else 0F // Only use positive acceleration values
            if(PDREnabled) {
                nextPosition = computeNextStep(stepSize, (deviceAttitudeHandler!!.orientationVals[0] + bearingAdjustment))
                Log.d(TAG, "Location: " + nextPosition.toString() + "  angle: " + (deviceAttitudeHandler!!.orientationVals[0] + bearingAdjustment) * 57.2958)
                Log.d("PDRActivity", "IS WALKING")
                Log.d(TAG, "IS WALKING")
                pdrAdapter.notifyDataSetChanged()
            }
        } else if (isWalking && isRecordingAngle) {
            Log.d("PDRActivity","IS RECORDING")
            recordCount++
            if (recordCount == 3) {
                recordCount = 0
                setAdjustedBearing(deviceAttitudeHandler!!.orientationVals[0])
                isRecordingAngle = false
            }

        }
    }

    private fun setAdjustedBearing(measuredAngle : Float) {
        val adjustmentFactor = 0 // 90 degrees
        Log.d("ADJUSTMENT", "Measured angle is ${measuredAngle*57.2958}")
        Log.d("ADJUSTMENT", "It should be ${adjustmentFactor*57.2958}")
        bearingAdjustment = -measuredAngle
        Log.d("ADJUSTMENT", "Adjustment is ${bearingAdjustment*57.2958}")
        isRecordingAngle = false
        pdrAdapter.notifyDataSetChanged()
    }

    fun startRecordingAngle() {
        isRecordingAngle = true
    }

    fun startPDR(){
        PDREnabled = true
    }

    override fun getNextPosition(): Location {
        return nextPosition
    }

     fun startSensorsHandlers() {
        if((stepDetectionHandler != null) && (deviceAttitudeHandler != null)) {
            stepDetectionHandler!!.start()
            deviceAttitudeHandler!!.start()
        }

    }

     fun stopSensorsHandlers() {
        if((stepDetectionHandler != null) && (deviceAttitudeHandler != null)) {
            stepDetectionHandler!!.stop()
            deviceAttitudeHandler!!.stop()
        }
         Log.d("PDRActivity","STOP SENSORS HANDLERS")
    }

    fun setupSensorsHandlers(loc: Location, adapter: BaseAdapter, sm: SensorManager, rawData: Boolean){
        pdrAdapter = adapter
        this.sensorManager = sm
        stepDetectionHandler = StepDetectionHandler(sensorManager,rawData)
        stepDetectionHandler!!.setStepListener(mStepDetectionListener)
        deviceAttitudeHandler = DeviceAttitudeHandler(sensorManager)
        setmCurrentLocation(loc)
        stepDetectionHandler!!.start()
        deviceAttitudeHandler!!.start()
    }


    fun getmCurrentLocation(): Location {
        return mCurrentLocation!!
    }

    fun setmCurrentLocation(mCurrentLocation: Location) {
        Log.d(TAG, "CURRENT LOCATION IS : " + mCurrentLocation.toString())
        this.mCurrentLocation = mCurrentLocation
    }

    /** Calculates the new user position from the current one
     * @param stepSize the size of the step the user has made
     * @param bearing the angle of direction
     * @return new location
     */
    fun computeNextStep(stepSize : Float, bearing : Float) : Location {
        Log.d(TAG, "COMPUTE NEXT STEP")
        val newLoc = mCurrentLocation

        val oldX = mCurrentLocation!!.getXMeters()
        val oldY = mCurrentLocation!!.getYMeters()

        //reconversion en degres

        Log.d(TAG, "STEP: $stepSize")
        Log.d(TAG, "ANgle: $bearing")
        Log.d(TAG, "COS ANgle: " + Math.cos(bearing.toDouble()))

        val newX = oldX + Math.cos(bearing.toDouble()) * stepSize
        newLoc!!.x = newX
        val newY = oldY + Math.sin(bearing.toDouble()) * stepSize
        newLoc!!.y = newY

        mCurrentLocation = newLoc

        return newLoc!!
    }

    fun getAcc() : Float{
        Log.d("PDRActivity", "ACCELERATION: $acceleration")
        return this.acceleration
    }

    fun getAngle() : Double {
        Log.d("PDRActivity", "ANGLE: $angle")
        return this.angle
    }

}