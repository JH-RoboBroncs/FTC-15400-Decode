package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class StarterBotShoot {
    private DcMotor motor;
    private CRServo servoOne;
    private CRServo servoTwo;
    private double ticksPerRotation;

    public void init(HardwareMap hwMap) {
        motor = hwMap.get(DcMotor.class, "shooter");
        servoOne = hwMap.get(CRServo.class, "servoOne");
        servoTwo = hwMap.get(CRServo.class, "servoTwo");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRotation = motor.getMotorType().getTicksPerRev();

    }



    public void shoot(double speed) {
        motor.setPower(speed);
    }

    public void load(double speed) {
        servoOne.setPower(speed);
        servoTwo.setPower(speed);
    }


}
