package com.prorish;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import java.util.Random;

import javax.swing.*;
import java.util.*;

public class carCounter {
    int cars;
    int height;
    int width;
    List<Integer> carList;

    public carCounter() {
        cars = 0;
        carList = new ArrayList<>();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture capture = new VideoCapture("F:\\Rish_Files\\Learning\\contest\\source\\DRONE-SURVEILLANCE-CONTEST-VIDEO.mp4");

        JFrame dis = new JFrame();
        JLabel disLabel = new JLabel();
        dis.setBounds(0,0,756, 490);
        dis.add(disLabel);
        dis.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        dis.setVisible(true);

        while (true){
            Mat img = new Mat();
            capture.read(img);

            if (img.empty()){
                System.out.println("Failed to read images");
                break;
            }
            Imgproc.resize(img, img, new Size(756, 480));

            height = img.height();
            width = img.width();

            detectContours(img, img);
            updateIndices(img, cars);

            disLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(img)));
        }
    }

    public void updateIndices(Mat img, int cars){
        Imgproc.putText(img, "Risvanth K M", new Point(320, 40), Core.FONT_HERSHEY_SIMPLEX, 0.6, new Scalar(0,0,0), 2);
        Imgproc.putText(img, String.valueOf(cars), new Point(660, 40), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 0), 2);
    }

    public void detectContours(Mat img, Mat imgToDraw){
        Random ran = new Random();

        Mat gray = new Mat();
        Mat blurImg = new Mat();
        Mat thres = new Mat();

        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, blurImg, new Size(9,9),1);
        Imgproc.threshold(blurImg, thres, 140, 250, Imgproc.THRESH_BINARY_INV);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(thres, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint cnt : contours) {
            double area = Imgproc.contourArea(cnt);
            MatOfPoint2f curve = new MatOfPoint2f(cnt.toArray());

            if (area > 700) {
                double peri = Imgproc.arcLength(curve, true);
                MatOfPoint2f approx = new MatOfPoint2f();
                Imgproc.approxPolyDP(curve, approx, 0.02*peri, true);
                Rect bbox = Imgproc.boundingRect(approx);

                if (bbox.height < 200 & bbox.width < 100 & bbox.y > 20) {
                    Imgproc.putText(imgToDraw, String.valueOf(ran.nextInt(1000)), new Point(bbox.x, bbox.y+10), Core.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0,0,255), 1);
                    Imgproc.rectangle(imgToDraw, new Point(bbox.x, bbox.y), new Point(bbox.x + bbox.width, bbox.y + bbox.height), new Scalar(0, 0, 255), 1);
                    int carPos = Integer.parseInt(String.valueOf((bbox.y + bbox.height) / 2));
                    int complete = 100;

                    if (carPos < complete+1  & carPos > complete-1.01) {
                        cars += 1;
                        carList.add(cars);
                        System.out.println(cars);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new carCounter();
    }
}
