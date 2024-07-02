import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
 

public class FistLinearOpMode extends LinearOpMode {

    //creates 4 motors
     private DcMotor frontLeft = null;
     private DcMotor backLeft = null;
     private DcMotor frontRight = null;
     private DcMotor backRight = null;

     private DcMotor armMotor = null;
     private DcMotor gripperMotor = null;

 
     @Override
     public void runOpMode() {
 
         frontLeft  = hardwareMap.get(DcMotor.class, "front_left_Drive");
         backLeft = hardwareMap.get(DcMotor.class, "back_left_drive");
         frontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
         backRight = hardwareMap.get(DcMotor.class, "back_right_drive");

         armMotor = hardwareMap.get(DcMotor.class, "arm_motor");
         gripperMotor = hardwareMap.get(DcMotor.class, "gripper_motor");
        
         // Set the drive motor directions
         frontLeft.setDirection(DcMotor.Direction.REVERSE);
         backLeft.setDirection(DcMotor.Direction.REVERSE);
         frontRight.setDirection(DcMotor.Direction.FORWARD);
         backRight.setDirection(DcMotor.Direction.FORWARD);

        //  armMotor.setDirection(DcMotor.Direction.REVERSE); 
 
         telemetry.addData("Status", "Initialized");
         telemetry.update();
 
         waitForStart();
         runtime.reset();
 
         while (opModeIsActive()) {
             double max;

             //left stick y is forward and backward
             double drive = -gamepad1.left_stick_y;
             //left stick x is strafe
             double strafe =  gamepad1.left_stick_x;
             //right stick x is turn
             double turn =  gamepad1.right_stick_x;

             //right trigger to move arm up (normalized so it is between -1 and 1)
             double armPower = -gamepad1.left_trigger + gamepad1.right_trigger;
 
             double frontLeftPower  = drive + strafe + turn;
             double frontRightPower = drive - strafe - turn;
             double backLeftPower   = drive - strafe + turn;
             double backRightPower  = drive + strafe - turn;

             double gripperPower = 0;
             if (gamepad1.right_bumper) {
                 gripperPower = -1.0; // opens the gripper
             } else if (gamepad1.left_bumper) {
                 gripperPower = 1.0; // closes the gripper
             }
 
            // Normalize the values so neither exceed +/- 1.0
             max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
             max = Math.max(max, Math.abs(leftBackPower));
             max = Math.max(max, Math.abs(rightBackPower));
 
             if (max > 1.0) {
                 leftFrontPower  /= max;
                 rightFrontPower /= max;
                 leftBackPower   /= max;
                 rightBackPower  /= max;
             }
 
             frontLeft.setPower(leftFrontPower);
             frontRight.setPower(rightFrontPower);
             backLeft.setPower(leftBackPower);
             backRight.setPower(rightBackPower);

             armMotor.setPower(armPower);
             gripperMotor.setPower(gripperPower);
         }
     }}