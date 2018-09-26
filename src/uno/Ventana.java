
package uno;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Ventana extends JFrame{
    JPanel panel;
    public Ventana(int[]is){
        setTitle("Como Hacer Graficos con Java");
        setSize(800,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        init(is);
    }
 
    public void init(int[]is) {
        panel = new JPanel();
        getContentPane().add(panel);
       
        DefaultCategoryDataset linea = new DefaultCategoryDataset();
        int ar1[] = is;
         
        for (int i = 0; i < ar1.length; i++) {
            linea.addValue(ar1[i], "#", i+"");
        }
 
        // Creando el Grafico
        JFreeChart chart=ChartFactory.createLineChart("Histograma",
                "0 - 255","#nu veces",linea,PlotOrientation.VERTICAL,
                true,true,false);
        
        // Mostrar Grafico
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel);
    }
}
 