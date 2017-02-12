package virtualcpu;

/**
 *
 * @author Nicklas Molving
 */
public class VirtualCPU {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Welcome to the awesome CPU program");
        Program program = new Program("00101001", "00000001", "MOV B +7");
//        Program program = new Program("MOV A +3", "MOV A +3");
        Machine machine = new Machine();
        machine.load(program);
        machine.print(System.out);
        machine.tick();
        machine.print(System.out);
        machine.tick();
        machine.print(System.out);
        machine.tick();
        machine.print(System.out);
//        machine.tick();
//        machine.print(System.out);

        for (int line : program) {
            System.out.println(">>> " + line);
        }

    }
}
