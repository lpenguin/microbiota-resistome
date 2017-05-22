import java.io.FileNotFoundException;

/**
 * main program class, which run simulation
 */

public class Main {


    public static void main(String[] args) {

        for (int i = 0; i < args.length; i++) {
            System.out.println("Argument "+(i+1)+" = "+args[i]);
        }
        int iterationNum = Integer.parseInt(args[0]);
        String outDir = "out/simulations/";
        String fileName = args[1];
        try {
            new Simulation(iterationNum, fileName);
        } catch (FileNotFoundException e) {
            System.out.println("File not found "+fileName);
            //e.printStackTrace();
        }
//  //      SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//  //              JFrame frame = new JFrame();
//                frame.add(new MyComponentFromVera());
//  //              frame.add(new Rectangle()); //mooving rectangle
//                frame.pack();
//  //              frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.setLocationRelativeTo(null); //?
//                frame.setVisible(true);   //?
//            }
//        });
    }
}