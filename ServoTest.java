import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "ServoTest", group = "The Cone Crusaders")
public class ServoTest extends LinearOpMode {

    private Servo armMotor = null;
    private Servo gripperMotor = null;
    
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

        armMotor = hardwareMap.get(Servo.class, "AM");
        gripperMotor = hardwareMap.get(Servo.class, "GM");
        
        armMotor.setDirection(Servo.Direction.FORWARD); 
        gripperMotor.setDirection(Servo.Direction.FORWARD); 
        
        telemetry.addData("Status", "Initialized");
        telemetry.update();
 
        waitForStart();
        runtime.reset();
 
        while (opModeIsActive()) {
            //arm and gripper controls
           double armPower = 0.0;
            if (gamepad1.right_bumper) {
                armPower = 0.4; // moves arm up
            } 
            else if (gamepad1.left_bumper) {
                armPower = -0.4; // moves arm down
            }
            double gripperPower = 0.0;
            if (gamepad1.right_trigger > 0.1) {
                gripperPower = -0.5; // opens the gripper
            } else if (gamepad1.left_trigger > 0.1) {
                gripperPower = 0.5; // closes the gripper
            }
            
            armMotor.setPosition((armPower + 1) /2); // Convert to a range of 0 to 1
            gripperMotor.setPosition((gripperPower + 1) / 2); // Convert to a range of 0 to 1
        }
    }
}
