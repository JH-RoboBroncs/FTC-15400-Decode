package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class StarterBotShoot {
    private DcMotorEx motor;
    private CRServo servoOne;
    private CRServo servoTwo;
    private double ticksPerRotation;
    private int phase = 1;


    public void init(HardwareMap hwMap) {
        motor = hwMap.get(DcMotorEx.class, "shooter");
        servoOne = hwMap.get(CRServo.class, "servoOne");
        servoTwo = hwMap.get(CRServo.class, "servoTwo");
        motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        ticksPerRotation = motor.getMotorType().getTicksPerRev();

    }



    public void shoot(double speed) {
        motor.setPower(speed);
    }

    public void load(double speed) {
        servoOne.setPower(-speed);
        servoTwo.setPower(speed);
    }


    public void brake(boolean brake) {
        if (brake) {
            motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        } else {
            motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        }
    }

    public void shoot2(ElapsedTime timer) {
        timer.reset();
        if (timer.seconds() > 2 && timer.seconds() < 10) {
            motor.setPower(0.45); //0.45
        } else if (timer.seconds() < 10) {
            motor.setPower(0.45);
        } else {
            motor.setPower(0);
        }

        if ( (timer.seconds() > 1.5 && timer.seconds() < 1.75) || (timer.seconds() > 2.75 && timer.seconds() < 3.25)) {
            phase = 2; // load
        } else if ((timer.seconds() > 0.25 && timer.seconds() < .5)) {
            phase = 3;
        } else {
            phase = 1; // idle
        }


        switch (phase){
            case 1: // waiting
                servoTwo.setPower(0);
                servoOne.setPower(0);
                break;
            case 2: //loading
                servoTwo.setPower(0.7); //.65
                servoOne.setPower(-0.7);
                break;
            case 3: //loading
                servoTwo.setPower(0.8);
                servoOne.setPower(-0.8);
                break;
            default:
                servoTwo.setPower(0);
                servoOne.setPower(0);
        }

    }

}
