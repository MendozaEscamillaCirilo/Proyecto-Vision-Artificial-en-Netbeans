/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dos;

/**
 *
 * @author cirilo
 */
import Estructura.Lista;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import uno.Ventana;

public class Imagen extends JFrame {

    private BufferedImage img = null;
    private File f = null;
    private int mpixel[][] = null;
    private int mrojo[][] = null;
    private int mverde[][] = null;
    private int mazul[][] = null;
    private int mgris[][] = null;
    private int mpromedio[][] = null;
    private int mbyn[][] = null;
    private int histograma[] = new int[256];
    private int mregiones[][] = null;
    private JFileChooser fc;
    private JPanel pnhistograma;
    int ancho = 0;
    int alto = 0;

    public Imagen() {
        super();
        setSize(800, 600);
        setName("dsvd");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel pnl = new JPanel();

        fc = new JFileChooser();
        JLabel label = new JLabel();
        JLabel label2 = new JLabel();
        label.setVisible(false);
        fc.addActionListener((ActionEvent ev) -> {
            try {
                f = fc.getSelectedFile();
                img = ImageIO.read(f);
                ancho = img.getWidth();
                alto = img.getHeight();
                mpixel = new int[alto][ancho];
                mrojo = new int[alto][ancho];
                mverde = new int[alto][ancho];
                mazul = new int[alto][ancho];
                mgris = new int[alto][ancho];
                mbyn = new int[alto][ancho];
                mpromedio = new int[alto][ancho];
                mregiones = new int[alto][ancho];

                System.out.println("ancho - " + ancho + "\nalto - " + alto + "\n");
                System.out.println("tam - " + ancho * alto);
                llena();
                llenagrises(true);
                /*System.out.println("promedio = "+mgris[18][18]);
                System.out.println("color = "+mpixel[18][18]);
                System.out.println("rojo  = "+mrojo[18][18]);
                System.out.println("verde = "+mverde[18][18]);
                System.out.println("azul = "+mazul[18][18]);*/
                //promedio(fc.getSelectedFile() + "gris.jpg");
                promedio(fc.getSelectedFile() + "gris.png");
                histograma(mpromedio);
                Ventana v = new Ventana(gethistograma());
                v.setVisible(true);
                System.out.println("otsu  =  " + otsu(histograma));
                blancoynegro(otsu(histograma), fc.getSelectedFile() + "promedio.png");
                /*System.out.println("cadenaCons = " + cadenaConstructor);*/
                //regiones();
                mregiones = mbyn;
                etiquetado(mbyn);
                //imprimeValores();
                regiones2();
                label2.setIcon(new javax.swing.ImageIcon(fc.getSelectedFile() + "gris.png"));
                label2.setVisible(true);
                label.setIcon(new javax.swing.ImageIcon(fc.getSelectedFile() + "blancoynegro.png"));
                label.setVisible(true);

                //fc.setVisible(false);
                //int valor = otsu(histograma);//(ancho * alto)/255;//= Integer.parseInt(JOptionPane.showInputDialog("Ingresa numero..."));
                //label.setIcon(new javax.swing.ImageIcon(fc.getSelectedFile().getAbsolutePath() + "gris.png"));
                //pnhistograma.setVisible(true);
                //imprime();
                //System.out.println("El menor es = "+gethumbral());
                //gethistograma();
            } catch (IOException e) {
                System.out.println(e);
            }
        });
        pnhistograma = new JPanel();
        pnhistograma.setVisible(false);
        pnl.add(fc);
        pnl.add(pnhistograma);
        pnl.add(label2);
        pnl.add(label);
        add(pnl);
        setVisible(true);
    }

    /**
     * Llena mpixel,mrojo,mverde,mazul con los valores de la imagen tomada
     */
    private void llena() {
        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j++) {
                mpixel[j][i] = img.getRGB(i, j);
                mrojo[j][i] = (img.getRGB(i, j) >> 16) & 0xff;
                mverde[j][i] = (img.getRGB(i, j) >> 8) & 0xff;
                mazul[j][i] = img.getRGB(i, j) & 0xff;
            }
        }
    }

    /*Obtine los pixeles de la imagen y rellena la matriz MGRIS con nuevos valores*/
    private void llenagrises(boolean si) {
        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j++) {

                int r = mrojo[j][i];
                int g = mverde[j][i];
                int b = mazul[j][i];

                int avg = (r + g + b) / 3;

                mgris[j][i] = avg;

                int p = 0 | (avg << 16) | (avg << 8) | avg;

                img.setRGB(i, j, p);
            }
        }
        try {
            f = new File(fc.getSelectedFile() + "gris.png");
            if (si) {
                ImageIO.write(img, "png", f);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("listo sin errores...");
    }

    /*Filtro promedio*/
    private void promedio(String valor) {
        try {
            File f1 = new File(valor);
            BufferedImage img1 = ImageIO.read(f1);

            for (int i = 1; i < alto - 1; i++) {
                for (int j = 1; j < ancho - 1; j++) {
                    int p = img1.getRGB(j, i);
                    int n = (mgris[i - 1][j - 1] * 1)
                            + (mgris[i - 1][j] * 1)
                            + (mgris[i - 1][j + 1] * 1)
                            + (mgris[i][j + 1] * 1)
                            + (mgris[i + 1][j + 1] * 1)
                            + (mgris[i + 1][j] * 1)
                            + (mgris[i + 1][j - 1] * 1)
                            + (mgris[i][j - 1] * 1);
                    mpromedio[i][j] = n / 9;
                    p = (((p >> 24) & 0xff) << 24) | ((n / 9) << 16) | ((n / 9) << 8) | (n / 9);
                    img1.setRGB(j, i, p);
                }
            }

            File f2 = new File(fc.getSelectedFile() + "promedio.png");
            ImageIO.write(img1, "png", f2);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /*Asigan a el arreglo "HISTOGRAMA" las veces que aparece un número*/
    private void histograma(int mpromedio[][]) {
        //int suma = 0;
        for (int t = 0; t <= 255; t++) {
            int c = 0;
            for (int i = 0; i < ancho; i++) {
                for (int j = 0; j < alto; j++) {
                    if (mpromedio[j][i] == t) {
                        c++;
                    }
                }
            }
            histograma[t] = c;
            //suma += c;
        }
    }

    /*Retorna el arreglo HISTOGRAMA*/
    public int[] gethistograma() {
        return histograma;
    }

    /*Método de OTZU(aún está mal)*/
    public int otsu(int ar[]) {
        int aux1[] = new int[256];
        int aux2[] = new int[256];
        int aux3 = 0;
        int aux4 = ancho * alto;
        aux1[0] = aux3;
        aux2[0] = aux4;
        for (int i = 0; i < 256; i++) {
            aux3 += histograma[i];
            aux4 -= histograma[i];
            if (i < 255) {
                aux1[i + 1] = aux3;
                aux2[i + 1] = aux4;
            }
        }

        for (int i = 0; i < 256; i++) {
            if (aux1[i] >= aux2[i]) {
                if (i > 255 && (i - 60) > 0) {
                    i -= 45;
                } else if ((i + 60) < 255) {
                    i += 45;
                } else {
                    return i;
                }
                return i;
            }
        }
        return 0;
    }

    /*Obtiene una imagen en escala de grises y la convierte a imagen binaria*/
    private void blancoynegro(int valor, String ruta) {
        try {
            f = new File(ruta);
            img = ImageIO.read(f);
            for (int i = 0; i < ancho; i++) {
                for (int j = 0; j < alto; j++) {
                    int p = img.getRGB(i, j);
                    int a = (p >> 24) & 0xff;
                    int r = (p >> 16) & 0xff;
                    int g = (p >> 8) & 0xff;
                    int b = p & 0xff;
                    //----Rojo--
                    if (r <= valor) {
                        r = 255;
                    } else {
                        r = 0;
                    }
                    //----Verde--
                    if (g <= valor) {
                        g = 255;
                    } else {
                        g = 0;
                    }
                    //----Azul--
                    if (b <= valor) {
                        b = 255;
                    } else {
                        b = 0;
                    }
                    p = (a << 24) | (r << 16) | (g << 8) | b;
                    if (p == 0 || p == -1) {
                        mbyn[j][i] = 0;
                    } else {
                        mbyn[j][i] = 1;
                    }

                    img.setRGB(i, j, p);
                }
            }
            f = new File(fc.getSelectedFile() + "blancoynegro.png");
            ImageIO.write(img, "png", f);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        // Imagen2 i = new Imagen2();
        new Imagen().setVisible(true);
    }

    private void etiquetado(int[][] matriz) {
        int contador = 0;
        Lista lista = new Lista();
        for (int i = 0; i < alto; i++) {
            for (int j = 0; j < ancho; j++) {
                if (matriz[i][j] != 0) {
                    if (vecinosCeros(matriz, i, j)) {
                        contador++;
                        mregiones[i][j] = contador;
                    } else if (numeroIgual(mregiones, i, j, contador)) {
                        mregiones[i][j] = contador;
                    } else {
                        mregiones[i][j] = numeroVecino(mregiones, i, j);
                        int[] arr = arreglomodificado(mregiones, i, j);
                        lista.insertar(arr);
                    }
                } else {
                    mregiones[i][j] = 0;
                }
            }
        }
        for (int i = 0; i < alto; i++) {
            for (int j = 0; j < ancho; j++) {
                mregiones[i][j] = lista.buscar(mregiones[i][j]);
            }
        }
        lista.imprime();

    }

    private void imprimeValores() {
        int numero = 0, numero2 = 0, numero3 = 0, numero4 = 0, numero5 = 0, numero6 = 0, numero7 = 0, numero8 = 0, numero9 = 0, numero10 = 0, numero11 = 0, numero12 = 0;
        int conta1 = 0, contad2 = 0, contad3 = 0, contad4 = 0, contad5 = 0, contad6 = 0, contad7 = 0, contad8 = 0, contad9 = 0, contad10 = 0, contad11 = 0, contad12 = 0;

        int count = 0;
        boolean activo1 = false;
        for (int i = 0; i < alto; i++) {
            for (int j = 0; j < ancho; j++) {
                if (mregiones[i][j] != 0 && !activo1) {
                    numero = mregiones[i][j];
                    activo1 = true;
                    count++;
                }
                if (mregiones[i][j] != 0 && activo1 && count == 1 && mregiones[i][j] != numero) {
                    numero2 = mregiones[i][j];
                    count++;
                }
                if (mregiones[i][j] != 0 && activo1 && count == 2 && mregiones[i][j] != numero && mregiones[i][j] != numero2) {
                    numero3 = mregiones[i][j];
                    count++;
                }
                if (mregiones[i][j] != 0 && activo1 && count == 3 && mregiones[i][j] != numero && mregiones[i][j] != numero2 && mregiones[i][j] != numero3) {
                    numero4 = mregiones[i][j];
                    count++;
                }
                if (mregiones[i][j] != 0 && activo1 && count == 4 && mregiones[i][j] != numero && mregiones[i][j] != numero2 && mregiones[i][j] != numero3 && mregiones[i][j] != numero4) {
                    numero5 = mregiones[i][j];
                    count++;
                }
                if (mregiones[i][j] != 0 && activo1 && count == 5 && mregiones[i][j] != numero && mregiones[i][j] != numero2 && mregiones[i][j] != numero3 && mregiones[i][j] != numero4 && mregiones[i][j] != numero5) {
                    numero6 = mregiones[i][j];
                    count++;
                }
                if (mregiones[i][j] != 0 && activo1 && count == 6 && mregiones[i][j] != numero && mregiones[i][j] != numero2 && mregiones[i][j] != numero3 && mregiones[i][j] != numero4 && mregiones[i][j] != numero5 && mregiones[i][j] != numero6) {
                    numero7 = mregiones[i][j];
                    count++;
                }
                if (mregiones[i][j] != 0 && activo1 && count == 7 && mregiones[i][j] != numero && mregiones[i][j] != numero2 && mregiones[i][j] != numero3 && mregiones[i][j] != numero4 && mregiones[i][j] != numero5 && mregiones[i][j] != numero6 && mregiones[i][j] != numero7) {
                    numero8 = mregiones[i][j];
                    count++;
                }
                if (mregiones[i][j] != 0 && activo1 && count == 8 && mregiones[i][j] != numero && mregiones[i][j] != numero2 && mregiones[i][j] != numero3 && mregiones[i][j] != numero4 && mregiones[i][j] != numero5 && mregiones[i][j] != numero6 && mregiones[i][j] != numero7 && mregiones[i][j] != numero8) {
                    numero9 = mregiones[i][j];
                    count++;
                }
                if (mregiones[i][j] != 0 && activo1 && count == 9 && mregiones[i][j] != numero && mregiones[i][j] != numero2 && mregiones[i][j] != numero3 && mregiones[i][j] != numero4 && mregiones[i][j] != numero5 && mregiones[i][j] != numero6 && mregiones[i][j] != numero7 && mregiones[i][j] != numero8 && mregiones[i][j] != numero9) {
                    numero10 = mregiones[i][j];
                    count++;
                }
                if (mregiones[i][j] != 0 && activo1 && count == 10 && mregiones[i][j] != numero && mregiones[i][j] != numero2 && mregiones[i][j] != numero3 && mregiones[i][j] != numero4 && mregiones[i][j] != numero5 && mregiones[i][j] != numero6 && mregiones[i][j] != numero7 && mregiones[i][j] != numero8 && mregiones[i][j] != numero9 && mregiones[i][j] != numero10) {
                    numero11 = mregiones[i][j];
                    count++;
                }
                if (mregiones[i][j] != 0 && activo1 && count == 11 && mregiones[i][j] != numero && mregiones[i][j] != numero2 && mregiones[i][j] != numero3 && mregiones[i][j] != numero4 && mregiones[i][j] != numero5 && mregiones[i][j] != numero6 && mregiones[i][j] != numero7 && mregiones[i][j] != numero8 && mregiones[i][j] != numero9 && mregiones[i][j] != numero10 && mregiones[i][j] != numero11) {
                    numero12 = mregiones[i][j];
                    count++;
                }
            }
        }
        for (int i = 0; i < alto; i++) {
            for (int j = 0; j < ancho; j++) {
                if (mregiones[i][j] == numero) {
                    conta1++;
                }
                if (mregiones[i][j] == numero2) {
                    contad2++;
                }
                if (mregiones[i][j] == numero3) {
                    contad3++;
                }
                if (mregiones[i][j] == numero4) {
                    contad4++;
                }
                if (mregiones[i][j] == numero5) {
                    contad5++;
                }
                if (mregiones[i][j] == numero6) {
                    contad6++;
                }
                if (mregiones[i][j] == numero7) {
                    contad7++;
                }
                if (mregiones[i][j] == numero8) {
                    contad8++;
                }
                if (mregiones[i][j] == numero9) {
                    contad9++;
                }
                if (mregiones[i][j] == numero10) {
                    contad10++;
                }
                if (mregiones[i][j] == numero11) {
                    contad11++;
                }
                if (mregiones[i][j] == numero12) {
                    contad12++;
                }
            }
        }

        System.out.println("\n" + numero + " = " + conta1
                + ((contad2 > 40 && numero2 != 0) ? ("\n" + numero2 + " = " + contad2) : "")
                + ((contad3 > 40 && numero3 != 0) ? ("\n" + numero3 + " = " + contad3) : "")
                + ((contad4 > 40 && numero4 != 0) ? ("\n" + numero4 + " = " + contad4) : "")
                + ((contad5 > 40 && numero5 != 0) ? ("\n" + numero5 + " = " + contad5) : "")
                + ((contad6 > 40 && numero6 != 0) ? ("\n" + numero6 + " = " + contad6) : "")
                + ((contad7 > 40 && numero7 != 0) ? ("\n" + numero7 + " = " + contad7) : "")
                + ((contad8 > 40 && numero8 != 0) ? ("\n" + numero8 + " = " + contad8) : "")
                + ((contad9 > 40 && numero9 != 0) ? ("\n" + numero9 + " = " + contad9) : "")
                + ((contad10 > 40 && numero10 != 0) ? ("\n" + numero10 + " = " + contad10) : "")
                + ((contad11 > 40 && numero11 != 0) ? ("\n" + numero11 + " = " + contad11) : "")
                + ((contad12 > 40 && numero12 != 0) ? ("\n" + numero12 + " = " + contad12) : "")
        );

        try {
            File f1 = new File(fc.getSelectedFile() + "promedio.png");
            BufferedImage img1 = ImageIO.read(f1);
            BufferedImage img2 = ImageIO.read(f1);
            BufferedImage img3 = ImageIO.read(f1);
            BufferedImage img4 = ImageIO.read(f1);
            BufferedImage img5 = ImageIO.read(f1);
            BufferedImage img6 = ImageIO.read(f1);
            BufferedImage img7 = ImageIO.read(f1);
            BufferedImage img8 = ImageIO.read(f1);
            BufferedImage img9 = ImageIO.read(f1);
            BufferedImage img10 = ImageIO.read(f1);
            BufferedImage img11 = ImageIO.read(f1);
            BufferedImage img12 = ImageIO.read(f1);
            //int numeroDeRegiones = 0;

            for (int i = 0; i < ancho; i++) {
                for (int j = 0; j < alto; j++) {
                    int p = img1.getRGB(i, j);
                    if ((conta1 > 40 && numero != 0)) {
                        //numeroDeRegiones++;
                        if (mregiones[j][i] == numero) {
                            p = (((p >> 24) & 0xff) << 24) | (0 << 16) | (0 << 8) | 0;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        img1.setRGB(i, j, p);
                    }
                    if ((contad2 > 40 && numero2 != 0)) {
                        //numeroDeRegiones++;
                        if (mregiones[j][i] == numero2) {
                            p = (((p >> 24) & 0xff) << 24) | (0 << 16) | (0 << 8) | 0;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        img2.setRGB(i, j, p);
                    }
                    if ((contad3 > 40 && numero3 != 0)) {
                        //numeroDeRegiones++;
                        if (mregiones[j][i] == numero3) {
                            p = (((p >> 24) & 0xff) << 24) | (0 << 16) | (0 << 8) | 0;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        img3.setRGB(i, j, p);
                    }
                    if ((contad4 > 40 && numero4 != 0)) {
                        //numeroDeRegiones++;
                        if (mregiones[j][i] == numero4) {
                            p = (((p >> 24) & 0xff) << 24) | (0 << 16) | (0 << 8) | 0;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        img4.setRGB(i, j, p);
                    }
                    if ((contad5 > 40 && numero5 != 0)) {
                        //numeroDeRegiones++;
                        if (mregiones[j][i] == numero5) {
                            p = (((p >> 24) & 0xff) << 24) | (0 << 16) | (0 << 8) | 0;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        img5.setRGB(i, j, p);
                    }
                    if ((contad6 > 40 && numero6 != 0)) {
                        //numeroDeRegiones++;
                        if (mregiones[j][i] == numero6) {
                            p = (((p >> 24) & 0xff) << 24) | (0 << 16) | (0 << 8) | 0;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        img6.setRGB(i, j, p);
                    }
                    if ((contad7 > 40 && numero7 != 0)) {
                        //numeroDeRegiones++;
                        if (mregiones[j][i] == numero7) {
                            p = (((p >> 24) & 0xff) << 24) | (0 << 16) | (0 << 8) | 0;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        img7.setRGB(i, j, p);
                    }
                    if ((contad8 > 40 && numero8 != 0)) {
                        //numeroDeRegiones++;
                        if (mregiones[j][i] == numero8) {
                            p = (((p >> 24) & 0xff) << 24) | (0 << 16) | (0 << 8) | 0;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        img8.setRGB(i, j, p);
                    }
                    if ((contad9 > 40 && numero9 != 0)) {
                        //numeroDeRegiones++;
                        if (mregiones[j][i] == numero9) {
                            p = (((p >> 24) & 0xff) << 24) | (0 << 16) | (0 << 8) | 0;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        img9.setRGB(i, j, p);
                    }
                    if ((contad10 > 40 && numero10 != 0)) {
                        //numeroDeRegiones++;
                        if (mregiones[j][i] == numero10) {
                            p = (((p >> 24) & 0xff) << 24) | (0 << 16) | (0 << 8) | 0;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        img10.setRGB(i, j, p);
                    }
                    if ((contad11 > 40 && numero11 != 0)) {
                        //numeroDeRegiones++;
                        if (mregiones[j][i] == numero11) {
                            p = (((p >> 24) & 0xff) << 24) | (0 << 16) | (0 << 8) | 0;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        img11.setRGB(i, j, p);
                    }
                    if ((contad12 > 40 && numero12 != 0)) {
                        //numeroDeRegiones++;
                        if (mregiones[j][i] == numero12) {
                            p = (((p >> 24) & 0xff) << 24) | (0 << 16) | (0 << 8) | 0;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        img12.setRGB(i, j, p);
                    }
                }
            }

            //System.out.println("Total de regiones = "+numeroDeRegiones);
            if ((conta1 > 40 && numero != 0)) {
                File f2 = new File(fc.getSelectedFile() + "etiqueta1.png");
                ImageIO.write(img1, "png", f2);
            }
            if ((contad2 > 40 && numero2 != 0)) {
                File f2 = new File(fc.getSelectedFile() + "etiqueta2.png");
                ImageIO.write(img2, "png", f2);
            }
            if ((contad3 > 40 && numero3 != 0)) {
                File f2 = new File(fc.getSelectedFile() + "etiqueta3.png");
                ImageIO.write(img3, "png", f2);
            }
            if ((contad4 > 40 && numero4 != 0)) {
                File f2 = new File(fc.getSelectedFile() + "etiqueta4.png");
                ImageIO.write(img4, "png", f2);
            }
            if ((contad5 > 40 && numero5 != 0)) {
                File f2 = new File(fc.getSelectedFile() + "etiqueta5.png");
                ImageIO.write(img5, "png", f2);
            }
            if ((contad6 > 40 && numero6 != 0)) {
                File f2 = new File(fc.getSelectedFile() + "etiqueta6.png");
                ImageIO.write(img6, "png", f2);
            }
            if ((contad7 > 40 && numero7 != 0)) {
                File f2 = new File(fc.getSelectedFile() + "etiqueta7.png");
                ImageIO.write(img7, "png", f2);
            }
            if ((contad8 > 40 && numero8 != 0)) {
                File f2 = new File(fc.getSelectedFile() + "etiqueta8.png");
                ImageIO.write(img8, "png", f2);
            }
            if ((contad9 > 40 && numero9 != 0)) {
                File f2 = new File(fc.getSelectedFile() + "etiqueta9.png");
                ImageIO.write(img9, "png", f2);
            }
            if ((contad10 > 40 && numero10 != 0)) {
                File f2 = new File(fc.getSelectedFile() + "etiqueta10.png");
                ImageIO.write(img10, "png", f2);
            }
            if ((contad11 > 40 && numero11 != 0)) {
                File f2 = new File(fc.getSelectedFile() + "etiqueta11.png");
                ImageIO.write(img11, "png", f2);
            }
            if ((contad12 > 40 && numero12 != 0)) {
                File f2 = new File(fc.getSelectedFile() + "etiqueta12.png");
                ImageIO.write(img12, "png", f2);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    private void regiones2() {
        ArrayList<Integer> lista = new ArrayList<Integer>();
        for (int i = 0; i < alto; i++) {
            for (int j = 0; j < ancho; j++) {
                if (!checaLista(lista, mregiones[i][j])) {
                    lista.add(mregiones[i][j]);
                }
            }
        }
        System.out.println("Hay " + lista.size() + " regiones");
        ArrayList<BufferedImage> ll = new ArrayList<BufferedImage>();
        try {
            for (int i = 0; i < lista.size(); i++) {
                ll.add(new BufferedImage(ancho, alto, HEIGHT));
                System.out.println("valor " + i + " = " + lista.get(i));
            }
            for (int k = 0; k < lista.size(); k++) {
                for (int i = 0; i < ancho; i++) {
                    for (int j = 0; j < alto; j++) {
                        int p = ll.get(k).getRGB(i, j);
                        if (mregiones[j][i] == (int) lista.get(k)) {
                            p = (((p >> 24) & 0xff) << 24) | (k << 16) | (k << 8) | k;
                        } else {
                            p = (((p >> 24) & 0xff) << 24) | (255 << 16) | (255 << 8) | 255;
                        }
                        ll.get(k).setRGB(i, j, p);
                    }
                }
            }
            for (int i = 0; i < lista.size(); i++) {
                ImageIO.write(ll.get(i), "png", new File(fc.getSelectedFile() + "Dato" + i + ".png"));
            }
        } catch (Exception e) {
        }
    }

    private boolean checaLista(ArrayList lista, int x) {
        for (int i = 0; i < lista.size(); i++) {
            if (x == (int) lista.get(i)) {
                return true;
            }
        }
        return false;
    }

    private boolean numeroIgual(int[][] matriz, int x, int y, int cont) {
        try {
            if (matriz[x - 1][y - 1] == cont) {
                return true;
            }
            if (matriz[x - 1][y - 1] != 0) {
                return false;
            }
        } catch (Exception e) {
        }
        try {
            if (matriz[x - 1][y] == cont) {
                return true;
            }
            if (matriz[x - 1][y] != 0) {
                return false;
            }
        } catch (Exception e) {
        }
        try {
            if (matriz[x - 1][y + 1] == cont) {
                return true;
            }
            if (matriz[x - 1][y + 1] != 0) {
                return false;
            }
        } catch (Exception e) {
        }
        try {
            if (matriz[x][y - 1] == cont) {
                return true;
            }
            if (matriz[x][y - 1] != 0) {
                return false;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private int numeroVecino(int[][] matriz, int x, int y) {
        try {
            if (matriz[x - 1][y - 1] != 0) {
                return matriz[x - 1][y - 1];
            }
        } catch (Exception e) {
        }
        try {
            if (matriz[x - 1][y] != 0) {
                return matriz[x - 1][y];
            }
        } catch (Exception e) {
        }
        try {
            if (matriz[x - 1][y + 1] != 0) {
                return matriz[x - 1][y + 1];
            }
        } catch (Exception e) {
        }
        try {
            if (matriz[x][y - 1] != 0) {
                return matriz[x][y - 1];
            }
        } catch (Exception e) {
        }
        return 0;
    }

    private int[] arreglomodificado(int[][] matriz, int x, int y) {
        int[] aexistentes = new int[5];
        try {
            aexistentes[0] = matriz[x - 1][y - 1];
        } catch (Exception e) {
        }
        try {
            aexistentes[1] = matriz[x - 1][y];
        } catch (Exception e) {
        }
        try {
            aexistentes[2] = matriz[x - 1][y + 1];
        } catch (Exception e) {
        }
        try {
            aexistentes[3] = matriz[x][y - 1];
        } catch (Exception e) {
        }
        aexistentes[4] = matriz[x][y];
        return aexistentes;
    }

    private boolean vecinosCeros(int[][] matriz, int x, int y) {
        int contador = 0;
        try {
            contador += matriz[x - 1][y - 1];
        } catch (Exception e) {
        }
        try {
            contador += matriz[x - 1][y];
        } catch (Exception e) {
        }
        try {
            contador += matriz[x - 1][y + 1];
        } catch (Exception e) {
        }
        try {
            contador += matriz[x][y - 1];
        } catch (Exception e) {
        }
        return contador == 0;
    }

    /*Retorna el numero que apareció mas veces en el histograma(el pico mas alto)*/
    private int getmayornumero() {
        int hum = histograma[0];
        int s = 0;
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                if (hum >= histograma[j]) {
                    hum = histograma[j];
                    s = i;
                }
            }
        }
        return s;
    }

    /*Imprime las matrices MROJO,MVERDE,MAZUL*/
    private void imprime() {
        System.out.println("----ROJO----");
        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j++) {
                System.out.print(mrojo[j][i] + "|");
            }
            System.out.println();
        }
        System.out.println("----VERDE----");
        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j++) {
                System.out.print(mverde[j][i] + "|");
            }
            System.out.println();
        }
        System.out.println("----AZUL----");
        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j++) {
                System.out.print(mazul[j][i] + "|");
            }
            System.out.println();
        }
    }

}
