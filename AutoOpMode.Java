import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
 
@Autonomous(name = "Auto", group = "The Cone Crusaders")
public class AutoOpMode extends LinearOpMode {
   private DcMotor frontLeft = null;
   private DcMotor backLeft = null;
   private DcMotor frontRight = null;
   private DcMotor backRight = null;
 
   private Servo armMotor = null;
   private Servo gripperMotor = null;
 
   private ElapsedTime runtime = new ElapsedTime();
 
   // Calculate the COUNTS_PER_INCH for your specific drive train.
   // Go to your motor vendor website to determine your motor's COUNTS_PER_MOTOR_REV
   // For external drive gearing, set DRIVE_GEAR_REDUCTION as needed.
   // For example, use a value of 2.0 for a 12-tooth spur gear driving a 24-tooth spur gear.
   // This is gearing DOWN for less speed and more torque.
   // For gearing UP, use a gear ratio less than 1.0. Note this will affect the direction of wheel rotation.
   static final double     COUNTS_PER_MOTOR_REV    = 1497.0;  
   static final double     DRIVE_GEAR_REDUCTION    = 1.0;     // No External Gearing.
   static final double     WHEEL_DIAMETER_INCHES   = 3.8;     // For figuring circumference
   static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                      (WHEEL_DIAMETER_INCHES * 3.1415);
   static final double     DRIVE_SPEED             = 0.6;
   static final double     TURN_SPEED              = 0.5;

   private static final boolean USE_WEBCAM = true;
   private AprilTagProcessor aprilTag;
   private VisionPortal visionPortal;
 
   @Override
   public void runOpMode() {
      initAprilTag();
      frontLeft  = hardwareMap.get(DcMotor.class, "FLD");
      backLeft = hardwareMap.get(DcMotor.class, "BLD");
      frontRight = hardwareMap.get(DcMotor.class, "FRD");
      backRight = hardwareMap.get(DcMotor.class, "BRD");

      armMotor = hardwareMap.get(Servo.class, "AM");
      gripperMotor = hardwareMap.get(Servo.class, "GM");
        
      // Set the drive motor directions
      frontLeft.setDirection(DcMotor.Direction.REVERSE);
      backLeft.setDirection(DcMotor.Direction.REVERSE);
      frontRight.setDirection(DcMotor.Direction.FORWARD);
      backRight.setDirection(DcMotor.Direction.FORWARD);

      armMotor.setDirection(Servo.Direction.REVERSE); 
      gripperMotor.setDirection(Servo.Direction.FORWARD); 
        
      frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

      frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
      backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
      frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
      backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
 
      // Send telemetry message to indicate successful Encoder reset
      telemetry.addData("Starting at",  "%7d :%7d",
                        frontLeft.getCurrentPosition(),
                        backLeft.getCurrentPosition(),
                        frontRight.getCurrentPosition(),
                        backRight.getCurrentPosition());
      telemetry.update();
 
      // Wait for the game to start (driver presses PLAY)
      waitForStart();

      encoderDrive(DRIVE_SPEED, -10, 10, 10, -10, 1.0); //strafe left 
      encoderDrive(DRIVE_SPEED, 74, 74, 74, 74, 2.5); // drive forward
      // //open gripper/place 
      gripperMotor.setPosition(-1.0);
      // encoderDrive(DRIVE_SPEED, -60, -60, -60, -60, 1.0); // drive backwards
      // encoderDrive(DRIVE_SPEED, 10, -10, -10, 10, 1.0); // strafe right
      // encoderDrive(TURN_SPEED, 7, -7, 7, -7, 1.0); // turn right
      // //close gripper/grab cone
      gripperMotor.setPosition(1.0);
      encoderDrive(DRIVE_SPEED, -13, -13, -13, -13, 1.0)
      encoderDrive(DRIVE_SPEED, -7, 7, -7, 7, 1.0); // turn left
      encoderDrive(DRIVE_SPEED, 70, 70, 70, 70, 2.3);
      //open gripper/place cone
      gripperMotor.setPosition(-1.0);
      encoderDrive(DRIVE_SPEED, -60, -60, -60, -60, 1.0); // drive backwards
      encoderDrive(DRIVE_SPEED, 12, -12, -12, 12, 1.0); // strafe right
      encoderDrive(TURN_SPEED, 7, -7, 7, -7, 1.0); // turn right
      // //close gripper/grab cone
      gripperMotor.setPosition(1.0);
      encoderDrive(DRIVE_SPEED, -60, 60, 60, -60, 1.0); // strafe left
      encoderDrive(DRIVE_SPEED, -7, 7, -7, 7, 1.0); // turn left
      encoderDrive(DRIVE_SPEED, -10, 10, 10, -10, 1.0); // strafe left
      //open gripper/place cone
      gripperMotor.setPosition(-1.0);
      // encoderDrive(DRIVE_SPEED, -74, -74, -74, -74, 2.5); // drive backwards
      // encoderDrive(DRIVE_SPEED, 15, -15, -15, 15, 1.0); // strafe right


      telemetry.addData("Path", "Complete");
      telemetry.update();
      sleep(1000);  // pause to display final telemetry message.
   }
 
   public void encoderDrive(double speed,
                           double frontLeftInches, double backLeftInches, double frontRightInches, double backRightInches,
                           double timeoutS) {
      int newFrontLeftTarget;
      int newBackLeftTarget;
      int newFrontRightTarget;
      int newBackRightTarget;
 
      // Ensure that the opmode is still active
      if (opModeIsActive()) {
         telemetryAprilTag();

         // Push telemetry to the Driver Station.
          telemetry.update();
         // Determine new target position, and pass to motor controller
         newFrontLeftTarget = frontLeft.getCurrentPosition() + (int)(frontLeftInches * COUNTS_PER_INCH);
         newBackLeftTarget = backLeft.getCurrentPosition() + (int)(backLeftInches * COUNTS_PER_INCH);
         newFrontRightTarget = frontRight.getCurrentPosition() + (int)(frontRightInches * COUNTS_PER_INCH);
         newBackRightTarget = backRight.getCurrentPosition() + (int)(backRightInches * COUNTS_PER_INCH);

         // Set Target
         frontLeft.setTargetPosition(newFrontLeftTarget);
         backLeft.setTargetPosition(newBackLeftTarget);
         frontRight.setTargetPosition(newFrontRightTarget);
         backRight.setTargetPosition(newBackRightTarget);
            
 
         // Turn On RUN_TO_POSITION
         frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
         backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
         frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
         backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
         // reset the timeout time and start motion.
         runtime.reset();
         frontLeft.setPower(Math.abs(speed));
         backLeft.setPower(Math.abs(speed));
         frontRight.setPower(Math.abs(speed));
         backRight.setPower(Math.abs(speed));  

         // keep looping while we are still active, there is time left, and both motors are running.
         while (opModeIsActive() &&
               (runtime.seconds() < timeoutS) &&
               (frontLeft.isBusy() && backLeft.isBusy() && frontRight.isBusy() && backRight.isBusy())) {
 
               // Display it for the driver.
               telemetry.addData("Running to",  " %7d :%7d :%7d :%7d", newFrontLeftTarget, newBackLeftTarget, 
               newFrontRightTarget, newBackRightTarget);
               telemetry.addData("Currently at",  " at %7d :%7d :%7d :%7d",
                                 frontLeft.getCurrentPosition(), backLeft.getCurrentPosition(),
                                 frontRight.getCurrentPosition(), backRight.getCurrentPosition());
               telemetry.update();
         }
 
         // Stop all motion;
         frontLeft.setPower(0);
         backLeft.setPower(0);
         frontRight.setPower(0);
         backRight.setPower(0);
 
         // Turn off RUN_TO_POSITION
         frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
         backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
         frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
         backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
 
        sleep(250);   // optional pause after each move.
     }
  }
   private void initAprilTag() {

        // Create the AprilTag processor.
        aprilTag = new AprilTagProcessor.Builder()

            // The following default settings are available to un-comment and edit as needed.
            .setDrawAxes(false)
            .setDrawCubeProjection(false)
            .setDrawTagOutline(true)
            .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
            .setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
            .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
            
            // == CAMERA CALIBRATION ==
            // If you do not manually specify calibration parameters, the SDK will attempt
            // to load a predefined calibration for your camera.
            //setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
            // ... these parameters are fx, fy, cx, cy.

            .build();
            
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second (default)
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second (default)
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(3);

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(aprilTag);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Disable or re-enable the aprilTag processor at any time.
        visionPortal.setProcessorEnabled(aprilTag, true);

    }


    /**
     * Add telemetry about AprilTag detections.
     */
    private void telemetryAprilTag() {

        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }

        // Add "key" information to telemetry
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");

    }
  }

