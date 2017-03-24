/**
 * main program class, which run simulation
 */

public class Main {


    public static void main(String[] args) {

        for (int i = 0; i < args.length; i++) {
            System.out.println("Argument "+(i+1)+" = "+args[i]);
        }
        int iterationNum = Integer.parseInt(args[0]);
        new MyComponent(iterationNum);
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