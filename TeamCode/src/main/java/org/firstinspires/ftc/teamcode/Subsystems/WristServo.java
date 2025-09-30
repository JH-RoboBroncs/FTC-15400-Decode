package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class WristServo {
    private final Servo wristServo;

    public WristServo(HardwareMap hardwareMap) {
        wristServo = hardwareMap.get(Servo.class, "wristServo");

        //wristServo.setPosition(0.3);
    }

    public void up() {wristServo.setPosition(0);}
    public void mid() {wristServo.setPosition(.3);}
    public void down() {wristServo.setPosition(1);}
}
