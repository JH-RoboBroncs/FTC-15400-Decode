package org.firstinspires.ftc.teamcode.processors;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class TestLine implements VisionProcessor{


    @Override
    public void init(int width, int height, CameraCalibration calibration) {

    }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {


        return null;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {

        Paint lineColor = new Paint();
        lineColor.setColor(Color.GREEN);


    }


    public void drawLine(double x, double y, Mat imageMat) {
        Point startPoint = new Point(0,0);
        Point endPoint = new Point(x, y);



        Scalar lineColor = new Scalar(255, 0, 0);

        int lineThickness = 5;

        Imgproc.line(imageMat, startPoint, endPoint, lineColor, lineThickness);

    }


}
