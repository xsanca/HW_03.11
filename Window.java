package oct_13;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;

public class  Window extends JFrame  {
     static int centerX = 450;
     //static int width=4;
     static int centerY = 300;
    public int len = 5;
    boolean flag=true;
    static int scale=30;//деления на оси
    double x,y;
    double x0,x1,y1,y0;
    //double prev_x, prev_y;
    double precision = 1e-3;
    Polynom res;
    //int index =0;//для подсчета количества узлов(значений)
    //double pointsX[]=new double[30] ;//узловые точки
    //double pointsY[]=new double[30];//значения в узловых точках
    Animator a;
    public Window(){
        this.setBounds(500, 100, 910, 610);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //this.setResizable(false);
        this.setLayout((LayoutManager)null);//фиксируется
        this.setVisible(true);
        Graphics g= this.getGraphics();//объект для рисования на окне

        a=new Animator(g,scale,len);
        a.start();
        a.drawFrame();
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                centerX=(int)(e.getComponent().getSize().getWidth()/2);//изменение размера окна
                centerY=(int)(e.getComponent().getSize().getHeight()/2);
                a.changeBufferedImageSize();
                a.updateCenter(centerX,centerY);
                a.drawFrame();
            }
        });


        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                a.changeScaleAndCenter(-e.getWheelRotation(), e.getX(), e.getY());
                a.drawFrame();
            }
        });


        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
               // g.fillOval(e.getX() - 10/2, e.getY()-10/2, 10 ,10);
                x = Animator.tranX(e.getX());
                y = Animator.tranY(e.getY());
                a.drawAxis();
                for (int i=0;i<a.index;i++){//отлавливает одинаковые х и у
                   // if ((pointsX[i]!=tranX(x))&&((pointsY[i]!=tranY(y)))){flag=true;}
                    if ((Math.abs(a.untranX(a.pointsX[i])-x)>precision)){flag=true;}
                    else {flag=false;break;}
                }
                if(flag){
                    a.addPoint(x,y);}
                   // g.clearRect(0,0,910,610);//очищаем форму


                    res=lagrange(Arrays.copyOfRange(a.pointsX,0,a.index),Arrays.copyOfRange(a.pointsY,0,a.index));
                   System.out.println("x = " + x + " | y = " + y);//выводим координаты
                    a.setPolynom(res);
                    a.drawFrame();

            }

            @Override
            public void mousePressed(MouseEvent e) {
                x0=Animator.tranX(e.getX());
                y0=Animator.tranX(e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                x1=Animator.tranX(e.getX());
                y1=Animator.tranX(e.getY());
                double difx=x1-x0;
                double dify=y1-y0;
                a.updateCenter(Animator.untranX(difx),Animator.untranY(dify));
                a.drawFrame();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            //if(a != null) {
             //   a.drawAxis();
            //}
        }


    public static Polynom lagrange(double[]x, double[] f) {//принимает на входы два массива с узлами и значениями
        Polynom []l=new Polynom[x.length];
        //product;
        for (int i=0; i< l.length;i++){
            Polynom res=new Polynom(new double[]{1});
            for (int j=0;j< l.length;j++) {
                if (i != j) {
                    res=Polynom.multiplication(res, Polynom.multiplication(1 / (x[i] - x[j]),
                            new Polynom(new double[]{1, -1 * x[j]})));
                }
            }
            l[i]=res;
        }
        Polynom L=new Polynom(new double[]{0});
        for (int i=0;i< l.length;i++){

            L=Polynom.sum(L,Polynom.multiplication(f[i],l[i]));
        }
        System.out.println(L);
        return L;
    }
}