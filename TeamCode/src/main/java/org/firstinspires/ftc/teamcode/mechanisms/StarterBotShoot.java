package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class StarterBotShoot {
    private DcMotorEx motor;
    private CRServo servoOne;
    private CRServo servoTwo;
    private double ticksPerRotation;

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
}
