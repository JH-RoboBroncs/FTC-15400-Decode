package org.firstinspires.ftc.teamcode.mechanisms;

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
    private int mphase = 1;
    private int shootPhase = 0;
    private double ticksperrev;
    private double targetRPM;

    public void init(HardwareMap hwMap) {
        motor = hwMap.get(DcMotorEx.class, "shooter");
        servoOne = hwMap.get(CRServo.class, "servoOne");
        servoTwo = hwMap.get(CRServo.class, "servoTwo");
        motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        ticksPerRotation = motor.getMotorType().getTicksPerRev();
        ticksperrev = motor.getVelocity()/28 * 60;
    }



    public void shoot(double speed) {
        motor.setVelocity(ticksPerRotation/(1.766/speed));
    }

    public void load(double speed) {
        servoOne.setPower(-speed);
        servoTwo.setPower(speed);
    }


    public void shoot2(ElapsedTime timer) {
        //timer.reset();
        if (timer.seconds() > 3 && timer.seconds() < 5) { //3rd ball
            mphase = 2;
        } else if (timer.seconds() > 1.5 && timer.seconds() < 3) { //2nd ball
            mphase = 3;
        } else if (timer.seconds() < 10) { // 1st ball
            mphase = 4;
        } else {
            mphase = 1;
        }

        if ((timer.seconds() > 1.35 && timer.seconds() < 1.5) || (timer.seconds() > 2.85 && timer.seconds() < 3) || (timer.seconds() > 4.5 && timer.seconds() < 5)) {
            phase = 2; // load
        } else if ((timer.seconds() < .75) || (timer.seconds() > 1.5 && timer.seconds() < 2.25) || (timer.seconds() > 3.15 && timer.seconds() < 3.9)) {
            phase = 4;
        } else {
            phase = 1; // idle
        }


        switch (phase){
            case 1: // waiting
                servoTwo.setPower(0);
                servoOne.setPower(0);
                break;
            case 2: //loading
                servoTwo.setPower(.25); //.65
                servoOne.setPower(-0.25);
                break;
            case 3: //loading
                servoTwo.setPower(.5);
                servoOne.setPower(-.5);
                break;
            case 4:
                servoOne.setPower(1);
                servoTwo.setPower(-1);
                break;
            default:
                servoTwo.setPower(0);
                servoOne.setPower(0);
        }

        switch (mphase){
            case 1: // waiting
                //motor.setPower(0);
                motor.setVelocity(0);
                break;
            case 2: //loading
                //motor.setPower(.45); //1st ball
                motor.setVelocity(ticksPerRotation/(1.1766/.55)); //.475
                break;
            case 3: //loading
                //motor.setPower(.45); // 2nd ball
                motor.setVelocity(ticksPerRotation/(1.1766/.55)); //.475
                break;
            case 4: //loading
                //motor.setPower(.5); //3rd ball
                motor.setVelocity(ticksPerRotation/(1.1766/.5)); //.425
                break;
            default:
                //motor.setPower(0);
        }

    }

    public void singleShoot(ElapsedTime timer, int hoodPhase) {

     //   motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        //mphase = hoodPhase;

         if (timer.seconds() < 2) { // 1st ball
            mphase = hoodPhase + 1;
        } else {
            // motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            mphase = 0;
        }


         if (timer.seconds() > 1.25 && timer.seconds() < 1.5) {
            phase = 2;
        } else {
            phase = 1; // idle
        }





        switch (phase){
            case 1: // waiting
                servoTwo.setPower(0);
                servoOne.setPower(0);
                break;
            case 2:
                servoOne.setPower(-.25);
                servoTwo.setPower(.25);
                break;
            default:
                servoTwo.setPower(0);
                servoOne.setPower(0);
        }

         //raise motor speeds for steeper angles

        switch (mphase){
            case 0:
                motor.setVelocity(0);
                break;
            case 1: // upclose
                motor.setVelocity(ticksPerRotation/(1.1766/.5)); //.525
                break;
            case 2: // good for halfway
                motor.setVelocity(ticksPerRotation/(1.1766/.525)); //.475
                break;
            case 3: // end of triangle
                motor.setVelocity(ticksPerRotation/(1.1766/.6)); //.65
                break;
            case 4: //far triangle
                motor.setVelocity(3000);
               // motor.setVelocity(ticksPerRotation/(1.1766/.925)); //.925
                break;

        }

    }


    public boolean velocityShoot(int hoodPhase) throws InterruptedException {


        double targetRPM = 0;

        mphase = hoodPhase + 1;


synchronized (this) {

    if (ticksperrev < targetRPM + 150 && ticksperrev > targetRPM - 150) {
        phase = 2;
        this.wait(250);
        return false;
    } else {
        phase = 1; // idle
    }
}




        switch (phase){
            case 1: // waiting
                servoTwo.setPower(0);
                servoOne.setPower(0);
                break;
            case 2:
                servoOne.setPower(-.25);
                servoTwo.setPower(.25);
                break;
            default:
                servoTwo.setPower(0);
                servoOne.setPower(0);
        }

        //raise motor speeds for steeper angles

        switch (mphase){
            case 0:
                motor.setVelocity(0);
                break;
            case 1: // upclose
                targetRPM = 2750;
                motor.setVelocity((2750/60)*28); //.525
                break;
            case 2: // good for halfway
                targetRPM = 2600;
                motor.setVelocity((2600/60)*60); //.475
                break;
            case 3: // end of triangle
                targetRPM = 3250;
                motor.setVelocity((3250/60)*28); //.65
                break;
            case 4: //far triangle
                targetRPM = 4500;
                motor.setVelocity((4500/60)*28);
                // motor.setVelocity(ticksPerRotation/(1.1766/.925)); //.925
                break;

        }

        return true;
    }


    public void antiload(double speed) {
        servoTwo.setPower(-speed);
        servoOne.setPower(speed);
    }

}
