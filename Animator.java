package oct_13;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Animator extends Thread {
    private Graphics g;
    private double step=0.1;
    private static int scale;
    private int len;
    Polynom p;
    private static int centerX = Window.centerX;
    private static int centerY = Window.centerY;
    public int index=0;
    BufferedImage frame;
    Graphics frameGraphics;
    public double pointsX[]=new double[30] ;//узловые точки
    public double pointsY[]=new double[30];//значения в узловых точках
    DrawFrameThread th;

    public Animator(Graphics g, int scale, int len) {
        this.g = g;
        this.scale = scale;
        this.len = len;
        frame =new BufferedImage(Window.centerX*2,Window.centerY*2,BufferedImage.TYPE_INT_RGB);
        frameGraphics= frame.getGraphics();
        th=new DrawFrameThread();
        drawAxis();
    }

    public void updateCenter(int x,int y){
        centerX=x;
        centerY=y;
    }
    public void setPolynom(Polynom k){this.p=k;}

    public void displayCurrentFrame(Polynom k,double pointX[],double pointY[],int index){//не юзаем
        clear();
        drawPolynom(k);
        drawAxis();
        int w=4;

        frameGraphics.setColor(Color.GREEN);
        for (int i=0;i<index;i++){
            frameGraphics.fillOval(Animator.untranX(pointX[i])-w/2,Animator.untranY(pointY[i])-w/2,w,w);
        }
        frameGraphics.setColor(Color.BLACK);
    }
    public void changeScaleAndCenter(int sign, int x, int y){
        if(sign > 0){
            scale *= 1.0+step;
            double nx = 0 - (step)*tranX(x);
            double ny = 0 - (step)*tranY(y);
            updateCenter(untranX(nx), untranY(ny));
        }else{
            double nx = 0 - (1/(1.0+step) - 1)*tranX(x);
            double ny = 0 - (1/(1.0+step) - 1)*tranY(y);
            updateCenter(untranX(nx), untranY(ny));
            scale /= 1.0+step;
        }
    }

    public void drawAxis() {
        frameGraphics.setColor(Color.black);

        //рисуем 0 в центре
        frameGraphics.drawString("0", centerX-len*3, centerY-len);

        //ось Y
        frameGraphics.drawLine(centerX, 0, centerX, Window.centerY*2);
        frameGraphics.drawString("Y", centerX+10, 43);
        frameGraphics.drawLine(centerX, 30, centerX-10, 40);//стрелочка
        frameGraphics.drawLine(centerX, 30, centerX+10, 40);

        for (int i = 1; i < (int) (Window.centerY*2 /( 2 * scale)); i++) {
            int disp = len;
            if(i > 9) disp = 2*len - 3;
            frameGraphics.drawString("" + i, centerX - 3 *  disp, centerY - scale * i);
            frameGraphics.drawString("-" + i, centerX - 3 *  disp - 2 , centerY + scale * i); // -2 потому что еще минус!
            frameGraphics.drawLine(centerX - len, centerY + scale * i, centerX + len, centerY+scale * i); // отметка
            frameGraphics.drawLine(centerX - len, centerY -scale * i, centerX + len, centerY - scale * i); // отметка
        }

        //ось X
        frameGraphics.drawLine(0, centerY, Window.centerX*2, centerY);
        frameGraphics.drawString("X", Window.centerX*2-10, centerY-11);
        frameGraphics.drawLine(Window.centerX*2 - 10, centerY-10, Window.centerX*2-5, centerY);//стрееелочка
        frameGraphics.drawLine(Window.centerX*2-10, centerY+10, Window.centerX*2-5, centerY);

        for (int i = 1; i < (Window.centerX*2 /(2 * scale)); i++) {
            frameGraphics.drawString("" + i, centerX + scale * i - 2 * len, centerY - len - 2);
            frameGraphics.drawString("-" + i,centerX - scale * i - 2 * len, centerY - len - 2 );
            frameGraphics.drawLine(centerX - scale * i, centerY - len, centerX -scale * i, centerY + len); // отметка
            frameGraphics.drawLine(centerX + scale*i, centerY-len, centerX + scale*i, centerY+len); // отметка
        }
    }

    public void clear(){//очистка
        frameGraphics.setColor(Color.white);
        frameGraphics.fillRect(0,0,Window.centerX*2,Window.centerY*2);
        frameGraphics.setColor(Color.black);
    }

    static public double tranX (int x){
        return (x - centerX)* 1.0/scale;
    }

    static public double tranY (int y){
        return -(y-centerY)*1.0/scale;
    }

    static public int untranX (double x){
        return (int)((x *scale) + centerX);
    }

    static public int untranY (double y){
        return (int)(-y*scale+centerY);
    }


    public void drawPolynom(Polynom poly) {
        double precision = 1e-1;
        int interval = Window.centerX/scale;
        frameGraphics.setColor(Color.CYAN);
                 double prev_x = -interval;//отрисовка самого полинома
                    double prev_y = poly.getValue(-interval);
                    for(double i = -interval; i < interval; i+=precision) {
                      double curr_x = i;//текущая
                     double curr_y = poly.getValue(i);
                        frameGraphics.drawLine(untranX(prev_x), untranY(prev_y),
                                untranX(curr_x), untranY(curr_y));
                       prev_x = curr_x;//ранее(запоминаем предыдущие координаты)
                       prev_y = curr_y;
                       try{Thread.sleep(2);}
                       catch (InterruptedException e){e.printStackTrace();}
        }
    }
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            g.drawImage(frame,0,0, Window.centerX*2,Window.centerY*2,null);

        }

    }
    class DrawFrameThread implements Runnable {//замедленная отрисовка полинома
       // Объект типа Runnable  передается в конструктор объекта типа Thread.
        @Override
        public void run() {
            clear(); //очистить
            drawAxis(); //нарисовать оси
            drawPoints();
            if(p!=null){
                drawPolynom(p);
            }
        }
    }

    public void drawFrame(){
        new Thread(th).start();
    }

    public void drawPoints(){
        frameGraphics.setColor(Color.red);
        int wight = 4;
        for(int i=0; i < index; i++){
            frameGraphics.fillOval(untranX(pointsX[i]) - wight/2, untranY(pointsY[i])-wight/2, wight ,wight);
        }
        frameGraphics.setColor(Color.black);
    }

    public void addPoint(double x, double y){//обавляем точки
        pointsX[index] = x;
        pointsY[index] = y;
        index++;
    }

    public void changeBufferedImageSize(){
        frame = new BufferedImage(Window.centerX*2,Window.centerY*2,BufferedImage.TYPE_INT_RGB );
        frameGraphics = frame.getGraphics();
    }
}
