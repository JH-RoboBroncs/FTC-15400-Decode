package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

         myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(60, -12, Math.toRadians(180))) // BLUE AUTO
               .setTangent(180)
                .strafeTo(new Vector2d(0,-12))
                .strafeToLinearHeading(new Vector2d(-24, -24), Math.toRadians(225))
                .waitSeconds(5) //4.25

            /*    .strafeToLinearHeading(new Vector2d(36, -23), Math.toRadians(90))
                .strafeTo(new Vector2d(36,-50))
                .strafeTo(new Vector2d(36,-23))
                .strafeToLinearHeading(new Vector2d(-24, -24), Math.toRadians(225))
                .waitSeconds(4.25) */

                 .strafeToLinearHeading(new Vector2d(-9.5, -23), Math.toRadians(90))
                 .strafeTo(new Vector2d(-9.5,-50))
                 .strafeTo(new Vector2d(-9.5,-23))
                 .strafeToLinearHeading(new Vector2d(-24, -24), Math.toRadians(225))
                 .waitSeconds(5) //4.25

                .strafeToLinearHeading(new Vector2d(13,-23), Math.toRadians(90))
                .strafeTo(new Vector2d(13,-50))
                .strafeTo(new Vector2d(13,-23))
                .strafeToLinearHeading(new Vector2d(-24, -24), Math.toRadians(225))
                .build());

        /* myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(60, 12, Math.toRadians(180))) // RED AUTO
                .setTangent(180)
                .strafeTo(new Vector2d(0,12))
                .strafeToLinearHeading(new Vector2d(-24, 24), Math.toRadians(135))
                .waitSeconds(4.25)

               /* .strafeToLinearHeading(new Vector2d(36, 23), Math.toRadians(270))
                .strafeTo(new Vector2d(36,50))
                .strafeTo(new Vector2d(36,23))
                .strafeToLinearHeading(new Vector2d(-24, 24), Math.toRadians(135))
                .waitSeconds(4.25) */

              /*  .strafeToLinearHeading(new Vector2d(-9.5, 23), Math.toRadians(90))
                .strafeTo(new Vector2d(-9.5,50))
                .strafeTo(new Vector2d(-9.5,23))
                .strafeToLinearHeading(new Vector2d(-24, 24), Math.toRadians(225))
                .waitSeconds(5)

                .strafeToLinearHeading(new Vector2d(13,23), Math.toRadians(270))
                .strafeTo(new Vector2d(13,50))
                .strafeTo(new Vector2d(13,23))
                .strafeToLinearHeading(new Vector2d(-24, 24), Math.toRadians(135))
                .build()); */

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
