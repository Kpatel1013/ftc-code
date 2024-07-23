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
   static final double     COUNTS_PER_MOTOR_REV    = 312.0;  
   static final double     DRIVE_GEAR_REDUCTION    = 1.0;     // No External Gearing.
   static final double     WHEEL_DIAMETER_INCHES   = 1.4;     // For figuring circumference
   static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                      (WHEEL_DIAMETER_INCHES * 3.1415);
   static final double     DRIVE_SPEED             = 0.6;
   static final double     TURN_SPEED              = 0.5;
 
   @Override
   public void runOpMode() {
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

      encoderDrive(DRIVE_SPEED, 69, 69, 69, 69, 1.0); // drive forward
      encoderDrive(DRIVE_SPEED, -14, 14, 14, -14, 1.0); // strafe left
      // //open gripper/place 
      // gripperMotor.setPosition(-1.0);
      encoderDrive(DRIVE_SPEED, -60, -60, -60, -60, 1.0); // drive backwards
      encoderDrive(DRIVE_SPEED, 12, -12, -12, 12, 1.0); // strafe right
      encoderDrive(TURN_SPEED, 7, -7, 7, -7, 1.0); // turn right
      //close gripper/grab cone
      // gripperMotor.setPosition(1.0);
      encoderDrive(DRIVE_SPEED, -60, 60, 60, -60, 1.0); // strafe left
      encoderDrive(DRIVE_SPEED, -7, 7, -7, 7, 1.0); // turn left
      encoderDrive(DRIVE_SPEED, -22, 22, 22, -22, 1.0); // strafe left
      //open gripper/place cone
      // gripperMotor.setPosition(-1.0);
      encoderDrive(DRIVE_SPEED, -69, -69, -69, -69, 1.0); // drive backwards
      encoderDrive(DRIVE_SPEED, 20, -20, -20, 20, 1.0); // strafe right
      encoderDrive(TURN_SPEED, 7, -7, 7, -7, 1.0); // turn right
      //close gripper/grab cone
      // gripperMotor.setPosition(1.0);
      encoderDrive(DRIVE_SPEED, -60, 60, 60, -60, 1.0); // strafe left
      encoderDrive(DRIVE_SPEED, -7, 7, -7, 7, 1.0); // turn left
      encoderDrive(DRIVE_SPEED, -30, 30, 30, -30, 1.0); // strafe left
      //open gripper/place cone
      // gripperMotor.setPosition(-1.0);
      encoderDrive(DRIVE_SPEED, -69, -69, -69, -69, 1.0); // drive backwards
      encoderDrive(DRIVE_SPEED, 30, -30, -30, 30, 1.0); // strafe right


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
}
