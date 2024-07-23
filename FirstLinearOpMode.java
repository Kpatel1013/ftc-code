import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Teleop", group = "The Cone Crusaders")
public class FirstLinearOpMode extends LinearOpMode {

    //creates 4 motors
    private DcMotor frontLeft = null;
    private DcMotor backLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backRight = null;

    private Servo armMotor = null;
    private Servo gripperMotor = null;
    
    private ElapsedTime runtime = new ElapsedTime();

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
        
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        armMotor.setDirection(Servo.Direction.REVERSE); 
        gripperMotor.setDirection(Servo.Direction.FORWARD); 

        telemetry.addData("Status", "Initialized");
        telemetry.update();
 
        waitForStart();
        runtime.reset();
 
        while (opModeIsActive()) {
            double max;

            //left stick y-axis is forward and backward
            double drive = -gamepad1.left_stick_y;
            //left stick x-axis is strafe
            double strafe =  gamepad1.left_stick_x;
            //right stick x is turn
            double turn =  gamepad1.right_stick_x;

            double frontLeftPower  = drive + strafe + turn;
            double frontRightPower = drive - strafe - turn;
            double backLeftPower   = drive - strafe + turn;
            double backRightPower  = drive + strafe - turn;

            //arm and gripper controls
           double armPower = 0;
            if (gamepad1.right_bumper) {
                armPower = 0.5; // moves arm up
            } 
            else if (gamepad1.left_bumper) {
                armPower = -0.5; // moves arm down
            }
            double gripperPower = 0;
            if (gamepad1.right_trigger > 0.1) {
                gripperPower = -0.5; // opens the gripper
            } else if (gamepad1.left_trigger > 0.1) {
                gripperPower = 0.5; // closes the gripper
            }
 
            // Normalize the values so neither exceed +/- 1.0
            max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));
 
            if (max > 1.0) {
                frontLeftPower  /= max;
                frontRightPower /= max;
                backLeftPower   /= max;
                backRightPower  /= max;
            }
 
            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);

            armMotor.setPosition((armPower + 1) / 2); // Convert to a range of 0 to 1
            gripperMotor.setPosition((gripperPower + 1) / 2); // Convert to a range of 0 to 1
        }
    }
}
