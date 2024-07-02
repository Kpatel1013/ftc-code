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
 
     @Override
     public void runOpMode() {
 
         frontLeft  = hardwareMap.get(DcMotor.class, "front_left_Drive");
         backLeft = hardwareMap.get(DcMotor.class, "back_left_drive");
         frontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
         backRight = hardwareMap.get(DcMotor.class, "back_right_drive");
        
         // Set the drive motor directions
         frontLeft.setDirection(DcMotor.Direction.REVERSE);
         backLeft.setDirection(DcMotor.Direction.REVERSE);
         frontRight.setDirection(DcMotor.Direction.FORWARD);
         backRight.setDirection(DcMotor.Direction.FORWARD);
 
         telemetry.addData("Status", "Initialized");
         telemetry.update();
 
         waitForStart();
         runtime.reset();
 
         while (opModeIsActive()) {
             double max;

             double drive = -gamepad1.left_stick_y;
             double strafe =  gamepad1.left_stick_x;
             double turn =  gamepad1.right_stick_x;
 
             double frontLeftPower  = drive + strafe + turn;
             double frontRightPower = drive - strafe - turn;
             double backLeftPower   = drive - strafe + turn;
             double backRightPower  = drive + strafe - turn;
 
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
         }
     }}