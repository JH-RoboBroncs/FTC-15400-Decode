package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class PincherServo {
    private final Servo pincherServo;

    public PincherServo(HardwareMap hardwareMap) {
        pincherServo = hardwareMap.get(Servo.class, "pincherServo");
        //pincherServo.setPosition(1);
    }

    public void meh() {
        pincherServo.setPosition(0);
    }

    public void mur() {
        pincherServo.setPosition(1);
    }

    public void mir(){
        pincherServo.setPosition(.55);
    }

    public double getPosition() {
        pincherServo.getPosition();
        return pincherServo.getPosition();
    }
}
